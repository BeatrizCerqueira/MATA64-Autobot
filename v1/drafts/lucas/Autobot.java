package autobot.v1.drafts.lucas;

import autobot.v1.drafts.lucas.auxy.Consts;
import autobot.v1.drafts.lucas.auxy.Draw;
import autobot.v1.drafts.lucas.auxy.MathUtils;
import autobot.v1.drafts.lucas.genetic.GeneticAlgorithm;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.*;

public class Autobot extends AdvancedRobot {

    Point2D robotLocation;

    Enemy enemyBot = new Enemy();

    // Genetic Algorithm Variables:
    int velocityGA;
    int safeDistanceGA;
    int bordersMarginGA;

    public void run() {

        Prolog.loadPrologFile("robots/autobot/v1/drafts/lucas/Prolog.pl");
        GeneticAlgorithm.init();
        changeRobotColors();
        enablePrintGA();

        setAdjustRadarForRobotTurn(true); // Set gun to turn independent of the robot's turn
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

        //noinspection InfiniteLoopStatement
        do {
            nextTurn();

            boolean isRadarTurnComplete = Prolog.isRadarTurnComplete(getRadarTurnRemaining());

            if (isRadarTurnComplete) //poderia ir pra funcao especifica do radar...
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

            moveRobot();
            execute();

        } while (true);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // TODO: setTurn perpendicular ao inimigo
        // TODO verificar bordas para não andar em direção a parede. ahead negativo nesses casos
        // usar prolog?

        ahead(20);
    }

    public void onHitWall(HitWallEvent e) {
//        back(20);
        checkBorders();
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        enemyBot.scanned(this, e);

        setRadarTurn();
        setGunTurn();
        setFireTurn();

        setFire(1);

    }

    public void onRoundEnded(RoundEndedEvent event) {
        GeneticAlgorithm.saveGeneticData();

        if (getRoundNum() >= getNumRounds() - 1)
            // Battle finished
            GeneticAlgorithm.clearGeneticData();

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

    // # Class for Radar/Gun:

    private void setRadarTurn() {

        // Get the enemy angle
        double enemyAngle = enemyBot.getAngle();

        // Relativize enemy angle
        double radarInitialTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());

        // Radar goes that much further in the direction it is going to turn
        double extraRadarTurn = Math.toDegrees(Math.atan(Consts.RADAR_COVERAGE_DIST / enemyBot.getDistance()));
        double radarTotalTurn = radarInitialTurn + (extraRadarTurn * Math.signum(radarInitialTurn));

        // Radar goes to the less distance direction
        double normalizedRadarTotalTurn = Utils.normalRelativeAngleDegrees(radarTotalTurn);
        double radarTurn = (Math.min(Math.abs(normalizedRadarTotalTurn), Rules.RADAR_TURN_RATE)) * Math.signum(normalizedRadarTotalTurn);

        // Set radar turn
        setTurnRadarRight(radarTurn);
    }

    private void setGunTurn() {

        // Get the enemy angle
        double enemyAngle = enemyBot.getAngle();

        // Relativize enemy angle
        double gunInitialTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getGunHeading());

        // Gun goes to the less distance direction
        double gunTurn = (Math.min(Math.abs(gunInitialTurn), Rules.GUN_TURN_RATE)) * Math.signum(gunInitialTurn);

