package autobot.entities

import autobot.utils.Consts.RADAR_COVERAGE_DIST
import autobot.utils.MathUtils.Companion.random
import robocode.AdvancedRobot
import robocode.HitByBulletEvent
import robocode.HitWallEvent
import robocode.Rules.RADAR_TURN_RATE
import robocode.ScannedRobotEvent
import robocode.util.Utils.normalRelativeAngleDegrees
import java.lang.Math.*

class GeraRobot(
        val aleatorio: Int
) : AdvancedRobot() {
    override fun run() {
        while (true) {
            ahead(100.0)
            turnGunRight(360.0)
            back(100.0)
            turnGunRight(360.0)
        }
    }

    override fun onScannedRobot(event: ScannedRobotEvent) {
        val enemyAngle = heading + event.bearing

        val radarInitialTurn = normalRelativeAngleDegrees(enemyAngle - heading)
        val extraRadarTurn = toDegrees(atan(RADAR_COVERAGE_DIST / event.distance))

        val radarTotalTurn = radarInitialTurn + (extraRadarTurn * signum(radarInitialTurn))

        val normalizedRadarTotalTurn = normalRelativeAngleDegrees(radarTotalTurn)
        val radarTurn = (min(abs(normalizedRadarTotalTurn), RADAR_TURN_RATE) * signum(normalizedRadarTotalTurn))

        setTurnRadarRight(radarTurn)
    }

    override fun onHitByBullet(event: HitByBulletEvent) {
        val headTurn = random(30.0, 90.0) * Math.signum(random(-1.0, 1.0))
        out.println("HIT! Turn $headTurn")
        turnRight(headTurn)
        ahead(40.0)
    }

    override fun onHitWall(event: HitWallEvent) {
        turnRight(random(30.0, 90.0) * Math.signum(random(-1.0, 1.0)))
    }

}
