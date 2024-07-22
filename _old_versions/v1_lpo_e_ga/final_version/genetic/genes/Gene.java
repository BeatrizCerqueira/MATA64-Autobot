package autobot._old_versions.v1_lpo_e_ga.final_version.genetic.genes;

import java.io.Serializable;

import static autobot._old_versions.v1_lpo_e_ga.final_version.utils.Consts.MUTATION_PROBABILITY;
import static autobot._old_versions.v1_lpo_e_ga.final_version.utils.MathUtils.random;

public abstract class Gene implements Serializable {
    int value;
    int minValue;
    int maxValue;

    public abstract Gene copy();

    void copy(Gene copy) {
        this.value = copy.value;
        this.minValue = copy.minValue;
        this.maxValue = copy.maxValue;
    }

    void mutate() {
        this.value = random(minValue, maxValue);
    }

    public void randomlyMutate() {
        int randomNum = random(0, 100);
        if (randomNum < (MUTATION_PROBABILITY * 100)) {
            mutate();
        }
    }

    public int getValue() {
        return this.value;
    }

}
