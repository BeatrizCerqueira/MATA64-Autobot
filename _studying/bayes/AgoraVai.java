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
import weka.classifiers.bayes.net.MarginCalculator;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class InternalBayesNode {
    String name;
    List<String> values;
    List<String> parents;

    public InternalBayesNode(String name, List<String> values, List<String> parents) {
        this.name = name;
        this.values = values;
        this.parents = parents;
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", values=" + values +
                ", parents=" + parents +
                '}';
    }
}

class Weka {

    Instances dataset;
    EditableBayesNet bayesNet;
    MarginCalculator marginalDistributionCalculator;
    List<InternalBayesNode> internalNodes;

    public Weka(List<InternalBayesNode> internalNodes) throws Exception {
        this.internalNodes = internalNodes;
        initDataset();
        initBayesianNetwork();
        initMarginCalculator();
    }

    private void initDataset() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (InternalBayesNode internalNode : internalNodes) {
            attributes.add(new Attribute(internalNode.name, internalNode.values));
        }
        Instances dataset = new Instances("Dataset", attributes, 0);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        this.dataset = dataset;
    }

    private void initBayesianNetwork() throws Exception {
        EditableBayesNet bayesNet = new EditableBayesNet(dataset);
        for (InternalBayesNode internalNode : internalNodes) {
            if (!internalNode.parents.isEmpty()) {
                for (String parent : internalNode.parents) {
                    bayesNet.addArc(parent, internalNode.name);
                }
            }
        }
        this.bayesNet = bayesNet;
    }

    private void initMarginCalculator() throws Exception {
        MarginCalculator marginDistributionCalculator = new MarginCalculator();
        marginDistributionCalculator.calcMargins(bayesNet);
        this.marginalDistributionCalculator = marginDistributionCalculator;
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
            System.out.println(internalNode.name + ": " + Arrays.toString(bayesNet.getValues(internalNode.name)));
        }
    }

    private void printMarginalDistribution() {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Marginal Distribution <<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (int i = 0; i < internalNodes.size(); i++) {
            System.out.println(internalNodes.get(i).name + ": " + Arrays.toString(marginalDistributionCalculator.getMargin(i)));
        }
    }

    private void printConditionalDistribution() {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Conditional Distribution <<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (InternalBayesNode internalNode : internalNodes) {
            System.out.println(internalNode.name + ": " + Arrays.deepToString(bayesNet.getDistribution(internalNode.name)));
        }
    }

    public void printInit() {
        System.out.println("\n\n\n========================================= Initial network =========================================");
        printAll();
    }

    public void printAll() {
        printNetworkData();
        printDataset();
        printMarginalDistribution();
        printConditionalDistribution();
    }

}

class Jayes {
    BayesNet bayesNet;
    List<InternalBayesNode> internalNodes;

    public Jayes(List<InternalBayesNode> internalNodes) {
        this.internalNodes = internalNodes;
        initBayesianNetwork();
    }

    private void initBayesianNetwork() {
        this.bayesNet = new BayesNet();
        initNodes();
        initParents();
    }

    private void initNodes() {
        for (InternalBayesNode internalNode : internalNodes) {
            BayesNode jayesNode = bayesNet.createNode(internalNode.name);
            jayesNode.addOutcomes(internalNode.values.toArray(new String[0]));
        }
    }

    private void initParents() {
        for (InternalBayesNode internalNode : internalNodes) {
            if (!internalNode.parents.isEmpty()) {
                BayesNode jayesNode = bayesNet.getNode(internalNode.name);
                List<BayesNode> parents = new ArrayList<>();
                for (String parent : internalNode.parents) {
                    parents.add(bayesNet.getNode(parent));
                }
                jayesNode.setParents(parents);
            }
        }
    }

    public void printInit() {
        System.out.println("\n\n\n========================================= Initial network =========================================");
        printAll();
    }

    public void printAll() {
        for (BayesNode jayesNode : bayesNet.getNodes()) {
            System.out.println();
            System.out.println("Node: " + jayesNode);
            System.out.println("Values:" + jayesNode.getOutcomes());
            System.out.println("Parents:" + jayesNode.getParents());
            System.out.println("Expected # of probabilities: " + MathUtils.product(jayesNode.getFactor().getDimensions()));
        }
    }
}

public class AgoraVai {

    public static void main(String[] args) throws Exception {
        List<InternalBayesNode> internalNodes = new ArrayList<>();

        internalNodes.add(new InternalBayesNode("EnemyDistance", Arrays.asList("dist1", "dist2"), new ArrayList<>()));
        internalNodes.add(new InternalBayesNode("EnemyVelocity", Arrays.asList("sp1", "sp2"), new ArrayList<>()));
        internalNodes.add(new InternalBayesNode("EnemyAngle", Arrays.asList("ea1", "ea2"), new ArrayList<>()));
        internalNodes.add(new InternalBayesNode("MyGunAngle", Arrays.asList("mga1", "mga2"), new ArrayList<>()));
        internalNodes.add(new InternalBayesNode("FirePower", Arrays.asList("fp1", "fp2"), new ArrayList<>()));
        internalNodes.add(new InternalBayesNode("Hit", Arrays.asList("true", "false"), Arrays.asList("EnemyDistance", "EnemyVelocity", "EnemyAngle", "MyGunAngle", "FirePower")));

        Weka weka = new Weka(internalNodes);
        weka.printInit();
        Jayes jayes = new Jayes(internalNodes);
        jayes.printInit();

    }
}