package autobot.v1.drafts.lucas.genetic.genes;

import static autobot.v1.drafts.lucas.auxy.Consts.BORDER_MARGIN_MAX;
import static autobot.v1.drafts.lucas.auxy.Consts.BORDER_MARGIN_MIN;

public class BordersMargin extends Gene {

    private static final int MIN_VALUE = BORDER_MARGIN_MIN;
    private static final int MAX_VALUE = BORDER_MARGIN_MAX;

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
