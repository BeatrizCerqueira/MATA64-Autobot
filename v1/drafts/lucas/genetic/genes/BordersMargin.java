package autobot.v1.drafts.lucas.genetic.genes;

public class BordersMargin extends Gene {

    // TODO: Mover para consts
    private static final int MIN_VALUE = 30;
    private static final int MAX_VALUE = 100;

    public BordersMargin() {
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
        super.mutate();
    }

    public BordersMargin(int value) {
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
        super.value = value;
    }

    public BordersMargin(BordersMargin copy) {
        super.copy(copy);
    }

    @Override
    public BordersMargin copy() {
        return new BordersMargin(this);
    }

}
