package autobot.records;

import autobot.Enemy;

public record BulletResult(Enemy enemy, double myGunToEnemyAngle, double firePower, boolean hasHit) {
}
