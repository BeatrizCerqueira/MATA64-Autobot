package autobot.v1.drafts.lucas;

import java.util.ArrayList;
import java.util.List;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

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

public class Genetic {

    //
//    public double bordersMargin = 50.0;
//    public double RadarCoverageDistance = 15.0; // Distance we want to scan from middle of enemy to either side
//    public double enemySafeDistance = 150.0;

    private static int maxValue = 200;

    private static final int POPULATION_SIZE = 10;
    private static final String GENES = Integer.toBinaryString(maxValue);

    public static void main(String[] args) {
        // current generation
        int generation = 0;

        List<Genetic.Individual> population = new ArrayList<>();
        boolean found = false;

        // create initial population
        for (int i = 0; i < POPULATION_SIZE; i++) {
            String chromosome = createChromosome();
            population.add(new Individual(chromosome));
        }

//        System.out.println(population);

    }

    // Create random genes for mutation
    private static char mutateGenes() {
        return '0';
    }

    // Create chromosome or string of genes
    private static String createChromosome() {
        int len = GENES.length();
        StringBuilder chromosome = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int r = (int) random(0, 1);
            chromosome.append((char) r);
        }
        return chromosome.toString();
    }

    // Class representing individual in population
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

        // Calculate fitness score, it is the number of characters in string which differ from target string
        private int calFitness() {
            int len = GENES.length();
            int fitness = 0;
            for (int i = 0; i < len; i++) {
                if (chromosome.charAt(i) != GENES.charAt(i))
                    fitness++;
            }
            return fitness;
        }
    }
}
