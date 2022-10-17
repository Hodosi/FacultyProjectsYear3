import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReaderFromFile {
    public static String readFile(String FilePath) {
        try {
            final StringBuilder fileContent = new StringBuilder();
            final File file = new File(FilePath);
            final Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                fileContent.append(line);
            }
            scanner.close();
            return fileContent.toString();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return "";
        }
    }

    public static ArrayList<String> loadInputLinesToArrayList(String filePath) {
        final ArrayList<String> inputLinesArray = new ArrayList<>();
        try {
            final File file = new File(filePath);
            final Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                inputLinesArray.add(line);
            }
            scanner.close();
            return inputLinesArray;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return inputLinesArray;
        }
    }
}
