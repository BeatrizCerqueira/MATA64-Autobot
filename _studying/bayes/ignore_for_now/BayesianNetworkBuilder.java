package autobot._studying.bayes.ignore_for_now;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;

import java.util.*;

public class BayesianNetworkBuilder {
    private final BayesNet net;
    private final Map<String, BayesNode> nodes;
    private final Map<String, Map<String, Integer>> eventCounts;

    public BayesianNetworkBuilder() {
        net = new BayesNet();
        nodes = new HashMap<>();
        eventCounts = new HashMap<>();
    }

    public void addNode(String nodeName, List<String> outcomes) {
        BayesNode node = net.createNode(nodeName);
        node.addOutcomes(outcomes.toArray(new String[0]));
        nodes.put(nodeName, node);
        eventCounts.put(nodeName, new HashMap<>());
        for (String outcome : outcomes) {
            eventCounts.get(nodeName).put(outcome, 0);
        }
    }

    public void addEdge(String parentName, String childName) {
        BayesNode parent = nodes.get(parentName);
        BayesNode child = nodes.get(childName);
        child.setParents(Collections.singletonList(parent));
    }

    public void recordEvent(Map<String, String> event) {
        for (Map.Entry<String, String> entry : event.entrySet()) {
            String nodeName = entry.getKey();
            String outcome = entry.getValue();
            eventCounts.get(nodeName).put(outcome, eventCounts.get(nodeName).get(outcome) + 1);
        }
    }

    public void updateProbabilities() {
        for (Map.Entry<String, BayesNode> entry : nodes.entrySet()) {
            String nodeName = entry.getKey();
            BayesNode node = entry.getValue();
            Map<String, Integer> counts = eventCounts.get(nodeName);
            int total = counts.values().stream().mapToInt(Integer::intValue).sum();
            double[] probabilities = counts.values().stream().mapToDouble(count -> (double) count / total).toArray();
            node.setProbabilities(probabilities);
        }
    }

    public void performInference(Map<String, String> evidence) {
        IBayesInferer inferer = new JunctionTreeAlgorithm();
        inferer.setNetwork(net);

        Map<BayesNode, String> evidenceMap = new HashMap<>();
        for (Map.Entry<String, String> entry : evidence.entrySet()) {
            evidenceMap.put(nodes.get(entry.getKey()), entry.getValue());
        }
        inferer.setEvidence(evidenceMap);

        for (Map.Entry<String, BayesNode> entry : nodes.entrySet()) {
            double[] beliefs = inferer.getBeliefs(entry.getValue());
            System.out.println("Probability of " + entry.getKey() + ": " + Arrays.toString(beliefs));
        }
    }

    public static void main(String[] args) {
        BayesianNetworkBuilder builder = new BayesianNetworkBuilder();

        // Example usage
        builder.addNode("Manufacturer", Arrays.asList("Dell", "Compaq", "Gateway"));
        builder.addNode("OS", Arrays.asList("Windows", "Linux"));
        builder.addNode("Symptom", Arrays.asList("Can't print", "No display"));
        builder.addNode("Cause", Arrays.asList("Driver", "Hardware"));

        builder.addEdge("Cause", "Manufacturer");
        builder.addEdge("Cause", "OS");
        builder.addEdge("Cause", "Symptom");

        // Simulate event occurrences
        builder.recordEvent(Map.of("Manufacturer", "Dell", "OS", "Windows", "Symptom", "Can't print", "Cause", "Driver"));
        builder.recordEvent(Map.of("Manufacturer", "Compaq", "OS", "Linux", "Symptom", "Can't print", "Cause", "Driver"));
        builder.recordEvent(Map.of("Manufacturer", "Dell", "OS", "Linux", "Symptom", "No display", "Cause", "Hardware"));
        builder.recordEvent(Map.of("Manufacturer", "Gateway", "OS", "Windows", "Symptom", "Can't print", "Cause", "Hardware"));

        // Update probabilities based on recorded events
        builder.updateProbabilities();

        // Perform inference
        Map<String, String> evidence = new HashMap<>();
        evidence.put("Cause", "Driver");
        builder.performInference(evidence);
    }
}