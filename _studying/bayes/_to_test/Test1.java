package autobot._studying.bayes._to_test;

import autobot._studying.bayes.JayesWrapper;
import autobot._studying.bayes.enums.*;
import autobot._studying.bayes.records.EvidenceAttribute;

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
        List<EvidenceAttribute> evidenceAttributes = new ArrayList<>();

        evidenceAttributes.add(new EvidenceAttribute("EnemyDistance", EnemyDistance.RANGE_0_100.toString()));
        evidenceAttributes.add(new EvidenceAttribute("EnemyVelocity", EnemyVelocity.RANGE_0_1.toString()));
        evidenceAttributes.add(new EvidenceAttribute("EnemyAngle", EnemyAngle.RANGE_0_45.toString()));
        evidenceAttributes.add(new EvidenceAttribute("EnemyHeading", EnemyHeading.RANGE_0_45.toString()));
        evidenceAttributes.add(new EvidenceAttribute("MyGunToEnemyAngle", MyGunToEnemyAngle.RANGE_0_20.toString()));
        evidenceAttributes.add(new EvidenceAttribute("FirePower", FirePower.FP_01.toString()));

        String nodeToGetBeliefs = "Hit";

        double[] hitBeliefs = jayes.getBeliefs(evidenceAttributes, nodeToGetBeliefs);

        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Inference <<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("Beliefs of hit enemy: " + Arrays.toString(hitBeliefs));

    }

    private void calcBestFirePowerToHit() {
        List<EvidenceAttribute> evidenceAttributes = new ArrayList<>();

        evidenceAttributes.add(new EvidenceAttribute("EnemyDistance", EnemyDistance.RANGE_0_100.toString()));
        evidenceAttributes.add(new EvidenceAttribute("EnemyVelocity", EnemyVelocity.RANGE_0_1.toString()));
        evidenceAttributes.add(new EvidenceAttribute("EnemyAngle", EnemyAngle.RANGE_0_45.toString()));
        evidenceAttributes.add(new EvidenceAttribute("EnemyHeading", EnemyHeading.RANGE_0_45.toString()));
        evidenceAttributes.add(new EvidenceAttribute("MyGunToEnemyAngle", MyGunToEnemyAngle.RANGE_0_20.toString()));
        evidenceAttributes.add(new EvidenceAttribute("Hit", Hit.TRUE.toString()));

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

    }

    @SuppressWarnings("unused")
    public void run() throws Exception {

        calcHitBeliefs();
        calcBestFirePowerToHit();

    }
}
