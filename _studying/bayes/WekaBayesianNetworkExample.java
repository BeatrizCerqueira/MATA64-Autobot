package autobot._studying.bayes;

import weka.classifiers.bayes.net.EditableBayesNet;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;

public class WekaBayesianNetworkExample {
    public static void main(String[] args) throws Exception {
        // Define attributes
        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<String> manufacturerValues = new ArrayList<>();
        manufacturerValues.add("Dell");
        manufacturerValues.add("Compaq");
        manufacturerValues.add("Gateway");
        attributes.add(new Attribute("Manufacturer", manufacturerValues));

        ArrayList<String> osValues = new ArrayList<>();
        osValues.add("Windows");
        osValues.add("Linux");
        attributes.add(new Attribute("OS", osValues));

        ArrayList<String> symptomValues = new ArrayList<>();
        symptomValues.add("Can't print");
        symptomValues.add("No display");
        attributes.add(new Attribute("Symptom", symptomValues));

        ArrayList<String> causeValues = new ArrayList<>();
        causeValues.add("Driver");
        causeValues.add("Hardware");
        attributes.add(new Attribute("Cause", causeValues));

        // Create dataset
        Instances dataset = new Instances("Dataset", attributes, 0);
        dataset.setClassIndex(dataset.numAttributes() - 1);

        // Add instances
        double[] instance1 = new double[]{manufacturerValues.indexOf("Dell"), osValues.indexOf("Windows"), symptomValues.indexOf("Can't print"), causeValues.indexOf("Driver")};
        double[] instance2 = new double[]{manufacturerValues.indexOf("Compaq"), osValues.indexOf("Linux"), symptomValues.indexOf("Can't print"), causeValues.indexOf("Driver")};
        double[] instance3 = new double[]{manufacturerValues.indexOf("Dell"), osValues.indexOf("Linux"), symptomValues.indexOf("No display"), causeValues.indexOf("Hardware")};
        double[] instance4 = new double[]{manufacturerValues.indexOf("Gateway"), osValues.indexOf("Windows"), symptomValues.indexOf("Can't print"), causeValues.indexOf("Hardware")};

        dataset.add(new DenseInstance(1.0, instance1));
        dataset.add(new DenseInstance(1.0, instance2));
        dataset.add(new DenseInstance(1.0, instance3));
        dataset.add(new DenseInstance(1.0, instance4));

        // Create Bayesian network
        EditableBayesNet bayesNet = new EditableBayesNet();
        bayesNet.buildClassifier(dataset);

        System.out.println(bayesNet);
        System.out.println(bayesNet.getNode("Manufacturer"));
        System.out.println(Arrays.toString(bayesNet.getValues("Manufacturer")));

        System.out.println(bayesNet.getNode("OS"));
        System.out.println(Arrays.toString(bayesNet.getValues("OS")));

        System.out.println(bayesNet.getNode("Symptom"));
        System.out.println(Arrays.toString(bayesNet.getValues("Symptom")));

        System.out.println(bayesNet.getNode("Cause"));
        System.out.println(Arrays.toString(bayesNet.getValues("Cause")));

        System.out.println("\n\n");

        System.out.println(Arrays.deepToString(bayesNet.getDistribution("Manufacturer")));
        System.out.println(Arrays.deepToString(bayesNet.getDistribution("OS")));
        System.out.println(Arrays.deepToString(bayesNet.getDistribution("Symptom")));
        System.out.println(Arrays.deepToString(bayesNet.getDistribution("Cause")));


//        System.out.println(Arrays.toString(bayesNet.getMargin(0)));
//        System.out.println(Arrays.toString(bayesNet.getMargin(1)));
//        System.out.println(Arrays.toString(bayesNet.getMargin(2)));
//        System.out.println(Arrays.toString(bayesNet.getMargin(3)));

//        bayesNet.getDistributions()[0][0].setDebug(true);
//        System.out.println(bayesNet.getDistributions()[0][0]);
//        System.out.println(Arrays.toString(bayesNet.getDistributions()[0][0].getOptions()));
//        System.out.println(bayesNet.getDistributions()[0][0].getProbability(0));


    }
}