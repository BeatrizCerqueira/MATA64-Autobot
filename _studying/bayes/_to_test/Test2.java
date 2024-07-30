package autobot._studying.bayes._to_test;

import autobot._studying.bayes.JayesWrapper;
import autobot._studying.bayes.enums.*;
import autobot._studying.bayes.records.EvidenceAttribute;
import autobot.utils.Consts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "DuplicatedCode"})
class Test2 {

    private final JayesWrapper jayes;

    public Test2(JayesWrapper jayes) {
        this.jayes = jayes;
    }

    private FirePower calcBestFirePowerToHit(EnemyDistance ed, EnemyVelocity ev, EnemyAngle ea, EnemyHeading eh, MyGunToEnemyAngle mgtea) {
        List<EvidenceAttribute> evidenceAttributes = new ArrayList<>();

        evidenceAttributes.add(new EvidenceAttribute("EnemyDistance", ed.toString()));
        evidenceAttributes.add(new EvidenceAttribute("EnemyVelocity", ev.toString()));
        evidenceAttributes.add(new EvidenceAttribute("EnemyAngle", ea.toString()));
        evidenceAttributes.add(new EvidenceAttribute("EnemyHeading", eh.toString()));
        evidenceAttributes.add(new EvidenceAttribute("MyGunToEnemyAngle", mgtea.toString()));
        evidenceAttributes.add(new EvidenceAttribute("Hit", Hit.fromBoolean(true).toString()));

        String nodeToGetBeliefs = "FirePower";

        List<Double> firePowerBeliefs = Arrays.stream(jayes.getBeliefs(evidenceAttributes, nodeToGetBeliefs)).boxed().toList();

        Double maxFirePowerBelieve = Collections.max(firePowerBeliefs);
        int maxFirePowerBelieveIndex = firePowerBeliefs.indexOf(maxFirePowerBelieve);
        FirePower bestFirePower = FirePower.values()[maxFirePowerBelieveIndex];

        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Inference <<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("Beliefs of fire power: " + firePowerBeliefs);
        System.out.println("Max fire power believe: " + maxFirePowerBelieve);
        System.out.println("Index of max fire power believe: " + maxFirePowerBelieveIndex);
        System.out.println("Best fire power to hit: " + bestFirePower);

        return bestFirePower;

    }

    private double calcBestFirePowerToHit(double enemyDistance, double enemyVelocity, double enemyAngle, double enemyHeading, double myGunToEnemyAngle) {
        EnemyDistance ed = EnemyDistance.fromDouble(enemyDistance);
        EnemyVelocity ev = EnemyVelocity.fromDouble(enemyVelocity);
        EnemyAngle ea = EnemyAngle.fromDouble(enemyAngle);
        EnemyHeading eh = EnemyHeading.fromDouble(enemyHeading);
        MyGunToEnemyAngle mgtea = MyGunToEnemyAngle.fromDouble(myGunToEnemyAngle);

        System.out.println("EnemyDistance: " + enemyDistance + " = " + ed);
        System.out.println("EnemyVelocity: " + enemyVelocity + " = " + ev);
        System.out.println("EnemyAngle: " + enemyAngle + " = " + ea);
        System.out.println("EnemyHeading: " + enemyHeading + " = " + eh);
        System.out.println("MyGunToEnemyAngle: " + myGunToEnemyAngle + " = " + mgtea);

        return calcBestFirePowerToHit(ed, ev, ea, eh, mgtea).toDouble();
    }

    private void setFire(double firePower) {
        System.out.println("Firing with power: " + firePower);
    }

    @SuppressWarnings("unused")
    public void run() throws Exception {

        double enemyDistance = 200.0;    // 0 to 1000 px       |   10 ranges
        double enemyVelocity = 5.0;      // 0 to 8 px/turn     |   8 ranges
        double enemyAngle = 45.0;        // 0 to 360 degrees   |   8 ranges   |  ourGetHeading() + enemy.getBearing()
        double enemyHeading = 45.0;      // 0 to 360 degrees   |   8 ranges
        double myGunToEnemyAngle = 45.0; // 0 to 180 degrees   |   9 ranges   |  abs(gunInitialTurn + gunTurnThatWasSet)

        // FirePower 0.1 a 3.0 (range de 0.1 em 0.1)

        // Other variables
        double myEnergy = 100.0;         // 0 to 100

        //noinspection ConstantValue
        if (myEnergy > Consts.MIN_LIFE_TO_FIRE) {
            double firePower = calcBestFirePowerToHit(enemyDistance, enemyVelocity, enemyAngle, enemyHeading, myGunToEnemyAngle);
            setFire(firePower);
        } else {
            System.out.println("Not enough energy to fire");
        }
    }

}
