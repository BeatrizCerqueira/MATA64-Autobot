package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;
import java.util.ArrayList;

public class Bullet {
	private int x0;
	private int y0;

	public double x;
	public double y;
	public double firepower;
	public boolean inactive;

	private double velocity;
	private double radius;

	
	public Bullet(int x, int y, double firepower) {
		this.x0 = x;
		this.y0 = y;
		this.firepower = firepower;
		this.velocity = 20 - 3 * firepower;
		this.radius = 0;
	}

	public void incRadius() {
		radius += velocity;
		if (radius > 1000){
			this.inactive=true;
		}
	}

	public double getRadius() {
		return radius;
	}

	public void drawBulletRadius(Graphics2D g) {
		incRadius();
		int circ = (int) (2 * radius);
		g.setColor(Color.orange);
		g.drawOval((int) (x0 - radius), (int) (y0 - radius), circ, circ);
	}
}
