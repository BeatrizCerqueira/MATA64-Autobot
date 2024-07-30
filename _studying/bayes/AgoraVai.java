package autobot._studying.bayes;

import autobot.utils.Consts;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;
import org.eclipse.recommenders.jayes.util.MathUtils;
import weka.classifiers.bayes.net.EditableBayesNet;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.gui.graphvisualizer.GraphVisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

enum EnemyDistance {
    RANGE_0_100(0, 100),
    RANGE_100_200(100, 200),
    RANGE_200_300(200, 300),
    RANGE_300_400(300, 400),
    RANGE_400_500(400, 500),
    RANGE_500_600(500, 600),
    RANGE_600_700(600, 700),
    RANGE_700_800(700, 800),
    RANGE_800_900(800, 900),
    RANGE_900_1000(900, 1000);

    private final double min;
    private final double max;

    EnemyDistance(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static EnemyDistance fromDouble(double distance) {
        for (EnemyDistance ed : values()) {
            if (distance >= ed.min && distance < ed.max) {
                return ed;
            }
        }
        throw new IllegalArgumentException("Distance out of range: " + distance);
    }
}

enum EnemyVelocity {
    RANGE_0_1(0, 1),
    RANGE_1_2(1, 2),
    RANGE_2_3(2, 3),
    RANGE_3_4(3, 4),
    RANGE_4_5(4, 5),
    RANGE_5_6(5, 6),
    RANGE_6_7(6, 7),
    RANGE_7_8(7, 8);


    private final double min;
    private final double max;

    EnemyVelocity(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static EnemyVelocity fromDouble(double velocity) {
        for (EnemyVelocity ev : values()) {
            if (velocity >= ev.min && velocity < ev.max) {
                return ev;
            }
        }
        throw new IllegalArgumentException("Velocity out of range: " + velocity);
    }


}

enum EnemyAngle {
    RANGE_0_45(0, 45),
    RANGE_45_90(45, 90),
    RANGE_90_135(90, 135),
    RANGE_135_180(135, 180),
    RANGE_180_225(180, 225),
    RANGE_225_270(225, 270),
    RANGE_270_315(270, 315),
    RANGE_315_360(315, 360);

    private final double min;
    private final double max;

    EnemyAngle(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static EnemyAngle fromDouble(double angle) {
        for (EnemyAngle ea : values()) {
            if (angle >= ea.min && angle < ea.max) {
                return ea;
            }
        }
        throw new IllegalArgumentException("Angle out of range: " + angle);
    }
}

enum EnemyHeading {
    RANGE_0_45(0, 45),
    RANGE_45_90(45, 90),
    RANGE_90_135(90, 135),
    RANGE_135_180(135, 180),
    RANGE_180_225(180, 225),
    RANGE_225_270(225, 270),
    RANGE_270_315(270, 315),
    RANGE_315_360(315, 360);

    private final double min;
    private final double max;

    EnemyHeading(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static EnemyHeading fromDouble(double heading) {
        for (EnemyHeading eh : values()) {
            if (heading >= eh.min && heading < eh.max) {
                return eh;
            }
        }
        throw new IllegalArgumentException("Heading out of range: " + heading);
    }
}

enum MyGunToEnemyAngle {
    RANGE_0_20(0, 20),
    RANGE_20_40(20, 40),
    RANGE_40_60(40, 60),
    RANGE_60_80(60, 80),
    RANGE_80_100(80, 100),
    RANGE_100_120(100, 120),
    RANGE_120_140(120, 140),
    RANGE_140_160(140, 160),
    RANGE_160_180(160, 180);

    private final double min;
    private final double max;

    MyGunToEnemyAngle(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static MyGunToEnemyAngle fromDouble(double angle) {
        for (MyGunToEnemyAngle mgtea : values()) {
            if (angle >= mgtea.min && angle < mgtea.max) {
                return mgtea;
            }
        }
        throw new IllegalArgumentException("Angle out of range: " + angle);
    }
}

enum FirePower {
    FP_01(0.1),
    FP_02(0.2),
    FP_03(0.3),
    FP_04(0.4),
    FP_05(0.5),
    FP_06(0.6),
    FP_07(0.7),
    FP_08(0.8),
    FP_09(0.9),
    FP_10(1.0),
    FP_11(1.1),
    FP_12(1.2),
    FP_13(1.3),
    FP_14(1.4),
    FP_15(1.5),
    FP_16(1.6),
    FP_17(1.7),
    FP_18(1.8),
    FP_19(1.9),
    FP_20(2.0),
    FP_21(2.1),
    FP_22(2.2),
    FP_23(2.3),
    FP_24(2.4),
    FP_25(2.5),
    FP_26(2.6),
    FP_27(2.7),
    FP_28(2.8),
    FP_29(2.9),
    FP_30(3.0);

