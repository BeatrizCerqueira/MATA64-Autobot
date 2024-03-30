package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;

/* # Objective
 * 
 * Detect an energy drop to know when a bullet was fired and trace its trajectory
 * Identify multiple bullets
 * remove draw when exceeds arena's area
 * 
 */

/* # Useful information
 * 
 * After firing, a robot's gun heats up to a value of: 1 + (bulletPower / 5)
 * Bullet velocity 20 - 3 * firepower.
 * Colision damage = abs(velocity) * 0.5 - 1 | max = 8*0.5 -1 = 3,
 * The default cooling rate in Robocode is 0.1 per tick.
 * 
 */


public class DodgeBullets extends AdvancedRobot {
	
	// Paint/Debug properties
	double radarCoverageDist = 10; // Distance we want to scan from middle of enemy to either side
	int scannedX = 0;
	int scannedY = 0;
	boolean scannedBot = false;
	boolean shoot;
	double enemy_energy;
	double enemy_bullet_velocity;
	long shoot_turn;
	

	public void run() {
		do {
			if (getRadarTurnRemaining() == 0.0)
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

			execute();
		} while (true);
	}

	public void onScannedRobot(ScannedRobotEvent e) {
//		if (getGunHeat() == 0) 
//			fire(firepower);

		if (e.getEnergy() < enemy_energy) {
			shoot = true;
			enemy_bullet_velocity = 20 - (3 * (enemy_energy - e.getEnergy()));
			shoot_turn = getTime();
			out.println("shoot" + enemy_bullet_velocity + " " + shoot_turn);
		}

		enemy_energy = e.getEnergy();

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
	}

	public void onPaint(Graphics2D g) {
		// robot size = 40

		if (scannedBot) {

			// Draw enemy robot and distance

			g.setColor(new Color(0xff, 0, 0, 0x80));
			g.drawLine(scannedX, scannedY, (int) getX(), (int) getY());
			g.fillRect(scannedX - 20, scannedY - 20, 40, 40);

			// -------------

			// Draw enemy's bullet position
			if (shoot) {

				double radius = 16 + ((getTime() - shoot_turn) * enemy_bullet_velocity); // approximately the bullet's
																							// initial position
				int circ = (int) (2 * radius);

				g.setColor(Color.red);
				g.drawOval((int) (scannedX - radius), (int) (scannedY - radius), circ, circ);
			}

		}

	}

}
