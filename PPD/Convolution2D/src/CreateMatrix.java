import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class CreateMatrix {
    private static final String INPUT_FILES_PATH = "./src/resources/input/";
    private static final String FILENAME = "matrix10000pe10.txt";
    private static final int N = 10000;
    private static final int M = 10;

    public static void main(String[] args) {
        final String filePath = INPUT_FILES_PATH + FILENAME;
        Utils.createFile(filePath);
        try {
            final FileWriter fileWriter = new FileWriter(filePath);
            Random r = new Random();
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    double value = r.nextDouble() * 100;
//                    System.out.print(value + "\t");
                    fileWriter.write(value + " ");
                }
                System.out.print("\n");
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
