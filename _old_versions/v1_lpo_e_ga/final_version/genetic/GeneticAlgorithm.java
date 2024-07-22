package autobot._old_versions.v1_lpo_e_ga.final_version.genetic;

import java.util.List;

import static autobot._old_versions.v1_lpo_e_ga.final_version.utils.Consts.TURNS_TO_EVALUATE;

public class GeneticAlgorithm {

    static int turnsCount = 1; //TURNS_TO_INIT_GA

    static Chromosome currentChromosome;
    static double energyBeforeFitness;
    static Population population;

    public static void init(int roundNum) {

        population = new Population();

        if (roundNum == 0) {
            FileHandler.deleteFile();
        }

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

}

