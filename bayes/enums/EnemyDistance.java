package autobot.bayes.enums;

public enum EnemyDistance implements GenericAttribute {
    RANGE_0_200(0, 200),
    RANGE_200_400(200, 400),
//    RANGE_400_700(400, 700),
//    RANGE_700_1000(700, 1000)
    ;

    private final double min;
    private final double max;

    EnemyDistance(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static EnemyDistance fromDouble(double distance) {
        for (EnemyDistance ed : values()) {
            if (distance >= ed.min && distance <= ed.max) {
                return ed;
            }
        }
        throw new IllegalArgumentException("Distance out of range: " + distance);
    }
}
