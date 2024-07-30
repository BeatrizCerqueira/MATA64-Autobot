package autobot._studying.bayes._to_test;

import autobot._studying.bayes.JayesWrapper;
import autobot._studying.bayes.enums.*;
import autobot._studying.bayes.records.InternalEvidence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "DuplicatedCode"})
class Test1 {
    private final JayesWrapper jayes;

    public Test1(JayesWrapper jayes) {
        this.jayes = jayes;
    }

    private void calcHitBeliefs() {
        List<InternalEvidence> internalEvidences = new ArrayList<>();

        internalEvidences.add(new InternalEvidence("EnemyDistance", EnemyDistance.RANGE_0_100.toString()));
        internalEvidences.add(new InternalEvidence("EnemyVelocity", EnemyVelocity.RANGE_0_1.toString()));
        internalEvidences.add(new InternalEvidence("EnemyAngle", EnemyAngle.RANGE_0_45.toString()));
        internalEvidences.add(new InternalEvidence("EnemyHeading", EnemyHeading.RANGE_0_45.toString()));
        internalEvidences.add(new InternalEvidence("MyGunToEnemyAngle", MyGunToEnemyAngle.RANGE_0_20.toString()));
        internalEvidences.add(new InternalEvidence("FirePower", FirePower.FP_01.toString()));

        String nodeToGetBeliefs = "Hit";

        double[] hitBeliefs = jayes.getBeliefs(internalEvidences, nodeToGetBeliefs);

        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Inference <<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("Beliefs of hit enemy: " + Arrays.toString(hitBeliefs));

    }

    private void calcBestFirePowerToHit() {
        List<InternalEvidence> internalEvidences = new ArrayList<>();

        internalEvidences.add(new InternalEvidence("EnemyDistance", EnemyDistance.RANGE_0_100.toString()));
        internalEvidences.add(new InternalEvidence("EnemyVelocity", EnemyVelocity.RANGE_0_1.toString()));
        internalEvidences.add(new InternalEvidence("EnemyAngle", EnemyAngle.RANGE_0_45.toString()));
        internalEvidences.add(new InternalEvidence("EnemyHeading", EnemyHeading.RANGE_0_45.toString()));
        internalEvidences.add(new InternalEvidence("MyGunToEnemyAngle", MyGunToEnemyAngle.RANGE_0_20.toString()));
        internalEvidences.add(new InternalEvidence("Hit", Hit.TRUE.toString()));

        String nodeToGetBeliefs = "FirePower";

        List<Double> firePowerBeliefs = Arrays.stream(jayes.getBeliefs(internalEvidences, nodeToGetBeliefs)).boxed().toList();

        Double maxFirePowerBelieve = Collections.max(firePowerBeliefs);
        int maxFirePowerBelieveIndex = firePowerBeliefs.indexOf(maxFirePowerBelieve);
        FirePower bestFirePower = FirePower.values()[maxFirePowerBelieveIndex];

        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Inference <<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("Beliefs of fire power: " + firePowerBeliefs);
        System.out.println("Max fire power believe: " + maxFirePowerBelieve);
        System.out.println("Index of max fire power believe: " + maxFirePowerBelieveIndex);
        System.out.println("Best fire power to hit: " + bestFirePower);

    }

    @SuppressWarnings("unused")
    public void run() throws Exception {

        calcHitBeliefs();
        calcBestFirePowerToHit();

    }
}
