import java.io.File;
import java.io.IOException;

public class Utils {
    public static double applyConvolution(final double[][] matrix, final double[][] kernel, final int N, final int M, final int n, final int m, final int linMatrix, final int colMatrix) {
        double convolutionResult = 0;
        int linKernel, colKernel;
        for (linKernel = 0; linKernel < n; linKernel++) {
            for (colKernel = 0; colKernel < m; colKernel++) {
                int linConvolution = linMatrix + linKernel - n / 2;
                int colConvolution = colMatrix + colKernel - m / 2;

                if (linConvolution < 0) {
                    linConvolution = 0;
                }
                if (colConvolution < 0) {
                    colConvolution = 0;
                }
                if (linConvolution >= N) {
                    linConvolution = N - 1;
                }
                if (colConvolution >= M) {
                    colConvolution = M - 1;
                }

                convolutionResult += matrix[linConvolution][colConvolution] * kernel[linKernel][colKernel];
            }
        }
        return convolutionResult;
    }

    public static void createFile(String filePath) {
        try {
            final File file = new File(filePath);
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            System.out.println("Can't create file: " + filePath);
            e.printStackTrace();
        }
    }
}
