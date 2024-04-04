package autobot;

import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/* # Objective
 * Improve Move0 Robot
 *
 * Stay still when enemy will potentially fire (enemyHeat = 0)
 * While enemy gun cools, move randomly
 *
 * To improve movement:
 * 		turn head while waits his gun to cool down
 * 			maybe use move(), this will potentially keep my robot in center arena
 *
 * IF i was hit, move a few degrees away from him!
 *
 * 		identify if enemy hit the wall
 *
 * Determine what to do if enemy approaches "safeZone"
 * 		run away or colide with it, depending of his energy
 *
 * ------
 * Do not return to past positions?
 * Kill enemy bot coliding?
 *
 */

/* # Useful information
 *
 * After firing, a robot's gun heats up to a value of: 1 + (bulletPower / 5)
 * The default cooling rate in Robocode is 0.1 per tick.
 * Max rate of rotation is: (10 - 0.75 * abs(velocity)) deg/turn. The faster you're moving, the slower you turn.
 * 	4 ~ 9.25


 *  randomize from velocity 2 to 12 instead of 0 to 8. Everything higher than 8 will be cut off to 8, so a large part of the time you will travel at fullspeed.
 * 	https://robowiki.net/wiki/Maximum_Escape_Angle
 *
 */

public class Move2 extends AdvancedRobot {

    Point2D robotLocation;
    double radarCoverageDist = 20; // Distance we want to scan from middle of enemy to either side
    ArrayList<Bullet> bullets = new ArrayList<>();

    Point2D enemyLocation;
    double enemyEnergy = 100;
    double enemyHeat = 3;   //initial

    static final double WALL_MARGIN = 50;

//	double headTurn = 0;

    public void run() {

        setAdjustRadarForRobotTurn(true); // Set gun to turn independent from the robot's turn

        do {
            robotLocation = new Point2D.Double(getX(), getY());

            if (getRadarTurnRemaining() == 0.0)
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

            moveRobot();
            execute();

        } while (true);
    }

    public double random(double min, double max) {
        return min + Math.random() * ((max - min + 1));
    }

    public void moveRobot() {
        //TODO: ajustar enemyHeat minimo para mover mais

        //TODO: Mudar direção ao levar dano (evitar tiros)
        //TODO: Mudar direção ao colidir com parede
        //          prioridade eventos parede > tiro > scanned

        //TODO: se estiver mais perto, ande mais (se dist < X, ande o dobro)

        //TODO: estrategia defensiva de colisão (fugir do inimigo)

        //TODO: outras formas do inimigo perder energia (dano por tiro/colisão c parede)
        //          if  onBulletHit / energia<< e vel<<

        //TODO: aumentar distancia de fuga proporcional a distancia do robo inimigo


        double maxHeadTurn = (10 - (0.75 * getVelocity())); //max robot can turn considering its velocity
        double headTurn = random(-1 * maxHeadTurn, maxHeadTurn);    //random relative angle to turn

        if (enemyHeat < 0.3) { // enemy gun will shoot any time now. do not move
            out.print(".");
            setTurnRight(headTurn);
            setAhead(0);
            return;
        }
        out.print(">");

        // enemy gun is cooling down, move randomly
        enemyHeat -= getGunCoolingRate();

        if (getTurnRemaining() > 0) {   //still turning, go slowly
            setAhead(1);
            return;
        }

        // default behavior,  in center arena
        double aheadDist = random(0, 20);   //distance to move forward

        // aux variables
        double xLimit = getBattleFieldWidth() / 2;
        double yLimit = getBattleFieldHeight() / 2;

        double x = robotLocation.getX() - xLimit;
        double y = robotLocation.getY() - yLimit;

        boolean xMargin = xLimit - (Math.abs(x)) < WALL_MARGIN;
        boolean yMargin = yLimit - (Math.abs(y)) < WALL_MARGIN;


        if (xMargin || yMargin) {
            // next to borders, consider changing

            double maxAngle = 180;
            aheadDist = random(0, 3);  //reduce vel to not hit wall

            //consider moving back if facing wall

            if (xMargin && yMargin) {
                maxAngle = 90;
                xMargin = Math.signum(x) * Math.signum(y) > 1;  // minAngle in those edges is equal to xMargin, otherwise equal to yMargin
            }

            double minAngle = xMargin ? Math.acos(-1 * Math.signum(x)) : Math.asin(Math.signum(y));

            minAngle = Math.toDegrees(minAngle);
            maxAngle += minAngle;

            double absTurnAngle = random(minAngle, maxAngle);
            headTurn = Utils.normalRelativeAngleDegrees(absTurnAngle - getHeading());

        }
        setTurnRight(headTurn);
        setAhead(aheadDist);

    }


    public void onScannedRobot(ScannedRobotEvent e) {

        //TODO: fire algorithm

        // # Variables and calculations

        double enemyAngle = getHeading() + e.getBearing();
        double radarTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());
        double extraTurn = Math.toDegrees(Math.atan(radarCoverageDist / e.getDistance()));
        extraTurn = Math.min(extraTurn, Rules.RADAR_TURN_RATE);

        // Radar goes that much further in the direction it is going to turn
        double enemyDirection = Math.signum(radarTurn);
        radarTurn += extraTurn * enemyDirection;
        radarTurn += enemyDirection;
        setTurnRadarRight(radarTurn);

        // # Update enemy data

        // Enemy position
        double enemyAngleRadians = Math.toRadians(enemyAngle);
        enemyLocation = getLocation(robotLocation, enemyAngleRadians, e.getDistance());

        // Track enemy energy to identify his bullets
        double energyDec = enemyEnergy - e.getEnergy();
        //what if his energy increases?

        if (energyDec > 0 && energyDec <= 3) {
            bullets.add(new Bullet(enemyLocation, energyDec, e.getDistance()));
            enemyHeat = 1 + (energyDec / 5);

            out.println("Duck!");
            out.println("v: " + getVelocity());

        }
        enemyEnergy = e.getEnergy();
    }


    public Point2D getLocation(Point2D initLocation, double angle, double distance) {
        double x = (int) (initLocation.getX() + Math.sin(angle) * distance);
        double y = (int) (initLocation.getY() + Math.cos(angle) * distance);
        return new Point2D.Double(x, y);

    }

    public double getDistance(Point2D A, Point2D B) {
        return Point2D.distance(A.getX(), A.getY(), B.getX(), B.getY());
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
