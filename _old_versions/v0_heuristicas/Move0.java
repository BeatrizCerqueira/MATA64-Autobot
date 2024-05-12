package autobot._old_versions.v0_heuristicas;

import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/* # Objective
 *
 * Implement Point2D
 *
 * Random movement
 * 	when close to a wall, run along it
 *
 * Each turn, set random headTurn and Ahead Dist
 *
 * Stay still when enemy will potentially fire (enemyHeat = 0)
 * While enemy gun cools, move randomly
 *
 */

/* # Useful information
 *
 * After firing, a robot's gun heats up to a value of: 1 + (bulletPower / 5)
 * The default cooling rate in Robocode is 0.1 per tick.
 * Max rate of rotation is: (10 - 0.75 * abs(velocity)) deg/turn. The faster you're moving, the slower you turn.
 * 	4 ~ 9.25
 *
 */

/**
 * Move0, a useless robot
 */
public class Move0 extends AdvancedRobot {


    Point2D robotLocation;
    double radarCoverageDist = 20; // Distance we want to scan from middle of enemy to either side
    ArrayList<Bullet> bullets = new ArrayList<>();

    Point2D enemyLocation;
    double enemyEnergy = 100;
    double enemyHeat = 2.8;

    static final double WALL_MARGIN = 50;

    double headTurn = 0;

    public void run() {
        do {
            robotLocation = new Point2D.Double(getX(), getY());
            if (getRadarTurnRemaining() == 0.0)
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            execute();

        } while (true);
    }

    public double random(double min, double max) {
        return min + Math.random() * ((max - min + 1));
    }

    public void move() {


        // get Relative location
        double xLimit = getBattleFieldWidth() / 2;
        double yLimit = getBattleFieldHeight() / 2;

        double x = robotLocation.getX() - xLimit;
        double y = robotLocation.getY() - yLimit;

        boolean xMargin = (Math.abs(x) + WALL_MARGIN) > xLimit;
        boolean yMargin = (Math.abs(y) + WALL_MARGIN) > yLimit;

        // # To avoid hitting the wall, run along it

        if (xMargin || yMargin) {

            // run along the wall;
            double absoluteAngle = 0;
            double extraTurn = random(30, 60); // degrees
            extraTurn *= Math.signum(x) * Math.signum(y); // extra turn direction

            if (yMargin) {
                absoluteAngle = Math.asin(-1 * Math.signum(x));
                extraTurn *= -1;
                // x -1 L | turn right | asen(1) = 90 -30
                // x +1 R | turn left | asen(-1) = -90 +30
            }

            if (xMargin) {
                absoluteAngle = Math.acos(-1 * Math.signum(y));
                // y +1 U | turn down | acos(-1) = 180
                // y -1 D | turn up | acos( 1) = 0

            }

            absoluteAngle = Math.toDegrees(absoluteAngle);
            absoluteAngle += extraTurn;
            headTurn = Utils.normalRelativeAngleDegrees(absoluteAngle - getHeading());

        } else {
            double maxRotation = (10 - (0.75 * getVelocity()));
            headTurn = random(-1 * maxRotation, maxRotation);
        }

        setTurnRight(headTurn);
        setAhead(20);

    }

    public void onScannedRobot(ScannedRobotEvent e) {
//		if (getGunHeat() == 0) 
//			fire(firepower);

        // --------- MOVE
        double aheadDist = 0;
        headTurn = 0;

        if (enemyHeat > 0.4) {
            // enemy gun is cooling, move randomly
            enemyHeat -= 0.1;
            if (getTurnRemaining() == 0) {
                aheadDist = random(5, 20);
                move();
            }
        }

//        else {
//
//            // enemy robot will shot any time now, stay still
//        }

        setAhead(aheadDist);
        // ---------

        double enemyAngle = getHeading() + e.getBearing();
        double radarTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());
        double extraTurn = Math.toDegrees(Math.atan(radarCoverageDist / e.getDistance()));
        extraTurn = Math.min(extraTurn, Rules.RADAR_TURN_RATE);

        // Radar goes that much further in the direction it is going to turn
        double enemyDirection = Math.signum(radarTurn);
        radarTurn += extraTurn * enemyDirection;
        radarTurn += headTurn * enemyDirection;
        setTurnRadarRight(radarTurn);

        // PAINT debug

        // Calculate the angle and coordinates to the scanned robot
        double enemyAngleRadians = Math.toRadians(enemyAngle);
        enemyLocation = getLocation(robotLocation, enemyAngleRadians, e.getDistance());

        // ----------

        double energyDec = enemyEnergy - e.getEnergy();
        if (energyDec > 0 && energyDec <= 3) {
            double firepower = enemyEnergy - e.getEnergy();
            bullets.add(new Bullet(enemyLocation, firepower, e.getDistance()));
            enemyHeat = 1 + (firepower / 5);
        }
        enemyEnergy = e.getEnergy();

    }

    public Point2D getLocation(Point2D initLocation, double angle, double distance) {
        double x = (int) (initLocation.getX() + Math.sin(angle) * distance);
        double y = (int) (initLocation.getY() + Math.cos(angle) * distance);
        return new Point2D.Double(x, y);

    }

    public void onPaint(Graphics2D g) {
        // robot size = 40

        // Draw robot's security zone
        g.setColor(Color.green);
        drawCircle(g, getX(), getY(), 60);

        // Draw enemy robot and distance
        if (enemyLocation != null) {
            g.setColor(new Color(0xff, 0, 0, 0x80));
            drawLine(g, robotLocation, enemyLocation);
//			g.fillRect(x - 20, y - 20, 40, 40);
            drawBulletsRange(g);
        }
    }

    public void drawLine(Graphics2D g, Point2D source, Point2D target) {
        int sourceX = (int) source.getX();
        int sourceY = (int) source.getY();
        int targetX = (int) target.getX();
        int targetY = (int) target.getY();
        g.drawLine(sourceX, sourceY, targetX, targetY);
    }

    public void drawCircle(Graphics2D g, double x, double y, double radius) {
        int circ = (int) (2 * radius);
        g.drawOval((int) (x - radius), (int) (y - radius), circ, circ);
    }

    public void drawBulletsRange(Graphics2D g) {
        for (Bullet bullet : bullets) {
            bullet.drawBulletRadius(g);
        }
    }
}
