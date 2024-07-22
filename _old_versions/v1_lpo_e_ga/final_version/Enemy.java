package autobot._old_versions.v1_lpo_e_ga.final_version;

import autobot._old_versions.v1_lpo_e_ga.final_version.prolog.Prolog;
import autobot._old_versions.v1_lpo_e_ga.final_version.utils.Consts;
import autobot._old_versions.v1_lpo_e_ga.final_version.utils.MathUtils;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;

public class Enemy {

    Point2D location;

    double energy;
    double heat;
    double angle;
    double distance;
    double heading;
    double velocity;

    boolean isScanned = false;

    int turnsToBullet = 0;  // how many turns a bullet takes to reaches other bot, considering extra turns for escaping

    public Enemy() {
        energy = Consts.INITIAL_ENERGY;
        heat = Consts.INITIAL_GUN_HEAT;
    }

    public void scanned(AdvancedRobot myBot, ScannedRobotEvent e) {
        // update enemy attributes on scanned event

        angle = myBot.getHeading() + e.getBearing();
        distance = e.getDistance();
        heading = e.getHeading();
        velocity = e.getVelocity();

        identifyEnemyBullets(energy - e.getEnergy());
        energy = e.getEnergy();

        setLocation(myBot);
        isScanned = true;
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

    public double getHeading() {
        return heading;
    }

    public double getVelocity() {
        return velocity;
    }

    private void setLocation(AdvancedRobot myBot) {
        Point2D myBotLocation = new Point2D.Double(myBot.getX(), myBot.getY());
        location = MathUtils.getLocation(myBotLocation, getAngleRad(), distance);
    }

    public Point2D getLocation() {
        return location;
    }

    private void identifyEnemyBullets(double energyDecreased) {
        // check enemy energy decrease to identify if enemy has fired
        // if so, update heat

        boolean hasEnemyFired = Prolog.hasEnemyFired(energyDecreased);

        if (hasEnemyFired) {
            heat = 1 + (energyDecreased / 5);
        }

    }

    public void passTurn(double gunCoolingRate) {
        heat = Math.max(heat - gunCoolingRate, 0); // if negative, 0
        turnsToBullet = Math.max(turnsToBullet - 1, 0);
    }

    public boolean isGunReady() {
        return Prolog.isGunReady(heat);
    }

    public boolean isScanned() {
        return isScanned;
    }

}
