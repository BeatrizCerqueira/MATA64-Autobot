package autobot.v1.drafts.lucas.genetic.genes;

import autobot.v1.drafts.lucas.genetic.Gene;

public class Velocity extends Gene {

    // TODO: Mover para consts
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 20;

    public Velocity() {
        super();
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
    }

    public Velocity(int value) {
        super(value);
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
    }

    public Velocity(Velocity copy) {
        super(copy);
    }

}
