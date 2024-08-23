package autobot.neural;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

import java.io.File;

public class DataNormalizer {

    //    private static final String filepath = "robots/autobot/neural/data/Autobot.arff";
    private static final String filepath = "C:/robocode/robots/autobot/neural/data/Autobot.arff";


    public static void normalizeData() throws Exception {
        // Load the dataset
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(filepath);
        Instances data = source.getDataSet();

        // Normalize the data
        Normalize normalize = new Normalize();
        normalize.setInputFormat(data);
        Instances normalizedData = Filter.useFilter(data, normalize);

        // Save the normalized data to a new file
        ArffSaver saver = new ArffSaver();
        saver.setInstances(normalizedData);
        saver.setFile(new File(filepath.replace(".arff", "_normalized.arff")));
        saver.writeBatch();
    }

    public static void main(String[] args) {
        try {
            normalizeData();
            System.out.println("Data normalization complete.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
