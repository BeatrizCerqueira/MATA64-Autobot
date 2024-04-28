package autobot.v1.updated;

import autobot.v1.updated.aux.Consts;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;

public class Enemy {

    Point2D location;

    double energy;
    double heat;
    double angle;
    double distance;

    public Enemy() {
        energy = Consts.INITIAL_ENERGY;
        heat = Consts.INITIAL_GUN_HEAT;
    }

    public void scanned(AdvancedRobot myBot, ScannedRobotEvent e) {
        //update enemy attributes on scanned event
        angle = myBot.getHeading() + e.getBearing();
        energy = e.getEnergy();
        distance = e.getDistance();


//        setEnemyLocation(e);
//        moveAwayFromEnemy(e);
//        identifyEnemyBullets(e);
    }

    public double getAngle() {
        return angle;
    }

    public double getAngleRad() {
        return Math.toRadians(angle);
    }

    public double getDistance() {
        return distance;
    }
}
