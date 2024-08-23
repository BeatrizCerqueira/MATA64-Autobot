package autobot.neural;


import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.end.EarlyStoppingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.IOException;

public class NeuralNetwork {

    // ========================
    // ROBOCODE Integration
    static FileHandler fileHandler = new FileHandler();
    static int numOutputs;


    public static void initDataCollection(String[] attributesNames, int numOutputs) {
        fileHandler.initDataset(attributesNames);
        NeuralNetwork.numOutputs = numOutputs;
    }

    public static void addInstance(double... values) {
        fileHandler.addInstances(values);   // DataHandler.addInstance(currentX, currentY, enemyAngle, enemyHeading, velocity, nextX, nextY);
    }

    public static void saveDatasetFile() {
        fileHandler.updateDatasetFile();
    }


    // ========================
    // Neural network training
    static BasicMLDataSet trainingSet;
    static BasicNetwork network = new BasicNetwork();
    static MLTrain train;

    private static void trainNetwork() {
        try {
            buildNeuralNetwork();
            configureTraining();
            runTraining();
            printTrainingResults();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void buildNeuralNetwork() throws Exception {
        // Load the dataset
//        String filepath = "robots/autobot/neural/data/Autobot_normalized.arff";
        String filepath = "C:/robocode/robots/autobot/neural/data/Autobot_normalized.arff";
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(filepath);
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        // Prepare the input and output arrays
        double[][] input = new double[data.numInstances()][data.numAttributes() - 1];
        double[][] output = new double[data.numInstances()][1];
        populateDataArrays(data, input, output);

        trainingSet = new BasicMLDataSet(input, output);

        // Create the neural network
        network.addLayer(new BasicLayer(null, true, data.numAttributes() - 1));  // Input layer
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 10));  // Hidden layer 1 with 10 neurons
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));  // Output layer with 1 neuron
        network.getStructure().finalizeStructure();
        network.reset();
    }

    private static void configureTraining() {
//        train = new Backpropagation(network, trainingSet, 0.1, 0.9);  // Backpropagation configuration
        train = new ResilientPropagation(network, trainingSet);  // RPROP configuration
        EarlyStoppingStrategy earlyStopping = new EarlyStoppingStrategy(trainingSet);
        train.addStrategy(earlyStopping);
    }

    private static void runTraining() {
        int epoch = 1;
        while (!train.isTrainingDone()) {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error: " + train.getError());
            epoch++;
//            if (train.getError() < 0.1 || epoch > 500) break;
        }
        train.finishTraining();
        Encog.getInstance().shutdown();
    }

    private static void printTrainingResults() {
        System.out.println("Results for Autobot dataset:");
        for (MLDataPair pair : trainingSet) {
            final MLData outputData = network.compute(pair.getInput());
            System.out.print("Input: " + pair.getInput().toString() + " - ");
            System.out.print("Expected: " + pair.getIdeal().getData(0) + " - ");
            System.out.println("Output: " + outputData.getData(0));
        }
    }

    // ===
    // UTILS
    private static void populateDataArrays(Instances data, double[][] input, double[][] output) {
        for (int i = 0; i < data.numInstances(); i++) {
            for (int j = 0; j < data.numAttributes() - 1; j++) {
                input[i][j] = data.instance(i).value(j);
            }
            // Ensure the output array is correctly populated
            for (int k = 0; k < output[i].length; k++) {
                output[i][k] = data.instance(i).value(data.numAttributes() - output[i].length + k);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // create dataset
        // normalize data
        trainNetwork();
    }
}
