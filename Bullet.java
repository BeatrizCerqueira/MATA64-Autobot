package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;
import java.util.ArrayList;

public class Bullet {
	private int x0;
	private int y0;
	private double maxRad;
	
	public double firepower;
	private double radius;
	private double velocity;

	public boolean active;

	public Bullet(int x, int y, double firepower, double maxRad) {
		this.x0 = x;
		this.y0 = y;
		this.firepower = firepower;
		this.velocity = 20 - 3 * firepower;
		this.radius = 0;
		this.maxRad = maxRad;
		this.active = true;
	}

	public void incRadius() {
		radius += velocity;
		if (radius > maxRad+50)
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
			g.drawOval((int) (x0 - radius), (int) (y0 - radius), circ, circ);
		}

	}

	public void remove() {
	}
}
