package autobot.v2;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;


public class Bot {

    // Arena
    private final int width = 800;
    private final int height = 600;
    private final int halfWidth = width / 2;
    private final int halfHeight = height / 2;

    //Bot
    private Point2D position;
    private double energy;
    private double gunHeat;

    //AdvancedRobot
    private double coolingRate;

    public Bot(double coolingRate) {
        this.energy = 100;
        this.gunHeat = 3;
        this.coolingRate = coolingRate;
    }

    // Update methods
    public void update(AdvancedRobot myRobot) { // using with myRobot
        gunHeat = myRobot.getGunHeat();
        energy = myRobot.getEnergy();
        position = new Point2D.Double(myRobot.getX(), myRobot.getY());

        print("MY");
    }

    public void update(ScannedRobotEvent event, Point2D myPosition, double myHeading) {

        double enemyAngle = myHeading + event.getBearing();
        double enemyAngleRadians = Math.toRadians(enemyAngle);
        double energyDecrease = energy - event.getEnergy();
        boolean hasGunFired = energyDecrease > 0 && energyDecrease <= 3;

        if (hasGunFired)
            gunHeat = 1 + (energyDecrease / 5);

        energy = event.getEnergy();
        position = getLocation(myPosition, enemyAngleRadians, event.getDistance());

        print("ENEMY");
    }

    public Point2D getPosition() {
        return position;
    }

    public Point2D getCartesianPosition() {
        double x = position.getX() - halfWidth;
        double y = position.getY() - halfHeight;
        return new Point2D.Double(x, y);
    }

    // General data
    public void coolGun() {
        gunHeat = gunHeat > 0 ? gunHeat - coolingRate : 0;
    }

    // DEBUG
    public void print(String ref) {
        System.out.println(ref);
        System.out.println("Position: " + position);
        System.out.println("Energy: " + energy);
        System.out.println("GunHeat: " + gunHeat);
        System.out.println();
    }

    //UTILS
    private Point2D getLocation(Point2D initLocation, double angle, double distance) {
        double x = (int) (initLocation.getX() + Math.sin(angle) * distance);
        double y = (int) (initLocation.getY() + Math.cos(angle) * distance);
        return new Point2D.Double(x, y);

    }
}
