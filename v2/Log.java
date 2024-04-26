package autobot.v2;

import org.jpl7.Query;
import robocode.Robot;

public class Log extends Robot {
    public void run() {
        if (!Query.hasSolution("consult('test.pl').")) {
            System.out.println("Consult failed");

        }

        while (true) {
            // Replace the next 4 lines with any behavior you would like
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
    }

}
