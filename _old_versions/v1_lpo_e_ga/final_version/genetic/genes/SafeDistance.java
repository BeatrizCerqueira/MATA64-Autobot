package autobot._old_versions.v1_lpo_e_ga.final_version.genetic.genes;

import static autobot._old_versions.v1_lpo_e_ga.final_version.utils.Consts.SAFE_DISTANCE_MAX;
import static autobot._old_versions.v1_lpo_e_ga.final_version.utils.Consts.SAFE_DISTANCE_MIN;

public class SafeDistance extends Gene {

    private static final int MIN_VALUE = SAFE_DISTANCE_MIN;
    private static final int MAX_VALUE = SAFE_DISTANCE_MAX;

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
        super.copy(copy);
    }

    @Override
    public SafeDistance copy() {
        return new SafeDistance(this);
    }
}
