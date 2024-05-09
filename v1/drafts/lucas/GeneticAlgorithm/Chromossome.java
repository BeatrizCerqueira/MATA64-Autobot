package autobot.v1.drafts.lucas.GeneticAlgorithm;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

//private static class Individual implements Comparable<GeneticAlgorithmDec.Individual> {
public class Chromossome implements Comparable<Chromossome> {
    List<Gene> genes;
    int fitness;

    public Chromossome(List<Gene> baseGenes) {
        List<Gene> genes = SerializationUtils.clone((ArrayList<Gene>) baseGenes);

        for (Gene gene : genes)
            gene.mutate();

        this.genes = genes;
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
        return parent1;
    }

//    public static Chromossome mate(Chromossome parent1, Chromossome parent2) {
//        int mutationRate = 6;
//        int p2InheritRate = (100 - mutationRate);
//        int p1InheritRate = p2InheritRate / 2;
//
//
//        StringBuilder strChildChromosome = new StringBuilder();
//
//        String strParent1 = Integer.toBinaryString(parent1.value);
//        String strParent2 = Integer.toBinaryString(parent2.value);
//
//
//        for (int i = 0; i < strParent1.length(); i++) {
//            //check probabily for taking gene of par1, par2 or mutation
//            int prob = random(0, 100);
//            char gene;
//
//            if (prob < p1InheritRate)
//                gene = strParent1.charAt(i);
//
//            else if (prob < p2InheritRate)
//                gene = strParent2.charAt(i);
//
//            else
//                gene = 0;
////                gene = mutateGene();
//
//            strChildChromosome.append(gene);
//        }
//
//        int childChromosome = Integer.parseInt(strChildChromosome.toString(), 2);
//
//        if (Gene.isValid(childChromosome))
//            return new Chromossome(childChromosome);
//
//        return mate(parent1, parent2);
//    }
}

