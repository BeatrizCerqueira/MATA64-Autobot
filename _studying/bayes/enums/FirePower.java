package autobot._studying.bayes.enums;

public enum FirePower {
    FP_01(0.1),
    FP_02(0.2),
    FP_03(0.3),
    FP_04(0.4),
    FP_05(0.5),
    FP_06(0.6),
    FP_07(0.7),
    FP_08(0.8),
    FP_09(0.9),
    FP_10(1.0),
    FP_11(1.1),
    FP_12(1.2),
    FP_13(1.3),
    FP_14(1.4),
    FP_15(1.5),
    FP_16(1.6),
    FP_17(1.7),
    FP_18(1.8),
    FP_19(1.9),
    FP_20(2.0),
    FP_21(2.1),
    FP_22(2.2),
    FP_23(2.3),
    FP_24(2.4),
    FP_25(2.5),
    FP_26(2.6),
    FP_27(2.7),
    FP_28(2.8),
    FP_29(2.9),
    FP_30(3.0);

    private final double power;

    FirePower(double power) {
        this.power = power;
    }

    public double toDouble() {
        return power;
    }

}
