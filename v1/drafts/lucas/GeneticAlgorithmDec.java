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
    List<Individual> nextGeneration = new ArrayList<>();
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

    private static char mutateGene() {
        return (char) (random(0, 1) + '0');
    }

    private static boolean isValid(int chromosome) {
        return (chromosome >= minValue) && (chromosome <= maxValue);
    }

    public int getNextChromosome() {
        int chromosome = population.get(currentChromosome).chromosome;
        updateCurrentChromosome();
        return chromosome;
    }

    public void updateCurrentChromosome() {
        currentChromosome++;
        if (currentChromosome == populationSize) {
            currentChromosome = 0;
            newGeneration();
        }
    }

    public void setFitScore(int score) {
        population.get(currentChromosome).setFitness(score);
    }

    private void newGeneration() {
        Collections.sort(population);

        generation++;
        System.out.println();
        System.out.println("Gen" + generation);
        printPopulation();

        elitism();
        crossover();

    }

    public void elitism() {
        double elitismRate = 20;
        // X% of fittest population goes to the next generation
        int range = (int) ((elitismRate * populationSize) / 100);
        for (int i = 0; i < range; i++)
            nextGeneration.add(population.get(i));

    }

    public void crossover() {
        // crossover between X% of fittest population
        double crossoverRate = 60;
        int crossoverRange = (int) (crossoverRate * populationSize) / 100;

        int range = populationSize - nextGeneration.size();
        for (int i = 0; i < range; i++) {

            int r = random(0, crossoverRange);
            Individual parent1 = population.get(r);

            r = random(0, crossoverRange);
            Individual parent2 = population.get(r);

            Individual child = Individual.mate(parent1, parent2);
            nextGeneration.add(child);
        }
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

        public static Individual mate(Individual parent1, Individual parent2) {
            int mutationRate = 6;
            int p2InheritRate = (100 - mutationRate);
            int p1InheritRate = p2InheritRate / 2;


            StringBuilder strChildChromosome = new StringBuilder();

            String strParent1 = Integer.toBinaryString(parent1.chromosome);
            String strParent2 = Integer.toBinaryString(parent2.chromosome);


            for (int i = 0; i < strParent1.length(); i++) {
                //check probabily for taking gene of par1, par2 or mutation
                int prob = random(0, 100);
                char gene;

                if (prob < p1InheritRate)
                    gene = strParent1.charAt(i);

                else if (prob < p2InheritRate)
                    gene = strParent2.charAt(i);

                else
                    gene = mutateGene();

                strChildChromosome.append(gene);
            }

            int childChromosome = Integer.parseInt(strChildChromosome.toString(), 2);

            if (isValid(childChromosome))
                return new Individual(childChromosome);

            return mate(parent1, parent2);
        }
    }

    // ====== DEBUG ======
    public static void main(String[] args) {

        GeneticAlgorithmDec GA = new GeneticAlgorithmDec();
        int generations = 5;

        for (int i = 0; i < populationSize * generations; i++) {
            int chromo = GA.getNextChromosome();
            GA.setFitScore(random(1, 5));
        }
        GA.printPopulation();

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
