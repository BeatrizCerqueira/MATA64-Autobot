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
import org.encog.persist.EncogDirectoryPersistence;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class NeuralNetwork {
    final String filepath = "C:/robocode/robots/autobot/neural/data/";
    final String trainingSuffix = "Training.arff";
    final String validationSuffix = "Validation.arff";
    static final String networkSuffix = "Network.eg";
    BasicNetwork network;
    int outputCount;


    public NeuralNetwork(String filename) {
        this.network = getNetwork(filename + networkSuffix);
        outputCount = network.getOutputCount();
    }

    public NeuralNetwork(int inputCount, int hiddenCount, int outputCount) {
        this.network = createNetworkLayers(inputCount, hiddenCount, outputCount);
    }

    // Load data from files
    private Instances getInstances(String filename) {
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(filepath + filename);
            return source.getDataSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BasicMLDataSet getDataSet(String filename, int outputSize) {

        // Load the dataset
        Instances data = getInstances(filename);
        data.setClassIndex(data.numAttributes() - outputSize);

        // Prepare the input and output arrays
        double[][] input = new double[data.numInstances()][data.numAttributes() - outputSize];
        double[][] output = new double[data.numInstances()][outputSize];
        populateDataArrays(data, input, output);

        return new BasicMLDataSet(input, output);
    }

    private BasicNetwork getNetwork(String filename) {
        File networkFile = new File(filepath + filename);
        BasicNetwork loadedNetwork = (BasicNetwork) EncogDirectoryPersistence.loadObject(networkFile);
        System.out.println("Network loaded successfully from " + networkFile.getAbsolutePath());
        return loadedNetwork;
    }


    private void populateDataArrays(Instances data, double[][] input, double[][] output) {
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

    private BasicNetwork createNetworkLayers(int inputCount, int hiddenCount, int outputCount) {
        network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, inputCount));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, hiddenCount));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, outputCount));
        network.getStructure().finalizeStructure();
        network.reset();
        return network;
    }


    private void runTraining(BasicMLDataSet dataset) {
        System.out.println("Training the network...");
        // Configure the training
        MLTrain train;
        train = new ResilientPropagation(network, dataset);  // RPROP configuration
        train.addStrategy(new StoppingStrategy(10)); // Stop training after 10 epochs without improvement
        // train = new Backpropagation(network, dataset, 0.01, 0.9);  // Backpropagation configuration
        // Backprogation did not converge. More tem 50k epochs and error still high

        int epoch = 1;
        while (!train.isTrainingDone()) {
            train.iteration();
//            System.out.println("Epoch #" + epoch + " Error: " + train.getError());
            epoch++;
        }
        train.finishTraining();
        System.out.println("Training complete. " + epoch + " epochs. " + train.getError() + " error.");
        Encog.getInstance().shutdown();
    }

    private void splitDataset(String filename, double trainRatio) {
        Instances data = getInstances(filename);
        data.randomize(new Random());

        int trainSize = (int) Math.round(data.numInstances() * trainRatio);

        Instances trainingData = new Instances(data, 0, trainSize);
        Instances validationData = new Instances(data, trainSize, data.numInstances() - trainSize);

        saveInstances(trainingData, filename.replace(".arff", trainingSuffix));
        saveInstances(validationData, filename.replace(".arff", validationSuffix));


    }

    private void saveInstances(Instances data, String filename) {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        try {
            saver.setFile(new File(filepath + filename));
            saver.writeBatch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveNetwork(String filename) {
        File networkFile = new File(filepath + filename);
        EncogDirectoryPersistence.saveObject(networkFile, network);
        System.out.println("Network saved successfully to " + networkFile.getAbsolutePath());
    }


    private void printDatasetResults(BasicMLDataSet dataset) {
        System.out.println("Results for dataset:");
        for (MLDataPair pair : dataset) {
            final MLData outputData = network.compute(pair.getInput());
            System.out.print("Input: " + Arrays.toString(pair.getInput().getData()) + " - ");
            System.out.print("Expected: " + Arrays.toString(pair.getIdeal().getData()) + " - ");
            System.out.println("Output: " + Arrays.toString(outputData.getData()));
        }
    }


    public void initTraining(String filename, double trainRatio) {
        // train network given the dataset .arff and save .eg network

        // Split the dataset into training and validation
        splitDataset(filename, trainRatio);

        // Load datasets
        BasicMLDataSet trainingSet = getDataSet(filename.replace(".arff", trainingSuffix), 2);
        BasicMLDataSet validationSet = getDataSet(filename.replace(".arff", validationSuffix), 2);


        // Train network
        runTraining(trainingSet);
        saveNetwork(filename.replace(".arff", networkSuffix));

        System.out.println("Training Error: " + network.calculateError(trainingSet));
        System.out.println("Validation Error: " + network.calculateError(validationSet));

//        printDatasetResults(trainingSet);
//        printDatasetResults(validationSet);

        System.out.println("\nTraining complete.\n");
    }


    public static void main(String[] args) {
//        NeuralNetwork net = new NeuralNetwork(3, 10, 2);
        NeuralNetwork net = new NeuralNetwork("Autobot");
        net.initTraining("Autobot.arff", 0.8);
    }
}
