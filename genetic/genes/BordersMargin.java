package autobot.genetic.genes;

import autobot.aux.Consts;

public class BordersMargin extends Gene {

    private static final int MIN_VALUE = Consts.BORDER_MARGIN_MIN;
    private static final int MAX_VALUE = Consts.BORDER_MARGIN_MAX;

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
