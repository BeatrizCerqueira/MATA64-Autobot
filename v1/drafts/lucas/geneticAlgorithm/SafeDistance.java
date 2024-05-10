package autobot.v1.drafts.lucas.geneticAlgorithm;

public class SafeDistance extends Gene {

    // TODO: Mover para consts
    private static final int MIN_VALUE = 40;
    private static final int MAX_VALUE = 1000;

    public SafeDistance() {
        super();
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
    }

    public SafeDistance(int value) {
        super(value);
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
    }

    public SafeDistance(SafeDistance copy) {
        super(copy);
    }
}
