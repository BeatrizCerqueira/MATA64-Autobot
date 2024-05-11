package autobot.v1.drafts.lucas.auxy;

public class Consts {
    public static final double RADAR_COVERAGE_DIST = 15.0; // Distance we want to scan from middle of enemy to either side

    // Genetic Algorithm consts
    public static final int POPULATION_SIZE = 8;

    public static final int TURNS_TO_INIT_GA = 20; // How many turns until start evaluating chromosomes
    public static final int TURNS_TO_EVALUATE = 10; // How many turns each evaluating cycle lasts

    public static final double MUTATION_PROBABILITY = 0.05;

    // Percentage of individuals passed to next generations by each method
    private static final double ELITISM_PERCENTAGE = 0;
    private static final double CROSSOVER_PERCENTAGE = (1 - ELITISM_PERCENTAGE);

    // How many individuals will pass to next generation according to each method
    public static final int ELITISM_COUNT = (int) (ELITISM_PERCENTAGE * POPULATION_SIZE);
    public static final int CROSSOVER_COUNT = (int) (CROSSOVER_PERCENTAGE * POPULATION_SIZE);

    // Percentage for parent selection on crossover.
    // Parent1 will be selected among CROSSOVER_PARENT1_RATE % of fittest individuals, Parent2 among the rest
    private static final double CROSSOVER_PARENT1_RATE = 0.4;
    private static final double CROSSOVER_PARENT2_RATE = (1 - CROSSOVER_PARENT1_RATE);

    // How many individuals are in each group for crossover
    public static final int CROSSOVER_PARENT1_COUNT = (int) (CROSSOVER_PARENT1_RATE * POPULATION_SIZE);
    public static final int CROSSOVER_PARENT2_COUNT = (int) (CROSSOVER_PARENT2_RATE * POPULATION_SIZE);

}
