package autobot.bayes.enums;

public enum MyGunToEnemyAngle implements GenericAttribute {
    RANGE_0_15(0, 15),
    RANGE_15_30(15, 30),
//    RANGE_30_45(30, 45),
//    RANGE_45_60(45, 60),
//    RANGE_60_90(60, 90),
//    MORE_THAN_90(90, 180)
    ;

    private final double min;
    private final double max;

    MyGunToEnemyAngle(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static MyGunToEnemyAngle fromDouble(double angle) {
        for (MyGunToEnemyAngle mgtea : values()) {
            if (angle >= mgtea.min && angle <= mgtea.max) {
                return mgtea;
            }
        }
        throw new IllegalArgumentException("Angle out of range: " + angle);
    }
}
