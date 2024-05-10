package autobot.v1.drafts.lucas.genetic.genes;

import autobot.v1.drafts.lucas.genetic.Gene;

public class BordersMargin extends Gene {

    // TODO: Mover para consts
    private static final int MIN_VALUE = 10;
    private static final int MAX_VALUE = 300;

    public BordersMargin() {
        super();
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
    }

    public BordersMargin(int value) {
        super(value);
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
    }

    public BordersMargin(BordersMargin copy) {
        super(copy);
    }

}
