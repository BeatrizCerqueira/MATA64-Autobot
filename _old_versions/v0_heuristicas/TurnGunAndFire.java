package autobot._old_versions.v0_heuristicas;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.*;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * TurnGunAndFire - a robot by Beatriz Cerqueira and Lucas Morais
 */
public class TurnGunAndFire extends AdvancedRobot {
    /**
     * run: TurnGunAndFire's default behavior
     */
    @Override
    public void run() {

        setColors(Color.red, Color.blue, Color.green); // body, gun, radar

        // Robot main loop
        while (true) {
            turnGunRight(20);
            execute();
        }
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        turnGunRight(normalizeTarget(getHeading() + e.getBearing() - getGunHeading()));
        fire(1);
    }

    private double normalizeTarget(double angle) {
        if (angle > 180) {
            angle -= 360;
        }
        if (angle < -180) {
            angle += 360;
        }
        return angle;
    }
}
