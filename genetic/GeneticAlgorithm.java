package autobot.genetic;

import java.util.List;

import static autobot.auxy.Consts.TURNS_TO_EVALUATE;

public class GeneticAlgorithm {
    private static boolean enablePrints = false;

    static int turnsCount = 1; //TURNS_TO_INIT_GA

    static Chromosome currentChromosome;
    static double energyBeforeFitness;
    static Population population;


    public static void init() {

        population = new Population();

        // Check if there is file to load genetic data
        if (FileHandler.fileExists()) {
            loadGeneticData(); // may exist and be null
        }

    }

    public static void updateGA(double currentEnergy) {

        turnsCount--;

        if (turnsCount < 1) {
            turnsCount = TURNS_TO_EVALUATE;

            if (currentChromosome != null) {
                // Calculate energy diff since last evaluation cycle; positive values if scoring, neg values if getting damage
                double energyDiff = currentEnergy - energyBeforeFitness;
                currentChromosome.setFitness(energyDiff);
            }

            energyBeforeFitness = currentEnergy;
            currentChromosome = population.getNextChromosome();
            if (enablePrints)
                printCurrentChromosome();

        }
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

    private static void printCurrentChromosome() {
        System.out.print("# Testing ");
        currentChromosome.printChromosome();
    }

    public static void enablePrintTestingChromosomes() {
        GeneticAlgorithm.enablePrints = true;
    }

    public static void enablePrintGenerationScoring() {
        population.enablePrints();
    }

    public static void saveGeneticData() {
        FileHandler.saveToFile(population);
    }

    public static void loadGeneticData() {
        List<Chromosome> chromosomeList = FileHandler.loadGenesFromFile();
        population = new Population(chromosomeList);
    }

    public static void clearGeneticData() {
        FileHandler.deleteFile();
    }

    public static void main(String[] args) {
        clearGeneticData();
        init();
        saveGeneticData();
        updateGA(100);

    }
}

