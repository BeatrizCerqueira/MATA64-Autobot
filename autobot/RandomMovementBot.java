package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.geom.*;

// This code is released under the RoboWiki Public Code Licence (RWPCL), datailed on:
// http://robowiki.net/?RWPCL
//
// RandomMovementBot, by PEZ. Demonstrating a simple, extensible way to go about random 1v1 movement.
// Code and discussions about this bot available on: http://robowiki.net/?RandomMovementBot

public class RandomMovementBot extends AdvancedRobot {
    static final double MAX_VELOCITY = 8;
    static final double WALL_MARGIN = 25;
    Point2D robotLocation;
    Point2D enemyLocation;
    double enemyDistance;
    double enemyAbsoluteBearing;
    double movementLateralAngle = 0.2;

    public void run() {
        setAdjustRadarForGunTurn(true);

        do {
            turnRadarRightRadians(Double.POSITIVE_INFINITY); 
        } while (true);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
		robotLocation = new Point2D.Double(getX(), getY());
        enemyAbsoluteBearing = getHeadingRadians() + e.getBearingRadians();
        enemyDistance = e.getDistance();
        enemyLocation = vectorToLocation(enemyAbsoluteBearing, enemyDistance, robotLocation);

	move();

        setTurnRadarRightRadians(Utils.normalRelativeAngle(enemyAbsoluteBearing - getRadarHeadingRadians()) * 2);
    }

    // Always try to move a bit further away from the enemy.
    // Only when a wall forces us we will close in on the enemy. We never bounce off walls.
    void move() {
	considerChangingDirection();
	Point2D robotDestination = null;
	double tries = 0;
	do {
	    robotDestination = vectorToLocation(absoluteBearing(enemyLocation, robotLocation) + movementLateralAngle,
		    enemyDistance * (1.1 - tries / 100.0), enemyLocation);
	    tries++;
	} while (tries < 100 && !fieldRectangle(WALL_MARGIN).contains(robotDestination));
	goTo(robotDestination);
    }

    void considerChangingDirection() {
	// Change lateral direction at random
	// Tweak this to go for flat movement
	double flattenerFactor = 0.05;
	if (Math.random() < flattenerFactor) {
	    movementLateralAngle *= -1;
	}
    }

    RoundRectangle2D fieldRectangle(double margin) {
        return new RoundRectangle2D.Double(margin, margin,
	    getBattleFieldWidth() - margin * 2, getBattleFieldHeight() - margin * 2, 75, 75);
    }

    void goTo(Point2D destination) {
        double angle = Utils.normalRelativeAngle(absoluteBearing(robotLocation, destination) - getHeadingRadians());
	double turnAngle = Math.atan(Math.tan(angle));
        setTurnRightRadians(turnAngle);
        setAhead(robotLocation.distance(destination) * (angle == turnAngle ? 1 : -1));
	// Hit the brake pedal hard if we need to turn sharply
	setMaxVelocity(Math.abs(getTurnRemaining()) > 33 ? 0 : MAX_VELOCITY);
    }

    static Point2D vectorToLocation(double angle, double length, Point2D sourceLocation) {
	return vectorToLocation(angle, length, sourceLocation, new Point2D.Double());
    }

    static Point2D vectorToLocation(double angle, double length, Point2D sourceLocation, Point2D targetLocation) {
        targetLocation.setLocation(sourceLocation.getX() + Math.sin(angle) * length,
            sourceLocation.getY() + Math.cos(angle) * length);
	return targetLocation;
    }

    static double absoluteBearing(Point2D source, Point2D target) {
        return Math.atan2(target.getX() - source.getX(), target.getY() - source.getY());
    }
}