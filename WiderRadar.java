package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;

public class WiderRadar extends AdvancedRobot {

	int scannedX = 0;
	int scannedY = 0;
	boolean scannedBot = false;

	public void run() {
		while (true) {
			if (getRadarTurnRemaining() == 0) {
				setTurnRadarRight(Double.POSITIVE_INFINITY);	//will not execute until you call execute() or take an action that executes.
			}
			
			execute();
				// Executes any pending actions, or continues executing actions that are in
				// process.

		}
	}

	
	public void onScannedRobot(ScannedRobotEvent e) {

		// Calculate target position in degrees
		double targetAngle = getHeading() + e.getBearing();
		double radarTurn = Utils.normalRelativeAngle(targetAngle - getRadarHeading());
		// the normalized angle that will be in the range of [-180,180[

		out.println("Radar:" + Rules.RADAR_TURN_RATE);
		// To keep enemy centralized at radar
		// "36.0 is how many units from the center of enemy robot it scans." HOW?
				
//		double extraRadarAngle = Math.tan( e.getDistance() / 36.0);
		
//		double extraTurn = Math.min(Math.atan(36 / e.getDistance()), Rules.RADAR_TURN_RATE);

		setTurnRadarRight(radarTurn);
		
		
		// Calculate the angle to the scanned robot
		double angle = Math.toRadians((targetAngle) % 360);
		
		out.println(e.getDistance());
		// Calculate the coordinates of target robot
		scannedBot = true;
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