package autobot.v1.drafts.lucas;

import java.util.ArrayList;
import java.util.Collections;
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


    List<Individual> population = new ArrayList<>();
    int generation = 0;
    int currentChromosome = 0;


    public GeneticAlgorithmDec() {
        // Default values
        populationSize = 5;
        minValue = 20;
        maxValue = 200;
        // Initialize GA
        initializePopulation();
    }

    public GeneticAlgorithmDec(int minValue, int maxValue) {
        // Default values
        populationSize = 5;
        this.minValue = minValue;
        this.maxValue = maxValue;

        // Initialize GA
        initializePopulation();
    }

    private void initializePopulation() {
        for (int i = 0; i < populationSize; i++) {
            int chromosome = createChromosome();
            population.add(new Individual(chromosome));
        }
    }

    private static int createChromosome() {
        return random(minValue, maxValue);
    }

    private static int mutateGene() {
        return random(0, 1);
    }

    private static boolean isValid(int chromosome) {
        return (chromosome >= minValue) && (chromosome <= maxValue);
    }


    public int getChromosome() {
        return population.get(currentChromosome).chromosome;
    }

    public void setFitScore(int score) {
        population.get(currentChromosome).setFitness(score);
        nextChromosome();

    }

    public void nextChromosome() {
        currentChromosome++;
        if (currentChromosome == populationSize) {
            currentChromosome = 0;
            newGeneration();
        }
    }

    private void newGeneration() {
        Collections.sort(population);
        printPopulation();
    }

    private static class Individual implements Comparable<Individual> {
        int chromosome;
        int fitness;

        Individual(int chromosome) {
            this.chromosome = chromosome;
        }

        @Override
        public int compareTo(Individual o) {
            // Ascending order
            return Integer.compare(o.fitness, this.fitness);
        }

        // Calculate fitness score
        public void setFitness(int fitness) {
            this.fitness = fitness;
        }
    }

    // ====== DEBUG ======
    public static void main(String[] args) {
        GeneticAlgorithmDec GA = new GeneticAlgorithmDec(); //criei a população
        for (int i = 0; i < populationSize; i++) {
            int chromo = GA.getChromosome();
            GA.setFitScore(random(1, 5));
        }
    }

    private void printPopulation() {
        for (int i = 0; i < population.size(); i++) {
            int chromosome = population.get(i).chromosome;
            int score = population.get(i).fitness;
            System.out.print(i + ": ");
            System.out.print(chromosome);
            System.out.print(" - ");
            System.out.print(score);
            System.out.println();
        }
    }
}
