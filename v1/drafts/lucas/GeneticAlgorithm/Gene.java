package autobot.v1.drafts.lucas.GeneticAlgorithm;

import java.io.Serializable;

import static autobot.v1.drafts.lucas.auxy.MathUtils.random;

public class Gene implements Serializable {
    String description;
    int minValue;
    int maxValue;
    int value;

    public Gene(String description, int minValue, int maxValue) {
        this.description = description;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public boolean isValid(int value) {
        return (value >= minValue) && (value <= maxValue);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void mutate() {
        this.value = random(minValue, maxValue);
    }


}
