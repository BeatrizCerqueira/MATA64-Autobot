package _autobot.old_versions.v0_heuristicas;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/* # Objective
 * Improve Move0 Robot
 *
 * Stay still when enemy will potentially fire (enemyHeat = 0)
 * While enemy gun cools, move randomly
 *
 * To improve movement:
 * 		turn head while waits his gun to cool down
 * 			maybe use move(), this will potentially keep my robot in center arena
 *
 * IF i was hit, move a few degrees away from him!
 *
 * 		identify if enemy hit the wall
 *
 * Determine what to do if enemy approaches "safeZone"
 * 		run away or colide with it, depending of his energy
 *
 * ------
 * Do not return to past positions?
 * Kill enemy bot coliding?
 *
 */

/* # Useful information
 *
 * After firing, a robot's gun heats up to a value of: 1 + (bulletPower / 5)
 * The default cooling rate in Robocode is 0.1 per tick.
 * Max rate of rotation is: (10 - 0.75 * abs(velocity)) deg/turn. The faster you're moving, the slower you turn.
 * 	4 ~ 9.25


 *  randomize from velocity 2 to 12 instead of 0 to 8. Everything higher than 8 will be cut off to 8, so a large part of the time you will travel at fullspeed.
 * 	https://robowiki.net/wiki/Maximum_Escape_Angle
 *
 */

public class TurnRange extends AdvancedRobot {

    Point2D robotLocation;
    double radarCoverageDist = 20; // Distance we want to scan from middle of enemy to either side
    ArrayList<Bullet> bullets = new ArrayList<>();

    Point2D enemyLocation;
    double enemyEnergy = 100;
    double enemyHeat = 2.8;

    static final double WALL_MARGIN = 50;

//	double headTurn = 0;

    public void run() {
        do {
            robotLocation = new Point2D.Double(getX(), getY());
            execute();
            turnRange();

        } while (true);
    }

    public double random(double min, double max) {
        return min + Math.random() * ((max - min + 1));
    }

    public void turnRange() {

        // get Relative location
        double xLimit = getBattleFieldWidth() / 2;
        double yLimit = getBattleFieldHeight() / 2;

        double x = robotLocation.getX() - xLimit;
        double y = robotLocation.getY() - yLimit;

        double xBase = Math.asin(-1 * Math.signum(x));
        double yBase = Math.acos(-1 * Math.signum(y));

        out.println(xBase + " " + yBase);

        setTurnRight(0);
//		return 0;

    }

    public void onScannedRobot(ScannedRobotEvent e) {

    }

    public Point2D getLocation(Point2D initLocation, double angle, double distance) {
        double x = (int) (initLocation.getX() + Math.sin(angle) * distance);
        double y = (int) (initLocation.getY() + Math.cos(angle) * distance);
        return new Point2D.Double(x, y);

    }

    public double getDistance(Point2D A, Point2D B) {
        return Point2D.distance(A.getX(), A.getY(), B.getX(), B.getY());
    }
}
