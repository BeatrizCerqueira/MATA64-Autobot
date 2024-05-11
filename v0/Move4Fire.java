package autobot.v0;

import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Move4Fire extends AdvancedRobot {

    Point2D robotLocation;
    ArrayList<Bullet> bullets = new ArrayList<>();

    Point2D enemyLocation;
    double enemyEnergy = 100;
    double enemyHeat = 3;   //initial

    static final double WALL_MARGIN = 50;
    final double RADAR_COVERAGE_DIST = 15;  // // Distance we want to scan from middle of enemy to either side
    final double SAFE_DISTANCE = 150;

//	double headTurn = 0;

    public void run() {

        setAdjustRadarForRobotTurn(true); // Set gun to turn independent of the robot's turn
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

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

        //WIP se estiver mais perto, ande mais (se dist < X, ande o dobro)

        //WIP estrategia defensiva de colisão (fugir do inimigo)

        double maxHeadTurn = (10 - (0.75 * getVelocity())); //max robot can turn considering its velocity
        double headTurn = random(-1 * maxHeadTurn, maxHeadTurn);    //random relative angle to turn


//        double distance = (enemyLocation != null) ? getDistance(robotLocation, enemyLocation) : 0;

        if (enemyHeat < 0.3) { // enemy gun will shoot any time now. do not move
            setTurnRight(headTurn);
            setAhead(0);
            return;
        }

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

    public void onHitByBullet(HitByBulletEvent e) {
        // Done! Mudar direção ao levar dano (evitar tiros)
        // ? prioridade eventos parede > tiro > scanned

        double headTurn = random(30, 90) * Math.signum(random(-1, 1));

        out.println("HIT! Turn " + headTurn);
        turnRight(headTurn);
        ahead(40);

    }

    public void onHitWall(HitWallEvent e) {
        // DONE! Mudar direção ao colidir com parede
        turnRight(random(30, 90) * Math.signum(random(-1, 1)));

    }


    public void onScannedRobot(ScannedRobotEvent e) {

        double enemyAngle = getHeading() + e.getBearing();


        // --------- Radar angle

        double radarInitialTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getRadarHeading());
        double extraRadarTurn = Math.toDegrees(Math.atan(RADAR_COVERAGE_DIST / e.getDistance()));

        // Radar goes that much further in the direction it is going to turn
        double radarTotalTurn = radarInitialTurn + (extraRadarTurn * Math.signum(radarInitialTurn));

        // Radar goes to the less distance direction
        double normalizedRadarTotalTurn = Utils.normalRelativeAngleDegrees(radarTotalTurn);
        double radarTurn = (Math.min(Math.abs(normalizedRadarTotalTurn), Rules.RADAR_TURN_RATE)) * Math.signum(normalizedRadarTotalTurn);

        setTurnRadarRight(radarTurn);

        // --------- Gun

        double gunInitialTurn = Utils.normalRelativeAngleDegrees(enemyAngle - getGunHeading());
        double gunTurn = (Math.min(Math.abs(gunInitialTurn), Rules.GUN_TURN_RATE)) * Math.signum(gunInitialTurn);

        setTurnGunRight(gunTurn);

        // --------- Fire

        setFire(1);


        // Enemy position
        double enemyAngleRadians = Math.toRadians(enemyAngle);
        enemyLocation = getLocation(robotLocation, enemyAngleRadians, e.getDistance());

        // Enemy is getting closer, move away
        if (e.getDistance() < SAFE_DISTANCE) {
//            out.println("RUN!");
            ahead(100);
        }

        // Track enemy energy to identify his bullets
        double energyDec = enemyEnergy - e.getEnergy();

        if (energyDec > 0 && energyDec <= 3) {
            bullets.add(new Bullet(enemyLocation, energyDec, e.getDistance()));
            enemyHeat = 1 + (energyDec / 5);
        }
        enemyEnergy = e.getEnergy();
    }


    public Point2D getLocation(Point2D initLocation, double angle, double distance) {
        double x = (int) (initLocation.getX() + Math.sin(angle) * distance);
        double y = (int) (initLocation.getY() + Math.cos(angle) * distance);
        return new Point2D.Double(x, y);

    }

    public double getAngle(Point2D A, Point2D B) {
        return Math.asin((B.getY() - A.getY()) / getDistance(A, B));
    }

    public double getDistance(Point2D A, Point2D B) {
        return Point2D.distance(A.getX(), A.getY(), B.getX(), B.getY());
    }

    public void onPaint(Graphics2D g) {
        // robot size = 40

        // Draw robot's security zone
        g.setColor(Color.green);
        drawCircle(g, getX(), getY(), SAFE_DISTANCE);

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
