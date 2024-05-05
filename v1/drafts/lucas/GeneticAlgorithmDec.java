package autobot.v1.drafts.lucas;

import java.util.ArrayList;
import java.util.List;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

public class GeneticAlgorithmDec {

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
    private static int minValue;
    private static int maxValue;
//    private static int Genes;

    List<Individual> population = new ArrayList<>();
    int generation = 0;


    public GeneticAlgorithmDec() {
        // Default values
        populationSize = 5;
        minValue = 20;
        maxValue = 200;

        // Initialize GA
        initializePopulation();
    }

    private void initializePopulation() {
        // create initial population
        for (int i = 0; i < populationSize; i++) {
            int chromosome = createChromosome();
            population.add(new Individual(chromosome));
        }
    }

    private static int createChromosome() {
        int chromosome = mutateGene();

        if (isValid(chromosome))
            return chromosome;
        else
            return createChromosome();
    }

    private static int mutateGene() {
        return random(minValue, maxValue);

    }

    private static boolean isValid(int chromosome) {

        return (chromosome >= minValue) && (chromosome <= maxValue);
    }


    private void converge() {
//        parents = selectParents();
//        newPopulation = newGeneration(parents);
//        mutate();
//        checkFitness();
    } //WIP

    private static class Individual implements Comparable<Individual> {
        int chromosome;
        int fitness;

        Individual(int chromosome) {
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

        GeneticAlgorithmDec GA = new GeneticAlgorithmDec();
        GA.printPopulation();

    }

    private void printPopulation() {
        for (int i = 0; i < population.size(); i++) {
            int chromosome = population.get(i).chromosome;
            System.out.print(i + ": ");
            System.out.println(chromosome);
        }
    }


}
