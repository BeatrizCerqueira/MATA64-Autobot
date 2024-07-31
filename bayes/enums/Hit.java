package autobot.bayes.enums;

public enum Hit implements GenericAttribute {
    FALSE(false),
    TRUE(true);

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
