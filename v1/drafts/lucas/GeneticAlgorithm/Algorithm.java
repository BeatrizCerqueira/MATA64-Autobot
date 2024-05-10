package autobot.v1.drafts.lucas.GeneticAlgorithm;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

public class Algorithm {
    // Rates must be between 0 and 1. Determines probability of occurs
    private static final double ELITISM_RATE = 0.4;
    private static final double CROSSOVER_RATE = 0.4;
    private static final double MUTATION_RATE = 0.05;

    private static int populationSize = 5;

    List<Chromossome> population = new ArrayList<>();
    List<Chromossome> nextGeneration = new ArrayList<>();

    int generation = 0;
    int currentChromosomeIndex = -1;

    public Algorithm(List<Gene> genes) {
        // Initialize GA
        initializePopulation(genes);
    }

    private void initializePopulation(List<Gene> genes) {
        for (int i = 0; i < populationSize; i++) {
            Chromossome chromossome = new Chromossome(genes);
            chromossome.initialize();
            population.add(chromossome);
        }
    }

    private void printPopulation() {
        for (Chromossome individual : population) {
            System.out.println("Score: " + individual.fitness);
            for (Gene gene : individual.genes) {
                System.out.print(gene.getDescription());
                System.out.print(" ");
                System.out.println(gene.getValue());
            }
            System.out.println();
        }
    }

    public void setScore(int score) {
        population.get(currentChromosomeIndex).setFitness(score);
    }

    public Chromossome getChromossome() {
        currentChromosomeIndex++;
        if (currentChromosomeIndex >= populationSize) {
            generation++;
            System.out.println("# Generation: " + generation + '\n');
            printPopulation();
            newGeneration();
        }
        return population.get(currentChromosomeIndex);
    }

    private void newGeneration() {
        Collections.sort(population);

        elitism();
        crossover();

        currentChromosomeIndex = 0;
        population = SerializationUtils.clone((ArrayList<Chromossome>) nextGeneration);
        nextGeneration = new ArrayList<>();

    }

    public void elitism() {
        // X% of fittest population goes to the next generation
        int range = (int) (ELITISM_RATE * populationSize);
        for (int i = 0; i < range; i++)
            nextGeneration.add(population.get(i));
    }

    public void crossover() {
        // crossover between X% of fittest population and 1-X% of unfittest

        int populationRange = populationSize - nextGeneration.size(); // how many individuals left to complete population
        for (int i = 0; i < populationRange; i++) {

            int crossoverRange = (int) (CROSSOVER_RATE * populationSize);

            int indexP1 = random(0, crossoverRange);
            Chromossome parent1 = population.get(indexP1);

            int indexP2 = random((populationSize - crossoverRange), populationSize - 1);
            Chromossome parent2 = population.get(indexP2);

            Chromossome child = Chromossome.mate(parent1, parent2);
            //TODO: may mutate child
            nextGeneration.add(child);
        }
    }


    // ====== DEBUG ======
    public static void main(String[] args) {


        List<Gene> genes = new ArrayList<>();

        genes.add(new Gene("velocity", 0, 20));
        genes.add(new Gene("safeDistance", 40, 1000));
        genes.add(new Gene("bordersMargin", 10, 300));

        Algorithm GA = new Algorithm(genes);

        Chromossome chromo = GA.getChromossome(); //1st
        for (int i = 0; i < populationSize * 3; i++) {
            GA.setScore(random(1, 20));
            chromo = GA.getChromossome();
        }
//        Chromosome c = GA.getChromossome();

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
