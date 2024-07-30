package autobot._studying.bayes.enums;

public enum EnemyDistance implements GenericAttribute {
    RANGE_0_100(0, 100),
    RANGE_100_200(100, 200),
    RANGE_200_300(200, 300),
    RANGE_300_400(300, 400),
    RANGE_400_500(400, 500),
    RANGE_500_600(500, 600),
    RANGE_600_700(600, 700),
    RANGE_700_800(700, 800),
    RANGE_800_900(800, 900),
    RANGE_900_1000(900, 1000);

    private final double min;
    private final double max;

    EnemyDistance(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static EnemyDistance fromDouble(double distance) {
        for (EnemyDistance ed : values()) {
            if (distance >= ed.min && distance < ed.max) {
                return ed;
            }
        }
        throw new IllegalArgumentException("Distance out of range: " + distance);
    }
}
