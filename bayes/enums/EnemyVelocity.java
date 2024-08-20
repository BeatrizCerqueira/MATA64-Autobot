package autobot.bayes.enums;

public enum EnemyVelocity implements GenericAttribute {
    RANGE_0_2(0, 3),
    RANGE_2_4(3, 5),
    RANGE_6_8(5, 8);

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
