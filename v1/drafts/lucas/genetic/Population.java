package autobot.v1.drafts.lucas.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

public class Population {
    // Rates must be between 0 and 1. Determines probability of occurs

    // TODO: Mover para consts
    private static final int POPULATION_SIZE = 5;

    private static final double ELITISM_RATE = 0.4;
    private static final double CROSSOVER_RATE = (1 - ELITISM_RATE);

    private static final double CROSSOVER_FIRST_PARENT_RATE = 0.4;
    private static final double CROSSOVER_SECOND_PARENT_RATE = (1 - CROSSOVER_FIRST_PARENT_RATE);

    private static final int ELITISM_COUNT = (int) (ELITISM_RATE * POPULATION_SIZE);
    private static final int CROSSOVER_COUNT = (int) (CROSSOVER_RATE * POPULATION_SIZE);
    private static final int CROSSOVER_FIRST_PARENT_COUNT = (int) (CROSSOVER_FIRST_PARENT_RATE * POPULATION_SIZE);
    private static final int CROSSOVER_SECOND_PARENT_COUNT = (int) (CROSSOVER_SECOND_PARENT_RATE * POPULATION_SIZE);

    List<Chromossome> currentGeneration = new ArrayList<>();
    List<Chromossome> nextGeneration = new ArrayList<>();

    int generation = 0;
    int currentChromosomeIndex = -1;

    public Population() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Chromossome chromossome = new Chromossome();
            currentGeneration.add(chromossome);
        }
    }

//    private void printPopulation() {
//        for (Chromossome individual : population) {
//            System.out.println("Score: " + individual.fitness);
//            for (Gene gene : individual.genes) {
//                System.out.print(gene.getDescription());
//                System.out.print(" ");
//                System.out.println(gene.getValue());
//            }
//            System.out.println();
//        }
//    }

    public Chromossome getNextChromossome() {
        currentChromosomeIndex++;
        if (currentChromosomeIndex >= POPULATION_SIZE) newGeneration();
        return currentGeneration.get(currentChromosomeIndex);
    }

    private void newGeneration() {
        generation++;
        currentChromosomeIndex = 0;

        Collections.sort(currentGeneration);

        elitism();
        crossover();

        currentGeneration.clear();

        for (Chromossome chromossome : nextGeneration)
            currentGeneration.add(new Chromossome(chromossome.getGenes()));

        nextGeneration.clear();
    }

    public void elitism() {
        // X% of fittest population goes to the next generation
        for (int i = 0; i < ELITISM_COUNT; i++)
            nextGeneration.add(currentGeneration.get(i));
    }

    public void crossover() {
        // crossover between X% of fittest population and 1-X% of unfittest

        for (int i = 0; i < CROSSOVER_COUNT; i++) {

            int indexP1 = random(0, CROSSOVER_FIRST_PARENT_COUNT - 1);
            Chromossome parent1 = currentGeneration.get(indexP1);

            int indexP2 = random(CROSSOVER_SECOND_PARENT_COUNT, POPULATION_SIZE - 1);
            Chromossome parent2 = currentGeneration.get(indexP2);

            Chromossome child = Chromossome.generateChild(parent1, parent2);

            nextGeneration.add(child);
        }
    }


    // ====== DEBUG ======
    public static void main(String[] args) {

        Population population = new Population();

        Chromossome currentChromossome = population.getNextChromossome(); //1st

        // To access chromossome data
        int velocity = currentChromossome.getVelocity();
        int safeDistance = currentChromossome.getSafeDistance();
        int bordersMargin = currentChromossome.getBordersMargin();


        for (int i = 0; i < POPULATION_SIZE * 3; i++) {
            currentChromossome.setFitness(random(1, 20));
            currentChromossome = population.getNextChromossome();
        }

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
