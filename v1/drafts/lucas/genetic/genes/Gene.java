package autobot.v1.drafts.lucas.genetic.genes;

import java.io.Serializable;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

public abstract class Gene implements Serializable {
    // TODO: Mover para consts
    private static final double MUTATION_RATE = 0.05;

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
        if (randomNum < (MUTATION_RATE * 100)) {
            mutate();
        }
    }

    public int getValue() {
        return this.value;
    }

}
