package autobot.v1.drafts.lucas;

import autobot.v1.drafts.lucas.auxy.Consts;
import autobot.v1.drafts.lucas.auxy.Draw;
import autobot.v1.drafts.lucas.auxy.MathUtils;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;

public class Autobot extends AdvancedRobot {

    Point2D robotLocation;

    Enemy enemyBot = new Enemy();

//	double headTurn = 0;

    public void run() {

        setAdjustRadarForRobotTurn(true); // Set gun to turn independent of the robot's turn
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

        //noinspection InfiniteLoopStatement
        do {
            nextTurn();

            if (getRadarTurnRemaining() == 0.0) //poderia ir pra funcao especifica do radar...
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

            moveRobot();
            execute();

        } while (true);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        // Done! Mudar direção ao levar dano (evitar tiros)
        // ? prioridade eventos parede > tiro > scanned
        // TODO: Aprimorar - mover na perpendicular?

        double headTurn = MathUtils.random(30, 90) * Math.signum(MathUtils.random(-1, 1));

        out.println("HIT! Turn " + headTurn);
        turnRight(headTurn);
        ahead(40);

    }

    public void onHitWall(HitWallEvent e) {
        // DONE! Mudar direção ao colidir com parede
        turnRight(MathUtils.random(30, 90) * Math.signum(MathUtils.random(-1, 1)));

    }

    public void onScannedRobot(ScannedRobotEvent e) {

        enemyBot.scanned(this, e);

        setRadarTurn();
        setGunTurn();
        setFireTurn();

        setFire(1);

    }

    public void onPaint(Graphics2D g) {
        // robot size = 40

        // Draw robot's security zone
        g.setColor(Color.green);
        Draw.drawCircle(g, getX(), getY(), Consts.SAFE_DISTANCE);
    }

    // Class for Radar/Gun:

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

        int turns = 20;

        double enemyCurrentDistance = enemyBot.getDistance(); // e.getDistance();
        double enemyMovedDistanceAfterTurns = enemyBot.getVelocity() * turns;


        double angleDegree = enemyBot.getAngle() + 180 - enemyBot.getHeading();//getHeading() + e.getBearing() + 180 - e.getHeading();
        double angleRadians = Math.toRadians(angleDegree);

        double enemyDistanceAfterTurns =
                Math.sqrt
                        (Math.pow(enemyCurrentDistance, 2) + Math.pow(enemyMovedDistanceAfterTurns, 2)
                                - 2 * enemyCurrentDistance * enemyMovedDistanceAfterTurns * Math.cos(angleRadians));

        double firePowerToHit = (20 - (enemyDistanceAfterTurns / turns)) / 3;

        if (firePowerToHit >= Rules.MIN_BULLET_POWER) {
            double firePower = Math.min(firePowerToHit, Rules.MAX_BULLET_POWER);
            setFire(firePower);
        }

    }

    // Class for Movements:

    public void moveRandomly() {
        // set default movement attributes, considering robot is at center of arena

        // turn
        double maxHeadTurn = (10 - (0.75 * getVelocity())); //max robot can turn considering its velocity
        double headTurn = MathUtils.random(-1 * maxHeadTurn, maxHeadTurn);    //random relative angle to turn
        setTurnRight(headTurn);

        // ahead
        double aheadDist = MathUtils.random(0, 20);   //distance to move forward
        setAhead(aheadDist);
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
        if (isAtEdge)   //      boolean xMargin = isAtEdge ? xSign * ySign > 1 : xSign != 0;
            xMargin = xSign * ySign > 1;

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

    public void checkBorders() {

        double xLimit = getBattleFieldWidth() / 2;
        double yLimit = getBattleFieldHeight() / 2;

        double x = robotLocation.getX() - xLimit;
        double y = robotLocation.getY() - yLimit;

        boolean xMargin = xLimit - (Math.abs(x)) < Consts.WALL_MARGIN;
        boolean yMargin = yLimit - (Math.abs(y)) < Consts.WALL_MARGIN;

        double xSign = xMargin ? Math.signum(x) : 0;
        double ySign = yMargin ? Math.signum(y) : 0;

        if (xMargin || yMargin)
            avoidBorders(xSign, ySign);

    }

    public void moveRobot() {

        //WIP se estiver mais perto, ande mais (se dist < X, ande o dobro)

        //WIP estrategia defensiva de colisão (fugir do inimigo)

        //TODO: outras formas do inimigo perder energia (dano por tiro/colisão c parede)
        //          if  onBulletHit / energia<< e vel<<

        //TODO: aumentar distancia de fuga proporcional a distancia do robo inimigo

        //TODO: ajustar enemyHeat minimo para mover mais

        // set default movement attributes
        moveRandomly();
        checkBorders();

        // if enemy bot not scanned, skip next methods
        if (!enemyBot.isScanned())
            return;

        //if enemy any of following events occurs, override movement attributes
        if (enemyBot.isGunReady()) { // enemy gun will shoot any time now. do not move
            setAhead(0);
            return;
        }

        boolean isEnemyClose = enemyBot.getDistance() < Consts.SAFE_DISTANCE;
        if (isEnemyClose) {
            // moveAwayFromEnemy();
            ahead(100);
            return;
        }

        if (getTurnRemaining() > 0) {   //still turning, go slowly
            setAhead(1);
            return;
        }

    }

    public void nextTurn() {
        // update myRobot location
        robotLocation = new Point2D.Double(getX(), getY());

        //cool down enemy gun
        enemyBot.passTurn(getGunCoolingRate());
    }

}


