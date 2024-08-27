package autobot;


import autobot.fuzzy.Fuzzy;
import autobot.genetic.GeneticAlgorithm;
import autobot.neural.Dataset;
import autobot.neural.NeuralNetwork;
import autobot.prolog.Prolog;
import autobot.records.ActiveBullet;
import autobot.utils.Consts;
import autobot.utils.MathUtils;
import autobot.utils.draw.Draw;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.signum;

public class Autobot extends AdvancedRobot {

    Point2D robotLocation;
    List<ActiveBullet> activeBullets = new ArrayList<>();

    Enemy enemyBot = new Enemy();
    int direction = 1;

    // Genetic Algorithm Variables:
    int velocityGA;
    int safeDistanceGA;
    int bordersMarginGA;

    // Fuzzy Variables:
    int velocityFuzzy;
    boolean hasLifeRisk = false;

    // Bayes Variables:
    double gunAngleAfterRotation;

    // Neural Network variables:
    // input: "enemyDistance", "enemyVelocity", "enemyDirectionRelativeToGun"
    // output: "notHit", "hit"
    NeuralNetwork neuralNetwork = new NeuralNetwork("Autobot", 3, 10, 2);
    String[] attributesNames = {"enemyDistance", "enemyVelocity", "enemyDirectionRelativeToGun", "notHit", "hit"};
    Dataset neuralNetworkDataset = new Dataset(attributesNames);
    // {"enemyDistance", "enemyVelocity", "enemyAngle", "enemyHeading", "myGunToEnemyAngle", "firePower", "hit"};

    public void run() {

        initAutobot();

        //noinspection InfiniteLoopStatement
        do {
            nextTurn();

            boolean isRadarTurnComplete = Prolog.isRadarTurnComplete(getRadarTurnRemaining());
            if (isRadarTurnComplete) setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

            moveRobot();
            execute();

        } while (true);
    }

