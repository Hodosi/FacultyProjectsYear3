#include <iostream>
#include <thread>
#include <fstream>
#include <chrono>
#include <vector>
#include <mutex>
#include <condition_variable>

using namespace std;
#define MAX 10000 // dim maxima
#define MAX_KERNEL 5 // dim maxima

//string INPUT_FILES_PATH = "resources/input/";
//string OUTPUT_FILES_PATH = "resources/output/";

string INPUT_FILES_PATH = "C:\\UserDisk\\FacultyProjectsYear3\\PPD\\Convolution2DcppBarrier\\resources\\input\\";
string OUTPUT_FILES_PATH = "C:\\UserDisk\\FacultyProjectsYear3\\PPD\\Convolution2DcppBarrier\\resources\\output\\";

int p;
int withThreads;
string MATRIX_INPUT_FILENAME;
string MATRIX_OUTPUT_FILENAME;
string TEST_MATRIX_FILENAME;
string KERNEL_FILENAME;
int N;
int M;
int n;
int m;
double kernelStatic[5][5];
double matrixStatic[MAX][MAX];
double resultMatrixStatic[MAX][MAX];

class my_barrier {
public:
    my_barrier(int count) : thread_count(count), counter(0), waiting(0) {}

    int thread_count;

    void wait() {
        //fence mechanism
        std::unique_lock<std::mutex> lk(m);
        ++counter;
        ++waiting;
        cv.wait(lk, [&] { return counter >= thread_count; });
        cv.notify_one();
        --waiting;
        if (waiting == 0) {  //reset barrier
            counter = 0;
        }
        lk.unlock();
    }

private:
    std::mutex m;
    std::condition_variable cv;
    int counter;
    int waiting;
};

my_barrier barrier(0);

void ReadKernel_readStatic() {
    ifstream fin(INPUT_FILES_PATH + KERNEL_FILENAME);
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            fin >> kernelStatic[i][j];
        }
    }
    fin.close();
}


void PrintKernel_print() {
    cout << "\nKernel:\n";
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            cout << kernelStatic[i][j] << " ";
        }
        cout << "\n";
    }
}

void ReadMatrix_readStatic(bool inputMatrix) {
    ifstream fin;
    if (inputMatrix) {
        fin.open(INPUT_FILES_PATH + MATRIX_INPUT_FILENAME);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                fin >> matrixStatic[i][j];
            }
        }
    } else {
        fin.open(OUTPUT_FILES_PATH + TEST_MATRIX_FILENAME);


        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                fin >> resultMatrixStatic[i][j];
            }
        }
    }

    fin.close();
}

void PrintMatrix_print() {
    cout << "\nMatrix:\n";
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < M; j++) {
            cout << matrixStatic[i][j] << " ";
        }
        cout << "\n";
    }
}

void WriteMatrix_write() {
    ofstream fout(OUTPUT_FILES_PATH + MATRIX_OUTPUT_FILENAME);
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < M; j++) {
            fout << matrixStatic[i][j] << " ";
        }
        fout << "\n";
    }
    fout.close();
}

double applyConvolutionOnLines(double tempStart[][MAX], const double tempEnd[][MAX], int linMatrix, int colMatrix,
                               int linEnd) {

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
            convolutionResult += tempStart[linKernel][colConvolution] * kernelStatic[linKernel][colKernel];
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
                convolutionResult +=
                        tempEnd[linConvolution - linEnd + 1][colConvolution] * kernelStatic[linKernel][colKernel];
            } else {
                convolutionResult += matrixStatic[linConvolution][colConvolution] * kernelStatic[linKernel][colKernel];
            }
        }
    }
    return convolutionResult;
}

double
applyConvolutionOnColumns(double tempStart[][MAX_KERNEL], double tempEnd[][MAX_KERNEL], int linMatrix, int colMatrix,
                          int colEnd) {
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

            convolutionResult += tempStart[linConvolution][colKernel] * kernelStatic[linKernel][colKernel];
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
                convolutionResult +=
                        tempEnd[linConvolution][colConvolution - colEnd + 1] * kernelStatic[linKernel][colKernel];
            } else {
                convolutionResult += matrixStatic[linConvolution][colConvolution] * kernelStatic[linKernel][colKernel];
            }
        }
    }

    return convolutionResult;
}

