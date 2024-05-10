package autobot.v1.drafts.lucas.genetic;

import java.io.Serializable;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

public class Gene implements Serializable {
    // TODO: Mover para consts
    private static final double MUTATION_RATE = 0.05;

    protected int value;
    protected int minValue;
    protected int maxValue;

    public Gene() {
        mutate();
    }

    public Gene(int value) {
        this.value = value;
    }

    public Gene(Gene copy) {
        this.value = copy.value;
        this.minValue = copy.value;
        this.maxValue = copy.value;
    }

    public int getValue() {
        return this.value;
    }

    private void mutate() {
        this.value = random(minValue, maxValue);
    }

    public void randomlyMutate() {
        int randomNum = random(0, 100);
        if (randomNum < (MUTATION_RATE * 100)) {
            mutate();
        }
    }

}
