package autobot._others.bayes;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;

import java.util.Arrays;
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

    public static class Bayes {
        public static void main(String[] args) {

            BayesNet net = new BayesNet();

            org.eclipse.recommenders.jayes.BayesNode node1 = net.createNode("node1");
            org.eclipse.recommenders.jayes.BayesNode node2 = net.createNode("node2");

            node2.setParents(Arrays.asList(node1));

            node1.addOutcomes("true", "false");
            node1.setProbabilities(0.2, 0.8);

            node2.addOutcomes("one", "two", "three");
            node2.setProbabilities(
                    0.1, 0.4, 0.5,  // a == true
                    0.3, 0.4, 0.3   // a == false
            );

            org.eclipse.recommenders.jayes.inference.IBayesInferer inferer = new JunctionTreeAlgorithm();
            inferer.setNetwork(net);

            System.out.println("A " + Arrays.toString(inferer.getBeliefs(node1)));
            System.out.println("A " + Arrays.toString(inferer.getEvidence());


            // loop

            System.out.println("add inferencia 1");

    //        Map<BayesNode, String> evidence = new HashMap<>();
    //        evidence.put(node1, "true"); // Adiciona evidência para o nó 1
    //        evidence.put(node2, "three"); // Adiciona evidência para o nó 2
    //        inferer.setEvidence(evidence);
            inferer.addEvidence(node1, "true");
            inferer.addEvidence(node2, "two");

            System.out.println("A " + Arrays.toString(inferer.getBeliefs(node1)));
            System.out.println("B " + Arrays.toString(inferer.getBeliefs(node2)));


            // ====================
            System.out.println("====");
            inferer.addEvidence(node1, "false");
            inferer.addEvidence(node2, "one");

            System.out.println("A " + Arrays.toString(inferer.getBeliefs(node1)));
            System.out.println("B " + Arrays.toString(inferer.getBeliefs(node2)));


        }

    }
}