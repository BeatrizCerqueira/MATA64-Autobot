package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/* # Objective
 * 
 * Implement Point2D
 * 
 * Random movement
 * 	Avoid walls
 * 	turn randomly + ahead ranomly (inversely proportional)
 * 		OR
 * 	define ramdom destiny
 * 
 * Stay still when enemy will potentially fire (enemyHeat = 0)
 * While enemy gun cools, move randomly
 * 
 * Avoid enemy bullets
 * Keep safe distance from other robots

 * 
 * Kill enemy bot coliding
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

public class Defensive extends AdvancedRobot {

	// Paint/Debug properties
	double radarCoverageDist = 10; // Distance we want to scan from middle of enemy to either side
	double enemy_energy = 100;
	double enemy_heat = 2.8;
	ArrayList<Bullet> bullets = new ArrayList<>();

	static final double WALL_MARGIN = 25;
	Point2D robotLocation;
	Point2D enemyLocation;
	double enemyDistance;
	double enemyAbsoluteBearing;
	double movementLateralAngle = 0.2;

	int movementDistance = 0;

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

	public void onScannedRobot(ScannedRobotEvent e) {
//		if (getGunHeat() == 0) 
//			fire(firepower);
		double robotTurn = 0;
		double aheadDist = 0;
		// --------- MOVE
		if (enemy_heat > 0) {
			// enemy gun is cooling, move randomly

			enemy_heat -= 0.1;
			double maxRotation = (10 - (0.75 * getVelocity()));
			robotTurn = random(-1 * maxRotation, maxRotation);
			aheadDist = random(0, 20);

		} else {
			// enemy robot will shot, stay still
			// out.println("Any time now");
		}

		setTurnRight(robotTurn);
		setAhead(aheadDist);
		// ---------

		double enemyAngle = getHeading() + e.getBearing();
		double radarTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());
		double extraTurn = Math.toDegrees(Math.atan(radarCoverageDist / e.getDistance()));
		extraTurn = Math.min(extraTurn, Rules.RADAR_TURN_RATE);

		// Radar goes that much further in the direction it is going to turn
		double enemyDirection = Math.signum(radarTurn);
		radarTurn += extraTurn * enemyDirection;
		radarTurn += robotTurn * enemyDirection;
		setTurnRadarRight(radarTurn);

		// PAINT debug

		// Calculate the angle and coordinates to the scanned robot
		double enemyAngleRadians = Math.toRadians(enemyAngle);
		enemyLocation = getLocation(robotLocation, enemyAngleRadians, e.getDistance());

		// ----------

		double energy_dec = enemy_energy - e.getEnergy();
		if (energy_dec > 0 && energy_dec <= 3) {
			double firepower = enemy_energy - e.getEnergy();
			bullets.add(new Bullet(enemyLocation, firepower, e.getDistance()));
			enemy_heat = 1 + (firepower / 5);
		}
		enemy_energy = e.getEnergy();

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
