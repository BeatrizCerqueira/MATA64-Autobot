package autobot.v0;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.*;

public class PaintingRobot extends AdvancedRobot {

    int scannedX = 0;
    int scannedY = 0;
    boolean scannedBot = false;

    public void run() {
        while (true) {
            turnRadarRight(360);
            scan();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        // PAINT debug
        scannedBot = true;
        // Calculate the angle to the scanned robot
        double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);
        // Calculate the coordinates of target robot
        scannedX = (int) (getX() + Math.sin(angle) * e.getDistance());
        scannedY = (int) (getY() + Math.cos(angle) * e.getDistance());
    }

    // paint position of target robot
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
