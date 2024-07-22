package autobot._old_versions.v1_lpo_e_ga.final_version.genetic.genes;

import static autobot._old_versions.v1_lpo_e_ga.final_version.utils.Consts.VELOCITY_MAX;
import static autobot._old_versions.v1_lpo_e_ga.final_version.utils.Consts.VELOCITY_MIN;

public class Velocity extends Gene {

    private static final int MIN_VALUE = VELOCITY_MIN;
    private static final int MAX_VALUE = VELOCITY_MAX;

    public Velocity() {
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
        super.mutate();
    }

    public Velocity(int value) {
        super.minValue = MIN_VALUE;
        super.maxValue = MAX_VALUE;
        super.value = value;
    }

    public Velocity(Velocity copy) {
        super.copy(copy);
    }

    @Override
    public Velocity copy() {
        return new Velocity(this);
    }

}
