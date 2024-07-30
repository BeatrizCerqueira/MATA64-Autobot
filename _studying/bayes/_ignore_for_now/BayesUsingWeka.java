package autobot._studying.bayes._ignore_for_now;

import weka.classifiers.bayes.net.EditableBayesNet;
import weka.classifiers.bayes.net.MarginCalculator;
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

public class BayesUsingWeka {
    public static void main(String[] args) throws Exception {
        Instances dataset = createDataset();
        EditableBayesNet bayesNet = createBayesianNetwork(dataset);
        MarginCalculator marginCalculator = createMarginCalculator(bayesNet);

        System.out.println("\n\n\n========================================= Initial network =========================================");
        printNetworkInfo(bayesNet, marginCalculator, dataset);

        addInstances(dataset, bayesNet, marginCalculator);

        System.out.println("\n\n\n========================================= Updated network =========================================");
        printNetworkInfo(bayesNet, marginCalculator, dataset);

        displayWekaGraph(bayesNet);
    }

    private static void displayWekaGraph(EditableBayesNet bayesNet) throws Exception {
        GraphVisualizer graphVisualizer = new GraphVisualizer();
        graphVisualizer.readBIF(bayesNet.graph());

        final JFrame jFrame = new JFrame("Weka Classifier Graph Visualizer: Bayes net");
        jFrame.setSize(500, 400);
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

    private static MarginCalculator createMarginCalculator(EditableBayesNet bayesNet) throws Exception {
        MarginCalculator marginCalculator = new MarginCalculator();
        marginCalculator.calcMargins(bayesNet);
        return marginCalculator;
    }

    private static ArrayList<Attribute> defineAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        ArrayList<String> manufacturerValues = new ArrayList<>(Arrays.asList("Dell", "Compaq", "Gateway"));
        attributes.add(new Attribute("Manufacturer", manufacturerValues));

        ArrayList<String> osValues = new ArrayList<>(Arrays.asList("Windows", "Linux"));
        attributes.add(new Attribute("OS", osValues));

        ArrayList<String> symptomValues = new ArrayList<>(Arrays.asList("Can't print", "No display"));
        attributes.add(new Attribute("Symptom", symptomValues));

        ArrayList<String> causeValues = new ArrayList<>(Arrays.asList("Driver", "Hardware"));
        attributes.add(new Attribute("Cause", causeValues));

        return attributes;
    }

    private static Instances createDataset() {
        ArrayList<Attribute> attributes = defineAttributes();
        Instances dataset = new Instances("Dataset", attributes, 0);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        return dataset;
    }

    private static EditableBayesNet createBayesianNetwork(Instances dataset) throws Exception {
        EditableBayesNet bayesNet = new EditableBayesNet(dataset);
        bayesNet.addArc("Cause", "Manufacturer");
        bayesNet.addArc("Cause", "OS");
        bayesNet.addArc("Cause", "Symptom");
        return bayesNet;
    }

    private static void printNetworkInfo(EditableBayesNet bayesNet, MarginCalculator marginCalculator, Instances dataset) throws Exception {
        System.out.println(bayesNet);
        System.out.println("\nManufacturer index: " + bayesNet.getNode("Manufacturer"));
        System.out.println(Arrays.toString(bayesNet.getValues("Manufacturer")));
        System.out.println("\nOS index: " + bayesNet.getNode("OS"));
        System.out.println(Arrays.toString(bayesNet.getValues("OS")));
        System.out.println("\nSymptom index: " + bayesNet.getNode("Symptom"));
        System.out.println(Arrays.toString(bayesNet.getValues("Symptom")));
        System.out.println("\nCause index: " + bayesNet.getNode("Cause"));
        System.out.println(Arrays.toString(bayesNet.getValues("Cause")));
        System.out.println();
        System.out.println(dataset);
        System.out.println();
        System.out.println(Arrays.toString(marginCalculator.getMargin(0)));
        System.out.println(Arrays.toString(marginCalculator.getMargin(1)));
        System.out.println(Arrays.toString(marginCalculator.getMargin(2)));
        System.out.println(Arrays.toString(marginCalculator.getMargin(3)));
        System.out.println();
        System.out.println(Arrays.deepToString(bayesNet.getDistribution("Manufacturer")));
        System.out.println(Arrays.deepToString(bayesNet.getDistribution("OS")));
        System.out.println(Arrays.deepToString(bayesNet.getDistribution("Symptom")));
        System.out.println(Arrays.deepToString(bayesNet.getDistribution("Cause")));
    }

    private static void addInstances(Instances dataset, EditableBayesNet bayesNet, MarginCalculator marginCalculator) throws Exception {
        ArrayList<String> manufacturerValues = new ArrayList<>(Arrays.asList("Dell", "Compaq", "Gateway"));
        ArrayList<String> osValues = new ArrayList<>(Arrays.asList("Windows", "Linux"));
        ArrayList<String> symptomValues = new ArrayList<>(Arrays.asList("Can't print", "No display"));
        ArrayList<String> causeValues = new ArrayList<>(Arrays.asList("Driver", "Hardware"));

        double[] instance1 = new double[]{manufacturerValues.indexOf("Dell"), osValues.indexOf("Windows"), symptomValues.indexOf("Can't print"), causeValues.indexOf("Driver")};
        double[] instance2 = new double[]{manufacturerValues.indexOf("Compaq"), osValues.indexOf("Linux"), symptomValues.indexOf("Can't print"), causeValues.indexOf("Driver")};
        double[] instance3 = new double[]{manufacturerValues.indexOf("Dell"), osValues.indexOf("Linux"), symptomValues.indexOf("No display"), causeValues.indexOf("Hardware")};
        double[] instance4 = new double[]{manufacturerValues.indexOf("Gateway"), osValues.indexOf("Windows"), symptomValues.indexOf("Can't print"), causeValues.indexOf("Hardware")};

        dataset.add(new DenseInstance(1.0, instance1));
        dataset.add(new DenseInstance(1.0, instance2));
        dataset.add(new DenseInstance(1.0, instance3));
        dataset.add(new DenseInstance(1.0, instance4));

        bayesNet.setData(dataset);
        bayesNet.estimateCPTs();
        marginCalculator.calcMargins(bayesNet);
    }
}