    private void initAutobot() {
        Prolog.loadPrologFile();
        GeneticAlgorithm.init(getRoundNum());
        Fuzzy.init();

        neuralNetwork.setupNetwork();

        // if first round, delete arff files
        if (getRoundNum() == 0) {
            neuralNetworkDataset.deleteDatasetFiles();
        }


//        try {
//            Bayes.init();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        changeRobotColors();

        setAdjustRadarForRobotTurn(true); // Set gun to turn independent of the robot's turn
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);
    }

    public void onRoundEnded(RoundEndedEvent event) {
        GeneticAlgorithm.saveGeneticData();

        neuralNetwork.trainAndUpdateNetwork(neuralNetworkDataset);
        neuralNetworkDataset.saveFile("Autobot" + getRoundNum() + ".arff");

//      Bayes.saveDataForNextRound();

        // Battle finished
        if (getRoundNum() >= getNumRounds() - 1) {
            GeneticAlgorithm.clearGeneticData();
//            neuralNetworkDataset.saveHistoryFiles(); // TODO: save history files. make a final training and update neural network. save on .eg file
//            Bayes.clearDataset();
        }

    }

    // ============= BULLET EVENTS ================

    public void onBulletHit(BulletHitEvent event) {
        recordBulletResult(event.getBullet(), true);
    }

    public void onBulletMissed(BulletMissedEvent event) {
        recordBulletResult(event.getBullet(), false);
    }

    public void onBulletHitBullet(BulletHitBulletEvent event) {
        recordBulletResult(event.getBullet(), false);
    }

    // ============= HIT EVENTS ================

    public void onHitByBullet(HitByBulletEvent e) {
        ahead(20);
        setTurnRight(MathUtils.random(-30, 30));
    }

    public void onHitWall(HitWallEvent e) {
        checkBorders();
    }

    // ============= SCANNED ROBOT ================

    public void onScannedRobot(ScannedRobotEvent e) {
        enemyBot.scanned(this, e);
        setRadarRotation();
        setGunRotation();
        handleSetFire();
    }

    // ============= RADAR / GUN ================

    private void setRadarRotation() {

        // Get the enemy angle
        double enemyAngle = enemyBot.getAngle();

        // Relativize enemy angle
        double radarInitialTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());

        // Radar goes that much further in the direction it is going to turn
        double extraRadarTurn = Math.toDegrees(Math.atan(Consts.RADAR_COVERAGE_DIST / enemyBot.getDistance()));
        double radarTotalTurn = radarInitialTurn + (extraRadarTurn * signum(radarInitialTurn));

        // Radar goes to the less distance direction
        double normalizedRadarTotalTurn = Utils.normalRelativeAngleDegrees(radarTotalTurn);
        double radarTurn = (Math.min(Math.abs(normalizedRadarTotalTurn), Rules.RADAR_TURN_RATE)) * signum(normalizedRadarTotalTurn);

        // Set radar turn
        setTurnRadarRight(radarTurn);
    }

    private void setGunRotation() {

        // Get the enemy angle
        double enemyAngle = enemyBot.getAngle();

        // Relativize enemy angle
        double gunInitialTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getGunHeading());

        // Gun goes to the less distance direction
        double gunTurn = (Math.min(Math.abs(gunInitialTurn), Rules.GUN_TURN_RATE)) * signum(gunInitialTurn);

        // Set gun angle after rotation for Bayes
        this.gunAngleAfterRotation = Utils.normalRelativeAngleDegrees(getGunHeading() + gunTurn);

        // Set gun turn
        setTurnGunRight(gunTurn);

    }

    private void handleSetFire() {

        double myGunToEnemy = this.gunAngleAfterRotation;

        boolean shouldFire = (getGunHeat() == 0) && (getEnergy() > Consts.MIN_LIFE_TO_FIRE);
        if (shouldFire) {


//            double enemyDirectionToGun = enemyBot.getEnemyDirectionToGun(); // 1 if enemy is in front of gun, -1 otherwise

            // output[1] is the hit factor based on previous record and neural network calculations
            double[] neuralOutputs = neuralNetwork.getOutputs(enemyBot.getDistance(), enemyBot.getVelocity(), enemyBot.getEnemyDirectionToGun());
            double hitFactor = neuralOutputs[1];
            double firePower = hitFactor * (300 / enemyBot.getDistance());
            double bestFirePowerToHit = Math.min(firePower, 3);

            System.out.println();
            System.out.println("Neural network inputs: " + enemyBot.getDistance() + ", " + enemyBot.getVelocity() + ", " + enemyBot.getEnemyDirectionToGun());
            System.out.println("Neural network outputs: " + neuralOutputs[0] + ", " + neuralOutputs[1]);
            System.out.println("Best fire power to hit: " + bestFirePowerToHit);

            Bullet bullet = fireBullet(bestFirePowerToHit);
            if (bullet != null) {
                activeBullets.add(new ActiveBullet(bullet, enemyBot.clone(), myGunToEnemy, bestFirePowerToHit));
            }
        }
    }

    // ============= MOVEMENTS ================

    public void moveRandomly() {
        // set default movement attributes, considering robot is at center of arena

        double maxHeadTurn = (10 - (0.75 * getVelocity()));                   //max robot can turn considering its velocity
        double headTurn = MathUtils.random(-1 * maxHeadTurn, maxHeadTurn);    //random relative angle to turn

        setTurnRight(headTurn);
        setAhead(velocityGA * direction);
    }

    public void checkBorders() {
        double xLimit = getBattleFieldWidth() / 2;
        double yLimit = getBattleFieldHeight() / 2;

        double x = robotLocation.getX() - xLimit;
        double y = robotLocation.getY() - yLimit;

        boolean xMargin = xLimit - (Math.abs(x)) < bordersMarginGA;
        boolean yMargin = yLimit - (Math.abs(y)) < bordersMarginGA;

        if (xMargin || yMargin) {
            double xSign = xMargin ? signum(x) : 0;
            double ySign = yMargin ? signum(y) : 0;
            avoidBorders(xSign, ySign);
        }

    }

    public void avoidBorders(double xSign, double ySign) {
        // To avoid borders, turn robot in an absolute angle, defined according to which border it is approaching
        // e.g.: when robot approaches left border, must turn in any angle between 0º ~ 180º

        // Quadrants map:
        // 1Q | 2Q
        // 3Q | 4Q

        boolean xMargin = xSign != 0;
        boolean yMargin = ySign != 0;
        boolean isAtEdge = xMargin && yMargin;

        // at positive edges (2Q and 3Q), use same calc as xMargin
        if (isAtEdge) {
            xMargin = (xSign * ySign) > 0;
        }

        // If next to x borders, or in positive edges, use Math.acos(-x)
        // Otherwise, use Math.asin(y)

        double minAngle = xMargin ? Math.acos(-1 * xSign) : Math.asin(ySign);
        minAngle = Math.toDegrees(minAngle);

        double maxAngle = isAtEdge ? 90 : 180;
        maxAngle += minAngle;

        double absTurnAngle = MathUtils.random(minAngle, maxAngle);
        double headTurn = Utils.normalRelativeAngleDegrees(absTurnAngle - getHeading());
        setTurnRight(headTurn);

        double aheadDist = MathUtils.random(0, 3);  //reduce vel to not hit wall
        setAhead(aheadDist);

        //If not facing wall, move faster!
        if (getHeading() > minAngle && getHeading() < maxAngle) {
            setAhead(max(velocityFuzzy, velocityGA));
        }
    }

    public void checkEnemyGunReady() {
        if (enemyBot.isGunReady()) { // enemy gun will shoot any time now. do not move
            setAhead(0);
            direction = (int) signum(MathUtils.random(-1.0, 1.0));               // may move backwards randomly
        }
    }

    public void checkEnemyIsClose() {
        boolean isEnemyClose = Prolog.isEnemyClose(enemyBot.getDistance(), safeDistanceGA);
        if (isEnemyClose) {
            int escapeVelocity = MathUtils.random(10, 20); // random velocity to allow more freedom to turn
            setAhead(escapeVelocity);
        }
    }

    public void checkLifeRisk() {
        if (hasLifeRisk) {
            setAhead(velocityFuzzy);
        }
    }

    public void moveRobot() {
        // Set movement attributes (turn and ahead)

        // Set distance and turn
        moveRandomly();         // default behavior - less priority
        checkLifeRisk();        // must check before borders to avoid them
        checkBorders();         // turn to escape borders - setTurn


        // Skip next methods to avoid error or override
        if (!enemyBot.isScanned() || hasLifeRisk) return;

        // Override setAhead
        checkEnemyGunReady();
        checkEnemyIsClose();

        // Events priority: LifeRisk > EnemyIsClose > EnemyGunReady > default random


    }

    // ============= GENETIC ALGORITHM ================

    public void applyGeneticAlgorithm() {
        GeneticAlgorithm.updateGA(getEnergy());
        velocityGA = GeneticAlgorithm.getVelocity();
        safeDistanceGA = GeneticAlgorithm.getSafeDistance();
        bordersMarginGA = GeneticAlgorithm.getBordersMargin();
    }

    // ============= FUZZY ================

    public void applyFuzzyAlgorithm() {
//      Variables: distance, enemy_energy, autobot_energy
        Fuzzy.setFuzzyValues(enemyBot.getDistance(), enemyBot.getEnergy(), getEnergy());
        defuzzyResults();
    }

    public void defuzzyResults() {
        double riskFactor = Fuzzy.getDefuzzyValue();
        hasLifeRisk = riskFactor > 0;
        if (hasLifeRisk) {
            // Higher risk must avoid borders and enemyBot at all costs
            // In order to avoid hit borders, velocity is reduced when is atMargin

            final int MAX_FUZZY_VELOCITY = 30;
            final int MAX_FUZZY_DISTANCE = 250;
            final double MAX_FUZZY_BORDER_MARGIN = 115;

            velocityFuzzy = (int) (MAX_FUZZY_VELOCITY * riskFactor);
            safeDistanceGA = (int) (MAX_FUZZY_DISTANCE * riskFactor);
            bordersMarginGA = (int) (MAX_FUZZY_BORDER_MARGIN * riskFactor);

        }

    }

    // ============= BAYES ================

    private void recordBulletResult(Bullet eventBullet, boolean hasHit) {
        for (ActiveBullet activeBullet : activeBullets) {
            if (activeBullet.bulletInstance().equals(eventBullet)) {

                Enemy enemySnapshot = activeBullet.enemySnapshot();
//                double myGunToEnemyAngle = activeBullet.myGunToEnemyAngle();
//                double firePower = activeBullet.firePower();

//                BulletResult bulletResult = new BulletResult(enemySnapshot, myGunToEnemyAngle, firePower, hasHit);
//                activeBullets.remove(activeBullet);


                // Normalize inputs and add instance to dataset
                // Variables: {"enemyDistance", "enemyVelocity", "enemyDirectionRelativeToGun", "notHit", "hit"};
                double enemyDistance = enemySnapshot.getDistance() / 1000;
                double enemyVelocity = enemySnapshot.getVelocity() / 8;
                double enemyDirectionRelativeToGun = enemyBot.getEnemyDirectionToGun(); // 1 if enemy is in front of gun, -1 otherwise
                neuralNetworkDataset.appendInstance(enemyDistance, enemyVelocity, enemyDirectionRelativeToGun, hasHit ? 0 : 1, hasHit ? 1 : 0);

                /*
                try {
                    Bayes.recordBulletResult(bulletResult);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                */

            }
        }
    }

    // ============= OTHERS ================

    public void nextTurn() {
        // update myRobot location
        robotLocation = new Point2D.Double(getX(), getY());

        //cool down enemy gun
        enemyBot.passTurn(getGunCoolingRate());

        // apply genetic algorithm
        applyGeneticAlgorithm();
        applyFuzzyAlgorithm();

    }


    private void changeRobotColors() {
        setBodyColor(Color.WHITE);
        setGunColor(Color.BLACK);
        setRadarColor(Color.WHITE);
//        setBulletColor(Color.WHITE);
    }

    public void onPaint(Graphics2D g) {
        // Draw robot's security zone
        g.setColor(Color.green);
        Draw.drawCircle(g, getX(), getY(), safeDistanceGA);

        // Draw bordersMargin
        int w = (int) getBattleFieldWidth();
        int h = (int) getBattleFieldHeight();
        int margin = bordersMarginGA;

        g.setColor(new Color(0xff, 0x00, 0x00, 0x20));
        g.fillRect(0, 0, margin, h);            // left
        g.fillRect(0, 0, w, margin);            // bottom
        g.fillRect(w - margin, 0, margin, h);   // right
        g.fillRect(0, h - margin, w, margin);   // upper


    }
}
