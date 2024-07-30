package autobot.genetic;

import autobot.genetic.genes.Gene;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHandler {

    static String separatorGeneValues = ";";
    static String separatorChromosomes = "---";

    static final String filename = "Autobot.txt";
    static String filepath = "robots/autobot/genetic/data/" + filename;

    public static boolean fileExists() {
        return (new File(filepath)).exists();
    }

    public static void deleteFile() {
        File file = new File(filepath);
        if (file.delete())
            System.out.println("File deleted successfully.");
    }

    public static void saveToFile(Population population) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            for (Chromosome chromosome : population.getCurrentGeneration()) {
                for (Map.Entry<String, Gene> entry : chromosome.getGenes().entrySet()) {
                    String className = entry.getValue().getClass().getName();
                    writer.write(entry.getKey() + separatorGeneValues + className + separatorGeneValues + entry.getValue().getValue());
                    writer.newLine();
                }
                writer.write(separatorChromosomes); // Separator for chromosomes
                writer.newLine();
            }
            System.out.println("File saved successfully.");
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    public static List<Chromosome> loadGenesFromFile() {
        List<Chromosome> chromosomes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            Map<String, Gene> genes = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                if (line.equals(separatorChromosomes)) { // Separator for chromosomes
                    chromosomes.add(new Chromosome(genes));
                    genes = new HashMap<>();
                } else {
                    String[] geneDataComponents = line.split(separatorGeneValues);
                    String key = geneDataComponents[0];
                    String className = geneDataComponents[1];
                    int value = Integer.parseInt(geneDataComponents[2]);
                    Gene gene = (Gene) Class.forName(className).getConstructor(int.class).newInstance(value);
                    genes.put(key, gene);
                }
            }
            if (!genes.isEmpty()) {
                chromosomes.add(new Chromosome(genes));
            }

            System.out.println("File loaded successfully.");

        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return chromosomes;
    }
}
