import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class CreateMatrix {
    private static final String INPUT_FILES_PATH = "./resources/input/";
    private static final String FILENAME = "matrix3pe3.txt";
    private static final int N = 3;
    private static final int M = 3;

    public static void main(String[] args) {
        final String filePath = INPUT_FILES_PATH + FILENAME;
        CreateMatrix.createFile(filePath);
        try {
            final FileWriter fileWriter = new FileWriter(filePath);
            Random r = new Random();
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    System.out.print(r.nextDouble() + "\t");
                    fileWriter.write(r.nextDouble() + " ");
                }
                System.out.print("\n");
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createFile(String filePath) {
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
