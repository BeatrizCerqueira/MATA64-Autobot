package autobot.v1.drafts.lucas.geneticAlgorithm;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

//private static class Individual implements Comparable<GeneticAlgorithmDec.Individual> {
public class Chromossome implements Comparable<Chromossome>, Serializable {
    List<Gene> genes;
    int fitness;

//    public Chromossome(List<Gene> baseGenes) {
//        List<Gene> genes = SerializationUtils.clone((ArrayList<Gene>) baseGenes);
//
//        for (Gene gene : genes)
//            gene.mutate();
//
//        this.genes = genes;
//    }

    public Chromossome(List<Gene> genes) {
        this.genes = genes;
    }

    public void initialize() {
        List<Gene> newGenes = SerializationUtils.clone((ArrayList<Gene>) genes);

        for (Gene gene : newGenes)
            gene.mutate();

        this.genes = newGenes;
    }

    public void mutate(double mutationRate) {
        int probability = random(0, 100);

        if (probability < (mutationRate * 100)) {
            int geneIndex = random(0, genes.size() - 1);
            genes.get(geneIndex).mutate();
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
//        Chromossome child = new Chromossome(genes);

        List<Gene> childGenes = new ArrayList<>();

        for (int i = 0; i < parent1.genes.size(); i++) {

            int selectedParent = random(1, 2);

            Gene selectedGene;

            if (selectedParent < 2) {
                selectedGene = parent1.genes.get(i);

            } else {
                selectedGene = parent2.genes.get(i);
            }

            selectedGene = SerializationUtils.clone(selectedGene);
            childGenes.add(selectedGene);

//            childGenes.get(i).mutate();

        }
        return new Chromossome(childGenes);
    }


}

