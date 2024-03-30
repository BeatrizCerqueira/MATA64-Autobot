package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;

/*
 * Identify and map risk zones (potentially with bullets)	
 * Detect an energy drop to know that a bullet was fired
 * 
 */

// After firing, a robot's gun heats up to a value of: 1 + (bulletPower / 5)
// Bullet velocity 20 - 3 * firepower.
// Colision damage = abs(velocity) * 0.5 - 1 = max = 8*0.5 -1 = 3

public class DodgeBullets extends AdvancedRobot {

	double radarCoverageDist = 10; // Distance we want to scan from middle of enemy to either side
	int scannedX = 0;
	int scannedY = 0;
	boolean scannedBot = false;

	double enemy_energy;
	boolean shoot;
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
			enemy_bullet_velocity = 20 - 3 * (e.getEnergy() - enemy_energy);
			shoot_turn = getTime();
			out.println("shoot" + enemy_bullet_velocity + " " + shoot_turn);
		}

		enemy_energy = e.getEnergy();

		// ---------

		double enemyAngle = getHeading() + e.getBearing();
		double radarTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());

		// Distance we want to scan from middle of enemy to either side
		double extraTurn = Math.toDegrees(Math.atan(radarCoverageDist / e.getDistance()));
		extraTurn = Math.min(extraTurn, Rules.RADAR_TURN_RATE);

		// Adjust the radar turn so it goes that much further in the direction it is
		// going to turn
		radarTurn += extraTurn * Math.signum(radarTurn);

		// Turn the radar
		setTurnRadarRight(radarTurn);

		// PAINT debug
		scannedBot = true;
		// Calculate the angle to the scanned robot
		double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);
		// Calculate the coordinates of target robot
		scannedX = (int) (getX() + Math.sin(angle) * e.getDistance());
		scannedY = (int) (getY() + Math.cos(angle) * e.getDistance());
	}

	public void onPaint(Graphics2D g) {
		// robot size = 40

		if (scannedBot) {

			// Draw enemy robot

			// Set the paint color to a red half transparent color
			g.setColor(new Color(0xff, 0, 0, 0x80));

			// Draw a line from our robot to the scanned robot
			g.drawLine(scannedX, scannedY, (int) getX(), (int) getY());

			// Draw a filled square on top of the scanned robot that covers it
			g.fillRect(scannedX - 20, scannedY - 20, 40, 40);

			// -------------

			// Draw enemy's bullet position
			if (shoot) {
				
				double radius = ((getTime()-shoot_turn) * enemy_bullet_velocity); // 30 = robot + gun size
				int circ = (int) (2 * radius);

				g.setColor(Color.red);
				g.drawOval((int) (scannedX - radius), (int) (scannedY - radius), circ, circ);
			}

		}

	}

}
