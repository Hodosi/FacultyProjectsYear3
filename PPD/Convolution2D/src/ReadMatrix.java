import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadMatrix {
    public static double[][] read(final int N, final int M, final String filePath) {
        double[][] matrix = new double[N][M];
        try {
            final File file = new File(filePath);
            final Scanner scanner = new Scanner(file);
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    matrix[i][j] = scanner.nextDouble();
                }
            }
            scanner.close();
            return matrix;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return null;
        }

    }
}
