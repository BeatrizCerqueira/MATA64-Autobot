package autobot.neural.drafts;

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

public class FileHandler {
    private Instances dataset;

    private final String filename = "Autobot.arff";
    private final String filepath = "robots/autobot/neural/data/" + filename;
//    private final String filepath = "C:/robocode/robots/autobot/neural/data/" + filename;

    public void initDataset(String[] attributesNames) {
        ArrayList<Attribute> attributes = new ArrayList<>();

        // Define attributes
        for (String name : attributesNames) {
            attributes.add(new Attribute(name));
        }
        // Create dataset
        dataset = new Instances("AutobotDataset", attributes, 0);
    }

    public void addInstances(double[] values) {
        double[] instance = new double[values.length];
        System.arraycopy(values, 0, instance, 0, values.length);
        dataset.add(new DenseInstance(1.0, instance));
    }

    public void saveDatasetFile() {
        System.out.println("Saving dataset to file: " + filepath);
        ArffSaver saver = new ArffSaver();
        saver.setInstances(dataset);
        try {
            saver.setFile(new File(filepath));
            saver.writeBatch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Dataset saved successfully.");
    }

    public void loadDatasetFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
            Instances existingData = arff.getData();
            reader.close();

            // Merge existing data with the new data
            for (int i = 0; i < existingData.numInstances(); i++) {
                dataset.add(existingData.instance(i));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateDatasetFile() {
        if (hasFile()) {
            loadDatasetFile();
        }
        saveDatasetFile();
    }

    public boolean hasFile() {
        return (new File(filepath)).exists();
    }

    public static void main(String[] args) {
        FileHandler fileHandler = new FileHandler();

//        String[] attributesNames = {"currentX", "currentY", "enemyAngle", "enemyHeading", "velocity", "prediction"};
        fileHandler.initDataset(new String[]{"currentX", "currentY", "enemyAngle", "enemyHeading", "velocity", "prediction"});

        double[] attributesValues = {1, 2, 3, 4, 5, 6};
        fileHandler.addInstances(attributesValues);

        fileHandler.saveDatasetFile();
    }

}
