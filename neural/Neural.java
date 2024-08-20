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

public class Neural {
    public static void main(String[] args) {
        // Define o conjunto de dados de treinamento (XOR)
        double[][] input = {
                {0.0, 0.0},
                {1.0, 0.0},
                {0.0, 1.0},
                {1.0, 1.0}
        };

        double[][] output = {
                {0.0},
                {1.0},
                {1.0},
                {0.0}
        };

        BasicMLDataSet trainingSet = new BasicMLDataSet(input, output);

        // Cria a rede neural
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 2));  // Camada de entrada (2 neurônios, com bias)
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 3));  // Camada oculta (3 neurônios, com bias)
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));  // Camada de saída (1 neurônio, sem bias)
        network.getStructure().finalizeStructure();
        network.reset();

        // Configura o treinamento da rede neural
        MLTrain train = new ResilientPropagation(network, trainingSet);

        // Estratégia de parada precoce (Early Stopping)
        EarlyStoppingStrategy earlyStopping = new EarlyStoppingStrategy(trainingSet);
        train.addStrategy(earlyStopping);

        // Treina a rede
        int epoch = 1;
        while (!train.isTrainingDone()) {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error: " + train.getError());
            epoch++;
        }

        train.finishTraining();

        // Testa a rede
        System.out.println("Resultado da rede neural para a função XOR:");
        for (MLDataPair pair : trainingSet) {
            final MLData outputData = network.compute(pair.getInput());
            System.out.printf("Entrada: %.1f, %.1f - Saída Esperada: %.1f - Saída da Rede: %.1f%n",
                    pair.getInput().getData(0), pair.getInput().getData(1),
                    pair.getIdeal().getData(0), outputData.getData(0));
        }

        // Libera recursos
        Encog.getInstance().shutdown();
    }

}
