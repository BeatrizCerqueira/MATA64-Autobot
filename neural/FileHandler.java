package autobot.neural;

import java.io.*;

public class FileHandler {

    public static boolean fileExists(String filepath) {
        return (new File(filepath)).exists();
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

    public static void deleteFilesWithExtension(String directoryPath, String extension) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Invalid directory: " + directoryPath);
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(extension));
        if (files != null) {
            for (File file : files) {
                if (file.delete()) {
                    System.out.println("Deleted file: " + file.getAbsolutePath());
                } else {
                    System.out.println("Failed to delete file: " + file.getAbsolutePath());
                }
            }
        }
    }

    private static void deleteFile(String filepathToDelete) {
        File file = new File(filepathToDelete);
        if (file.delete())
            System.out.println("Deleted File " + filepathToDelete);
    }

    public static void copyFileTo(String currentFilePath, String destDir) {
        String baseFilename = new File(currentFilePath).getName();
        String destFilePath = generateUniqueFilename(destDir, baseFilename);

        System.out.println("Copying file to: " + destFilePath);

        try {
            // Create destination directory if it does not exist
            File dir = new File(destDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Copy current file to destination directory
            File sourceFile = new File(currentFilePath);
            File destFile = new File(destFilePath);
            copyFile(sourceFile, destFile);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyFile(File source, File dest) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(source));
             BufferedWriter writer = new BufferedWriter(new FileWriter(dest))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static void main(String[] args) {
        deleteFilesWithExtension("C:/robocode/robots/autobot/neural/data", ".arff");


    }

}
