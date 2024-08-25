package autobot.neural;

import java.io.File;

public class FileHandler {

    public static boolean fileExists(String filepath) {
        return (new File(filepath)).exists();
    }

}