    private final double power;

    FirePower(double power) {
        this.power = power;
    }

    public double toDouble() {
        return power;
    }

}

enum Hit {
    TRUE(true),
    FALSE(false);

    private final boolean hit;

    Hit(boolean hit) {
        this.hit = hit;
    }

    public static Hit fromBoolean(boolean hit) {
        for (Hit h : values()) {
            if (hit == h.hit) {
                return h;
            }
        }
        throw new IllegalArgumentException("Hit out of range: " + hit);
    }
}

class InternalBayesNode {
    private final String name;
    private final List<String> values;
    private final List<String> parents;
    private double[][] distribution;

    public InternalBayesNode(String name, Class<? extends Enum<?>> enumClass, List<String> parents) {
        this.name = name;
        this.values = Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).collect(Collectors.toList());
        this.parents = parents;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public List<String> getParents() {
        return parents;
    }

    public double[] getFlattenedDistribution() {
        return Arrays.stream(distribution).flatMapToDouble(Arrays::stream).toArray();
    }

    public double[][] getDistribution() {
        return distribution;
    }

    public void setDistribution(double[][] distribution) {
        this.distribution = distribution;
    }

}

record InternalEvidence(String nodeName, String nodeValue) {
}

@SuppressWarnings("DuplicatedCode")
class Weka {

    private final List<InternalBayesNode> internalNodes;
    private Instances dataset;
    private EditableBayesNet bayesNet;

    public Weka(List<InternalBayesNode> internalNodes) throws Exception {
        this.internalNodes = internalNodes;
        initDataset();
        initBayesianNetwork();
    }

    private void initDataset() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (InternalBayesNode internalNode : internalNodes) {
            attributes.add(new Attribute(internalNode.getName(), internalNode.getValues()));
        }
        Instances dataset = new Instances("Dataset", attributes, 0);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        this.dataset = dataset;
    }

    private void initBayesianNetwork() throws Exception {
        EditableBayesNet bayesNet = new EditableBayesNet(dataset);
        initParents(bayesNet);
        initDistributions(bayesNet);
        this.bayesNet = bayesNet;
    }

    private void initParents(EditableBayesNet bayesNet) throws Exception {
        for (InternalBayesNode internalNode : internalNodes) {
            if (!internalNode.getParents().isEmpty()) {
                for (String parent : internalNode.getParents()) {
                    bayesNet.addArc(parent, internalNode.getName());
                }
            }
        }
    }

    private void initDistributions(EditableBayesNet bayesNet) {
        for (InternalBayesNode internalNode : internalNodes) {
            internalNode.setDistribution(bayesNet.getDistribution(internalNode.getName()));
        }
    }

    public void calcNewDistributions() throws Exception {
        bayesNet.setData(dataset);
        bayesNet.estimateCPTs();
        for (InternalBayesNode internalNode : internalNodes) {
            internalNode.setDistribution(bayesNet.getDistribution(internalNode.getName()));
        }
    }

    // TODO: Agnostic
    public void addInstance(EnemyDistance ed, EnemyVelocity ev, EnemyAngle ea, EnemyHeading eh, MyGunToEnemyAngle mgtea, FirePower fp, Hit hit) {
        double[] instance = new double[]{ed.ordinal(), ev.ordinal(), ea.ordinal(), eh.ordinal(), mgtea.ordinal(), fp.ordinal(), hit.ordinal()};
        dataset.add(new DenseInstance(1.0, instance));
    }

    private void printDataset() {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Dataset <<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(dataset);
    }

    private void printNetworkData() {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Network Data <<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println(bayesNet);

        System.out.println("Attributes with values:");
        for (InternalBayesNode internalNode : internalNodes) {
            System.out.println(internalNode.getName() + ": " + Arrays.toString(bayesNet.getValues(internalNode.getName())));
        }
    }

