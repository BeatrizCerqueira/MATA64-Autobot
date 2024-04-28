package autobot.v1.updated;

import autobot.v1.updated.aux.Consts;
import autobot.v1.updated.aux.MathUtils;
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
        distance = e.getDistance();

        energy = e.getEnergy();
        identifyEnemyBullets(energy - e.getEnergy());

        setLocation(myBot);
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

    private void setLocation(AdvancedRobot myBot) {
        Point2D myBotLocation = new Point2D.Double(myBot.getX(), myBot.getY());
        location = MathUtils.getLocation(myBotLocation, getAngleRad(), distance);
    }

    private void identifyEnemyBullets(double energyDecreased) {
        // check enemy energy decrease to identify if enemy has fired
        // if so, update heat

        boolean hasEnemyFired = energyDecreased > 0 && energyDecreased <= 3;
        if (hasEnemyFired)
            heat = 1 + (energyDecreased / 5);

    }

    public void passTurn(double gunCoolingRate) {
        heat -= gunCoolingRate;
    }

    public boolean isGunReady() {
        return heat < 0.3;
    }

}
