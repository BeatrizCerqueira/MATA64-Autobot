package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;

/*
 * Turn Multiplier Lock (by https://robowiki.net/wiki/One_on_One_Radar):
 * Point the radar at the enemy's last known location. This results in a thin beam which follows the enemy around the battlefield. 
 * 
 *  A robot's width is 36px (that's 18px from the middle) and it can only move at up to 8px/turn.
 *  If your beam is pointing at its centre, it won't be able to move entirely out of it in one turn. 
 *  A robot will only automatically scan for enemies if its radar is turning. 
 *  You must call scan() yourself to avoid losing lock. 
 *  If you skip turns, your enemy might be able to move out from your radar beam before you recover.
 *  
 */

public class RadarLock extends AdvancedRobot {

	int scannedX = 0;
	int scannedY = 0;
	boolean scannedBot = false;

	public void run() {

		turnRadarRightRadians(Double.POSITIVE_INFINITY);
		do {
			// Check for new targets.
			// Only necessary for Narrow Lock because sometimes our radar is already
			// pointed at the enemy and our onScannedRobot code doesn't end up telling
			// it to turn, so the system doesn't automatically call scan() for us
			// [see the javadocs for scan()].
			scan();
		} while (true);
	}

	public void onScannedRobot(ScannedRobotEvent e) {

		double enemyAngle = getHeadingRadians() + e.getBearingRadians();
		double radarTurn = enemyAngle - getRadarHeadingRadians();

		setTurnRadarRightRadians(1.9 * Utils.normalRelativeAngle(radarTurn));
		// the normalized angle that will be in the range of [-180,180[
		// factor 1.9 - Radar arc starts wide and slowly narrows as much as possible
		// while staying on target.

		// PAINT debug
		scannedBot = true;
		// Calculate the angle to the scanned robot
		double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);
		// Calculate the coordinates of target robot
		scannedX = (int) (getX() + Math.sin(angle) * e.getDistance());
		scannedY = (int) (getY() + Math.cos(angle) * e.getDistance());

	}

	public void onPaint(Graphics2D g) {
		if (scannedBot) {

			// Set the paint color to a red half transparent color
			g.setColor(new Color(0xff, 0x00, 0x00, 0x80));

			// Draw a line from our robot to the scanned robot
			g.drawLine(scannedX, scannedY, (int) getX(), (int) getY());

			// Draw a filled square on top of the scanned robot that covers it
			g.fillRect(scannedX - 20, scannedY - 20, 40, 40);
		}
	}

}