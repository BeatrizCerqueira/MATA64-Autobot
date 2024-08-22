package autobot.neural;


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

/*
    // ========================
    // Neural network
    static BasicMLDataSet trainingSet;
    static BasicNetwork network = new BasicNetwork();
    static MLTrain train;

    private static void trainNetwork() {
        try {
            loadDataSet();
            addNeuralNetworkLayers();
            configureTraining();
            runTraining();
            printTrainingResults();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadDataSet() throws Exception {
        // Load the dataset
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("autobot/neural/data/Autobot.arff");
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        // Prepare the input and output arrays
        double[][] input = new double[data.numInstances()][data.numAttributes() - 1];
        double[][] output = new double[data.numInstances()][1];

        for (int i = 0; i < data.numInstances(); i++) {
            input[i][0] = data.instance(i).value(0); // current x
            input[i][1] = data.instance(i).value(1); // current y
            input[i][2] = data.instance(i).value(2); // angle
            input[i][3] = data.instance(i).value(3); // direction
            input[i][4] = data.instance(i).value(4); // velocity
            output[i][0] = data.instance(i).value(5); // next x
            output[i][1] = data.instance(i).value(6); // next y
        }

        trainingSet = new BasicMLDataSet(input, output);
        network.addLayer(new BasicLayer(null, true, data.numAttributes() - 1));  // Input layer

    }

    private static void addNeuralNetworkLayers() {
//        network.addLayer(new BasicLayer(null, true, 5));  // Input layer with 5 neurons
        network.addLayer(new BasicLayer(new ActivationReLU(), true, 10));  // Hidden layer 1 with 10 neurons
        network.addLayer(new BasicLayer(new ActivationReLU(), true, 10));  // Hidden layer 2 with 10 neurons
        network.addLayer(new BasicLayer(new ActivationLinear(), false, 2));  // Output layer with 2 neurons
        network.getStructure().finalizeStructure();
        network.reset();
    }

    private static void configureTraining() {
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
            if (train.getError() < 0.1) break;
        }
        train.finishTraining();
        Encog.getInstance().shutdown();
    }

    private static void printTrainingResults() {
        System.out.println("Results for Autobot dataset:");
        for (MLDataPair pair : trainingSet) {
            final MLData outputData = network.compute(pair.getInput());
            System.out.printf("Input: %s - Expected: [%.1f, %.1f] - Output: [%.1f, %.1f]%n",
                    pair.getInput().toString(), pair.getIdeal().getData(0), pair.getIdeal().getData(1),
                    outputData.getData(0), outputData.getData(1));
        }
    }
    */


    public static void main(String[] args) throws IOException {
        String[] attributesNames = {"enemyDistance", "enemyVelocity", "enemyAngle", "enemyHeading", "myGunToEnemyAngle", "firePower", "hit"};
        initDataCollection(attributesNames, 1);
        addInstance(100, 5, 80, 10, 10, 1, 1);
        addInstance(200, 6, 90, 10, 8, 2, 1);
        addInstance(300, 5, 92, 15, 5, 1, 0);
        saveDatasetFile();
//        trainNetwork();
    }
}
