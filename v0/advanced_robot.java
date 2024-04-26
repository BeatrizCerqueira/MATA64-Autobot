package autobot.v0;

import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

public class advanced_robot extends AdvancedRobot {
    // Initialize gun turn speed to 3
    int gunIncrement = 5;

    public void run() {
        // Initialization of the robot should be put here

        setColors(Color.blue, Color.red, Color.blue); // body,gun,radar

        // Robot main loop
        while (true) {
            scan();
            turnGunRight(gunIncrement);
            gunIncrement = 3;
            // Replace the next 4 lines with any behavior you would like


//			turnGunRight(360);
//			ahead(100);
//			turnGunRight(360);
//			back(100);
//			turnGunRight(360);
        }
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        // Replace the next line with any behavior you would like
        gunIncrement = 0;
        out.println("I saw a robot at " + getRadarHeading());
    }

    /**
     * onHitByBullet: What to do when you're hit by a bullet
     */
    public void onHitByBullet(HitByBulletEvent e) {
        // Replace the next line with any behavior you would like
        back(10);
    }

    /**
     * onHitWall: What to do when you hit a wall
     */
    public void onHitWall(HitWallEvent e) {
        // Replace the next line with any behavior you would like
        back(20);
    }
}
