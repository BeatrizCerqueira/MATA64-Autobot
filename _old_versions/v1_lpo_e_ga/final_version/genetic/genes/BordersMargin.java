package autobot._old_versions.v1_lpo_e_ga.final_version.genetic.genes;

import autobot._old_versions.v1_lpo_e_ga.final_version.utils.Consts;

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
