import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileWrite {
    public static void writeArrayListToLines(String filePath, ArrayList<String> fileContent) {
        FileWrite.createFile(filePath);
        try {
            final FileWriter fileWriter = new FileWriter(filePath);
            for (String line : fileContent) {
                fileWriter.write(line + "\n");
            }
            fileWriter.close();
            System.out.println("Successfully wrote to the file: " + filePath);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            System.out.println("Can't write into file: " + filePath);
            e.printStackTrace();
        }
    }

    public static void createFile(String filePath) {
        try {
            final File file = new File(filePath);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            System.out.println("Can't create file: " + filePath);
            e.printStackTrace();
        }
    }
}
