package autobot._studying.bayes._ignore_for_now;
//	IMPORTACAO BAYES

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;

import java.util.Arrays;

public class BayesBot {
    public static void main(String[] args) {
        BayesNet net = new BayesNet();
        BayesNode a = net.createNode("a");

        a.addOutcomes("true", "false");
        a.setProbabilities(0.2, 0.8);

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

        // TODO: Test if Copilot can help us with the OS/Manufacturer/Symptom table
        // TODO: How to update the probabilities of the nodes?

/////////////////////////////////// Example 1:

        System.out.println("===========================================");
        System.out.println("A " + Arrays.toString(inferer.getBeliefs(a)));
        System.out.println("B " + Arrays.toString(inferer.getBeliefs(b)));
        System.out.println("C " + Arrays.toString(inferer.getBeliefs(c)));
//
//        Map<BayesNode, String> evidence = new HashMap<>();
//        evidence.put(a, "false");
//        evidence.put(b, "three");
//        inferer.setEvidence(evidence);
//
//        System.out.println("===========================================");
//        System.out.println("A " + Arrays.toString(inferer.getBeliefs(a)));
//        System.out.println("B " + Arrays.toString(inferer.getBeliefs(b)));
//        System.out.println("C " + Arrays.toString(inferer.getBeliefs(c)));
//
//        Map<BayesNode, String> evidence2 = new HashMap<>();
//        evidence2.put(a, "true");
//        evidence2.put(b, "two");
//        inferer.setEvidence(evidence2);
//
//        System.out.println("===========================================");
//        System.out.println("A " + Arrays.toString(inferer.getBeliefs(a)));
//        System.out.println("B " + Arrays.toString(inferer.getBeliefs(b)));
//        System.out.println("C " + Arrays.toString(inferer.getBeliefs(c)));


/////////////////////////////////// Example 2:

//        System.out.println("===========================================");
//        System.out.println("A " + Arrays.toString(inferer.getBeliefs(a)));
//        System.out.println("B " + Arrays.toString(inferer.getBeliefs(b)));
//        System.out.println("C " + Arrays.toString(inferer.getBeliefs(c)));
//
//        Map<BayesNode, String> evidence = new HashMap<>();
//        evidence.put(a, "false");
//        evidence.put(b, "three");
//
//        Map<BayesNode, String> evidence2 = new HashMap<>();
//        evidence2.put(a, "true");
//        evidence2.put(b, "two");
//
//        inferer.setEvidence(evidence);
//        inferer.setEvidence(evidence2);
//
//        System.out.println("===========================================");
//        System.out.println("A " + Arrays.toString(inferer.getBeliefs(a)));
//        System.out.println("B " + Arrays.toString(inferer.getBeliefs(b)));
//        System.out.println("C " + Arrays.toString(inferer.getBeliefs(c)));

    }

}
