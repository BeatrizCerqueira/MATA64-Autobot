package autobot.records;

import autobot.Enemy;

public record BulletResult(Enemy enemySnapshot, boolean hasHit) {
    public double getEnemyDistance() {
        return enemySnapshot.getDistance();
    }

    public double getEnemyVelocity() {
        return enemySnapshot.getVelocity();
    }

    public double getEnemyAngle() {
        return enemySnapshot.getAngle();
    }

    public double getEnemyHeading() {
        return enemySnapshot.getHeading();
    }

    public double getMyGunToEnemyAngle() {
        return 10.0; // TODO: @Bia parametro myGunToEnemyAngle sem valor/referencia
    }

    public double getFirePower() {
        return 0.6; // TODO: @Bia parametro firePower sem valor/referencia
    }
}
