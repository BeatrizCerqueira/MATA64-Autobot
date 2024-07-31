package autobot.bayes.enums;

public enum EnemyVelocity implements GenericAttribute {
    RANGE_0_2(0, 2),
    RANGE_2_4(2, 4),
    RANGE_4_6(4, 6),
    RANGE_6_8(6, 8);

    private final double min;
    private final double max;

    EnemyVelocity(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static EnemyVelocity fromDouble(double velocity) {
        for (EnemyVelocity ev : values()) {
            if (velocity >= ev.min && velocity <= ev.max) {
                return ev;
            }
        }
        throw new IllegalArgumentException("Velocity out of range: " + velocity);
    }


}
