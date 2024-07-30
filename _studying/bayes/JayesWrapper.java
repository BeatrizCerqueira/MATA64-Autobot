package autobot._studying.bayes;

import autobot._studying.bayes.records.EvidenceAttribute;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferer;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;
import org.eclipse.recommenders.jayes.util.MathUtils;

import java.util.*;

public class JayesWrapper {
    private final List<InternalBayesNode> internalNodes;
    private BayesNet bayesNet;
    private IBayesInferer inferer;

    public JayesWrapper(List<InternalBayesNode> internalNodes) {
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

    public double[] getBeliefs(List<EvidenceAttribute> evidence, String nodeToGetBeliefs) {
        BayesNode jayesNodeToGetBeliefs = bayesNet.getNode(nodeToGetBeliefs);
        Map<BayesNode, String> jayesEvidence = new HashMap<>();
        for (EvidenceAttribute evidenceAttribute : evidence) {
            BayesNode jayesNode = bayesNet.getNode(evidenceAttribute.name());
            jayesEvidence.put(jayesNode, evidenceAttribute.value());
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
