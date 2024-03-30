package autobot;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;
import java.util.ArrayList;

public class Bullet {
	public int x;
	public int y;
	public double firepower;
	private double velocity;
	private int turns;

	public Bullet(int x, int y, double firepower) {
		this.x = x;
		this.y = y;
		this.firepower = firepower;
		this.velocity = 20 - 3 * firepower;
		this.turns = 0;
	}

	public double getRadius() {
		turns++;
		return velocity * turns;
	}

	public void drawBulletRadius(Graphics2D g) {
		g.setColor(Color.orange);
		double radius = getRadius();
		int circ = (int) (2 * radius);
		g.drawOval((int) (x - radius), (int) (y - radius), circ, circ);
	}
}
