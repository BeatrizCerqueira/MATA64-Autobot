package autobot.neural.drafts;


import autobot.neural.FileHandler;

import java.io.IOException;

public class NeuralNetworkFileHandler {

    // Passo a passo
    // [OK] 1. Coletar dados
    // [OK] 2. Preparar os dados
    // [OK] 2.1. Normalizar os dados
    // [--] 2.2. Dividir os dados em treino e teste
    // [OK] 3. Definir a arquitetura da rede neural
    // [OK] 3.1. Definir o número de camadas e neurônios
    // [OK] 3.2. Definir a função de ativação
    // [??] 3.3 Inicializar os pesos (como? técncias de inicialização xavier?)
    // [  ] 4. Treinar a rede neural
    // [  ] 4.1 Função de custo (MSE, Cross-Entropy)??
    // [OK] 4.2 Otimizador (SGD, Adam, Backpropagation, RPROP, etc)???
    // [  ] 5. Avaliar a rede neural
    // [  ] 5.1. Métricas de avaliação (Acurácia, Precisão, Recall, F1-Score, etc)?
    // [  ] 5.2. Matriz de confusão?
    // [  ] 5.3. Curva ROC?
    // [  ] 5.4. Curva de aprendizado?


    // ========================
    // TODO: make methods non-static so i can declare the neural network as a field, with different attributes
    // TODO: set activation function as parameter
    // TODO: divide data folders?
    // ========================
    // ROBOCODE Integration
    static FileHandler fileHandler = new FileHandler();
    static int numOutputs;


    public static void initDataCollection(String[] attributesNames, int numOutputs) {
        fileHandler.initDataset(attributesNames);
        NeuralNetworkFileHandler.numOutputs = numOutputs;
    }

    public static void addInstance(double... values) {
        fileHandler.addInstances(values);   // DataHandler.addInstance(currentX, currentY, enemyAngle, enemyHeading, velocity, nextX, nextY);
    }

    public static void saveDatasetFile() {
        fileHandler.updateDatasetFile();
    }

    public static void loadNeuralNetwork() {
        // load neural network
    }


    public static void main(String[] args) throws IOException {
        // create dataset
        // normalize data
    }
}
