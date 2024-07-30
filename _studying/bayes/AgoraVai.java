// [{ name: EnemyDistance
//    values: [dist1, dist2]
//    parents: [] },
//  { name: EnemyVelocity
//    values: [sp1, sp2]
//    parents: [] },
//  { name: EnemyAngle
//    values: [ea1, ea2]
//    parents: [] },
//  { name: MyGunAngle
//    values: [mga1, mga2]
//    parents: [] },
//  { name: FirePower
//    values: [fp1, fp2]
//    parents: [] },
//  { name: Hit
//    values: [true, false]
//    parents: [EnemyDistance, EnemyVelocity, EnemyAngle, MyGunAngle, FirePower] }]

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
    DIST1, DIST2
}

enum EnemyVelocity {
    EV1, EV2
}

enum EnemyAngle {
    EA1, EA2
}

enum MyGunAngle {
    MGA1, MGA2
}

enum FirePower {
    FP1, FP2
}

enum Hit {
    TRUE, FALSE
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
    public void addInstance(EnemyDistance ed, EnemyVelocity ev, EnemyAngle ea, MyGunAngle mga, FirePower fp, Hit hit) {
        double[] instance = new double[]{ed.ordinal(), ev.ordinal(), ea.ordinal(), mga.ordinal(), fp.ordinal(), hit.ordinal()};
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

class Test1 {
    private final Weka weka;
    private final Jayes jayes;

    public Test1(Weka weka, Jayes jayes) {
        this.weka = weka;
        this.jayes = jayes;
    }

    private void calcHitBeliefs() {
        List<InternalEvidence> internalEvidences = new ArrayList<>();

        internalEvidences.add(new InternalEvidence("EnemyDistance", EnemyDistance.DIST1.toString()));
        internalEvidences.add(new InternalEvidence("EnemyVelocity", EnemyVelocity.EV1.toString()));
        internalEvidences.add(new InternalEvidence("EnemyAngle", EnemyAngle.EA1.toString()));
        internalEvidences.add(new InternalEvidence("MyGunAngle", MyGunAngle.MGA1.toString()));
        internalEvidences.add(new InternalEvidence("FirePower", FirePower.FP1.toString()));

        String nodeToGetBeliefs = "Hit";

        double[] hitBeliefs = jayes.getBeliefs(internalEvidences, nodeToGetBeliefs);

        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Inference <<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("Beliefs of hit enemy: " + Arrays.toString(hitBeliefs));

    }

    private void calcBestFirePowerToHit() {
        List<InternalEvidence> internalEvidences = new ArrayList<>();

        internalEvidences.add(new InternalEvidence("EnemyDistance", EnemyDistance.DIST1.toString()));
        internalEvidences.add(new InternalEvidence("EnemyVelocity", EnemyVelocity.EV1.toString()));
        internalEvidences.add(new InternalEvidence("EnemyAngle", EnemyAngle.EA1.toString()));
        internalEvidences.add(new InternalEvidence("MyGunAngle", MyGunAngle.MGA1.toString()));
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

class Test2 {

    private Weka weka;
    private Jayes jayes;

    public Test2(Weka weka, Jayes jayes) {
        this.weka = weka;
        this.jayes = jayes;
    }

    private FirePower calcBestFirePowerToHit(EnemyDistance ed, EnemyVelocity ev, EnemyAngle ea, MyGunAngle mga) {
        List<InternalEvidence> internalEvidences = new ArrayList<>();

        internalEvidences.add(new InternalEvidence("EnemyDistance", ed.toString()));
        internalEvidences.add(new InternalEvidence("EnemyVelocity", ev.toString()));
        internalEvidences.add(new InternalEvidence("EnemyAngle", ea.toString()));
        internalEvidences.add(new InternalEvidence("MyGunAngle", mga.toString()));
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

        return bestFirePower;

    }

    private double foo(double enemyDistance, double enemyVelocity, double enemyAngle, double myGunAngle, double myEnergy) throws Exception {

        // Classify values in ranges


        // Return double value of the choose FP
        return 0.5;
    }

    private void setFire(double firePower) {
        System.out.println("Firing with power: " + firePower);
    }

    @SuppressWarnings("unused")
    public void run() throws Exception {

        double enemyDistance = 200.0;    // 0 to 1000 px       |   10 ranges
        double enemyVelocity = 10.0;     // 0 to 8 px/turn     |   8 ranges
        double enemyAngle = 45.0;        // 0 to 360 degrees   |   8 ranges   |  ourGetHeading() + enemy.getBearing()
        double enemyHeading = 45.0;      // 0 to 360 degrees   |   8 ranges
        double myGunToEnemyAngle = 45.0; // 0 to 180 degrees   |   9 ranges   |  abs(gunInitialTurn + gunTurnThatWasSet)

        // Other variables
        double myEnergy = 100.0;         // 0 to 100

        //noinspection ConstantValue
        if (myEnergy > Consts.MIN_LIFE_TO_FIRE) {
            double firePower = foo(enemyDistance, enemyVelocity, enemyHeading, myGunToEnemyAngle, myEnergy);
            setFire(firePower);
        } else {
            System.out.println("Not enough energy to fire");
        }
    }

}

public class AgoraVai {

    private static List<InternalBayesNode> initInternalBayesNodes() {
        List<InternalBayesNode> internalNodes = new ArrayList<>();

        InternalBayesNode enemyDistance = new InternalBayesNode("EnemyDistance", EnemyDistance.class, new ArrayList<>());
        InternalBayesNode enemyVelocity = new InternalBayesNode("EnemyVelocity", EnemyVelocity.class, new ArrayList<>());
        InternalBayesNode enemyAngle = new InternalBayesNode("EnemyAngle", EnemyAngle.class, new ArrayList<>());
        InternalBayesNode myGunAngle = new InternalBayesNode("MyGunAngle", MyGunAngle.class, new ArrayList<>());
        InternalBayesNode firePower = new InternalBayesNode("FirePower", FirePower.class, new ArrayList<>());
        InternalBayesNode hit = new InternalBayesNode("Hit", Hit.class, Arrays.asList("EnemyDistance", "EnemyVelocity", "EnemyAngle", "MyGunAngle", "FirePower"));

        internalNodes.add(enemyDistance);
        internalNodes.add(enemyVelocity);
        internalNodes.add(enemyAngle);
        internalNodes.add(myGunAngle);
        internalNodes.add(firePower);
        internalNodes.add(hit);

        return internalNodes;
    }

    private static void addSomeInstances(Weka weka, Jayes jayes) throws Exception {
        weka.addInstance(EnemyDistance.DIST1, EnemyVelocity.EV1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP1, Hit.TRUE);
        weka.addInstance(EnemyDistance.DIST1, EnemyVelocity.EV1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP1, Hit.TRUE);
        weka.addInstance(EnemyDistance.DIST1, EnemyVelocity.EV1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP1, Hit.TRUE);

        weka.addInstance(EnemyDistance.DIST1, EnemyVelocity.EV1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP2, Hit.TRUE);
        weka.addInstance(EnemyDistance.DIST1, EnemyVelocity.EV1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP2, Hit.TRUE);
        weka.addInstance(EnemyDistance.DIST1, EnemyVelocity.EV1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP2, Hit.TRUE);

        weka.addInstance(EnemyDistance.DIST2, EnemyVelocity.EV1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP2, Hit.TRUE);

        weka.calcNewDistributions();
        jayes.setNewProbabilities();
    }

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

//        Test1 test1 = new Test1(weka, jayes);
//        test1.run();

        Test2 test2 = new Test2(weka, jayes);
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
