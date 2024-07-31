package autobot.bayes.enums;

public enum EnemyHeading implements GenericAttribute {
    RANGE_0_90(0, 90),
    RANGE_90_180(90, 180),
//    RANGE_180_270(180, 270),
//    RANGE_270_360(270, 360)
    ;

    private final double min;
    private final double max;

    EnemyHeading(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static EnemyHeading fromDouble(double heading) {
        for (EnemyHeading eh : values()) {
            if (heading >= eh.min && heading <= eh.max) {
                return eh;
            }
        }
        throw new IllegalArgumentException("Heading out of range: " + heading);
    }
}
