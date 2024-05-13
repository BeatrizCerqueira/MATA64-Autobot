package autobot.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static autobot.utils.Consts.*;
import static autobot.utils.MathUtils.random;

public class Population {

    List<Chromosome> currentGeneration = new ArrayList<>();
    List<Chromosome> nextGeneration = new ArrayList<>();

    int generation = 0;
    int currentChromosomeIndex = -1;

    public Population() {
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Chromosome chromosome = new Chromosome();
            currentGeneration.add(chromosome);
        }
    }

    public Population(List<Chromosome> chromosomeList) {
        currentGeneration.addAll(chromosomeList);
    }

    public Chromosome getNextChromosome() {
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

        for (Chromosome chromosome : nextGeneration)
            currentGeneration.add(new Chromosome(chromosome.getGenes()));

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

            int indexP1 = random(0, CROSSOVER_PARENT1_COUNT - 1);
            Chromosome parent1 = currentGeneration.get(indexP1);

            int indexP2 = random(CROSSOVER_PARENT2_COUNT, POPULATION_SIZE - 1);
            Chromosome parent2 = currentGeneration.get(indexP2);

            Chromosome child = Chromosome.generateChild(parent1, parent2);

            nextGeneration.add(child);
        }
    }

    public List<Chromosome> getCurrentGeneration() {
        return currentGeneration;
    }

}
