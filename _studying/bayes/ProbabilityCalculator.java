package autobot._studying.bayes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProbabilityCalculator {
    private final List<Map<String, String>> dataset;
    private final Map<String, Map<String, Double>> marginalProbabilities;
    private final Map<String, Map<String, Map<String, Double>>> conditionalProbabilities;

    public ProbabilityCalculator(List<Map<String, String>> dataset) {
        this.dataset = dataset;
        this.marginalProbabilities = new HashMap<>();
        this.conditionalProbabilities = new HashMap<>();
        calculateProbabilities();
    }

    private void calculateProbabilities() {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Map<String, Integer>> conditionalCounts = new HashMap<>();
        int total = dataset.size();

        for (Map<String, String> record : dataset) {
            for (String key : record.keySet()) {
                counts.put(key + "_" + record.get(key), counts.getOrDefault(key + "_" + record.get(key), 0) + 1);
            }
            String cause = record.get("Cause");
            for (String key : record.keySet()) {
                if (!key.equals("Cause")) {
                    conditionalCounts.putIfAbsent(cause, new HashMap<>());
                    conditionalCounts.get(cause).put(key + "_" + record.get(key), conditionalCounts.get(cause).getOrDefault(key + "_" + record.get(key), 0) + 1);
                }
            }
        }

        for (String key : counts.keySet()) {
            String[] parts = key.split("_");
            String attribute = parts[0];
            String value = parts[1];
            marginalProbabilities.putIfAbsent(attribute, new HashMap<>());
            marginalProbabilities.get(attribute).put(value, (double) counts.get(key) / total);
        }

        for (String cause : conditionalCounts.keySet()) {
            conditionalProbabilities.putIfAbsent(cause, new HashMap<>());
            for (String key : conditionalCounts.get(cause).keySet()) {
                String[] parts = key.split("_");
                String attribute = parts[0];
                String value = parts[1];
                conditionalProbabilities.get(cause).putIfAbsent(attribute, new HashMap<>());
                conditionalProbabilities.get(cause).get(attribute).put(value, (double) conditionalCounts.get(cause).get(key) / counts.get("Cause_" + cause));
            }
        }
    }

    public Map<String, Map<String, Double>> getMarginalProbabilities() {
        return marginalProbabilities;
    }

    public Map<String, Map<String, Map<String, Double>>> getConditionalProbabilities() {
        return conditionalProbabilities;
    }

    public static void main(String[] args) {
        List<Map<String, String>> dataset = List.of(
                Map.of("Manufacturer", "Dell", "OS", "Windows", "Symptom", "Can't print", "Cause", "Driver"),
                Map.of("Manufacturer", "Compaq", "OS", "Linux", "Symptom", "Can't print", "Cause", "Driver"),
                Map.of("Manufacturer", "Dell", "OS", "Linux", "Symptom", "No display", "Cause", "Hardware"),
                Map.of("Manufacturer", "Gateway", "OS", "Windows", "Symptom", "Can't print", "Cause", "Hardware")
        );

        ProbabilityCalculator calculator = new ProbabilityCalculator(dataset);
        System.out.println("Marginal Probabilities: " + calculator.getMarginalProbabilities());
        System.out.println("Conditional Probabilities: " + calculator.getConditionalProbabilities());
    }
}