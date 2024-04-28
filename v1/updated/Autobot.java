package autobot.v1.updated;

import autobot.v1.updated.aux.Consts;
import autobot.v1.updated.aux.Draw;
import autobot.v1.updated.aux.MathUtils;
import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;

public class Autobot extends AdvancedRobot {

    Point2D robotLocation;
    Point2D enemyLocation;

    //    double enemyEnergy = Consts.INITIAL_ENERGY;
//    double enemyHeat = Consts.INITIAL_GUN_HEAT;
    Enemy enemyBot = new Enemy();

//	double headTurn = 0;

    public void run() {

        setAdjustRadarForRobotTurn(true); // Set gun to turn independent of the robot's turn
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

        //noinspection InfiniteLoopStatement
        do {
            robotLocation = new Point2D.Double(getX(), getY());

            if (getRadarTurnRemaining() == 0.0)
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

        setRadarTurn(e);
        setGunTurn(e);
        setFireTurn(e);

        setFire(1);

        // --------- Other

//        setEnemyLocation(e);
        moveAwayFromEnemy(e);
//        identifyEnemyBullets(e);
    }

    public void onPaint(Graphics2D g) {
        // robot size = 40

        // Draw robot's security zone
        g.setColor(Color.green);
        Draw.drawCircle(g, getX(), getY(), Consts.SAFE_DISTANCE);

        // Draw enemy robot and distance
        if (enemyLocation != null) {
            g.setColor(new Color(0xff, 0, 0, 0x80));
            Draw.drawLine(g, robotLocation, enemyLocation);
//			g.fillRect(x - 20, y - 20, 40, 40);
            Draw.drawBulletsRange(g);
        }
    }

    // Class for Radar/Gun:

    private void setRadarTurn(ScannedRobotEvent e) {

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

    private void setGunTurn(ScannedRobotEvent e) {

        // Get the enemy angle
        double enemyAngle = enemyBot.getAngle();

        // Relativize enemy angle
        double gunInitialTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getGunHeading());

        // Gun goes to the less distance direction
        double gunTurn = (Math.min(Math.abs(gunInitialTurn), Rules.GUN_TURN_RATE)) * Math.signum(gunInitialTurn);

        // Set gun turn
        setTurnGunRight(gunTurn);

    }

    private void setFireTurn(ScannedRobotEvent e) {

        int turns = 20;

        double enemyCurrentDistance = e.getDistance();
        double enemyMovedDistanceAfterTurns = e.getVelocity() * turns;


        double angleDegree = getHeading() + e.getBearing() + 180 - e.getHeading();
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

    // Class for Enemy attributes:

    private void setEnemyLocation(ScannedRobotEvent e) {

        // Enemy position
        double enemyAngleRadians = enemyBot.getAngleRad();
        enemyLocation = MathUtils.getLocation(robotLocation, enemyAngleRadians, enemyBot.getDistance());
    }

    private void identifyEnemyBullets(ScannedRobotEvent e) {
        // Track enemy energy to identify his bullets
        double energyDec = enemyBot.energy - e.getEnergy();

        if (energyDec > 0 && energyDec <= 3) {
            enemyBot.heat = 1 + (energyDec / 5);
        }

        enemyBot.energy = e.getEnergy();
    }

    // Class for Movements:

    private void moveAwayFromEnemy(ScannedRobotEvent e) {
        // Enemy is getting closer, move away
        if (e.getDistance() < Consts.SAFE_DISTANCE) {
            ahead(100);
        }
    }

    public void moveRobot() {

        //WIP se estiver mais perto, ande mais (se dist < X, ande o dobro)

        //WIP estrategia defensiva de colisão (fugir do inimigo)

        //TODO: outras formas do inimigo perder energia (dano por tiro/colisão c parede)
        //          if  onBulletHit / energia<< e vel<<

        //TODO: aumentar distancia de fuga proporcional a distancia do robo inimigo

        //TODO: ajustar enemyHeat minimo para mover mais


        double maxHeadTurn = (10 - (0.75 * getVelocity())); //max robot can turn considering its velocity
        double headTurn = MathUtils.random(-1 * maxHeadTurn, maxHeadTurn);    //random relative angle to turn


//        double distance = (enemyLocation != null) ? getDistance(robotLocation, enemyLocation) : 0;

        if (enemyBot.isGunReady()) { // enemy gun will shoot any time now. do not move
            setTurnRight(headTurn);
            setAhead(0);
            return;
        }

        // enemy gun is cooling down, move randomly
        enemyBot.passTurn(getGunCoolingRate());

        if (getTurnRemaining() > 0) {   //still turning, go slowly
            setAhead(1);
            return;
        }


        // default behavior,  in center arena
        double aheadDist = MathUtils.random(0, 20);   //distance to move forward


        // aux variables
        double xLimit = getBattleFieldWidth() / 2;
        double yLimit = getBattleFieldHeight() / 2;

        double x = robotLocation.getX() - xLimit;
        double y = robotLocation.getY() - yLimit;

        boolean xMargin = xLimit - (Math.abs(x)) < Consts.WALL_MARGIN;
        boolean yMargin = yLimit - (Math.abs(y)) < Consts.WALL_MARGIN;


        if (xMargin || yMargin) {
            // next to borders, consider changing

            double maxAngle = 180;
            aheadDist = MathUtils.random(0, 3);  //reduce vel to not hit wall

            //consider moving back if facing wall

            if (xMargin && yMargin) {
                maxAngle = 90;
                xMargin = Math.signum(x) * Math.signum(y) > 1;  // minAngle in those edges is equal to xMargin, otherwise equal to yMargin
            }

            double minAngle = xMargin ? Math.acos(-1 * Math.signum(x)) : Math.asin(Math.signum(y));

            minAngle = Math.toDegrees(minAngle);
            maxAngle += minAngle;

            double absTurnAngle = MathUtils.random(minAngle, maxAngle);
            headTurn = Utils.normalRelativeAngleDegrees(absTurnAngle - getHeading());

        }

        setTurnRight(headTurn);
        setAhead(aheadDist);

    }

}
