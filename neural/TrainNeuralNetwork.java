package autobot.neural;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.end.StoppingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.IOException;
import java.util.Arrays;

public class TrainNeuralNetwork {

    // ========================
    // Neural network training
    static BasicMLDataSet dataset;
    static BasicNetwork network = new BasicNetwork();
    static MLTrain train;

    private static void trainNetwork() {
        buildNeuralNetwork();
        configureTraining();
        runTraining();
    }

    private static void buildNeuralNetwork() {

        // Load the dataset
        String filepath = "C:/robocode/robots/autobot/neural/data/Autobot.arff";
        Instances data = loadDataset(filepath);
        data.setClassIndex(data.numAttributes() - 1);

        // Prepare the input and output arrays
        double[][] input = new double[data.numInstances()][data.numAttributes() - 1];
        double[][] output = new double[data.numInstances()][1];
        populateDataArrays(data, input, output);

        dataset = new BasicMLDataSet(input, output);
        network = createNetwork(input[0].length, 10, output[0].length);
    }

    private static Instances loadDataset(String filepath) {
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(filepath);
            return source.getDataSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BasicNetwork createNetwork(int inputCount, int hiddenCount, int outputCount) {
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, inputCount));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, hiddenCount));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, outputCount));
        network.getStructure().finalizeStructure();
        network.reset();
        return network;
    }

    private static void configureTraining() {
//        train = new Backpropagation(network, dataset, 0.01, 0.9);  // Backpropagation configuration
        // Backprogation did not converged. More tem 50k epochs and error still high

        train = new ResilientPropagation(network, dataset);  // RPROP configuration
        train.addStrategy(new StoppingStrategy(10)); // Stop training after 10 epochs without improvement

//        EarlyStoppingStrategy earlyStopping = new EarlyStoppingStrategy(trainingSet);
//        train.addStrategy(earlyStopping);

    }

    private static void runTraining() {
        int epoch = 1;
        while (!train.isTrainingDone()) {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error: " + train.getError());
            epoch++;
        }
        train.finishTraining();
        Encog.getInstance().shutdown();
    }

    private static void printTrainingResults() {
        System.out.println("Results for Autobot dataset:");
        for (MLDataPair pair : dataset) {
            final MLData outputData = network.compute(pair.getInput());
            System.out.print("Input: " + Arrays.toString(pair.getInput().getData()) + " - ");
            System.out.print("Expected: " + Arrays.toString(pair.getIdeal().getData()) + " - ");
            System.out.println("Output: " + Arrays.toString(outputData.getData()));
        }
    }

    // ===
    // UTILS
    private static void populateDataArrays(Instances data, double[][] input, double[][] output) {
        for (int i = 0; i < data.numInstances(); i++) {
            for (int j = 0; j < data.numAttributes() - output[i].length; j++) {
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
        printTrainingResults();

    }
}
