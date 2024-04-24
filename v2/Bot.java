package autobot.v2;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

import java.awt.geom.Point2D;


public class Bot extends AdvancedRobot {

    // Arena
    private int width = 800;
    private int height = 600;
    private int halfWidth = width / 2;
    private int halfHeight = height / 2;


    //Bot
    private Point2D position;
    private AdvancedRobot refBot;

    private double energy;
    private double gunHeat;

    // enemy
    public ScannedRobotEvent scanned;


    public Bot(AdvancedRobot bot) {
        refBot = bot;
        energy = 100;
        gunHeat = 3;
    }

    public void update() { //using to enemyBot
        coolGun();
    }

    public void update(AdvancedRobot myBot) { // using with myBot
        refBot = myBot;
        setPosition(calcPosition());
        coolGun();
    }


    public void update(ScannedRobotEvent event) {
        setScanned(event);
        checkFireBullet();
    }


    public void update(AdvancedRobot myBot, ScannedRobotEvent e) { //called on event
        double absAngle = myBot.getHeading() + e.getBearing();
        Point2D myBotPos = calcPosition(myBot);
        setPosition(calcPosition(myBotPos, Math.toRadians(absAngle), e.getDistance()));
    }

    // Position
    public void setPosition(Point2D position) {
        this.position = position;
    }

    public Point2D calcPosition() {
        return new Point2D.Double(refBot.getX(), refBot.getY());
    }

    public Point2D calcPosition(AdvancedRobot bot) {
        return new Point2D.Double(bot.getX(), bot.getY());
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

    private void checkFireBullet() {
        double energyDec = energy - scanned.getEnergy();
        if (energyDec > 0 && energyDec <= 3) {
            gunHeat = 1 + (energyDec / 5);
        }
        energy = scanned.getEnergy();
    }

    // General data
    public void coolGun() {
        if (gunHeat > 0)
            gunHeat -= refBot.getGunCoolingRate();
    }

    // Enemy data

    // utils
    public Point2D calcPosition(Point2D initPosition, double angle, double distance) {
        double x = (int) (initPosition.getX() + Math.sin(angle) * distance);
        double y = (int) (initPosition.getY() + Math.cos(angle) * distance);
        return new Point2D.Double(x, y);

    }


}
