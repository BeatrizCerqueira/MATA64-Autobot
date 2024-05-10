package autobot.v1.drafts.lucas.geneticAlgorithm;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

public class Chromossome implements Comparable<Chromossome>, Serializable {
    Map<String, Gene> genes;
    int fitness;

    public Chromossome(Map<String, Gene> genes) {
        this.genes = genes;
    }

    public void initialize() {
        Map<String, Gene> newGenes = SerializationUtils.clone((HashMap<String, Gene>) genes);

        for (Gene gene : newGenes.values())
            gene.mutate();

        this.genes = newGenes;
    }

    public void mutate(double mutationRate) {
        int probability = random(0, 100);

        if (probability < (mutationRate * 100)) {
            Object[] keys = genes.keySet().toArray();
            String randomKey = (String) keys[random(0, keys.length - 1)];
            genes.get(randomKey).mutate();
        }
    }

    @Override
    public int compareTo(Chromossome o) {
        // Ascending order
        return Integer.compare(o.fitness, this.fitness);
    }

    // Calculate fitness score
    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public static Chromossome mate(Chromossome parent1, Chromossome parent2) {
        Map<String, Gene> childGenes = new HashMap<>();

        for (String key : parent1.genes.keySet()) {
            int selectedParent = random(1, 2);
            Gene selectedGene;

            if (selectedParent < 2) {
                selectedGene = parent1.genes.get(key);
            } else {
                selectedGene = parent2.genes.get(key);
            }

            selectedGene = SerializationUtils.clone(selectedGene);
            childGenes.put(key, selectedGene);
        }
        return new Chromossome(childGenes);
    }
}
