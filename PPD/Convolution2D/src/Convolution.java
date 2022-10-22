public class Convolution extends Thread {
    double[][] matrix, kernel, resultMatrix;
    int linStart, linEnd, colStart, colEnd;
    int N, M, n, m;

    public Convolution(double[][] matrix, double[][] kernel, double[][] resultMatrix, int linStart, int linEnd, int colStart, int colEnd, int N, int M, int n, int m) {
        this.matrix = matrix;
        this.kernel = kernel;
        this.resultMatrix = resultMatrix;
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

        for (int i = linStart; i < linEnd; i++) {
            for (int j = colStart; j < colEnd; j++) {
                resultMatrix[i][j] = applyConvolution(i, j);
            }
        }
    }

    private double applyConvolution(final int linMatrix, final int colMatrix) {
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
}
