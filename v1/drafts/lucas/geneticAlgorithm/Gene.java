package autobot.v1.drafts.lucas.geneticAlgorithm;

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

    public void setValue(int val) {
        value = val;
    }

    public void mutate() {
        value = random(minValue, maxValue);
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    //getvalue


}
