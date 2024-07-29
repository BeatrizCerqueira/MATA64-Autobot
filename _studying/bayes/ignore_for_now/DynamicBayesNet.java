package autobot._studying.bayes.ignore_for_now;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DynamicBayesNet {
    private BayesNet net;
    private BayesNode burglary;
    private BayesNode earthquake;
    private BayesNode alarm;
    private BayesNode johnCalls;
    private BayesNode maryCalls;
    private Map<String, Integer> eventCounts;

    public DynamicBayesNet() {
        // Step 1: Initialize the Bayesian Network
        net = new BayesNet();

        // Step 2: Define Variables (Nodes)
        burglary = net.createNode("Burglary");
        burglary.addOutcomes("true", "false");

        earthquake = net.createNode("Earthquake");
        earthquake.addOutcomes("true", "false");

        alarm = net.createNode("Alarm");
        alarm.addOutcomes("true", "false");
        alarm.setParents(Arrays.asList(burglary, earthquake));

        johnCalls = net.createNode("JohnCalls");
        johnCalls.addOutcomes("true", "false");
        johnCalls.setParents(Arrays.asList(alarm));

        maryCalls = net.createNode("MaryCalls");
        maryCalls.addOutcomes("true", "false");
        maryCalls.setParents(Arrays.asList(alarm));

        // Initialize event counts
        eventCounts = new HashMap<>();
        eventCounts.put("Burglary_true", 0);
        eventCounts.put("Burglary_false", 0);
        eventCounts.put("Earthquake_true", 0);
        eventCounts.put("Earthquake_false", 0);
        eventCounts.put("Alarm_true_true", 0);
        eventCounts.put("Alarm_true_false", 0);
        eventCounts.put("Alarm_false_true", 0);
        eventCounts.put("Alarm_false_false", 0);
        eventCounts.put("JohnCalls_true", 0);
        eventCounts.put("JohnCalls_false", 0);
        eventCounts.put("MaryCalls_true", 0);
        eventCounts.put("MaryCalls_false", 0);
    }

    // Step 4: Collect data on event occurrences during execution
    public void recordEvent(String event, boolean outcome) {
        String key = event + "_" + (outcome ? "true" : "false");
        eventCounts.put(key, eventCounts.get(key) + 1);
    }

    // Step 5: Update the Conditional Probability Tables (CPTs) based on collected data
    public void updateProbabilities() {
        int totalBurglary = eventCounts.get("Burglary_true") + eventCounts.get("Burglary_false");
        int totalEarthquake = eventCounts.get("Earthquake_true") + eventCounts.get("Earthquake_false");
        int totalAlarmTrue = eventCounts.get("Alarm_true_true") + eventCounts.get("Alarm_true_false");
        int totalAlarmFalse = eventCounts.get("Alarm_false_true") + eventCounts.get("Alarm_false_false");

        burglary.setProbabilities(
                (double) eventCounts.get("Burglary_true") / totalBurglary,
                (double) eventCounts.get("Burglary_false") / totalBurglary
        );

        earthquake.setProbabilities(
                (double) eventCounts.get("Earthquake_true") / totalEarthquake,
                (double) eventCounts.get("Earthquake_false") / totalEarthquake
        );

        alarm.setProbabilities(
                (double) eventCounts.get("Alarm_true_true") / totalAlarmTrue, (double) eventCounts.get("Alarm_true_false") / totalAlarmTrue,
                (double) eventCounts.get("Alarm_false_true") / totalAlarmFalse, (double) eventCounts.get("Alarm_false_false") / totalAlarmFalse
        );

        johnCalls.setProbabilities(
                (double) eventCounts.get("JohnCalls_true") / (eventCounts.get("JohnCalls_true") + eventCounts.get("JohnCalls_false")),
                (double) eventCounts.get("JohnCalls_false") / (eventCounts.get("JohnCalls_true") + eventCounts.get("JohnCalls_false"))
        );

        maryCalls.setProbabilities(
                (double) eventCounts.get("MaryCalls_true") / (eventCounts.get("MaryCalls_true") + eventCounts.get("MaryCalls_false")),
                (double) eventCounts.get("MaryCalls_false") / (eventCounts.get("MaryCalls_true") + eventCounts.get("MaryCalls_false"))
        );
    }

    // Step 6: Perform inference to get the probabilities of certain events
    public void performInference() {
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

    public static void main(String[] args) {
        DynamicBayesNet dbn = new DynamicBayesNet();

        // Simulate event occurrences
        dbn.recordEvent("Burglary", true);
        dbn.recordEvent("Earthquake", false);
        dbn.recordEvent("Alarm", true);
        dbn.recordEvent("JohnCalls", true);
        dbn.recordEvent("MaryCalls", true);

        // Update probabilities based on recorded events
        dbn.updateProbabilities();

        // Perform inference
        dbn.performInference();
    }
}