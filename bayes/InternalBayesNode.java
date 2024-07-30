package autobot.bayes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InternalBayesNode {
    private final String name;
    private final List<String> values;
    private final List<String> parents;
    private double[][] distribution;

    public InternalBayesNode(String name, Class<? extends Enum<?>> enumClass, List<String> parents) {
        this.name = name;
        this.values = Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.toList());
        this.parents = parents;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public List<String> getParents() {
        return parents;
    }

    public double[] getFlattenedDistribution() {
        return Arrays.stream(distribution).flatMapToDouble(Arrays::stream).toArray();
    }

    public double[][] getDistribution() {
        return distribution;
    }

    public void setDistribution(double[][] distribution) {
        this.distribution = distribution;
    }

}
