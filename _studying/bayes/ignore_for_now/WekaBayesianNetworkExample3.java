package autobot._studying.bayes.ignore_for_now;

import weka.classifiers.bayes.net.EditableBayesNet;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;

public class WekaBayesianNetworkExample3 {
    public static void main(String[] args) throws Exception {
        // Define attributes
        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<String> binaryValues = new ArrayList<>(Arrays.asList("true", "false"));

        attributes.add(new Attribute("Burglary", binaryValues));
        attributes.add(new Attribute("Earthquake", binaryValues));
        attributes.add(new Attribute("Alarm", binaryValues));
        attributes.add(new Attribute("JohnCalls", binaryValues));
        attributes.add(new Attribute("MaryCalls", binaryValues));

        // Create dataset
        Instances dataset = new Instances("BurglaryNetwork", attributes, 0);
        dataset.setClassIndex(dataset.numAttributes() - 1);

        // Add instances
        double[] instance1 = new double[]{binaryValues.indexOf("true"), binaryValues.indexOf("false"), binaryValues.indexOf("true"), binaryValues.indexOf("true"), binaryValues.indexOf("true")};
        double[] instance2 = new double[]{binaryValues.indexOf("true"), binaryValues.indexOf("false"), binaryValues.indexOf("true"), binaryValues.indexOf("true"), binaryValues.indexOf("false")};
        double[] instance3 = new double[]{binaryValues.indexOf("false"), binaryValues.indexOf("true"), binaryValues.indexOf("true"), binaryValues.indexOf("false"), binaryValues.indexOf("true")};
        double[] instance4 = new double[]{binaryValues.indexOf("false"), binaryValues.indexOf("false"), binaryValues.indexOf("false"), binaryValues.indexOf("false"), binaryValues.indexOf("false")};

        dataset.add(new DenseInstance(1.0, instance1));
        dataset.add(new DenseInstance(1.0, instance2));
        dataset.add(new DenseInstance(1.0, instance3));
        dataset.add(new DenseInstance(1.0, instance4));

        // Create Bayesian network
        EditableBayesNet bayesNet = new EditableBayesNet();
        bayesNet.buildClassifier(dataset);

        System.out.println(bayesNet);

//        System.out.println(bayesNet.getNode("Burglary"));
//        System.out.println(Arrays.toString(bayesNet.getValues("Burglary")));
//
//        System.out.println(bayesNet.getNode("Earthquake"));
//        System.out.println(Arrays.toString(bayesNet.getValues("Earthquake")));
//
//        System.out.println(bayesNet.getNode("Alarm"));
//        System.out.println(Arrays.toString(bayesNet.getValues("Alarm")));
//
//        System.out.println(bayesNet.getNode("JohnCalls"));
//        System.out.println(Arrays.toString(bayesNet.getValues("JohnCalls")));
//
//        System.out.println(bayesNet.getNode("MaryCalls"));
//        System.out.println(Arrays.toString(bayesNet.getValues("MaryCalls")));

        System.out.println("\n");

        System.out.println(Arrays.deepToString(bayesNet.getDistribution("Burglary")));
        System.out.println(Arrays.deepToString(bayesNet.getDistribution("Earthquake")));
        System.out.println(Arrays.deepToString(bayesNet.getDistribution("Alarm")));
        System.out.println(Arrays.deepToString(bayesNet.getDistribution("JohnCalls")));
        System.out.println(Arrays.deepToString(bayesNet.getDistribution("MaryCalls")));
    }
}