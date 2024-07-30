package autobot.bayes.enums;

public enum MyGunToEnemyAngle implements GenericAttribute {
    RANGE_0_20(0, 20),
    RANGE_20_40(20, 40),
    RANGE_40_60(40, 60),
    RANGE_60_80(60, 80),
    RANGE_80_100(80, 100),
    RANGE_100_120(100, 120),
    RANGE_120_140(120, 140),
    RANGE_140_160(140, 160),
    RANGE_160_180(160, 180);

    private final double min;
    private final double max;

    MyGunToEnemyAngle(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static MyGunToEnemyAngle fromDouble(double angle) {
        for (MyGunToEnemyAngle mgtea : values()) {
            if (angle >= mgtea.min && angle < mgtea.max) {
                return mgtea;
            }
        }
        throw new IllegalArgumentException("Angle out of range: " + angle);
    }
}
