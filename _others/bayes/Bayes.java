package autobot._others.bayes;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;

import java.util.HashMap;
import java.util.Map;

public class Bayes {
    public static void main(String[] args) {
        // Step 1: Initialize the Bayesian Network
        BayesNet bn = new BayesNet();

        // Step 2: Define Variables (Nodes)
        int burglary = bn.addNode("Burglary");
        int earthquake = bn.addNode("Earthquake");
        int alarm = bn.addNode("Alarm");
        int johnCalls = bn.addNode("JohnCalls");
        int maryCalls = bn.addNode("MaryCalls");

        // Step 3: Define Conditional Probability Tables (CPTs)
        // Burglary CPT
        bn.setProbabilities(burglary, 0.001, 0.999);

        // Earthquake CPT
        bn.setProbabilities(earthquake, 0.002, 0.998);

        // Alarm CPT
        bn.setProbabilities(alarm,
                0.95, 0.05,  // P(Alarm=true | Burglary=true, Earthquake=true)
                0.94, 0.06,  // P(Alarm=true | Burglary=true, Earthquake=false)
                0.29, 0.71,  // P(Alarm=true | Burglary=false, Earthquake=true)
                0.001, 0.999 // P(Alarm=true | Burglary=false, Earthquake=false)
        );

        // JohnCalls CPT
        bn.setProbabilities(johnCalls,
                0.90, 0.10,  // P(JohnCalls=true | Alarm=true)
                0.05, 0.95   // P(JohnCalls=true | Alarm=false)
        );

        // MaryCalls CPT
        bn.setProbabilities(maryCalls,
                0.70, 0.30,  // P(MaryCalls=true | Alarm=true)
                0.01, 0.99   // P(MaryCalls=true | Alarm=false)
        );

        // Step 4: Add Edges
        bn.addEdge(burglary, alarm);
        bn.addEdge(earthquake, alarm);
        bn.addEdge(alarm, johnCalls);
        bn.addEdge(alarm, maryCalls);

        // Step 5: Perform Inference
        JunctionTreeAlgorithm inference = new JunctionTreeAlgorithm();
        inference.setNetwork(bn);

        // Set evidence
        Map<Integer, String> evidence = new HashMap<>();
        evidence.put(burglary, "true");
        evidence.put(earthquake, "false");
        inference.setEvidence(evidence);

        // Get the probability of the alarm going off
        double[] alarmProbabilities = inference.getBeliefs(alarm);
        System.out.println("Probability of Alarm: " + alarmProbabilities[0]);

        // Get the probability of John calling
        double[] johnCallsProbabilities = inference.getBeliefs(johnCalls);
        System.out.println("Probability of John Calling: " + johnCallsProbabilities[0]);

        // Get the probability of Mary calling
        double[] maryCallsProbabilities = inference.getBeliefs(maryCalls);
        System.out.println("Probability of Mary Calling: " + maryCallsProbabilities[0]);
    }
}