    private void printDistributions() {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Distribution <<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (InternalBayesNode internalNode : internalNodes) {
            System.out.println(internalNode.getName() + ": " + Arrays.deepToString(internalNode.getDistribution()));
        }
    }

    @SuppressWarnings("unused")
    public void printAll() {
        System.out.println("\n========================================= Weka =========================================");
        printNetworkData();
        printDataset();
        printDistributions();
    }

    @SuppressWarnings("unused")
    public void displayGraph() throws Exception {
        GraphVisualizer graphVisualizer = new GraphVisualizer();
        graphVisualizer.readBIF(bayesNet.graph());

        final JFrame jFrame = new JFrame("Autobot Bayesian Network");
        jFrame.setSize(600, 400);
        jFrame.getContentPane().setLayout(new BorderLayout());
        jFrame.getContentPane().add(graphVisualizer, BorderLayout.CENTER);
        jFrame.addWindowFocusListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                jFrame.dispose();
            }
        });
        jFrame.setVisible(true);
        graphVisualizer.layoutGraph();
    }

}

class Jayes {
    private final List<InternalBayesNode> internalNodes;
    private BayesNet bayesNet;
    private IBayesInferer inferer;

    public Jayes(List<InternalBayesNode> internalNodes) {
        this.internalNodes = internalNodes;
        initBayesianNetwork();
        initInferer();
    }

    private void initInferer() {
        IBayesInferer inferer = new JunctionTreeAlgorithm();
        inferer.setNetwork(bayesNet);
        this.inferer = inferer;
    }

    private void initBayesianNetwork() {
        BayesNet bayesNet = new BayesNet();
        initNodes(bayesNet);
        initParents(bayesNet);
        initProbabilities(bayesNet);
        this.bayesNet = bayesNet;
    }

    private void initNodes(BayesNet bayesNet) {
        for (InternalBayesNode internalNode : internalNodes) {
            BayesNode jayesNode = bayesNet.createNode(internalNode.getName());
            jayesNode.addOutcomes(internalNode.getValues().toArray(new String[0]));
        }
    }

    private void initParents(BayesNet bayesNet) {
        for (InternalBayesNode internalNode : internalNodes) {
            if (!internalNode.getParents().isEmpty()) {
                BayesNode jayesNode = bayesNet.getNode(internalNode.getName());
                List<BayesNode> parents = new ArrayList<>();
                for (String parent : internalNode.getParents()) {
                    parents.add(bayesNet.getNode(parent));
                }
                jayesNode.setParents(parents);
            }
        }
    }

    private void initProbabilities(BayesNet bayesNet) {
        for (InternalBayesNode internalNode : internalNodes) {
            BayesNode jayesNode = bayesNet.getNode(internalNode.getName());
            jayesNode.setProbabilities(internalNode.getFlattenedDistribution());
        }
    }

    public void setNewProbabilities() {
        for (InternalBayesNode internalNode : internalNodes) {
            BayesNode jayesNode = bayesNet.getNode(internalNode.getName());
            jayesNode.setProbabilities(internalNode.getFlattenedDistribution());
        }
        inferer.setNetwork(bayesNet);
    }

    public double[] getBeliefs(List<InternalEvidence> internalEvidences, String nodeToGetBeliefs) {
        BayesNode jayesNodeToGetBeliefs = bayesNet.getNode(nodeToGetBeliefs);
        Map<BayesNode, String> jayesEvidence = new HashMap<>();
        for (InternalEvidence internalEvidence : internalEvidences) {
            BayesNode jayesNode = bayesNet.getNode(internalEvidence.nodeName());
            jayesEvidence.put(jayesNode, internalEvidence.nodeValue());
        }
        inferer.setEvidence(jayesEvidence);
        return inferer.getBeliefs(jayesNodeToGetBeliefs);
    }

    private void printNetworkData() {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Network Data <<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (BayesNode jayesNode : bayesNet.getNodes()) {
            System.out.println();
            System.out.println("Node: " + jayesNode);
            System.out.println("Values:" + jayesNode.getOutcomes());
            System.out.println("Parents:" + jayesNode.getParents());
            System.out.println("Expected # of probabilities: " + MathUtils.product(jayesNode.getFactor().getDimensions()));
        }
    }

    private void printProbabilities() {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Probabilities <<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (BayesNode jayesNode : bayesNet.getNodes()) {
            System.out.println(jayesNode.getName() + ": " + Arrays.toString(jayesNode.getProbabilities()));
        }
    }

