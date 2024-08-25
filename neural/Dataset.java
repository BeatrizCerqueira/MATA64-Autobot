package autobot.neural;

import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Dataset {
    Instances instances;
    int inputCount;
    int outputCount;

    private final String filepath;


    public Dataset(String filename) {
        String baseFilepath = "robots/autobot/neural/data/";
        this.filepath = baseFilepath + filename;
    }

    public void addInstance(double... values) { // must receive same number of values as inputCount
        double[] instance = new double[values.length];
        System.arraycopy(values, 0, instance, 0, values.length);
        instances.add(new DenseInstance(1.0, instance));
    }

    public void updateDatasetFile() {
        if (FileHandler.fileExists(filepath)) {
            loadDataset();
        }
        saveDataset();
    }

    public void saveDataset() {
        System.out.println("Saving dataset to file: " + filepath);
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instances);
        try {
            saver.setFile(new File(filepath));
            saver.writeBatch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Dataset saved successfully.");
    }

    public void loadDataset() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
            Instances existingData = arff.getData();
            reader.close();

            // Merge existing data with the new data
            for (int i = 0; i < existingData.numInstances(); i++) {
                instances.add(existingData.instance(i));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

    }

}
