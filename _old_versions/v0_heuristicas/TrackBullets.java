package _autobot.old_versions.v0_heuristicas;

import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
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
 * Colision damage = abs(velocity) * 0.5 - 1 | max = 8*0.5 -1 = 3,
 * The default cooling rate in Robocode is 0.1 per tick.
 *
 */

public class TrackBullets extends AdvancedRobot {

    // Paint/Debug properties
    double radarCoverageDist = 10; // Distance we want to scan from middle of enemy to either side
    boolean scannedBot = false;
    double enemy_energy = 100;
    double enemy_heat = 2.8;    //initial heat

    ArrayList<Bullet> bullets = new ArrayList<>();
    Point2D robotLocation;
    Point2D enemyLocation;

    double enemyDistance;
    double enemyAbsoluteBearing;
    double movementLateralAngle = 0.2;

    public void run() {
        do {
            robotLocation = new Point2D.Double(getX(), getY());
            if (getRadarTurnRemaining() == 0.0)
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            execute();
        } while (true);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
//		if (getGunHeat() == 0) 
//			fire(firepower);

        // ---------

        double enemyAngle = getHeading() + e.getBearing();
        double radarTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());
        double extraTurn = Math.toDegrees(Math.atan(radarCoverageDist / e.getDistance()));
        extraTurn = Math.min(extraTurn, Rules.RADAR_TURN_RATE);

        // Radar goes that much further in the direction it is going to turn
        radarTurn += extraTurn * Math.signum(radarTurn);
        setTurnRadarRight(radarTurn);

        // PAINT debug

        scannedBot = true;
        // Calculate the angle and coordinates to the scanned robot
        double enemyAngleRadians = Math.toRadians(enemyAngle);
        enemyLocation = getLocation(robotLocation, enemyAngleRadians, e.getDistance());

        // ----------
        if (enemy_heat > 0) {
            enemy_heat -= 0.1;
        }
        //else {
        //	out.println("Any time now");
        //}

        double energy_dec = enemy_energy - e.getEnergy();
        if (energy_dec > 0 && energy_dec <= 3) {
            double firepower = enemy_energy - e.getEnergy();
            bullets.add(new Bullet(enemyLocation, firepower, e.getDistance()));
            enemy_heat = 1 + (firepower / 5);
        }
        enemy_energy = e.getEnergy();
    }

    public Point2D getLocation(Point2D initLocation, double angle, double distance) {
        double x = (int) (initLocation.getX() + Math.sin(angle) * distance);
        double y = (int) (initLocation.getY() + Math.cos(angle) * distance);
        return new Point2D.Double(x, y);

    }

    public void onPaint(Graphics2D g) {
        // robot size = 40

        // Draw robot's security zone
        g.setColor(Color.green);
        drawCircle(g, getX(), getY(), 60);

        // Draw enemy robot and distance
        if (enemyLocation != null) {
            g.setColor(new Color(0xff, 0, 0, 0x80));
            drawLine(g, robotLocation, enemyLocation);
//			g.fillRect(x - 20, y - 20, 40, 40);
            drawBulletsRange(g);
        }
    }

    public void drawLine(Graphics2D g, Point2D source, Point2D target) {
        int sourceX = (int) source.getX();
        int sourceY = (int) source.getY();
        int targetX = (int) target.getX();
        int targetY = (int) target.getY();
        g.drawLine(sourceX, sourceY, targetX, targetY);
    }

    public void drawCircle(Graphics2D g, double x, double y, double radius) {
        int circ = (int) (2 * radius);
        g.drawOval((int) (x - radius), (int) (y - radius), circ, circ);
    }

    public void drawBulletsRange(Graphics2D g) {
        for (Bullet bullet : bullets) {
            bullet.drawBulletRadius(g);
        }
    }
}
