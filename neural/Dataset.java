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
    private final String filepath;
    private final String filename;
    //    private final String baseFilepath = "C:/robocode/robots/autobot/neural/data/";
    private final String baseFilepath = "robots/autobot/neural/data/";
    private final String historySuffix = "History.arff";


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
        if (FileHandler.fileExists(filepath)) {
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

/*

    public void updateDatasetFile() {
        if (FileHandler.fileExists(filepath)) {
            loadDataset();
        }
        saveDataset();
    }
*/

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

    /*

        private void appendInstances(Instances data, String filename)
        {
            try {
                File file = new File(filename);
                ArffSaver saver = new ArffSaver();
                saver.setInstances(data);

                if (file.exists()) {
                    // Append to existing file
                    saver.setFile(file);
                    saver.setDestination(file);
                    saver.setRetrieval(ArffSaver.INCREMENTAL);
                    saver.writeIncremental(data.get(0));

                    for (int i = 1; i < data.numInstances(); i++) {
                        saver.writeIncremental(data.get(i));
                    }

                    saver.writeIncremental(null); // Signal end of data
                } else {
                    // Create new file
                    saver.setFile(file);
                    saver.writeBatch();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void saveInstances(Instances data, String filepath) {
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            try {
                saver.setFile(new File(filepath));
                saver.writeBatch();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
    */

    public void saveDataset() {
        System.out.println("Saving dataset to file: " + filepath);
        saveDatasetFile(instances, filepath);
    }

    private void saveDatasetFile(Instances data, String filepathToSave) {
        try {
            File file = new File(filepathToSave);
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);

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
        String currentFile = filepath;
        String historyDir = baseFilepath + "history/";
        String historyFile = FileHandler.generateUniqueFilename(historyDir, filename);

        System.out.println("Saving history dataset: " + historyFile);

        try {
            // Create history directory if it does not exist
            File dir = new File(historyDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Copy current file to history directory
            File sourceFile = new File(currentFile);
            File destFile = new File(historyFile);
            FileHandler.copyFile(sourceFile, destFile);

            // Clear currentFile
            deleteFile(currentFile);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



/*
    public void saveHistoryDataset() {
        String currentFile = filepath;
        String historyFile = filepath.replace(".arff", historySuffix);

        System.out.println("Saving history dataset: " + historyFile);
        try {
            // Load the contents of currentFile
            Instances currentData = getInstances(currentFile);

            // Append the contents to history file
            saveDatasetFile(currentData, historyFile);

            // Clear currentFile
            deleteFile(currentFile);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
*/

    private void deleteFile(String filepathToDelete) {
        File file = new File(filepathToDelete);
        if (file.delete())
            System.out.println("Deleted File " + filepathToDelete);
    }

    public String getFilename() {
        return filename;
    }

    private Instances getInstances(String filepath) throws Exception {
        ArffLoader loader = new ArffLoader();
        loader.setFile(new File(filepath));
        return loader.getDataSet();
    }


    public static void main(String[] args) {

//        String[] attributesNames = {"enemyDistance", "enemyVelocity", "enemyAngleRelativeToGun", "hasHit", "hasNotHit"};
//        Dataset dataset = new Dataset("test.arff", attributesNames);
//        dataset.addInstance(1, 2, 3, 0, 1);

        String[] attributesNames = {"enemyDistance", "enemyVelocity", "enemyDirectionRelativeToGun", "notHit", "hit"};
        Dataset dataset = new Dataset("Autobot.arff", attributesNames);
        dataset.addInstance(1, 2, 3, 0, 1);
        dataset.saveDataset();
        dataset.saveHistoryDataset();

        Dataset ds2 = new Dataset("Autobot.arff", attributesNames);
        ds2.addInstance(2, 2, 3, 0, 1);
        ds2.saveDataset();
        ds2.saveHistoryDataset();


//        Dataset dataset = new Dataset("Autobot.arff");

    }

}
