package autobot;


import autobot.bayes.Bayes;
import autobot.bayes.enums.*;
import autobot.fuzzy.Fuzzy;
import autobot.genetic.GeneticAlgorithm;
import autobot.prolog.Prolog;
import autobot.records.ActiveBullet;
import autobot.records.BulletResult;
import autobot.utils.Consts;
import autobot.utils.MathUtils;
import autobot.utils.draw.Draw;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
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

    public void run() {

        Prolog.loadPrologFile();
        GeneticAlgorithm.init(getRoundNum());
        Fuzzy.init();
        try {
            Bayes.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        changeRobotColors();

        setAdjustRadarForRobotTurn(true); // Set gun to turn independent of the robot's turn
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

        //noinspection InfiniteLoopStatement
        do {
            nextTurn();

            boolean isRadarTurnComplete = Prolog.isRadarTurnComplete(getRadarTurnRemaining());

            if (isRadarTurnComplete) setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

            moveRobot();
            execute();

        } while (true);
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
        try {
            handleSetFire();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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

    private void handleSetFire() throws Exception {

        double enemyDistance = enemyBot.getDistance();
        double enemyVelocity = enemyBot.getVelocity();
        double enemyAngle = enemyBot.getAngle();
        double enemyHeading = enemyBot.getHeading();
        double myGunToEnemy = this.gunAngleAfterRotation;

        if (Prolog.shouldFire(getEnergy())) {
            double bestFirePowerToHit = Bayes.getBestFirePowerToHit(enemyDistance, enemyVelocity, enemyAngle, enemyHeading, myGunToEnemy);
            Bullet bullet = fireBullet(bestFirePowerToHit);
            System.out.println("Firing bullet with power: " + bestFirePowerToHit);
            Bayes.weka.displayGraph();
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
//            out.println("RUN! " + velocityFuzzy);
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
                double myGunToEnemyAngle = activeBullet.myGunToEnemyAngle();
                double firePower = activeBullet.firePower();

                BulletResult bulletResult = new BulletResult(enemySnapshot, myGunToEnemyAngle, firePower, hasHit);

                try {
                    Bayes.recordBulletResult(bulletResult);
                    activeBullets.remove(activeBullet);
                    return;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

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

    public void onRoundEnded(RoundEndedEvent event) {
        GeneticAlgorithm.saveGeneticData();

        if (getRoundNum() >= getNumRounds() - 1)
            // Battle finished
            GeneticAlgorithm.clearGeneticData();

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

    public static void main(String[] args) throws Exception {
        Bayes.init();


        // Add some instances to the dataset
        List<GenericAttribute> instance1 = Arrays.asList(
                EnemyDistance.RANGE_0_200,
                EnemyVelocity.RANGE_0_2,
                EnemyAngle.RANGE_0_90,
                EnemyHeading.RANGE_0_90,
                MyGunToEnemyAngle.RANGE_0_15,
                FirePower.FP_02,
                Hit.TRUE
        );
        Bayes.weka.addInstance(instance1);

        // Add more 20 instances with different values
        List<GenericAttribute> instance2 = Arrays.asList(
                EnemyDistance.RANGE_0_200,
                EnemyVelocity.RANGE_0_2,
                EnemyAngle.RANGE_0_90,
                EnemyHeading.RANGE_0_90,
                MyGunToEnemyAngle.RANGE_0_15,
                FirePower.FP_02,
                Hit.FALSE
        );
        Bayes.weka.addInstance(instance2);

        List<GenericAttribute> instance3 = Arrays.asList(
                EnemyDistance.RANGE_0_200,
                EnemyVelocity.RANGE_0_2,
                EnemyAngle.RANGE_0_90,
                EnemyHeading.RANGE_90_180,
                MyGunToEnemyAngle.RANGE_15_30,
                FirePower.FP_04,
                Hit.FALSE
        );
        Bayes.weka.addInstance(instance3);

        List<GenericAttribute> instance4 = Arrays.asList(
                EnemyDistance.RANGE_0_200,
                EnemyVelocity.RANGE_0_2,
                EnemyAngle.RANGE_0_90,
                EnemyHeading.RANGE_90_180,
                MyGunToEnemyAngle.RANGE_0_15,
                FirePower.FP_02,
                Hit.TRUE
        );
        Bayes.weka.addInstance(instance4);

        List<GenericAttribute> instance5 = Arrays.asList(
                EnemyDistance.RANGE_0_200,
                EnemyVelocity.RANGE_0_2,
                EnemyAngle.RANGE_0_90,
                EnemyHeading.RANGE_0_90,
                MyGunToEnemyAngle.RANGE_15_30,
                FirePower.FP_04,
                Hit.TRUE
        );
        Bayes.weka.addInstance(instance5);

        List<GenericAttribute> instance6 = Arrays.asList(
                EnemyDistance.RANGE_0_200,
                EnemyVelocity.RANGE_0_2,
                EnemyAngle.RANGE_0_90,
                EnemyHeading.RANGE_90_180,
                MyGunToEnemyAngle.RANGE_15_30,
                FirePower.FP_04,
                Hit.FALSE
        );
        Bayes.weka.addInstance(instance6);

        List<GenericAttribute> instance7 = Arrays.asList(
                EnemyDistance.RANGE_0_200,
                EnemyVelocity.RANGE_0_2,
                EnemyAngle.RANGE_0_90,
                EnemyHeading.RANGE_90_180,
                MyGunToEnemyAngle.RANGE_0_15,
                FirePower.FP_02,
                Hit.TRUE
        );
        Bayes.weka.addInstance(instance7);

        List<GenericAttribute> instance8 = Arrays.asList(
                EnemyDistance.RANGE_0_200,
                EnemyVelocity.RANGE_0_2,
                EnemyAngle.RANGE_0_90,
                EnemyHeading.RANGE_0_90,
                MyGunToEnemyAngle.RANGE_0_15,
                FirePower.FP_02,
                Hit.TRUE
        );
        Bayes.weka.addInstance(instance8);


        List<GenericAttribute> instance9 = Arrays.asList(
                EnemyDistance.RANGE_0_200,
                EnemyVelocity.RANGE_0_2,
                EnemyAngle.RANGE_0_90,
                EnemyHeading.RANGE_90_180,
                MyGunToEnemyAngle.RANGE_15_30,
                FirePower.FP_02,
                Hit.TRUE
        );
        Bayes.weka.addInstance(instance9);

        List<GenericAttribute> instance10 = Arrays.asList(
                EnemyDistance.RANGE_0_200,
                EnemyVelocity.RANGE_0_2,
                EnemyAngle.RANGE_0_90,
                EnemyHeading.RANGE_0_90,
                MyGunToEnemyAngle.RANGE_15_30,
                FirePower.FP_02,
                Hit.FALSE
        );
        Bayes.weka.addInstance(instance10);


        Bayes.weka.calcNewDistributions();
        Bayes.weka.displayGraph();

    }
}
