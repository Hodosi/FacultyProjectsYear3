import java.util.concurrent.BrokenBarrierException;

public class Convolution extends Thread {
    double[][] matrix, kernel, tempStart, tempEnd;
    int linStart, linEnd, colStart, colEnd;
    int N, M, n, m;

    public Convolution(double[][] matrix, double[][] kernel, int linStart, int linEnd, int colStart, int colEnd, int N, int M, int n, int m) {
        this.matrix = matrix;
        this.kernel = kernel;
        this.linStart = linStart;
        this.linEnd = linEnd;
        this.colStart = colStart;
        this.colEnd = colEnd;
        this.N = N;
        this.M = M;
        this.n = n;
        this.m = m;
    }

    @Override
    public void run() {
        super.run();

        if (N >= M) {
            // init temps
            tempStart = new double[n / 2 + 1][M];
            tempEnd = new double[n / 2 + 1][M];

            //copy start lines
            for (int i = linStart; i < linStart + n / 2 + 1; i++) {
                int linCopy = i - n / 2;
                if (linCopy < 0) {
                    linCopy = 0;
                }
                System.arraycopy(matrix[linCopy], 0, tempStart[i - linStart], 0, M);
            }

            // copy end lines
            for (int i = linEnd - 1; i < linEnd + n / 2; i++) {
                int linCopy = i;
                if (linCopy > N - 1) {
                    linCopy = N - 1;
                }
                System.arraycopy(matrix[linCopy], 0, tempEnd[i - linEnd + 1], 0, M);
            }

            try {
                Main.cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            // start convolution on lines
            for (int i = linStart; i < linEnd; i++) {

                double[] currentLine = new double[M];
                System.arraycopy(matrix[i], 0, tempStart[n / 2], 0, M);
                System.arraycopy(matrix[i], 0, currentLine, 0, M);

                // apply convolution on line
                for (int j = colStart; j < colEnd; j++) {
                    matrix[i][j] = this.applyConvolutionOnLines(i, j);
                }

                System.arraycopy(currentLine, 0, tempStart[n / 2], 0, M);

                // move lines
                for (int k = 1; k < n / 2 + 1; k++) {
                    System.arraycopy(tempStart[k], 0, tempStart[k - 1], 0, M);
                }
            }
        } else {
            // init temps
            tempStart = new double[N][m / 2 + 1];
            tempEnd = new double[N][m / 2 + 1];

            // copy start columns
            for (int j = colStart; j < colStart + m / 2 + 1; j++) {
                int colCopy = j - m / 2;
                if (colCopy < 0) {
                    colCopy = 0;
                }
                for (int i = 0; i < N; i++) {
                    tempStart[i][j - colStart] = matrix[i][colCopy];
                }
            }

            // copy end columns
            for (int j = colEnd - 1; j < colEnd + m / 2; j++) {
                int colCopy = j;
                if (colCopy > M - 1) {
                    colCopy = M - 1;
                }
                for (int i = 0; i < N; i++) {
                    tempEnd[i][j - colEnd + 1] = matrix[i][colCopy];
                }
            }

            try {
                Main.cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }

            // start convolution on columns
            for (int j = colStart; j < colEnd; j++) {

                double[][] currentColumn = new double[N][1];

                for (int i = 0; i < N; i++) {
                    tempStart[i][m / 2] = matrix[i][j];
                    currentColumn[i][0] = matrix[i][j];
                }

                // apply convolution on columns
                for (int i = linStart; i < linEnd; i++) {
                    matrix[i][j] = this.applyConvolutionOnColumns(i, j);
                }

                for (int i = 0; i < N; i++) {
                    tempStart[i][m / 2] = currentColumn[i][0];
                }

                // move columns
                for (int k = 0; k < N; k++) {
                    System.arraycopy(tempStart[k], 1, tempStart[k], 0, m / 2);
                }
            }
        }
    }

    public double applyConvolutionOnLines(final int linMatrix, final int colMatrix) {
        double convolutionResult = 0;
        int linKernel, colKernel;
        for (linKernel = 0; linKernel < n / 2 + 1; linKernel++) {
            for (colKernel = 0; colKernel < m; colKernel++) {
                int colConvolution = colMatrix + colKernel - m / 2;

                if (colConvolution < 0) {
                    colConvolution = 0;
                }

                if (colConvolution >= M) {
                    colConvolution = M - 1;
                }
                convolutionResult += tempStart[linKernel][colConvolution] * kernel[linKernel][colKernel];
            }
        }

        for (linKernel = n / 2 + 1; linKernel < n; linKernel++) {
            for (colKernel = 0; colKernel < m; colKernel++) {
                int linConvolution = linMatrix + linKernel - n / 2;
                int colConvolution = colMatrix + colKernel - m / 2;

                if (colConvolution < 0) {
                    colConvolution = 0;
                }
                if (colConvolution >= M) {
                    colConvolution = M - 1;
                }

                if (linConvolution >= linEnd - 1) {
                    convolutionResult += tempEnd[linConvolution - linEnd + 1][colConvolution] * kernel[linKernel][colKernel];
                } else {
                    convolutionResult += matrix[linConvolution][colConvolution] * kernel[linKernel][colKernel];
                }
            }
        }

        return convolutionResult;
    }

    public double applyConvolutionOnColumns(final int linMatrix, final int colMatrix) {
        double convolutionResult = 0;
        int linKernel, colKernel;
        for (linKernel = 0; linKernel < n; linKernel++) {
            for (colKernel = 0; colKernel < m / 2 + 1; colKernel++) {
                int linConvolution = linMatrix + linKernel - n / 2;

                if (linConvolution < 0) {
                    linConvolution = 0;
                }

                if (linConvolution >= N) {
                    linConvolution = N - 1;
                }

                convolutionResult += tempStart[linConvolution][colKernel] * kernel[linKernel][colKernel];
            }
        }

        for (linKernel = 0; linKernel < n; linKernel++) {
            for (colKernel = m / 2 + 1; colKernel < m; colKernel++) {
                int linConvolution = linMatrix + linKernel - n / 2;
                int colConvolution = colMatrix + colKernel - m / 2;

                if (linConvolution < 0) {
                    linConvolution = 0;
                }

                if (linConvolution >= N) {
                    linConvolution = N - 1;
                }

                if (colConvolution >= colEnd - 1) {
                    convolutionResult += tempEnd[linConvolution][colConvolution - colEnd + 1] * kernel[linKernel][colKernel];
                } else {
                    convolutionResult += matrix[linConvolution][colConvolution] * kernel[linKernel][colKernel];
                }
            }
        }

        return convolutionResult;
    }
}
