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

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

enum EnemyDistance {
    DIST1, DIST2
}

enum EnemyVelocity {
    SP1, SP2
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
        updateDistributions(bayesNet);
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

    private void updateDistributions(EditableBayesNet bayesNet) {
        for (InternalBayesNode internalNode : internalNodes) {
            internalNode.setDistribution(bayesNet.getDistribution(internalNode.getName()));
        }
    }

    private void updateDistributions() {
        for (InternalBayesNode internalNode : internalNodes) {
            internalNode.setDistribution(bayesNet.getDistribution(internalNode.getName()));
        }
    }

    private void update() throws Exception {
        bayesNet.setData(dataset);
        bayesNet.estimateCPTs();
        updateDistributions();
    }

    public void addInstance(EnemyDistance ed, EnemyVelocity ev, EnemyAngle ea, MyGunAngle mga, FirePower fp, Hit hit) throws Exception {
        double[] instance = new double[]{ed.ordinal(), ev.ordinal(), ea.ordinal(), mga.ordinal(), fp.ordinal(), hit.ordinal()};
        dataset.add(new DenseInstance(1.0, instance));
        update();
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
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Conditional Distribution <<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (InternalBayesNode internalNode : internalNodes) {
            System.out.println(internalNode.getName() + ": " + Arrays.deepToString(internalNode.getDistribution()));
        }
    }

    @SuppressWarnings("unused")
    public void printInit() {
        System.out.println("\n\n\n========================================= Initial network =========================================");
        printAll();
    }

    @SuppressWarnings("unused")
    public void printAll() {
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

    public Jayes(List<InternalBayesNode> internalNodes) {
        this.internalNodes = internalNodes;
        initBayesianNetwork();
    }

    private void initBayesianNetwork() {
        BayesNet bayesNet = new BayesNet();
        initNodes(bayesNet);
        initParents(bayesNet);
        updateProbabilities(bayesNet);
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

    private void updateProbabilities(BayesNet bayesNet) {
        for (InternalBayesNode internalNode : internalNodes) {
            BayesNode jayesNode = bayesNet.getNode(internalNode.getName());
            jayesNode.setProbabilities(internalNode.getFlattenedDistribution());
        }
    }

    public void updateProbabilities() {
        for (InternalBayesNode internalNode : internalNodes) {
            BayesNode jayesNode = bayesNet.getNode(internalNode.getName());
            jayesNode.setProbabilities(internalNode.getFlattenedDistribution());
        }
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
    public void printInit() {
        System.out.println("\n\n\n========================================= Initial network =========================================");
        printAll();
    }

    @SuppressWarnings("unused")
    public void printAll() {
        printNetworkData();
        printProbabilities();
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

    private static void addSomeInstances(Weka weka) throws Exception {
        weka.addInstance(EnemyDistance.DIST1, EnemyVelocity.SP1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP1, Hit.TRUE);
        weka.addInstance(EnemyDistance.DIST1, EnemyVelocity.SP1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP1, Hit.TRUE);
        weka.addInstance(EnemyDistance.DIST1, EnemyVelocity.SP1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP1, Hit.TRUE);
        weka.addInstance(EnemyDistance.DIST1, EnemyVelocity.SP1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP1, Hit.TRUE);
        weka.addInstance(EnemyDistance.DIST1, EnemyVelocity.SP1, EnemyAngle.EA1, MyGunAngle.MGA1, FirePower.FP1, Hit.TRUE);
    }

    public static void main(String[] args) throws Exception {
        List<InternalBayesNode> internalNodes = initInternalBayesNodes();

        Weka weka = new Weka(internalNodes);
        weka.printInit();

        Jayes jayes = new Jayes(internalNodes);
        jayes.printInit();

        addSomeInstances(weka);

        weka.printAll();

        jayes.updateProbabilities(); // TODO: How to avoid calling manually?
        jayes.printAll();

        weka.displayGraph();

    }
}
