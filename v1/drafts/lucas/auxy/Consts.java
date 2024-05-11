package autobot.v1.drafts.lucas.auxy;

public class Consts {
    public static final double RADAR_COVERAGE_DIST = 15.0; // Distance we want to scan from middle of enemy to either side

    // Genetic Algorithm consts
    public static final int POPULATION_SIZE = 8;

    public static final double MUTATION_RATE = 0.05;
    private static final double ELITISM_RATE = 0;
    private static final double CROSSOVER_RATE = (1 - ELITISM_RATE);

    private static final double CROSSOVER_FIRST_PARENT_RATE = 0.4;
    private static final double CROSSOVER_SECOND_PARENT_RATE = (1 - CROSSOVER_FIRST_PARENT_RATE);

    public static final int ELITISM_COUNT = (int) (ELITISM_RATE * POPULATION_SIZE);
    public static final int CROSSOVER_COUNT = (int) (CROSSOVER_RATE * POPULATION_SIZE);
    public static final int CROSSOVER_FIRST_PARENT_COUNT = (int) (CROSSOVER_FIRST_PARENT_RATE * POPULATION_SIZE);
    public static final int CROSSOVER_SECOND_PARENT_COUNT = (int) (CROSSOVER_SECOND_PARENT_RATE * POPULATION_SIZE);

}
