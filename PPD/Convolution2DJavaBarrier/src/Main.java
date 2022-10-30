import java.util.concurrent.CyclicBarrier;

import static java.lang.Math.abs;

public class Main extends Utils {
//    private static final String INPUT_FILES_PATH = "./src/resources/input/";
        private static final String INPUT_FILES_PATH = "resources/input/";
//    private static final String OUTPUT_FILES_PATH = "./src/resources/output/";
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
    private static double[][] kernel;
    private static double[][] matrix;

    public static CyclicBarrier cyclicBarrier;

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

//        p = 4;
//        withThreads = 1;
//        MATRIX_INPUT_FILENAME = "testMatrix.txt";
//        MATRIX_OUTPUT_FILENAME = "testMatrixWith0Threads.txt";
//        TEST_MATRIX_FILENAME = "testMatrixWith0Threads.txt";
//        KERNEL_FILENAME = "testKernel.txt";
//        N = 6;
//        M = 6;
//        n = 3;
//        m = 3;

        if(p > 0){
            cyclicBarrier = new CyclicBarrier(p);
        }

        kernel = ReadMatrix.read(n, m, INPUT_FILES_PATH + KERNEL_FILENAME);
        matrix = ReadMatrix.read(N, M, INPUT_FILES_PATH + MATRIX_INPUT_FILENAME);

        long startTime = System.nanoTime();

        if (withThreads == 0) {
            sequentially();
        } else if (withThreads == 1) {
            parallel();
        }

        long endTime = System.nanoTime();

        WriteMatrix.write(N, M, matrix, OUTPUT_FILES_PATH + MATRIX_OUTPUT_FILENAME);

        testResult();

        double time = (endTime - startTime) / 1e6;
        System.out.println(time);
    }


    private static void sequentially() {
        double[][] temp;
        if (N >= M) {
            // copy start lines
            temp = new double[n / 2 + 1][M];
            for (int i = 0; i < n / 2 + 1; i++) {
                System.arraycopy(matrix[0], 0, temp[i], 0, M);
            }

            // copy end line
            double[] tempEnd = new double[N];
            System.arraycopy(matrix[N - 1], 0, tempEnd, 0, M);

            for (int i = 0; i < N; i++) {

                double[] currentLine = new double[M];
                System.arraycopy(matrix[i], 0, temp[n / 2], 0, M);
                System.arraycopy(matrix[i], 0, currentLine, 0, M);

                // apply convolution on line
                for (int j = 0; j < M; j++) {
                    matrix[i][j] = Utils.applyConvolutionOnLines(matrix, temp, tempEnd, kernel, N, M, n, m, i, j);
                }

                System.arraycopy(currentLine, 0, temp[n / 2], 0, M);

                // move lines
                for (int k = 1; k < n / 2 + 1; k++) {
                    System.arraycopy(temp[k], 0, temp[k - 1], 0, M);
                }
            }
        } else {
            // copy start columns
            temp = new double[N][m / 2 + 1];
            for(int j = 0; j < m / 2 + 1; j++){
                for(int i = 0; i < N; i++){
                    temp[i][j] = matrix[i][0];
                }
            }

            // copy end column
            double[][] tempEnd = new double[N][1];
            for (int i = 0; i < N; i++) {
                tempEnd[i][0] = matrix[i][M - 1];
            }

            // start convolution on columns
            for (int j = 0; j < M; j++) {

                double[][] currentColumn = new double[N][1];

                for (int i = 0; i < N; i++) {
                    temp[i][m / 2] = matrix[i][j];
                    currentColumn[i][0] = matrix[i][j];
                }

                // apply convolution on columns
                for (int i = 0; i < N; i++) {
                    matrix[i][j] = Utils.applyConvolutionOnColumns(matrix, temp, tempEnd, kernel, N, M, n, m, i, j);
                }

                for (int i = 0; i < N; i++) {
                    temp[i][m / 2] = currentColumn[i][0];
                }

                // move columns
                for (int k = 0; k < N; k++) {
                    System.arraycopy(temp[k], 1, temp[k], 0, m / 2);
                }
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
            chunk = M / p;
            rest = M % p;
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

            threads[i] = new Convolution(matrix, kernel, linStart, linEnd, colStart, colEnd, N, M, n, m);
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
                if (abs(actualOutput[i][j] - realOutput[i][j]) > 1) {
                    System.out.println("Invalid result at line: " + i + " and column: " + j);
                    System.out.println("Actual value: " + actualOutput[i][j]);
                    System.out.println("Real value: " + realOutput[i][j]);
                }
            }
        }
    }
}
