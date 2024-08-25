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

// Passo a passo
// [OK] 1. Coletar dados
// [OK] 2. Preparar os dados
// [OK] 2.1. Normalizar os dados
// [OK] 2.2. Dividir os dados em treino e teste
// [OK] 3. Definir a arquitetura da rede neural
// [OK] 3.1. Definir o número de camadas e neurônios
// [OK] 3.2. Definir a função de ativação
// [OK] 3.3 Inicializar os pesos
// [OK] 4. Treinar a rede neural
// [  ] 4.1 Função de custo (MSE, Cross-Entropy)??
// [OK] 4.2 Otimizador (SGD, Adam, Backpropagation, RPROP, etc)???
// [  ] 5. Avaliar a rede neural
// [  ] 5.1. Métricas de avaliação (Acurácia, Precisão, Recall, F1-Score, etc)?
// [  ] 5.2. Matriz de confusão?
// [  ] 5.3. Curva ROC?
// [  ] 5.4. Curva de aprendizado?

// Notes
// Tipo de rede: classificação ou regressão?
// MAE ou MSE? // metricas para regressão?

public class NeuralNetwork {
    final String baseFilename;
    //    final String filepath = "C:/robocode/robots/autobot/neural/data/";
    final String filepath = "robots/autobot/neural/data/";
    final String trainingSuffix = "Training.arff";
    final String validationSuffix = "Validation.arff";
    final String networkSuffix = "Network.eg";
    BasicNetwork network;
    int outputCount;

    public NeuralNetwork(String filename, int inputCount, int hiddenCount, int outputCount) {
        System.out.println("Creating new network...");
        baseFilename = filename;
        this.network = createNetworkLayers(inputCount, hiddenCount, outputCount);
    }

    public NeuralNetwork(String filename) {
        System.out.println("Loading network...");
        baseFilename = filename;
        this.network = getNetwork(filename + networkSuffix);
        outputCount = network.getOutputCount();
    }

    // ===== Robocode Integration =====

    public void init() { // Check if there is saved network on file directory
        System.out.println("Initializing network...");
        String filename = baseFilename + networkSuffix;
        if (FileHandler.fileExists(filepath + filename))
            network = getNetwork(filename);
    }

    public void trainAndUpdateNetwork(Dataset dataset) {
        // Save dataset on .arff file for training
//        dataset.updateDatasetFile();
        dataset.saveDataset();
        String datasetFilename = dataset.getFilename();

        // Train network
        initTraining(datasetFilename, 0.8);

        // Save dataset history and clear current dataset
        dataset.saveHistoryDataset();

        // Save network on .eg file, so it can be loaded on next round;
        saveNetwork(baseFilename + networkSuffix);

    }

    public double[] predict(double... input) {
        MLData outputData = network.compute(new BasicMLDataSet(new double[][]{input}, new double[][]{{0, 0}}).get(0).getInput());
        return outputData.getData();
    } // TODO: implement on robocode

    // ===== Neural Network Config =====

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
        return (BasicNetwork) EncogDirectoryPersistence.loadObject(networkFile);
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
        System.out.print("Training the network... ");
        // Configure the training
        MLTrain train;
        train = new ResilientPropagation(network, dataset);  // RPROP configuration
        train.addStrategy(new StoppingStrategy(10)); // Stop training after 10 epochs without improvement

        // train = new Backpropagation(network, dataset, 0.01, 0.9);  // Backpropagation configuration
        // Backprogation did not converge. More tem 50k epochs and error still high

        System.out.println(dataset.size() + " instances...");
        int epoch = 1;
        while (!train.isTrainingDone()) {
            train.iteration();
//            System.out.println("Epoch #" + epoch + " Error: " + train.getError());
            epoch++;
        }
        train.finishTraining();
        System.out.println("Training complete. " + epoch + " epochs. " + train.getError() + " error.\n");
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
        System.out.println();

    }

    public void initTraining(String datasetFilename, double trainRatio) {
        // train network given the dataset .arff and save .eg network

        // Split the dataset into training and validation
        splitDataset(datasetFilename, trainRatio);

        // Load datasets
        BasicMLDataSet trainingSet = getDataSet(datasetFilename.replace(".arff", trainingSuffix), 2);
        BasicMLDataSet validationSet = getDataSet(datasetFilename.replace(".arff", validationSuffix), 2);

        // Train network
        runTraining(trainingSet);

        System.out.println("Training Error: " + network.calculateError(trainingSet));
        System.out.println("Validation Error: " + network.calculateError(validationSet));
        System.out.println();


//        printDatasetResults(trainingSet);
//        printDatasetResults(validationSet);
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


    public static void main(String[] args) {
        NeuralNetwork net = new NeuralNetwork("Autobot", 3, 10, 2);

//        net.init();
//        net.trainAndUpdateNetwork(new Dataset("Autobot"));
//        net.initTraining("Autobot.arff", 0.8);
//        net.saveNetwork("AutobotNetwork.eg");


//        net.saveHistoryDataset("Autobot.arff");


    }
}
