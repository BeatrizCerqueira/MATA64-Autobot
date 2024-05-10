package autobot.v1.drafts.lucas.geneticAlgorithm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

public class Chromossome implements Comparable<Chromossome>, Serializable {

    Map<String, Gene> genes;
    int fitness;

    public Chromossome() {

        Map<String, Gene> genes = new HashMap<>();

        genes.put("velocity", new Velocity());
        genes.put("safeDistance", new SafeDistance());
        genes.put("bordersMargin", new BordersMargin());

        this.genes = genes;
    }

    public Chromossome(Map<String, Gene> genes) {
        this.genes = genes;
    }

    public Map<String, Gene> getGenes() {
        return genes;
    }

    // Calculate fitness score
    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public static Chromossome generateChild(Chromossome parent1, Chromossome parent2) {
        Map<String, Gene> childGenes = new HashMap<>();
        List<Chromossome> parents = List.of(parent1, parent2);
        for (String key : parent1.genes.keySet()) {
            int selectedParent = random(0, 1);
            Gene selectedGene = new Gene(parents.get(selectedParent).genes.get(key));
            selectedGene.randomlyMutate();
            childGenes.put(key, selectedGene);
        }
        return new Chromossome(childGenes);
    }

    @Override
    public int compareTo(Chromossome o) {
        // Ascending order
        return Integer.compare(o.fitness, this.fitness);
    }
}
