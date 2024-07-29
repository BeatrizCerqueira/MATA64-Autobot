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
    private final String name;
    private final List<String> values;
    private final List<String> parents;
    private double[] marginalDistribution;
    private double[][] conditionalDistribution;

    public InternalBayesNode(String name, List<String> values, List<String> parents) {
        this.name = name;
        this.values = values;
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

    public double[] getMarginalDistribution() {
        return marginalDistribution;
    }

    public double[] getFlattenedConditionalDistribution() {
        return Arrays.stream(conditionalDistribution).flatMapToDouble(Arrays::stream).toArray();
    }

    public double[][] getConditionalDistribution() {
        return conditionalDistribution;
    }

    public void setMarginalDistribution(double[] marginalDistribution) {
        this.marginalDistribution = marginalDistribution;
    }

    public void setConditionalDistribution(double[][] conditionalDistribution) {
        this.conditionalDistribution = conditionalDistribution;
    }

}

class Weka {

    private Instances dataset;
    private EditableBayesNet bayesNet;
    private MarginCalculator marginalDistributionCalculator;
    private final List<InternalBayesNode> internalNodes;

    public Weka(List<InternalBayesNode> internalNodes) throws Exception {
        this.internalNodes = internalNodes;
        initDataset();
        initBayesianNetwork();
        initMarginCalculator();
        updateDistributions();
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
        for (InternalBayesNode internalNode : internalNodes) {
            if (!internalNode.getParents().isEmpty()) {
                for (String parent : internalNode.getParents()) {
                    bayesNet.addArc(parent, internalNode.getName());
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

    private void updateDistributions() {
        for (int i = 0; i < internalNodes.size(); i++) {
            InternalBayesNode internalNode = internalNodes.get(i);
            internalNode.setMarginalDistribution(marginalDistributionCalculator.getMargin(i));
            internalNode.setConditionalDistribution(bayesNet.getDistribution(i));
        }
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

    private void printMarginalDistributions() {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Marginal Distribution <<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (InternalBayesNode internalNode : internalNodes) {
            System.out.println(internalNode.getName() + ": " + Arrays.toString(internalNode.getMarginalDistribution()));
        }
    }

    private void printConditionalDistributions() {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Conditional Distribution <<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (InternalBayesNode internalNode : internalNodes) {
            System.out.println(internalNode.getName() + ": " + Arrays.deepToString(internalNode.getConditionalDistribution()));
        }
    }

    public void printInit() {
        System.out.println("\n\n\n========================================= Initial network =========================================");
        printAll();
    }

    public void printAll() {
        printNetworkData();
        printDataset();
        printMarginalDistributions();
        printConditionalDistributions();
    }

}

class Jayes {
    private BayesNet bayesNet;
    private final List<InternalBayesNode> internalNodes;

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
            BayesNode jayesNode = bayesNet.createNode(internalNode.getName());
            jayesNode.addOutcomes(internalNode.getValues().toArray(new String[0]));
        }
    }

    private void initParents() {
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

    public void updateProbabilities() {
        for (InternalBayesNode internalNode : internalNodes) {
            BayesNode jayesNode = bayesNet.getNode(internalNode.getName());
            jayesNode.setProbabilities(internalNode.getFlattenedConditionalDistribution());
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

    public void printInit() {
        System.out.println("\n\n\n========================================= Initial network =========================================");
        printAll();
    }

    public void printAll() {
        printNetworkData();
        printProbabilities();
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
        jayes.updateProbabilities();
        jayes.printAll();

    }
}