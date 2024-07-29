package autobot._studying.bayes.ignore_for_now;

import java.util.*;

public class ProbabilityCalculator {
    private List<Map<String, String>> dataset;
    private Map<String, Map<String, Double>> marginalProbabilities;
    private Map<String, Map<String, Map<String, Double>>> conditionalProbabilities;
    private Set<String> manufacturers;
    private Set<String> osSet;
    private Set<String> symptoms;
    private Set<String> causes;

    public ProbabilityCalculator(List<Map<String, String>> dataset) {
        this.dataset = dataset;
        this.marginalProbabilities = new HashMap<>();
        this.conditionalProbabilities = new HashMap<>();
        this.manufacturers = new HashSet<>();
        this.osSet = new HashSet<>();
        this.symptoms = new HashSet<>();
        this.causes = new HashSet<>();
        extractUniqueValues();
        calculateProbabilities();
    }

    private void extractUniqueValues() {
        for (Map<String, String> record : dataset) {
            manufacturers.add(record.get("Manufacturer"));
            osSet.add(record.get("OS"));
            symptoms.add(record.get("Symptom"));
            causes.add(record.get("Cause"));
        }
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

        for (String manufacturer : manufacturers) {
            marginalProbabilities.putIfAbsent("Manufacturer", new HashMap<>());
            marginalProbabilities.get("Manufacturer").put(manufacturer, (double) counts.getOrDefault("Manufacturer_" + manufacturer, 0) / total);
        }

        for (String os : osSet) {
            marginalProbabilities.putIfAbsent("OS", new HashMap<>());
            marginalProbabilities.get("OS").put(os, (double) counts.getOrDefault("OS_" + os, 0) / total);
        }

        for (String symptom : symptoms) {
            marginalProbabilities.putIfAbsent("Symptom", new HashMap<>());
            marginalProbabilities.get("Symptom").put(symptom, (double) counts.getOrDefault("Symptom_" + symptom, 0) / total);
        }

        for (String cause : causes) {
            marginalProbabilities.putIfAbsent("Cause", new HashMap<>());
            marginalProbabilities.get("Cause").put(cause, (double) counts.getOrDefault("Cause_" + cause, 0) / total);
        }

        for (String cause : causes) {
            conditionalProbabilities.putIfAbsent(cause, new HashMap<>());
            for (String manufacturer : manufacturers) {
                conditionalProbabilities.get(cause).putIfAbsent("Manufacturer", new HashMap<>());
                conditionalProbabilities.get(cause).get("Manufacturer").put(manufacturer, (double) conditionalCounts.getOrDefault(cause, new HashMap<>()).getOrDefault("Manufacturer_" + manufacturer, 0) / counts.getOrDefault("Cause_" + cause, 0));
            }
            for (String os : osSet) {
                conditionalProbabilities.get(cause).putIfAbsent("OS", new HashMap<>());
                conditionalProbabilities.get(cause).get("OS").put(os, (double) conditionalCounts.getOrDefault(cause, new HashMap<>()).getOrDefault("OS_" + os, 0) / counts.getOrDefault("Cause_" + cause, 0));
            }
            for (String symptom : symptoms) {
                conditionalProbabilities.get(cause).putIfAbsent("Symptom", new HashMap<>());
                conditionalProbabilities.get(cause).get("Symptom").put(symptom, (double) conditionalCounts.getOrDefault(cause, new HashMap<>()).getOrDefault("Symptom_" + symptom, 0) / counts.getOrDefault("Cause_" + cause, 0));
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