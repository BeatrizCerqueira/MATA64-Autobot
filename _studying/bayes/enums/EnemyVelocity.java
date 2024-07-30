package autobot._studying.bayes.enums;

public enum EnemyVelocity implements GenericAttribute {
    RANGE_0_1(0, 1),
    RANGE_1_2(1, 2),
    RANGE_2_3(2, 3),
    RANGE_3_4(3, 4),
    RANGE_4_5(4, 5),
    RANGE_5_6(5, 6),
    RANGE_6_7(6, 7),
    RANGE_7_8(7, 8);


    private final double min;
    private final double max;

    EnemyVelocity(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static EnemyVelocity fromDouble(double velocity) {
        for (EnemyVelocity ev : values()) {
            if (velocity >= ev.min && velocity < ev.max) {
                return ev;
            }
        }
        throw new IllegalArgumentException("Velocity out of range: " + velocity);
    }


}
