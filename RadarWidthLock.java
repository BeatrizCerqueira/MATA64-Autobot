package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;

/*
 * Width Lock (by https://robowiki.net/wiki/One_on_One_Radar):
 * The Width Radar Lock or Wide Radar Lock tries to scan a fixed distance to either side of the enemy. 
 * Its constant motion means the radar will not slip as long as you don't miss any turns.
 *  
 */

public class RadarWidthLock extends AdvancedRobot {

	public void run() {
		do {
			// ...
			// Turn the radar if we have no more turn, starts it if it stops and at the
			// start of round
			if (getRadarTurnRemaining() == 0.0)
				setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

			execute();
		} while (true);
	}

	public void onScannedRobot(ScannedRobotEvent e) {

		// Absolute angle towards target
		double angleToEnemy = getHeadingRadians() + e.getBearingRadians();

		// Subtract current radar heading to get the turn required to face the enemy, be
		// sure it is normalized
		double radarTurn = Utils.normalRelativeAngle(angleToEnemy - getRadarHeadingRadians());

		// Distance we want to scan from middle of enemy to either side
		// The 36.0 is how many units from the center of the enemy robot it scans.
		double extraTurn = Math.min(Math.atan(10 / e.getDistance()), Rules.RADAR_TURN_RATE_RADIANS);

		// Adjust the radar turn so it goes that much further in the direction it is
		// going to turn
		// Basically if we were going to turn it left, turn it even more left, if right,
		// turn more right.
		// This allows us to overshoot our enemy so that we get a good sweep that will
		// not slip.
		if (radarTurn < 0)
			radarTurn -= extraTurn;
		else
			radarTurn += extraTurn;

		// Turn the radar
		setTurnRadarRightRadians(radarTurn);
	}
}