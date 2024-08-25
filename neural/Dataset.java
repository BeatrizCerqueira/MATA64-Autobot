package autobot.neural;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Dataset {
    Instances instances;
    int inputCount;
    int outputCount;

    private final String filepath;


    public Dataset(String filename, String[] attributesNames) {
//        String baseFilepath = "C:/robocode/robots/autobot/neural/data/";
        String baseFilepath = "robots/autobot/neural/data/";
        this.filepath = baseFilepath + filename;
        createInstances(filename, attributesNames);
    }

    public void createInstances(String filename, String[] attributesNames) {
        ArrayList<Attribute> attributes = new ArrayList<>();

        // Define attributes
        for (String name : attributesNames) {
            attributes.add(new Attribute(name));
        }
        // Create dataset
        instances = new Instances(filename, attributes, 0);
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
//        System.out.println("Dataset saved successfully.");
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

        String[] attributesNames = {"enemyDistance", "enemyVelocity", "enemyAngleRelativeToGun", "hasHit", "hasNotHit"};
        Dataset dataset = new Dataset("test.arff", attributesNames);
        dataset.addInstance(1, 2, 3, 0, 1);
//        dataset.addInstance(enemyDistance, enemyVelocity, enemyAngleRelativeToGun, hasHit ? 0 : 1, hasHit ? 1 : 0);

    }

}
