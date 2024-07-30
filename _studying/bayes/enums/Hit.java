package autobot._studying.bayes.enums;

public enum Hit {
    TRUE(true),
    FALSE(false);

    private final boolean hit;

    Hit(boolean hit) {
        this.hit = hit;
    }

    public static Hit fromBoolean(boolean hit) {
        for (Hit h : values()) {
            if (hit == h.hit) {
                return h;
            }
        }
        throw new IllegalArgumentException("Hit out of range: " + hit);
    }
}
