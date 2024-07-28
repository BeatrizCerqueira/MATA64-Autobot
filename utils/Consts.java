package autobot.utils;

public class Consts {
    public static final double INITIAL_ENERGY = 100;
    public static final double INITIAL_GUN_HEAT = 3;

    public static final double RADAR_COVERAGE_DIST = 15.0; // Distance we want to scan from middle of enemy to either side
    public static final double MIN_LIFE_TO_FIRE = 10.0;
    public static final double GUN_HEAT_JUST_BEFORE_COOLING = 0.3;

    public static final int MAX_TURNS_TO_FIRE = 30;

    // Genetic Algorithm consts
    public static final int POPULATION_SIZE = 10;

    public static final int TURNS_TO_EVALUATE = 10; // How many turns each evaluating cycle lasts

    public static final double MUTATION_PROBABILITY = 0.1;

    // Percentage of individuals passed to next generations by each method
    public static final double ELITISM_PERCENTAGE = 0.2;
    private static final double CROSSOVER_PERCENTAGE = (1 - ELITISM_PERCENTAGE);

    // How many individuals will pass to next generation according to each method
    public static final int ELITISM_COUNT = (int) (ELITISM_PERCENTAGE * POPULATION_SIZE);
    public static final int CROSSOVER_COUNT = (int) (CROSSOVER_PERCENTAGE * POPULATION_SIZE);

    // Percentage for parent selection on crossover.
    // Parent1 will be selected among CROSSOVER_PARENT1_RATE % of fittest individuals, Parent2 among the rest
    private static final double CROSSOVER_PARENT1_RATE = 0.2;
    private static final double CROSSOVER_PARENT2_RATE = (1 - CROSSOVER_PARENT1_RATE);

    // How many individuals are in each group for crossover
    public static final int CROSSOVER_PARENT1_COUNT = (int) (CROSSOVER_PARENT1_RATE * POPULATION_SIZE);
    public static final int CROSSOVER_PARENT2_COUNT = (int) (CROSSOVER_PARENT2_RATE * POPULATION_SIZE);

    // Limit values for each gene
    public static final int BORDER_MARGIN_MIN = 30;
    public static final int BORDER_MARGIN_MAX = 150;

    public static final int SAFE_DISTANCE_MIN = 40;
    public static final int SAFE_DISTANCE_MAX = 200;

    public static final int VELOCITY_MIN = 0;
    public static final int VELOCITY_MAX = 20;

}
