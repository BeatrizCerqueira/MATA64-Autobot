package autobot.utils

class MathUtils {
    companion object {
        fun random(min: Double, max: Double): Double {
            return min + Math.random() * (max - min + 1)
        }
    }
}
