package autobot.bayes;

import autobot.bayes.enums.*;
import autobot.bayes.records.EvidenceAttribute;
import autobot.records.BulletResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Bayes {

    public static WekaWrapper weka;
    public static JayesWrapper jayes;
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

    public static FirePower calcBestFirePowerToHit(EnemyDistance ed, EnemyVelocity ev, EnemyAngle ea, EnemyHeading eh, MyGunToEnemyAngle mgtea) {
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

        printFirePowerInference(firePowerBeliefs, fpMaxBelieve, fpMaxBelieveIndex, FirePower.values()[fpMaxBelieveIndex]);

        return FirePower.values()[fpMaxBelieveIndex];

    }

    // TODO: Create a record for this
    public static double getBestFirePowerToHit(double enemyDistance, double enemyVelocity, double enemyAngle, double enemyHeading, double myGunToEnemyAngle) {
        EnemyDistance ed = EnemyDistance.fromDouble(Math.abs(enemyDistance));
        EnemyVelocity ev = EnemyVelocity.fromDouble(Math.abs(enemyVelocity));
        EnemyAngle ea = EnemyAngle.fromDouble(Math.abs(enemyAngle));
        EnemyHeading eh = EnemyHeading.fromDouble(Math.abs(enemyHeading));
        MyGunToEnemyAngle mgtea = MyGunToEnemyAngle.fromDouble(Math.abs(myGunToEnemyAngle));

//        printGetParams(enemyDistance, enemyVelocity, enemyAngle, enemyHeading, myGunToEnemyAngle, ed, ev, ea, eh, mgtea);

        return calcBestFirePowerToHit(ed, ev, ea, eh, mgtea).toDouble();
    }

    public static void recordBulletResult(BulletResult bulletResult) throws Exception {

        EnemyDistance ed = EnemyDistance.fromDouble(Math.abs(bulletResult.enemy().getDistance()));
        EnemyVelocity ev = EnemyVelocity.fromDouble(Math.abs(bulletResult.enemy().getVelocity()));
        EnemyAngle ea = EnemyAngle.fromDouble(Math.abs(bulletResult.enemy().getAngle()));
        EnemyHeading eh = EnemyHeading.fromDouble(Math.abs(bulletResult.enemy().getHeading()));
        MyGunToEnemyAngle mgtea = MyGunToEnemyAngle.fromDouble(Math.abs(bulletResult.myGunToEnemyAngle()));
        FirePower fp = FirePower.fromDouble(Math.abs(bulletResult.firePower()));
        Hit hit = Hit.fromBoolean(bulletResult.hasHit());

        List<GenericAttribute> instance = Arrays.asList(ed, ev, ea, eh, mgtea, fp, hit);

//        printSetParams(bulletResult.getEnemyDistance(), bulletResult.getEnemyVelocity(), bulletResult.getEnemyAngle(), bulletResult.getEnemyHeading(), bulletResult.getMyGunToEnemyAngle(), bulletResult.getFirePower(), bulletResult.hasHit(), ed, ev, ea, eh, mgtea, fp, hit);

        weka.addInstance(instance);
        weka.calcNewDistributions();
        jayes.setNewProbabilities();
    }

    public static void saveDataForNextRound() throws IOException {
        weka.saveDatasetFile();
    }

    @SuppressWarnings("unused")
    private static void printFirePowerInference(List<Double> firePowerBeliefs, Double maxFirePowerBelieve, int maxFirePowerBelieveIndex, FirePower bestFirePower) {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Inference <<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("Beliefs of fire power: " + firePowerBeliefs);
        System.out.println("Fire power max believe: " + maxFirePowerBelieve);
        System.out.println("Index of fire power max believe: " + maxFirePowerBelieveIndex);
        System.out.println("Best fire power to hit: " + bestFirePower);
    }

    @SuppressWarnings({"unused", "DuplicatedCode"})
    private static void printGetParams(double enemyDistance, double enemyVelocity, double enemyAngle, double enemyHeading, double myGunToEnemyAngle, EnemyDistance ed, EnemyVelocity ev, EnemyAngle ea, EnemyHeading eh, MyGunToEnemyAngle mgtea) {
        System.out.println("EnemyDistance: " + enemyDistance + " = " + ed);
        System.out.println("EnemyVelocity: " + enemyVelocity + " = " + ev);
        System.out.println("EnemyAngle: " + enemyAngle + " = " + ea);
        System.out.println("EnemyHeading: " + enemyHeading + " = " + eh);
        System.out.println("MyGunToEnemyAngle: " + myGunToEnemyAngle + " = " + mgtea);
    }

    @SuppressWarnings("unused")
    private static void printSetParams(double enemyDistance, double enemyVelocity, double enemyAngle, double enemyHeading, double myGunToEnemyAngle, double firePower, boolean hit, EnemyDistance ed, EnemyVelocity ev, EnemyAngle ea, EnemyHeading eh, MyGunToEnemyAngle mgtea, FirePower fp, Hit h) {
        //noinspection DuplicatedCode
        System.out.println("EnemyDistance: " + enemyDistance + " = " + ed);
        System.out.println("EnemyVelocity: " + enemyVelocity + " = " + ev);
        System.out.println("EnemyAngle: " + enemyAngle + " = " + ea);
        System.out.println("EnemyHeading: " + enemyHeading + " = " + eh);
        System.out.println("MyGunToEnemyAngle: " + myGunToEnemyAngle + " = " + mgtea);
        System.out.println("FirePower: " + firePower + " = " + fp);
        System.out.println("Hit: " + hit + " = " + h);
    }

}
