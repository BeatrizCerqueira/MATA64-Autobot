package autobot.neural;

import java.io.*;

public class FileHandler {

    public static boolean fileExists(String filepath) {
        return (new File(filepath)).exists();
    }

    public static void copyFile(File source, File dest) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(source));
             BufferedWriter writer = new BufferedWriter(new FileWriter(dest))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static String generateUniqueFilename(String baseDir, String baseFilename) {
        int counter = 1;
        String uniqueFilename;
        do {
            uniqueFilename = baseDir + baseFilename.replace(".arff", "_" + counter + ".arff");
            counter++;
        } while (new File(uniqueFilename).exists());
        return uniqueFilename;
    }

}
