package autobot.v1.drafts.lucas.genetic;

import autobot.v1.drafts.lucas.genetic.genes.BordersMargin;
import autobot.v1.drafts.lucas.genetic.genes.Gene;
import autobot.v1.drafts.lucas.genetic.genes.SafeDistance;
import autobot.v1.drafts.lucas.genetic.genes.Velocity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

public class Chromosome implements Comparable<Chromosome>, Serializable {

    Map<String, Gene> genes;
    int fitness;

    public Chromosome() {

        Map<String, Gene> genes = new HashMap<>();

        genes.put("velocity", new Velocity());
        genes.put("safeDistance", new SafeDistance());
        genes.put("bordersMargin", new BordersMargin());

        this.genes = genes;
    }

    public Chromosome(Map<String, Gene> genes) {
        this.genes = genes;
    }

    public Map<String, Gene> getGenes() {
        return genes;
    }

    private int getGeneValue(String key) {
        return genes.get(key).getValue();
    }

    public int getVelocity() {
        return getGeneValue("velocity");
    }

    public int getSafeDistance() {
        return getGeneValue("safeDistance");
    }

    public int getBordersMargin() {
        return getGeneValue("bordersMargin");
    }

    // TODO: (Maybe) move the calculation of the fitness score to here
    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public static Chromosome generateChild(Chromosome parent1, Chromosome parent2) {
        Map<String, Gene> childGenes = new HashMap<>();
        List<Chromosome> parents = List.of(parent1, parent2);
        for (String key : parent1.genes.keySet()) {
            int selectedParent = random(0, 1);
            Gene selectedGene = parents.get(selectedParent).genes.get(key).copy();
            selectedGene.randomlyMutate();
            childGenes.put(key, selectedGene);
        }
        return new Chromosome(childGenes);
    }

    @Override
    public int compareTo(Chromosome o) {
        // Ascending order
        return Integer.compare(o.fitness, this.fitness);
    }
}
