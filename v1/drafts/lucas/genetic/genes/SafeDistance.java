package autobot.v1.drafts.lucas.genetic.genes;

import autobot.v1.drafts.lucas.genetic.Gene;

public class SafeDistance extends Gene {

    // TODO: Mover para consts
    private static final int MIN_VALUE = 40;
    private static final int MAX_VALUE = 1000;

    public SafeDistance() {
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
        super.mutate();
    }

    public SafeDistance(int value) {
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
        super.value = value;
    }

    public SafeDistance(SafeDistance copy) {
        super(copy);
    }
}
