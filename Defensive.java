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
 * Stay still when enemy will potentially fire (enemyHeat = 0)
 * While enemy gun cools, move randomly
 * 
 * Avoid enemy bullets
 * Keep safe distance from other robots
 * Avoid walls
 * 
 * Kill enemy bot coliding
 * 
 */

/* # Useful information
 * 
 * After firing, a robot's gun heats up to a value of: 1 + (bulletPower / 5)
 * The default cooling rate in Robocode is 0.1 per tick.
 *  randomize from velocity 2 to 12 instead of 0 to 8. Everything higher than 8 will be cut off to 8, so a large part of the time you will travel at fullspeed.
 * https://robowiki.net/wiki/Maximum_Escape_Angle
 * 
 */

public class Defensive extends AdvancedRobot {

	// Paint/Debug properties
	double radarCoverageDist = 10; // Distance we want to scan from middle of enemy to either side
	int scannedX = 0;
	int scannedY = 0;
	boolean scannedBot = false;
	double enemy_energy = 100;
	double enemy_heat = 2.8;
	ArrayList<Bullet> bullets = new ArrayList<>();

	//
	static final double WALL_MARGIN = 25;
	Point2D robotLocation;
	Point2D enemyLocation;
	double enemyDistance;
	double enemyAbsoluteBearing;
	double movementLateralAngle = 0.2;

	public void run() {

//		double field_width = getBattleFieldWidth();
//		double field_height = getBattleFieldHeight();

		do {
			robotLocation = new Point2D.Double(getX(), getY());
			if (getRadarTurnRemaining() == 0.0)
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
			move();
			execute();
		} while (true);
	}

	public void move() {
		double x = getX();
		double y = getY();
		double heading = getHeading();

	}

	void goTo(Point2D destination) {

//		double angle = Utils.normalRelativeAngleDegrees(absoluteBearing(robotLocation, destination) - getHeading());
//		double turnAngle = Math.atan(Math.tan(angle));
//		setTurnRightRadians(turnAngle);
//		setAhead(robotLocation.distance(destination) * (angle == turnAngle ? 1 : -1));
//		// Hit the brake pedal hard if we need to turn sharply
//		setMaxVelocity(Math.abs(getTurnRemaining()) > 33 ? 0 : MAX_VELOCITY);
	}

	public void onScannedRobot(ScannedRobotEvent e) {
//		if (getGunHeat() == 0) 
//			fire(firepower);

		// ---------

		double enemyAngle = getHeading() + e.getBearing();
		double radarTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());
		double extraTurn = Math.toDegrees(Math.atan(radarCoverageDist / e.getDistance()));
		extraTurn = Math.min(extraTurn, Rules.RADAR_TURN_RATE);

		// Radar goes that much further in the direction it is going to turn
		radarTurn += extraTurn * Math.signum(radarTurn);
		setTurnRadarRight(radarTurn);

		// PAINT debug

		scannedBot = true;
		// Calculate the angle and coordinates to the scanned robot
		double enemyAngleRadians = Math.toRadians(enemyAngle);
		scannedX = (int) (getX() + Math.sin(enemyAngleRadians) * e.getDistance());
		scannedY = (int) (getY() + Math.cos(enemyAngleRadians) * e.getDistance());
		enemyLocation = getLocation(robotLocation, enemyAngleRadians, e.getDistance());

		// ----------
		if (enemy_heat > 0) {
			enemy_heat -= 0.1;
		} else {
//			out.println("Any time now");
		}

		double energy_dec = enemy_energy - e.getEnergy();
		if (energy_dec > 0 && energy_dec <= 3) {
			double firepower = enemy_energy - e.getEnergy();
			bullets.add(new Bullet(scannedX, scannedY, firepower, e.getDistance()));
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
//			g.fillRect(scannedX - 20, scannedY - 20, 40, 40);
			drawBulletsRange(g);
		}

//		if (scannedBot) {
//
//			// Draw enemy robot and distance
//			g.setColor(new Color(0xff, 0, 0, 0x80));
//			g.drawLine(scannedX, scannedY, (int) getX(), (int) getY());
//			g.fillRect(scannedX - 20, scannedY - 20, 40, 40);
//
//			// Draw enemy's bullet position
//			drawBulletsRange(g);
//
//		}
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
