package autobot.auxy;

import java.awt.*;
import java.awt.geom.Point2D;

public class Bullet {
//	private int x0;
//	private int y0;

    private Point2D initLocation;
    private double maxRad;

    private double radius;
    private double velocity;

    public double firepower;
    public boolean active;

    public Bullet(Point2D initLocation, double firepower, double maxRad) {
        this.initLocation = initLocation;
        this.firepower = firepower;
        this.velocity = 20 - 3 * firepower;
        this.radius = 0;
        this.maxRad = maxRad;
        this.active = true;
    }

    public void incRadius() {
        radius += velocity;
        if (radius > maxRad + 50)
            this.active = false;
    }

    public double getRadius() {
        return radius;
    }

    public void drawBulletRadius(Graphics2D g) {
        if (this.active) {
            incRadius();
            int circ = (int) (2 * radius);
            g.setColor(Color.orange);
            g.drawOval((int) (initLocation.getX() - radius), (int) (initLocation.getY() - radius), circ, circ);
        }
    }
}