        // Set gun turn
        setTurnGunRight(gunTurn);

    }

    private void setFireTurn() {

        List<Map<String, Double>> history = new ArrayList<>();

        for (int i = 1; i <= Consts.MAX_TURNS_TO_FIRE; i++) {

            double enemyCurrentDistance = enemyBot.getDistance();
            double enemyMovAfterTurns = enemyBot.getVelocity() * i;

            double enemyMovDirection = enemyBot.getAngle() + 180 - enemyBot.getHeading();
            double enemyMovDirectionRad = Math.toRadians(enemyMovDirection);

            double enemyDistanceAfterTurns =
                    Math.sqrt
                            (Math.pow(enemyCurrentDistance, 2) + Math.pow(enemyMovAfterTurns, 2)
                                    - 2 * enemyCurrentDistance * enemyMovAfterTurns * Math.cos(enemyMovDirectionRad));

            double bulletVelocityNeededToHit = enemyDistanceAfterTurns / i;
            double firePowerNeededToHit = (20 - bulletVelocityNeededToHit) / 3;

            boolean shouldFire = Prolog.shouldFire(firePowerNeededToHit, getEnergy());

            if (shouldFire) {
                Map<String, Double> pair = new HashMap<>();
                pair.put("distance", enemyDistanceAfterTurns);
                pair.put("firePower", firePowerNeededToHit);
                history.add(pair);
            }

        }

        if (!history.isEmpty()) {
            history.sort(Comparator.comparing(m -> m.get("distance")));
            setFire(history.get(0).get("firePower"));
        }
    }

    // # Class for Movements:

    public void moveRandomly() {
        // set default movement attributes, considering robot is at center of arena

        // turn
        double maxHeadTurn = (10 - (0.75 * getVelocity())); //max robot can turn considering its velocity
        double headTurn = MathUtils.random(-1 * maxHeadTurn, maxHeadTurn);    //random relative angle to turn
        setTurnRight(headTurn);

        // ahead
//        double aheadDist = MathUtils.random(0, 20);   //distance to move forward
//        setAhead(aheadDist);
        setAhead(velocityGA);
    }

    public void checkBorders() {
        double xLimit = getBattleFieldWidth() / 2;
        double yLimit = getBattleFieldHeight() / 2;

        double x = robotLocation.getX() - xLimit;
        double y = robotLocation.getY() - yLimit;

        boolean xMargin = xLimit - (Math.abs(x)) < bordersMarginGA;
        boolean yMargin = yLimit - (Math.abs(y)) < bordersMarginGA;

        if (xMargin || yMargin) {
            double xSign = xMargin ? Math.signum(x) : 0;
            double ySign = yMargin ? Math.signum(y) : 0;
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
    }

    /**
     * If enemy gun is ready, setAhead(0)
     */
    public void checkEnemyGunReady() {
        if (enemyBot.isGunReady()) { // enemy gun will shoot any time now. do not move
            setAhead(0);
        }
    }

    public void checkEnemyIsClose() {
        boolean isEnemyClose = Prolog.isEnemyClose(enemyBot.getDistance(), safeDistanceGA);
        if (isEnemyClose) {
            setAhead(20);
        }
    }

    public void moveRobot() {
        // Set movement attributes (turn and ahead)

        // Set distance and turn
        moveRandomly();         // default behavior - less priority
        checkBorders();         // turn to escape borders - setTurn

        // if enemy bot not scanned, skip next methods
        if (!enemyBot.isScanned())
            return;

        // Override setAhead
        checkEnemyGunReady();
        checkEnemyIsClose();

        // Events priority: EnemyIsClose > EnemyGunReady > default random
    }

    public void nextTurn() {
        // update myRobot location
        robotLocation = new Point2D.Double(getX(), getY());

        //cool down enemy gun
        enemyBot.passTurn(getGunCoolingRate());

        // apply genetic algorithm
        applyGeneticAlgorithm();

    }

    public void applyGeneticAlgorithm() {
        GeneticAlgorithm.updateGA(getEnergy());
        velocityGA = GeneticAlgorithm.getVelocity();
        safeDistanceGA = GeneticAlgorithm.getSafeDistance();
        bordersMarginGA = GeneticAlgorithm.getBordersMargin();
    }

    private void enablePrintGA() {
        GeneticAlgorithm.enablePrintTestingChromosomes();
        System.out.println("====");
        GeneticAlgorithm.enablePrintGenerationScoring();
    }

    private void changeRobotColors() {
        setBodyColor(Color.BLACK);
        setGunColor(Color.BLACK);
        setRadarColor(Color.white);
    }
}
