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
//        private final String baseFilepath = "C:/robocode/robots/autobot/neural/data/";
    private final String baseFilepath = "robots/autobot/neural/data/";
    Instances instances;

    public Dataset(String[] attributesNames) { // init dataset from attributes
        createInstances(attributesNames);
    }

    public Dataset(String name) {  // init dataset with all arff files from current directory
        instances = new Instances(name, new ArrayList<>(), 0);

        // for each file in directory, load instances
        File[] files = FileHandler.getFilesWithExtension(baseFilepath, ".arff");

        for (File file : files) {
            loadInstancesFromFile(file.getName());
        }
    }



    public Instances getInstances() {
        return instances;
    }

    public void createInstances(String[] attributesNames) {
        ArrayList<Attribute> attributes = new ArrayList<>();

        // Define attributes
        for (String name : attributesNames) {
            attributes.add(new Attribute(name));
        }
        // Create dataset
        instances = new Instances("AutobotDataset", attributes, 0);
    }

    private Instances getInstancesFromFile(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
            return arff.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadInstancesFromFile(String filename) {
        String filepath = baseFilepath + filename;
        Instances existingData = getInstancesFromFile(filepath);
        // Merge existing data with the new data
        for (int i = 0; i < existingData.numInstances(); i++) {
            instances.add(existingData.instance(i));
        }
    }

    public void appendInstance(double... values) { // expects value for each attribute in the dataset
        double[] instance = new double[values.length];
        System.arraycopy(values, 0, instance, 0, values.length);
        instances.add(new DenseInstance(1.0, instance));
    }

    public void saveFile(String filename, String dir) {
        String filepathToSave = baseFilepath + dir + "/" + filename;
        saveFileToDir(filepathToSave);
    }

    public void saveFile(String filename){
        String filepathToSave = baseFilepath + filename;
        saveFileToDir(filepathToSave);
    }

    private void saveFileToDir(String filepathToSave) {
//        String filepathToSave = baseFilepath + filename;
        try {
            File file = new File(filepathToSave);
            ArffSaver saver = new ArffSaver();
            saver.setInstances(instances);
            System.out.print("Saving... ");
            if (file.exists()) {
                System.out.println("Existing file: " + filepathToSave);

                // Append to existing file
                BufferedReader reader = new BufferedReader(new FileReader(filepathToSave));
                ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
                Instances existingData = arff.getData();
                reader.close();

                // Merge existing data with the new data
                for (int i = 0; i < existingData.numInstances(); i++) {
                    existingData.add(existingData.instance(i));
                }

                // Save merged data
                saver.setInstances(existingData);
                saver.setFile(file);
                saver.writeBatch();

            } else {
                System.out.println("New file: " + filepathToSave);
                // Create new file
                saver.setFile(file);
                saver.writeBatch();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteDatasetFiles() {
        FileHandler.deleteFilesWithExtension(baseFilepath, ".arff");
    }

    public static void main(String[] args) {
        Dataset dataset = new Dataset("history");
        dataset.saveFile("AutobotDatasetHistory.arff", "history"); // Save history files with retrieved data until now
        dataset.deleteDatasetFiles(); // Remove temp arff files, keep only history

    }

}
