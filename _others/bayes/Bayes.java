package autobot._others.bayes;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Bayes {
    public static void main(String[] args) {
        // Step 1: Initialize the Bayesian Network
        BayesNet net = new BayesNet();

        // Step 2: Define Variables (Nodes)
        BayesNode burglary = net.createNode("Burglary");
        burglary.addOutcomes("true", "false");
        burglary.setProbabilities(0.001, 0.999);

        BayesNode earthquake = net.createNode("Earthquake");
        earthquake.addOutcomes("true", "false");
        earthquake.setProbabilities(0.002, 0.998);

        BayesNode alarm = net.createNode("Alarm");
        alarm.addOutcomes("true", "false");
        alarm.setParents(Arrays.asList(burglary, earthquake));
        alarm.setProbabilities(
                0.95, 0.05,  // P(Alarm=true | Burglary=true, Earthquake=true)
                0.94, 0.06,  // P(Alarm=true | Burglary=true, Earthquake=false)
                0.29, 0.71,  // P(Alarm=true | Burglary=false, Earthquake=true)
                0.001, 0.999 // P(Alarm=true | Burglary=false, Earthquake=false)
        );

        BayesNode johnCalls = net.createNode("JohnCalls");
        johnCalls.addOutcomes("true", "false");
        johnCalls.setParents(Arrays.asList(alarm));
        johnCalls.setProbabilities(
                0.90, 0.10,  // P(JohnCalls=true | Alarm=true)
                0.05, 0.95   // P(JohnCalls=true | Alarm=false)
        );

        BayesNode maryCalls = net.createNode("MaryCalls");
        maryCalls.addOutcomes("true", "false");
        maryCalls.setParents(Arrays.asList(alarm));
        maryCalls.setProbabilities(
                0.70, 0.30,  // P(MaryCalls=true | Alarm=true)
                0.01, 0.99   // P(MaryCalls=true | Alarm=false)
        );

        // Step 4: Perform Inference
        IBayesInferer inferer = new JunctionTreeAlgorithm();
        inferer.setNetwork(net);

        // Set evidence
        Map<BayesNode, String> evidence = new HashMap<>();
        evidence.put(burglary, "true");
        evidence.put(earthquake, "false");
        inferer.setEvidence(evidence);

        // Get the probability of the alarm going off
        double[] alarmProbabilities = inferer.getBeliefs(alarm);
        System.out.println("Probability of Alarm: " + alarmProbabilities[0]);

        // Get the probability of John calling
        double[] johnCallsProbabilities = inferer.getBeliefs(johnCalls);
        System.out.println("Probability of John Calling: " + johnCallsProbabilities[0]);

        // Get the probability of Mary calling
        double[] maryCallsProbabilities = inferer.getBeliefs(maryCalls);
        System.out.println("Probability of Mary Calling: " + maryCallsProbabilities[0]);
    }
}
