package autobot.v1_lpo_e_ga.drafts.lucas.genetic.genes;

import static autobot.v1_lpo_e_ga.drafts.lucas.auxy.Consts.VELOCITY_MAX;
import static autobot.v1_lpo_e_ga.drafts.lucas.auxy.Consts.VELOCITY_MIN;

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