    @SuppressWarnings("unused")
    public void printAll() {
        System.out.println("\n========================================= Jayes =========================================");
        printNetworkData();
        printProbabilities();
    }

}

@SuppressWarnings({"unused", "DuplicatedCode"})
class Test1 {
    private final Jayes jayes;

    public Test1(Jayes jayes) {
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

@SuppressWarnings({"unused", "DuplicatedCode"})
class Test2 {

    private final Jayes jayes;

    public Test2(Jayes jayes) {
        this.jayes = jayes;
    }

    private FirePower calcBestFirePowerToHit(EnemyDistance ed, EnemyVelocity ev, EnemyAngle ea, EnemyHeading eh, MyGunToEnemyAngle mgtea) {
        List<InternalEvidence> internalEvidences = new ArrayList<>();

        internalEvidences.add(new InternalEvidence("EnemyDistance", ed.toString()));
        internalEvidences.add(new InternalEvidence("EnemyVelocity", ev.toString()));
        internalEvidences.add(new InternalEvidence("EnemyAngle", ea.toString()));
        internalEvidences.add(new InternalEvidence("EnemyHeading", eh.toString()));
        internalEvidences.add(new InternalEvidence("MyGunToEnemyAngle", mgtea.toString()));
        internalEvidences.add(new InternalEvidence("Hit", Hit.fromBoolean(true).toString()));

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

@SuppressWarnings("CommentedOutCode")
public class AgoraVai {

    private static List<InternalBayesNode> initInternalBayesNodes() {
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

        return internalNodes;
    }

    private static void addSomeInstances(Weka weka, Jayes jayes) throws Exception {
        weka.addInstance(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_03, Hit.TRUE);
        weka.addInstance(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_03, Hit.TRUE);
        weka.addInstance(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_03, Hit.TRUE);
        weka.addInstance(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_08, Hit.TRUE);
        weka.addInstance(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_08, Hit.TRUE);
        weka.addInstance(EnemyDistance.RANGE_0_100, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_08, Hit.TRUE);

        weka.addInstance(EnemyDistance.RANGE_100_200, EnemyVelocity.RANGE_0_1, EnemyAngle.RANGE_0_45, EnemyHeading.RANGE_0_45, MyGunToEnemyAngle.RANGE_0_20, FirePower.FP_08, Hit.TRUE);

        weka.calcNewDistributions();
        jayes.setNewProbabilities();
    }

    @SuppressWarnings("unused")
    private static void printAll(String tip, Weka weka, Jayes jayes) {
        System.out.println("\n\n\n========================================= " + tip + " =========================================");
        System.out.println("========================================= " + tip + " =========================================");
        System.out.println("========================================= " + tip + " =========================================");
        weka.printAll();
        jayes.printAll();
    }

    public static void main(String[] args) throws Exception {

        List<InternalBayesNode> internalNodes = initInternalBayesNodes();

        Weka weka = new Weka(internalNodes);
        Jayes jayes = new Jayes(internalNodes);
//        printAll("Initial network", weka, jayes);

        addSomeInstances(weka, jayes);
//        printAll("After changes", weka, jayes);

//        Test1 test1 = new Test1(jayes);
//        test1.run();

        Test2 test2 = new Test2(jayes);
        test2.run();

//        weka.displayGraph();

    }
}

// LUCAS:
// TODO: * Salvar e pegar dados entre as rodadas
// TODO: * Como saber se a bala acertou ou não?


// PARA APRESENTAR:
// TODO: * Entender melhor o caso do 0


// MELHORIA DE CÓDIGO
// TODO: * Arquivos de classes


// FUNCIONALIDADES NECESSÁRIAS:
// TODO: * Ao atirar, pega os dados, calcula prob, escolhe, adiciona nas instâncias com o resultado
// TODO:    - Classificador entre valores do enum
// TODO:    - Atualizar o dataset com o resultado
// TODO: * Cassar com a lógica existente de tiro


// FUNCIONALIDADES DESEJÁVEIS:
// * TODO: Bayes para ver pra onde girar a arma baseado em posição, velocidade e direção do inimigo


// CANCELADO (Se tem energia, atira. Quem tem limite é município!)
// TODO: * Valor mínimo de believe para atirar
