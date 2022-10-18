public class Main {
    private static final String INPUT_FILES_PATH = "./resources/input/";
    private static final String MATRIX_FILENAME = "testMatrix.txt";
    private static final String WINDOW_FILENAME = "testWindow.txt";
    private static final int N = 6;
    private static final int M = 6;
    private static final int n = 3;
    private static final int m = 3;
    private static double[][] resultMatrix;
    private static double[][] window;
    private static double[][] matrix;


    public static void main(String[] args) {
        resultMatrix = new double[N][M];
        window = ReadMatrix.read(n, m, INPUT_FILES_PATH + WINDOW_FILENAME);
        matrix = ReadMatrix.read(N, M, INPUT_FILES_PATH + MATRIX_FILENAME);
        long startTime = System.nanoTime();
        sequentially();
        long endTime = System.nanoTime();
        printResultMatrix();
        double time = (endTime - startTime) / 1e6;
        System.out.println(time);
    }

    private static void sequentially() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                resultMatrix[i][j] = applyConvolution(i, j);
            }
        }
    }

    private static double applyConvolution(final int linMatrix, final int colMatrix) {
        double convolutionResult = 0;
        for (int linWindow = 0; linWindow < n; linWindow++) {
            for (int colWindow = 0; colWindow < m; colWindow++) {
                int linConvolution = linMatrix + linWindow - n / 2;
                int colConvolution = colMatrix + colWindow - m / 2;

                while (linConvolution < 0) {
                    linConvolution++;
                }
                while (colConvolution < 0) {
                    colConvolution++;
                }
                while (linConvolution >= N) {
                    linConvolution--;
                }
                while (colConvolution >= M) {
                    colConvolution--;
                }

                convolutionResult += matrix[linConvolution][colConvolution] * window[linWindow][colWindow];
            }
        }
        return convolutionResult;
    }

    private static void printResultMatrix() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                System.out.print(resultMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}