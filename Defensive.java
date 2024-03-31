package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;
import java.util.ArrayList;
/* # Objective
 * 
 * If moving when enemy fires, then stop 
 * 	amount of movement defined by his firepower
 * If stopped when we fires, then start moving
 * 
 * Ideally stay still right before he shoots
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
 * 
 */

public class Defensive extends AdvancedRobot {

	// Paint/Debug properties
	double radarCoverageDist = 10; // Distance we want to scan from middle of enemy to either side
	int scannedX = 0;
	int scannedY = 0;
	boolean scannedBot = false;
	double enemy_energy = 100;
	ArrayList<Bullet> bullets = new ArrayList<>();

	public void run() {

		double field_width = getBattleFieldWidth();
		double field_height = getBattleFieldHeight();

		do {
			if (getRadarTurnRemaining() == 0.0)
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

			execute();
		} while (true);
	}

	public void onScannedRobot(ScannedRobotEvent e) {
//		if (getGunHeat() == 0) 
//			fire(firepower);

		// ---------

		double enemyAngle = getHeading() + e.getBearing();
		double radarTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());

		double extraTurn = Math.toDegrees(Math.atan(radarCoverageDist / e.getDistance()));
		extraTurn = Math.min(extraTurn, Rules.RADAR_TURN_RATE);

		// Adjust the radar turn so it goes that much further in the direction it is
		// going to turn
		radarTurn += extraTurn * Math.signum(radarTurn);

		// Turn the radar
		setTurnRadarRight(radarTurn);

		// PAINT debug
		scannedBot = true;
		// Calculate the angle and coordinates to the scanned robot
		double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);
		scannedX = (int) (getX() + Math.sin(angle) * e.getDistance());
		scannedY = (int) (getY() + Math.cos(angle) * e.getDistance());

		// ----------

		double energy_dec = enemy_energy - e.getEnergy();
		if (energy_dec > 0 && energy_dec <= 3) {
			double firepower = enemy_energy - e.getEnergy();
			bullets.add(new Bullet(scannedX, scannedY, firepower, e.getDistance()));
		}
		enemy_energy = e.getEnergy();
	}

	public void newBullet(double firepower) {

	}

	public void onPaint(Graphics2D g) {
		// robot size = 40

		// Draw robot's security zone
		g.setColor(Color.green);
		drawCircle(g, getX(), getY(), 60);

		if (scannedBot) {

			// Draw enemy robot and distance
			g.setColor(new Color(0xff, 0, 0, 0x80));
			g.drawLine(scannedX, scannedY, (int) getX(), (int) getY());
			g.fillRect(scannedX - 20, scannedY - 20, 40, 40);

			// Draw enemy's bullet position
			drawBulletsRange(g);

		}
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
