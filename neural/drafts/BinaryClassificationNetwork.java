package autobot.neural.drafts;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class BinaryClassificationNetwork {

    static int numOutput = 2;

    public static BasicNetwork createNetwork(int inputCount, int hiddenCount) {
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, inputCount));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, hiddenCount));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, numOutput));
        network.getStructure().finalizeStructure();
        network.reset();
        return network;
    }

    private static MLDataSet LoadTrainingSet() {

        try {
            String filepath = "C:/robocode/robots/autobot/neural/data/Autobot.arff";
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(filepath);
            Instances data = source.getDataSet();

            data.setClassIndex(data.numAttributes() - 1);

            // Prepare the input and output arrays
            double[][] input = new double[data.numInstances()][data.numAttributes() - numOutput];
            double[][] output = new double[data.numInstances()][2];

            //populate input and output arrays
            for (int i = 0; i < data.numInstances(); i++) {
                for (int j = 0; j < data.numAttributes() - output[i].length; j++) {
                    input[i][j] = data.instance(i).value(j);
                }
                // Ensure the output array is correctly populated
                for (int k = 0; k < output[i].length; k++) {
                    output[i][k] = data.instance(i).value(data.numAttributes() - output[i].length + k);
                }
            }


            return new BasicMLDataSet(input, output);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void createNetwork() {

    }

    public static void main(String[] args) {
        int inputCount = 3; // input feature count
        int hiddenCount = 5; // hidden layer neuron count
        BasicNetwork network = createNetwork(inputCount, hiddenCount);

//        double[][] input = { /* Your input data */};
//        double[][] output = { /* Your output data (0 or 1) */};
//        MLDataSet trainingSet = new BasicMLDataSet(input, output);

        MLDataSet trainingSet = LoadTrainingSet();

        MLTrain train = new Backpropagation(network, trainingSet, 0.7, 0.7);

        for (int i = 0; i < 1000; i++) { // Example iteration count
            train.iteration();
            System.out.println("Iteration #" + i + " Error: " + train.getError());
        }

        printTrainingResults(trainingSet, network);

        Encog.getInstance().shutdown();
    }


    private static void printTrainingResults(MLDataSet dataSet, BasicNetwork network) {
        System.out.println("Results:");
        for (MLDataPair pair : dataSet) {
            final MLData outputData = network.compute(pair.getInput());
            System.out.print("Input: " + pair.getInput().toString() + " - ");
            System.out.print("Expected: " + pair.getIdeal().toString() + " - ");
            System.out.println("Output: " + outputData.toString());
            ;
        }
    }
}