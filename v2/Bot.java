package autobot.v2;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;


public class Bot extends AdvancedRobot {

    // Arena
    private final int width = 800;
    private final int height = 600;
    private final int halfWidth = width / 2;
    private final int halfHeight = height / 2;

    //Bot
    private AdvancedRobot refBot;
    private Point2D position;
    private double energy;
    private double gunHeat;

    // enemy
    public ScannedRobotEvent scanned;

    // Constructor
    public Bot(AdvancedRobot bot) {
        refBot = bot;
        energy = 100;
        gunHeat = 3;
    }

    // Update methods
    public void update() { //using to enemyBot
        coolGun();

        print("ENEMY");
    }

    public void update(AdvancedRobot myBot) { // using with myBot
        refBot = myBot;

        energy = myBot.getEnergy();
        gunHeat = myBot.getGunHeat();

        setPosition(calcPosition());
        coolGun();
        print("MY");

    }

    public void update(ScannedRobotEvent event) {
        setScanned(event);

        double energyDec = energy - scanned.getEnergy();
        if (energyDec > 0 && energyDec <= 3)
            gunHeat = 1 + (energyDec / 5);

        energy = scanned.getEnergy();
    }

    // Position
    public void setPosition(Point2D position) {
        this.position = position;
    }

    public Point2D calcPosition() {
        return new Point2D.Double(refBot.getX(), refBot.getY());
    }

    public Point2D getPosition() {
        return position;
    }

    public Point2D getCartesianPosition() {
        double x = position.getX() - halfWidth;
        double y = position.getY() - halfHeight;
        return new Point2D.Double(x, y);
    }

    // Scanned methods
    private void setScanned(ScannedRobotEvent event) {
        this.scanned = event;
    }

    // General data
    public void coolGun() {
        gunHeat = gunHeat > 0 ? gunHeat - refBot.getGunCoolingRate() : 0;
    }

    // Enemy data

    // DEBUG
    public void print(String ref) {
        System.out.println(ref);
        System.out.println("Position: " + position);
        System.out.println("Energy: " + energy);
        System.out.println("GunHeat: " + gunHeat);
        System.out.println();
    }
}
