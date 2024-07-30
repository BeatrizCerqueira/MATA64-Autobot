package autobot.bayes.enums;

public enum EnemyAngle implements GenericAttribute {
    RANGE_0_45(0, 45),
    RANGE_45_90(45, 90),
    RANGE_90_135(90, 135),
    RANGE_135_180(135, 180),
    RANGE_180_225(180, 225),
    RANGE_225_270(225, 270),
    RANGE_270_315(270, 315),
    RANGE_315_360(315, 360);

    private final double min;
    private final double max;

    EnemyAngle(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static EnemyAngle fromDouble(double angle) {
        for (EnemyAngle ea : values()) {
            if (angle >= ea.min && angle < ea.max) {
                return ea;
            }
        }
        throw new IllegalArgumentException("Angle out of range: " + angle);
    }
}
