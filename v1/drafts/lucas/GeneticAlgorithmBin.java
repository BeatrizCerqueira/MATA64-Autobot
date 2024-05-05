package autobot.v1.drafts.lucas;

import java.util.ArrayList;
import java.util.List;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

public class GeneticAlgorithmBin {

    /*
     *
     * 1) Randomly initialize populations p
     * 2) Determine fitness of population
     * 3) Until convergence repeat:
     *       a) Select parents from population
     *       b) Crossover and generate new population
     *       c) Perform mutation on new population
     *       d) Calculate fitness for new population
     *
     */

    private static int populationSize;
    private static int maxValue;
    private static String Genes;

    List<Individual> population = new ArrayList<>();
    int generation = 0;


    public GeneticAlgorithmBin() {
        // Default values
        populationSize = 5;
        maxValue = 200;
        Genes = Integer.toBinaryString(maxValue);

        // Initialize GA
        initializePopulation();
    }

    private void initializePopulation() {
        // create initial population
        for (int i = 0; i < populationSize; i++) {
            String chromosome = createChromosome();
            population.add(new Individual(chromosome));
        }
    }

    private static String createChromosome() {
        int len = Genes.length();
        StringBuilder chromosome = new StringBuilder();
        for (int i = 0; i < len; i++) {
            chromosome.append(mutateGene());
        }

        String chromosomeStr = chromosome.toString();

        if (isValid(chromosomeStr))
            return chromosomeStr;
        else
            return createChromosome();
    }

    private static char mutateGene() {
        return (char) (random(0, 1) + '0');

    }

    private static boolean isValid(String chromosome) {
        return Integer.parseInt(chromosome, 2) <= maxValue;
    }


    private void converge() {
//        parents = selectParents();
//        newPopulation = newGeneration(parents);
//        mutate();
//        checkFitness();
    } //WIP

    private static class Individual implements Comparable<Individual> {
        String chromosome;
        int fitness;

        Individual(String chromosome) {
            this.chromosome = chromosome;
            fitness = calFitness();
        }

        @Override
        public int compareTo(Individual o) {
            return Integer.compare(this.fitness, o.fitness);
        }

        // Calculate fitness score,
        private int calFitness() {
            return 0;
        }
    }


    // ====== DEBUG ======
    public static void main(String[] args) {

        GeneticAlgorithmBin GA = new GeneticAlgorithmBin();
        GA.printPopulation();

    }

    private void printPopulation() {
        System.out.println("G: " + Genes);

        for (int i = 0; i < population.size(); i++) {
            String chromosome = population.get(i).chromosome;
            System.out.print(i + ": ");
            System.out.print(chromosome);
            System.out.print(" = ");
            System.out.println(Integer.parseInt(chromosome, 2));
        }
    }


}
