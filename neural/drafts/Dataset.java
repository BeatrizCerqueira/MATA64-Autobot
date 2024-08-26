package autobot.neural.drafts;

import autobot.neural.FileHandler;
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
    private final String filepath;
    private final String filename;
    //    private final String baseFilepath = "C:/robocode/robots/autobot/neural/data/";
    private final String baseFilepath = "robots/autobot/neural/data/";

    public Dataset(String filename, String[] attributesNames) {
        this.filepath = baseFilepath + filename;
        this.filename = filename;
        createInstances(filename, attributesNames);
    }

    public Dataset(String filename) {
        this.filepath = baseFilepath + filename;
        this.filename = filename;
        initInstances();
        loadDataset();
    }

    private void initInstances() {
        // initialize instances with loaded data
        if (autobot.neural.FileHandler.fileExists(filepath)) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(filepath));
                ArffLoader.ArffReader arff = new ArffLoader.ArffReader(reader);
                instances = arff.getData();
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            instances = new Instances(filename, new ArrayList<>(), 0);
        }
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

    public void addInstance(double... values) { // expects value for each attribute in the dataset
        double[] instance = new double[values.length];
        System.arraycopy(values, 0, instance, 0, values.length);
        instances.add(new DenseInstance(1.0, instance));
    }


    public void updateDataset() {

    }

    public void saveDataset() {
        saveDatasetFile(instances, filepath);
    }

    private void saveDatasetFile(Instances data, String filepathToSave) {
        try {
            File file = new File(filepathToSave);
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
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

    public void saveHistoryDataset() {
        String historyFilename = autobot.neural.FileHandler.generateUniqueFilename(baseFilepath, filename);
        saveDatasetFile(instances, historyFilename);
    }


    public String getFilename() {
        return filename;
    }


    private Instances getInstances(String filepath) throws Exception {
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File(filepath));
        return loader.getDataSet();
    }

    public void resetDatasetFiles() {
        FileHandler.deleteFilesWithExtension(baseFilepath, ".arff");
    }

    public static void main(String[] args) {

    }

}
