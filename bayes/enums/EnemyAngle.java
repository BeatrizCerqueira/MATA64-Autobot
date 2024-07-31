package autobot.bayes.enums;

public enum EnemyAngle implements GenericAttribute {
    RANGE_0_90(0, 90),
    RANGE_90_180(90, 180),
//    RANGE_180_270(180, 270),
//    RANGE_270_360(270, 360)
    ;

    private final double min;
    private final double max;

    EnemyAngle(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static EnemyAngle fromDouble(double angle) {
        for (EnemyAngle ea : values()) {
            if (angle >= ea.min && angle <= ea.max) {
                return ea;
            }
        }
        throw new IllegalArgumentException("Angle out of range: " + angle);
    }
}
