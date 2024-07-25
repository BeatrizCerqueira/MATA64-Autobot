package autobot._others.teacher_examples.tutorial_bayes_fuzzy.rbayes;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;
import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Rbayes extends Robot {
    /**
     * run: Rbayes's default behavior
     */
    public void run() {
        // Initialization of the robot should be put here

        // After trying out your robot, try uncommenting the import at the top,
        // and the next line:

        // setColors(Color.red,Color.blue,Color.green); // body,gun,radar

        // Robot main loop
        BayesNet net = new BayesNet();
        BayesNode a = net.createNode("a");
        a.addOutcomes("true", "false");
        a.setProbabilities(0.2, 0.8);

        Arrays.asList(a);

        BayesNode b = net.createNode("b");
        b.addOutcomes("one", "two", "three");
        b.setParents(Arrays.asList(a));
        b.setProbabilities(
                0.1, 0.4, 0.5, // a == true
                0.3, 0.4, 0.3 // a == false
        );

        BayesNode c = net.createNode("c");
        c.addOutcomes("true", "false");
        c.setParents(Arrays.asList(a, b));

        c.setProbabilities(
                // a == true
                0.1, 0.9, // b == one
                0.0, 1.0, // b == two
                0.5, 0.5, // b == three
                // a == false
                0.2, 0.8, // b == one
                0.0, 1.0, // b == two
                0.7, 0.3 // b == three
        );

        IBayesInferer inferer = new JunctionTreeAlgorithm();
        inferer.setNetwork(net);
		/*
		Map<BayesNode,String> evidence = new HashMap<BayesNode,String>();
		evidence.put(a, "false");
		evidence.put(b, "three");
		inferer.setEvidence(evidence);
		double[] beliefsC = inferer.getBeliefs(c);
		 */
        while (true) {
			/* // Replace the next 4 lines with any behavior you would like
			ahead(100);
			turnGunRight(360);
			back(100);
			turnGunRight(360);*/
            //double[] beliefsC = inferer.getBeliefs(c);

            Map<BayesNode, String> evidence = new HashMap<>();
            evidence.put(a, "false"); // Exemplo de evidência para o nó a
            evidence.put(b, "three"); // Exemplo de evidência para o nó b
            inferer.setEvidence(evidence);

            double[] beliefsC = inferer.getBeliefs(c);

            // Tomar decisões com base nas crenças probabilísticas de c
            if (beliefsC[0] > beliefsC[1]) {
                // Se a probabilidade de um inimigo estar próximo for alta ("true")
                ahead(100);
            } else {
                // Caso contrário, ajuste o comportamento de movimento de acordo
                back(50);
                turnRight(90);
            }

            // Girar o canhão independentemente da decisão de movimento
            turnGunRight(360);
        }
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        // Replace the next line with any behavior you would like
        fire(1);
    }

    /**
     * onHitByBullet: What to do when you're hit by a bullet
     */
    public void onHitByBullet(HitByBulletEvent e) {
        // Replace the next line with any behavior you would like
        back(10);
    }

    /**
     * onHitWall: What to do when you hit a wall
     */
    public void onHitWall(HitWallEvent e) {
        // Replace the next line with any behavior you would like
        back(20);
    }
}
