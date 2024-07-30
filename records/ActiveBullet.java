package autobot.records;

import autobot.Enemy;
import robocode.Bullet;

public record ActiveBullet(Bullet bulletInstance, Enemy enemySnapshot) {
}
