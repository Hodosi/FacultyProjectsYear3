public class Main {
    //        private static final String INPUT_FILES_PATH = "./src/resources/input/";
    private static final String INPUT_FILES_PATH = "resources/input/";
    //        private static final String OUTPUT_FILES_PATH = "./src/resources/output/";
    private static final String OUTPUT_FILES_PATH = "resources/output/";
    private static int p;
    private static int withThreads;
    private static String MATRIX_INPUT_FILENAME;
    private static String MATRIX_OUTPUT_FILENAME;
    private static String TEST_MATRIX_FILENAME;
    private static String KERNEL_FILENAME;
    private static int N;
    private static int M;
    private static int n;
    private static int m;
    private static double[][] resultMatrix;
    private static double[][] kernel;
    private static double[][] matrix;

    public static void main(String[] args) throws InterruptedException {

        p = Integer.parseInt(args[1]);
        withThreads = Integer.parseInt(args[2]);
        MATRIX_INPUT_FILENAME = args[3];
        MATRIX_OUTPUT_FILENAME = args[4];
        TEST_MATRIX_FILENAME = args[5];
        KERNEL_FILENAME = args[6];
        N = Integer.parseInt(args[7]);
        M = Integer.parseInt(args[8]);
        n = Integer.parseInt(args[9]);
        m = Integer.parseInt(args[10]);

        resultMatrix = new double[N][M];
        kernel = ReadMatrix.read(n, m, INPUT_FILES_PATH + KERNEL_FILENAME);
        matrix = ReadMatrix.read(N, M, INPUT_FILES_PATH + MATRIX_INPUT_FILENAME);

        long startTime = System.nanoTime();

        if (withThreads == 0) {
            sequentially();
        } else if (withThreads == 1) {
            parallel();
        }

        long endTime = System.nanoTime();

        WriteMatrix.write(N, M, resultMatrix, OUTPUT_FILES_PATH + MATRIX_OUTPUT_FILENAME);

        testResult();

        double time = (endTime - startTime) / 1e6;
        System.out.println(time);
    }

    private static void sequentially() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                resultMatrix[i][j] = Utils.applyConvolution(matrix, kernel, N, M, n, m, i, j);
            }
        }
    }

    public static void parallel() throws InterruptedException {
        Thread[] threads = new Thread[p];

        int linStart = 0, linEnd = 0, colStart = 0, colEnd = 0, chunk, rest;
        if (N >= M) {
            colEnd = M;
            chunk = N / p;
            rest = N % p;
        } else {
            linEnd = N;
            chunk = N / p;
            rest = N % p;
        }

        for (int i = 0; i < p; i++) {
            if (N >= M) {
                linEnd = linStart + chunk;
                if (rest > 0) {
                    linEnd++;
                    rest--;
                }
            } else {
                colEnd = colStart + chunk;
                if (rest > 0) {
                    colEnd++;
                    rest--;
                }
            }

            threads[i] = new Convolution(matrix, kernel, resultMatrix, linStart, linEnd, colStart, colEnd, N, M, n, m);
            threads[i].start();

            if (N >= M) {
                linStart = linEnd;
            } else {
                colStart = colEnd;
            }
        }

        for (int i = 0; i < p; i++) {
            threads[i].join();
        }

    }

    private static void testResult() {
        double[][] realOutput = ReadMatrix.read(N, M, OUTPUT_FILES_PATH + TEST_MATRIX_FILENAME);
        double[][] actualOutput = ReadMatrix.read(N, M, OUTPUT_FILES_PATH + MATRIX_OUTPUT_FILENAME);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (actualOutput[i][j] != realOutput[i][j]) {
                    System.out.println("Invalid result at line: " + i + " and column: " + j);
                    System.out.println("Actual value: " + actualOutput[i][j]);
                    System.out.println("Real value: " + realOutput[i][j]);
                }
            }
        }
    }
}