void sequentially(int linStart, int linEnd, int colStart, int colEnd) {
    if (N >= M) {

        // init temps
//        double tempStart[n / 2 + 1][MAX];
//        double tempEnd[n / 2 + 1][MAX];

//        double tempStart[MAX_KERNEL / 2 + 1][MAX];
//        double tempEnd[MAX_KERNEL / 2 + 1][MAX];

        auto tempStart = new double [n / 2 + 1][MAX];
        auto tempEnd = new double [n / 2 + 1][MAX];

        // copy start lines
        for (int i = linStart; i < linStart + n / 2 + 1; i++) {
            int linCopy = i - n / 2;
            if (linCopy < 0) {
                linCopy = 0;
            }

//            System.arraycopy(matrix[linCopy], 0, tempStart[i - linStart], 0, M);
            for (int j = 0; j < M; j++) {
                tempStart[i - linStart][j] = matrixStatic[linCopy][j];
            }
        }

        // copy end lines
        for (int i = linEnd - 1; i < linEnd + n / 2; i++) {
            int linCopy = i;
            if (linCopy > N - 1) {
                linCopy = N - 1;
            }

//            System.arraycopy(matrix[linCopy], 0, tempEnd[i - linEnd + 1], 0, M);
            for (int j = 0; j < M; j++) {
                tempEnd[i - linEnd + 1][j] = matrixStatic[linCopy][j];
            }
        }

        //todo: barrier here
        barrier.wait();

        for (int i = linStart; i < linEnd; i++) {

//            double currentLine[M];
//            double currentLine[MAX];
            auto currentLine = new double[M];
//            System.arraycopy(matrix[i], 0, temp[n / 2], 0, M);
//            System.arraycopy(matrix[i], 0, currentLine, 0, M);
            for (int j = 0; j < M; j++) {
                tempStart[n / 2][j] = matrixStatic[i][j];
                currentLine[j] = matrixStatic[i][j];
            }

            // apply convolution on line
            for (int j = colStart; j < colEnd; j++) {
                matrixStatic[i][j] = applyConvolutionOnLines(tempStart, tempEnd, i, j, linEnd);
            }

//            System.arraycopy(currentLine, 0, temp[n / 2], 0, M);
            for (int j = 0; j < M; j++) {
                tempStart[n / 2][j] = currentLine[j];
            }
            // move lines
            for (int k = 1; k < n / 2 + 1; k++) {
//                System.arraycopy(temp[k], 0, temp[k - 1], 0, M);
                for (int j = 0; j < M; j++) {
                    tempStart[k - 1][j] = tempStart[k][j];
                }
            }
        }
    } else {
        // copy start columns
//        double tempStart[N][MAX_KERNEL];
//        double tempEnd[N][MAX_KERNEL];

//        double tempStart[MAX][MAX_KERNEL];
//        double tempEnd[MAX][MAX_KERNEL];

        auto tempStart = new double [N][MAX_KERNEL];
        auto tempEnd = new double [N][MAX_KERNEL];

        // copy start columns
        for (int j = colStart; j < colStart + m / 2 + 1; j++) {
            int colCopy = j - m / 2;
            if (colCopy < 0) {
                colCopy = 0;
            }
            for (int i = 0; i < N; i++) {
                tempStart[i][j - colStart] = matrixStatic[i][colCopy];
            }
        }

        // copy end columns
        for (int j = colEnd - 1; j < colEnd + m / 2; j++) {
            int colCopy = j;
            if (colCopy > M - 1) {
                colCopy = M - 1;
            }
            for (int i = 0; i < N; i++) {
                tempEnd[i][j - colEnd + 1] = matrixStatic[i][colCopy];
            }
        }

        //todo: barrier here
        barrier.wait();

        // start convolution on columns
        for (int j = colStart; j < colEnd; j++) {
//            double currentColumn[N][1];
//            double currentColumn[MAX][1];
            auto currentColumn = new double [N][1];

            for (int i = 0; i < N; i++) {
                tempStart[i][m / 2] = matrixStatic[i][j];
                currentColumn[i][0] = matrixStatic[i][j];
            }

            // apply convolution on columns
            for (int i = 0; i < N; i++) {
                matrixStatic[i][j] = applyConvolutionOnColumns(tempStart, tempEnd, i, j, colEnd);
            }

            for (int i = 0; i < N; i++) {
                tempStart[i][m / 2] = currentColumn[i][0];
            }

            // move columns
            for (int k = 0; k < N; k++) {
//                System.arraycopy(temp[k], 1, temp[k], 0, m / 2);
                for (int i = 1; i < m / 2 + 1; i++) {
                    tempStart[k][i - 1] = tempStart[k][i];
                }
            }
        }
    }
}


void parallel() {
    thread threads[16];
    my_barrier new_barrier(p);
    barrier.thread_count = p;

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

        threads[i] = thread(sequentially, linStart, linEnd, colStart, colEnd);

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

void testResult() {
    ReadMatrix_readStatic(false);
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < M; j++) {
            if (abs(resultMatrixStatic[i][j] - matrixStatic[i][j]) > 1) {
                cout << "Invalid result at line: " << i << " and column: " << j << "\n";
                cout << "Actual value: " << resultMatrixStatic[i][j] << "\n";
                cout << "Real value: " << matrixStatic[i][j] << "\n";
            }
        }
    }
}

int main(int argc, char *argv[]) {
    p = 4;
    withThreads = 1;
    MATRIX_INPUT_FILENAME = "testMatrix.txt";
    MATRIX_OUTPUT_FILENAME = "testMatrixWith0Threads.txt";
    TEST_MATRIX_FILENAME = "testMatrixWith0Threads.txt";
    KERNEL_FILENAME = "testKernel.txt";
    N = 6;
    M = 6;
    n = 3;
    m = 3;

/*
    p = strtol(argv[1], NULL, 10);
    withThreads = strtol(argv[2], NULL, 10);;
    MATRIX_INPUT_FILENAME = argv[3];
    MATRIX_OUTPUT_FILENAME = argv[4];
    TEST_MATRIX_FILENAME = argv[5];
    KERNEL_FILENAME = argv[6];
    N = strtol(argv[7], NULL, 10);
    M = strtol(argv[8], NULL, 10);
    n = strtol(argv[9], NULL, 10);
    m = strtol(argv[10], NULL, 10);
*/
    ReadKernel_readStatic();
//    PrintKernel_print();
    ReadMatrix_readStatic(true);
//    PrintMatrix_print();

    auto startTime = chrono::high_resolution_clock::now();

    if (withThreads == 0) {
        sequentially(0, N, 0, M);
    } else if (withThreads == 1) {
        parallel();
    }

    auto endTime = chrono::high_resolution_clock::now();

    WriteMatrix_write();

    testResult();

    double duration = chrono::duration<double, milli>(endTime - startTime).count();
    cout << duration;
    return 0;
}
