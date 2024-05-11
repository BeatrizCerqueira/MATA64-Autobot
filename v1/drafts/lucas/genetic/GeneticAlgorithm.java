package autobot.v1.drafts.lucas.genetic;

import static autobot.v1.drafts.lucas.auxy.Consts.TURNS_TO_EVALUATE;

public class GeneticAlgorithm {
    static int turnsCount = 1;

    static Chromosome currentChromosome;
    static double energyBeforeFitness;
    static Population population;

    public static void init() {
        population = new Population();
    }

    public static void updateGA(double currentEnergy) {

        turnsCount--;

        if (turnsCount < 1) {
            turnsCount = TURNS_TO_EVALUATE;

            if (currentChromosome != null) {
                double energyDiff = energyBeforeFitness - currentEnergy; // Calculate energy diff since last evaluation cycle
                currentChromosome.setFitness(energyDiff);
            }

            energyBeforeFitness = currentEnergy;
            currentChromosome = population.getNextChromosome();
            printCurrentChromosome();

        }
    }

    private static void printCurrentChromosome() {
        System.out.print("# Testing ");
        currentChromosome.printChromosome();
    }

    public static int getVelocity() {
        return currentChromosome.getVelocity();
    }

    public static int getSafeDistance() {
        return currentChromosome.getSafeDistance();
    }

    public static int getBordersMargin() {
        return currentChromosome.getBordersMargin();
    }

    public static void main(String[] args) {
        init();
        updateGA(100);
    }
}

