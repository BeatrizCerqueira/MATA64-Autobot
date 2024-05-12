package autobot._old_versions.v0_heuristicas;

import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.*;

/*
 * Width Lock (by https://robowiki.net/wiki/One_on_One_Radar):
 * The Width Radar Lock or Wide Radar Lock tries to scan a fixed distance to either side of the enemy.
 * Its constant motion means the radar will not slip as long as you don't miss any turns.
 *
 */

public class RadarWidthLock extends AdvancedRobot {

    double radarCoveredDistance = 10; // Distance we want to scan from middle of enemy to either side
    int scannedX = 0;
    int scannedY = 0;
    boolean scannedBot = false;

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

        double enemyAngle = getHeadingRadians() + e.getBearingRadians();
        double radarTurn = Utils.normalRelativeAngle(enemyAngle - getRadarHeadingRadians());

        // Distance we want to scan from middle of enemy to either side
        double extraTurn = Math.min(Math.atan(10 / e.getDistance()), Rules.RADAR_TURN_RATE_RADIANS);

        // Adjust the radar turn so it goes that much further in the direction it is
        // going to turn
        if (radarTurn < 0)
            radarTurn -= extraTurn;
        else
            radarTurn += extraTurn;

        // Turn the radar
        setTurnRadarRightRadians(radarTurn);

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
