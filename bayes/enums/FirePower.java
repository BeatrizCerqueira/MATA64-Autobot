package autobot.bayes.enums;

public enum FirePower implements GenericAttribute {
    FP_02(0.2),
    FP_04(0.4),
    FP_06(0.6),
    FP_08(0.8),
    FP_10(1.0),
    FP_12(1.2),
    FP_14(1.4),
    FP_16(1.6),
    FP_18(1.8),
    FP_20(2.0),
    FP_22(2.2),
    FP_24(2.4),
    FP_26(2.6),
    FP_28(2.8),
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
