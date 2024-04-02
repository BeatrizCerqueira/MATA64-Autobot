package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.w3c.dom.ranges.Range;

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

public class Move1 extends AdvancedRobot {

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
			if (getRadarTurnRemaining() == 0.0)
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
			execute();

		} while (true);
	}

	public double random(double min, double max) {
		return min + Math.random() * ((max - min + 1));
	}

	public double turnRobot() {

		// get Relative location
		double xLimit = getBattleFieldWidth() / 2;
		double yLimit = getBattleFieldHeight() / 2;

		double x = robotLocation.getX() - xLimit;
		double y = robotLocation.getY() - yLimit;

		boolean xMargin = (Math.abs(x) + WALL_MARGIN) > xLimit;
		boolean yMargin = (Math.abs(y) + WALL_MARGIN) > yLimit;

		// # To avoid hitting the wall, run along it
		double absoluteTurn = getHeading();
//		double extraTurn = random(30, 60); // degrees

		double maxRotation = (10 - (0.75 * getVelocity()));
//		headTurn = random(-1 * maxRotation, maxRotation);

		double extraTurn = random(-1 * maxRotation, maxRotation); // max 10 deg per turn if stopped

		if (xMargin || yMargin) {

			// run along the wall;
			extraTurn = Math.signum(x) * Math.signum(y) * Math.abs(extraTurn); // extra turn direction

			if (yMargin) { // prioritize yTurn (-90º or 90º) to move across x axis
				absoluteTurn = Math.asin(-1 * Math.signum(x)); // if next to y margin (-90 ~ 90)
				extraTurn *= -1; // if next to y margin, extraTurn reverse

			} else if (xMargin)
				absoluteTurn = Math.acos(-1 * Math.signum(y)); // if next to x margin (0 ~ 180)

			absoluteTurn = Math.toDegrees(absoluteTurn);

			if (enemyHeat > 0.4)
				setAhead(20); // to leave border after turning
		}

		absoluteTurn += extraTurn;

		return Utils.normalRelativeAngleDegrees(absoluteTurn - getHeading());

	}

	public void onScannedRobot(ScannedRobotEvent e) {
//		if (getGunHeat() == 0) 
//			fire(firepower);

		// --------- MOVE
		double aheadDist = 0;
//		double turnAngle = 0; // may not be zero

		if (enemyHeat > 0.4) {
			// enemy gun is cooling, move randomly
			enemyHeat -= 0.1;
			if (getTurnRemaining() == 0) {
				aheadDist = random(5, 20);
			}

		} else {
			// enemy robot will shot any time now, stay still
		}
		double turnAngle = turnRobot();

		setAhead(aheadDist);
		setTurnRight(turnAngle);
		// ---------

		double enemyAngle = getHeading() + e.getBearing();
		double radarTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());
		double extraTurn = Math.toDegrees(Math.atan(radarCoverageDist / e.getDistance()));
		extraTurn = Math.min(extraTurn, Rules.RADAR_TURN_RATE);

		// Radar goes that much further in the direction it is going to turn
		double enemyDirection = Math.signum(radarTurn);
		radarTurn += extraTurn * enemyDirection;
		radarTurn += turnAngle * enemyDirection;
		setTurnRadarRight(radarTurn);

		// PAINT debug

		// Calculate the angle and coordinates to the scanned robot
		double enemyAngleRadians = Math.toRadians(enemyAngle);
		enemyLocation = getLocation(robotLocation, enemyAngleRadians, e.getDistance());

		// ----------

		double energy_dec = enemyEnergy - e.getEnergy();
		if (energy_dec > 0 && energy_dec <= 3) {
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
