package autobot.bayes.enums;

public enum FirePower implements GenericAttribute {
    FP_05(0.5),
    FP_10(1.0),
    FP_15(1.5),
    FP_20(2.0),
    FP_24(2.5),
    FP_30(3.0);

    private final double power;

    FirePower(double power) {
        this.power = power;
    }

    public static FirePower fromDouble(double power) {
        for (FirePower fp : values()) {
            if (power == fp.power) {
                return fp;
            }
        }
        throw new IllegalArgumentException("Power out of range: " + power);
    }

    public double toDouble() {
        return power;
    }

}
