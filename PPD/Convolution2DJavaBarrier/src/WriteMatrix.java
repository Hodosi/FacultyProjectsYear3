import java.io.FileWriter;
import java.io.IOException;

public class WriteMatrix {
    public static void write(final int N, final int M, final double[][] matrix, final String filePath) {
        Utils.createFile(filePath);
        try {
            final FileWriter fileWriter = new FileWriter(filePath);

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    fileWriter.write(matrix[i][j] + " ");
                }
                fileWriter.write("\n");
            }

            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            System.out.println("Can't write into file: " + filePath);
            e.printStackTrace();
        }
    }
}
