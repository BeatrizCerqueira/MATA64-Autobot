package autobot.utils;

import java.awt.geom.Point2D;
import java.util.Random;

@SuppressWarnings("CommentedOutCode")
public class MathUtils {

    public static Point2D getLocation(Point2D initLocation, double angle, double distance) {
        double x = (int) (initLocation.getX() + Math.sin(angle) * distance);
        double y = (int) (initLocation.getY() + Math.cos(angle) * distance);
        return new Point2D.Double(x, y);

    }

/*
    public static double getAngle(Point2D A, Point2D B) {
        return Math.asin((B.getY() - A.getY()) / getDistance(A, B));
    }

    public static double getDistance(Point2D A, Point2D B) {
        return Point2D.distance(A.getX(), A.getY(), B.getX(), B.getY());
    }
*/

    public static double random(double start, double end) {
        return start + Math.random() * ((end - start + 1));
    }

    public static int random(int start, int end) {
        Random rand = new Random();
        return rand.nextInt(end - start + 1) + start;
    }

}
