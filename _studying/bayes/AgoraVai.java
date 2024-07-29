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
    List<InternalBayesNode> nodes;

    public Weka(List<InternalBayesNode> nodes) throws Exception {
        this.nodes = nodes;
        initDataset();
        initBayesianNetwork();
        initMarginCalculator();
        printInit();
    }

    private void initDataset() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (InternalBayesNode node : nodes) {
            attributes.add(new Attribute(node.name, node.values));
        }
        Instances dataset = new Instances("Dataset", attributes, 0);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        this.dataset = dataset;
    }

    private void initBayesianNetwork() throws Exception {
        EditableBayesNet bayesNet = new EditableBayesNet(dataset);
        for (InternalBayesNode node : nodes) {
            if (!node.parents.isEmpty()) {
                for (String parent : node.parents) {
                    bayesNet.addArc(parent, node.name);
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
        for (InternalBayesNode node : nodes) {
            System.out.println(node.name + ": " + Arrays.toString(bayesNet.getValues(node.name)));
        }
    }

    private void printMarginalDistribution() {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Marginal Distribution <<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (int i = 0; i < nodes.size(); i++) {
            System.out.println(nodes.get(i).name + ": " + Arrays.toString(marginalDistributionCalculator.getMargin(i)));
        }
    }

    private void printConditionalDistribution() {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>> Conditional Distribution <<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (InternalBayesNode node : nodes) {
            System.out.println(node.name + ": " + Arrays.deepToString(bayesNet.getDistribution(node.name)));
        }
    }

    private void printInit() {
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

public class AgoraVai {

    public static void main(String[] args) throws Exception {
        List<InternalBayesNode> nodes = new ArrayList<>();

        nodes.add(new InternalBayesNode("EnemyDistance", Arrays.asList("dist1", "dist2"), new ArrayList<>()));
        nodes.add(new InternalBayesNode("EnemyVelocity", Arrays.asList("sp1", "sp2"), new ArrayList<>()));
        nodes.add(new InternalBayesNode("EnemyAngle", Arrays.asList("ea1", "ea2"), new ArrayList<>()));
        nodes.add(new InternalBayesNode("MyGunAngle", Arrays.asList("mga1", "mga2"), new ArrayList<>()));
        nodes.add(new InternalBayesNode("FirePower", Arrays.asList("fp1", "fp2"), new ArrayList<>()));
        nodes.add(new InternalBayesNode("Hit", Arrays.asList("true", "false"), Arrays.asList("EnemyDistance", "EnemyVelocity", "EnemyAngle", "MyGunAngle", "FirePower")));
        
        Weka weka = new Weka(nodes);

    }
}