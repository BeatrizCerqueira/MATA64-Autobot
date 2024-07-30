package autobot._studying.bayes;

import autobot._studying.bayes.enums.*;
import autobot._studying.bayes.records.EvidenceAttribute;
import autobot.records.BulletResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InternalBayesianNetwork {

    private static WekaWrapper weka;
    private static JayesWrapper jayes;
    private static List<InternalBayesNode> nodes;

    public static void init() throws Exception {
        initInternalNodes();
        initWeka();
        initJayes();

    }

    private static void initWeka() throws Exception {
        weka = new WekaWrapper(nodes);
    }

    private static void initJayes() {
        jayes = new JayesWrapper(nodes);
    }

    private static void initInternalNodes() {
        List<InternalBayesNode> internalNodes = new ArrayList<>();

        InternalBayesNode enemyDistance = new InternalBayesNode("EnemyDistance", EnemyDistance.class, new ArrayList<>());
        InternalBayesNode enemyVelocity = new InternalBayesNode("EnemyVelocity", EnemyVelocity.class, new ArrayList<>());
        InternalBayesNode enemyAngle = new InternalBayesNode("EnemyAngle", EnemyAngle.class, new ArrayList<>());
        InternalBayesNode enemyHeading = new InternalBayesNode("EnemyHeading", EnemyHeading.class, new ArrayList<>());
        InternalBayesNode myGunToEnemyAngle = new InternalBayesNode("MyGunToEnemyAngle", MyGunToEnemyAngle.class, new ArrayList<>());
        InternalBayesNode firePower = new InternalBayesNode("FirePower", FirePower.class, new ArrayList<>());
        InternalBayesNode hit = new InternalBayesNode("Hit", Hit.class, Arrays.asList("EnemyDistance", "EnemyVelocity", "EnemyAngle", "EnemyHeading", "MyGunToEnemyAngle", "FirePower"));

        internalNodes.add(enemyDistance);
        internalNodes.add(enemyVelocity);
        internalNodes.add(enemyAngle);
        internalNodes.add(enemyHeading);
        internalNodes.add(myGunToEnemyAngle);
        internalNodes.add(firePower);
        internalNodes.add(hit);

        nodes = internalNodes;
    }

    private static FirePower calcBestFirePowerToHit(EnemyDistance ed, EnemyVelocity ev, EnemyAngle ea, EnemyHeading eh, MyGunToEnemyAngle mgtea) {
        List<EvidenceAttribute> evidence = new ArrayList<>();

        evidence.add(new EvidenceAttribute("EnemyDistance", ed.toString()));
        evidence.add(new EvidenceAttribute("EnemyVelocity", ev.toString()));
        evidence.add(new EvidenceAttribute("EnemyAngle", ea.toString()));
        evidence.add(new EvidenceAttribute("EnemyHeading", eh.toString()));
        evidence.add(new EvidenceAttribute("MyGunToEnemyAngle", mgtea.toString()));
        evidence.add(new EvidenceAttribute("Hit", Hit.fromBoolean(true).toString()));

        String nodeToGetBeliefs = "FirePower";

        List<Double> firePowerBeliefs = Arrays.stream(jayes.getBeliefs(evidence, nodeToGetBeliefs)).boxed().toList();

        Double fpMaxBelieve = Collections.max(firePowerBeliefs);
        int fpMaxBelieveIndex = firePowerBeliefs.indexOf(fpMaxBelieve);
        FirePower bestFirePower = FirePower.values()[fpMaxBelieveIndex];

//        printFirePowerInference(firePowerBeliefs, maxFirePowerBelieve, maxFirePowerBelieveIndex, bestFirePower);

        return bestFirePower;

    }

    public static void recordBulletResult(BulletResult bulletResult) throws Exception {
//        weka.addInstance();
        weka.calcNewDistributions();
        jayes.setNewProbabilities();
    }

    public static double getBestFirePowerToHit(double enemyDistance, double enemyVelocity, double enemyAngle, double enemyHeading, double myGunToEnemyAngle) {
        EnemyDistance ed = EnemyDistance.fromDouble(enemyDistance);
        EnemyVelocity ev = EnemyVelocity.fromDouble(enemyVelocity);
        EnemyAngle ea = EnemyAngle.fromDouble(enemyAngle);
        EnemyHeading eh = EnemyHeading.fromDouble(enemyHeading);
        MyGunToEnemyAngle mgtea = MyGunToEnemyAngle.fromDouble(myGunToEnemyAngle);

//        printReceivedParams(enemyDistance, enemyVelocity, enemyAngle, enemyHeading, myGunToEnemyAngle, ed, ev, ea, eh, mgtea);

        return calcBestFirePowerToHit(ed, ev, ea, eh, mgtea).toDouble();
    }

    @SuppressWarnings("unused")
    private static void printFirePowerInference(List<Double> firePowerBeliefs, Double maxFirePowerBelieve, int maxFirePowerBelieveIndex, FirePower bestFirePower) {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Inference <<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("Beliefs of fire power: " + firePowerBeliefs);
        System.out.println("Max fire power believe: " + maxFirePowerBelieve);
        System.out.println("Index of max fire power believe: " + maxFirePowerBelieveIndex);
        System.out.println("Best fire power to hit: " + bestFirePower);
    }

    @SuppressWarnings("unused")
    private static void printReceivedParams(double enemyDistance, double enemyVelocity, double enemyAngle, double enemyHeading, double myGunToEnemyAngle, EnemyDistance ed, EnemyVelocity ev, EnemyAngle ea, EnemyHeading eh, MyGunToEnemyAngle mgtea) {
        System.out.println("EnemyDistance: " + enemyDistance + " = " + ed);
        System.out.println("EnemyVelocity: " + enemyVelocity + " = " + ev);
        System.out.println("EnemyAngle: " + enemyAngle + " = " + ea);
        System.out.println("EnemyHeading: " + enemyHeading + " = " + eh);
        System.out.println("MyGunToEnemyAngle: " + myGunToEnemyAngle + " = " + mgtea);
    }


}
