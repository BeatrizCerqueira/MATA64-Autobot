package autobot;

import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.geom.Point2D;

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
 * Collision damage = abs(velocity) * 0.5 - 1 | max = 8*0.5 -1 = 3,
 * The default cooling rate in Robocode is 0.1 per tick.
 *
 */

public class TrackBulletsBia extends AdvancedRobot {

    // Distance we want to scan from middle of enemy to either side
    final double RADAR_COVERAGE_DIST = 10;
    final int TURNS_TO_PREDICT = 20;

    Point2D robotLocation;

    public void run() {
        setAdjustRadarForGunTurn(true);

        //noinspection InfiniteLoopStatement
        do {
            robotLocation = new Point2D.Double(getX(), getY());

            if (getRadarTurnRemaining() == 0.0)
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

            execute();

        } while (true);
    }

    private void setRadarTurn(ScannedRobotEvent e) {

        // Get the enemy angle
        double enemyAngle = getHeading() + e.getBearing();

        // Relativize enemy angle
        double radarInitialTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());

        // Radar goes that much further in the direction it is going to turn
        double extraRadarTurn = Math.toDegrees(Math.atan(RADAR_COVERAGE_DIST / e.getDistance()));
        double radarTotalTurn = radarInitialTurn + (extraRadarTurn * Math.signum(radarInitialTurn));

        // Radar goes to the less distance direction
        double normalizedRadarTotalTurn = Utils.normalRelativeAngleDegrees(radarTotalTurn);
        double radarTurn = (Math.min(Math.abs(normalizedRadarTotalTurn), Rules.RADAR_TURN_RATE)) * Math.signum(normalizedRadarTotalTurn);

        // Set radar turn
        setTurnRadarRight(radarTurn);
    }


    private void setRobotFire(ScannedRobotEvent e) {

        // General

        double enemyCurrentDistance = e.getDistance();
        double enemyMovedDistanceAfterTurns = e.getVelocity() * TURNS_TO_PREDICT;

        double angleDegree = getHeading() + e.getBearing() + 180 - e.getHeading();
        double angleRadians = Math.toRadians(angleDegree);

        double enemyDistanceAfterTurns =
                Math.sqrt(
                        Math.pow(enemyCurrentDistance, 2) + Math.pow(enemyMovedDistanceAfterTurns, 2)
                                - 2 * enemyCurrentDistance * enemyMovedDistanceAfterTurns * Math.cos(angleRadians));


        //// Gun firepower

        double firePowerToHit = (20 - (enemyDistanceAfterTurns / TURNS_TO_PREDICT)) / 3;


        //// Gun angle

        double diffBearing =
                Math.asin((enemyMovedDistanceAfterTurns * Math.sin(angleRadians)) / enemyDistanceAfterTurns);

        double enemyBearingAfterTurns =
                Utils.normalRelativeAngleDegrees(e.getBearing() + diffBearing);

        double gunAngleToHit = Utils.normalRelativeAngleDegrees(enemyBearingAfterTurns - getGunHeading());


        //// Set the gun angle to turn, even if it's not possible to reach the required angle in the next turn

        double gunAngleToTurn = (Math.min(Math.abs(gunAngleToHit), Rules.GUN_TURN_RATE)) * Math.signum(gunAngleToHit);
        setTurnGunRight(gunAngleToTurn);


        //// Set fire if the robot can hit the enemy in the next turns

        if (firePowerToHit >= Rules.MIN_BULLET_POWER && Math.abs(gunAngleToHit) <= Rules.GUN_TURN_RATE) {
            double firePower = Math.min(firePowerToHit, Rules.MAX_BULLET_POWER);
            setFire(firePower);
        }

    }

    public void onScannedRobot(ScannedRobotEvent e) {

        setRadarTurn(e);
        setRobotFire(e);

    }

}
