package autobot.v1.drafts.lucas.GeneticAlgorithm;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    private static int populationSize = 5;

    List<Chromossome> population = new ArrayList<>();
    List<Chromossome> nextGeneration = new ArrayList<>();

    int generation = 0;
    int currentChromosome = 0;

    public Algorithm(List<Gene> genes) {
        // Initialize GA
        initializePopulation(genes);
    }

    private void initializePopulation(List<Gene> genes) {
        for (int i = 0; i < populationSize; i++) {
            population.add(newChromossome(genes));
        }
    }

    private Chromossome newChromossome(List<Gene> originalGenes) {
        List<Gene> newGenes = SerializationUtils.clone((ArrayList<Gene>) originalGenes);

        for (Gene gene : newGenes)
            gene.mutate();

        return new Chromossome(newGenes);
    }


    // ====== DEBUG ======
    public static void main(String[] args) {

        List<Gene> genes = new ArrayList<>();

        genes.add(new Gene("velocity", 0, 20));
        genes.add(new Gene("safeDistance", 40, 1000));
        genes.add(new Gene("bordersMargin", 10, 300));

        Algorithm GA = new Algorithm(genes);


        // -----------

//        GeneticAlgorithm GA = new GeneticAlgorithm(genes);
//        int generations = 5;
//
//        for (int i = 0; i < populationSize * generations; i++) {
//            int chromo = GA.getNextChromosome();
//            GA.setFitScore(random(1, 5));
//        }
//        GA.printPopulation();

    }


}
