#include <iostream>
#include <thread>
#include <fstream>
#include <chrono>
#include <vector>

using namespace std;
#define MAX 10000 // dim maxima

//string INPUT_FILES_PATH = "resources/input/";
//string OUTPUT_FILES_PATH = "resources/output/";

string INPUT_FILES_PATH = "C:\\UserDisk\\FacultyProjectsYear3\\PPD\\Convolution2Dcpp\\resources\\input\\";
string OUTPUT_FILES_PATH = "C:\\UserDisk\\FacultyProjectsYear3\\PPD\\Convolution2Dcpp\\resources\\output\\";

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
int dynamic;
double kernelStatic[5][5];
double matrixStatic[MAX][MAX];
double resultMatrixStatic[MAX][MAX];

vector<vector<double>> kernelDynamic;
vector<vector<double>> matrixDynamic;
vector<vector<double>> resultMatrixDynamic;


void ReadKernel_readStatic() {
    ifstream fin(INPUT_FILES_PATH + KERNEL_FILENAME);
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            fin >> kernelStatic[i][j];
        }
    }
    fin.close();
}

void ReadKernel_readDynamic() {
    ifstream fin(INPUT_FILES_PATH + KERNEL_FILENAME);
    double x;
    for (int i = 0; i < n; i++) {
        vector<double> line;
        for (int j = 0; j < m; j++) {
            fin >> x;
            line.push_back(x);
        }
        kernelDynamic.push_back(line);
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
    }
    else {
        fin.open(OUTPUT_FILES_PATH + TEST_MATRIX_FILENAME);
    }

    for (int i = 0; i < N; i++) {
        for (int j = 0; j < M; j++) {
            fin >> matrixStatic[i][j];
        }
    }
    fin.close();
}

void ReadMatrix_readDynamic(bool inputMatrix) {
    ifstream fin;
    if (inputMatrix) {
        fin.open(INPUT_FILES_PATH + MATRIX_INPUT_FILENAME);
    }
    else {
        fin.open(OUTPUT_FILES_PATH + TEST_MATRIX_FILENAME);
    }

    matrixDynamic.clear();
    double x;
    for (int i = 0; i < N; i++) {
        vector<double> line;
        for (int j = 0; j < M; j++) {
            fin >> x;
            line.push_back(x);
        }
        matrixDynamic.push_back(line);
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
            if (dynamic) {
                fout << resultMatrixDynamic[i][j] << " ";
            }
            else {
                fout << resultMatrixStatic[i][j] << " ";
            }
        }
        fout << "\n";
    }
    fout.close();
}

double applyConvolution(int linMatrix, int colMatrix) {
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

            if (dynamic) {
                convolutionResult +=
                    matrixDynamic[linConvolution][colConvolution] * kernelDynamic[linKernel][colKernel];
            }
            else {
                convolutionResult += matrixStatic[linConvolution][colConvolution] * kernelStatic[linKernel][colKernel];
            }
        }
    }
    return convolutionResult;
}


void sequentially(int linStart, int linEnd, int colStart, int colEnd) {
    for (int i = linStart; i < linEnd; i++) {
        for (int j = colStart; j < colEnd; j++) {
            if (dynamic) {
                resultMatrixDynamic[i][j] = applyConvolution(i, j);
            }
            else {
                resultMatrixStatic[i][j] = applyConvolution(i, j);
            }
        }
    }
}

void parallel() {
    thread threads[16];

    int linStart = 0, linEnd = 0, colStart = 0, colEnd = 0, chunk, rest;
    if (N >= M) {
        colEnd = M;
        chunk = N / p;
        rest = N % p;
    }
    else {
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
        }
        else {
            colEnd = colStart + chunk;
            if (rest > 0) {
                colEnd++;
                rest--;
            }
        }

        threads[i] = thread(sequentially, linStart, linEnd, colStart, colEnd);

        if (N >= M) {
            linStart = linEnd;
        }
        else {
            colStart = colEnd;
        }
    }

    for (int i = 0; i < p; i++) {
        threads[i].join();
    }
}

void testResult() {
    if (dynamic) {
        ReadMatrix_readDynamic(false);
    }
    else {
        ReadMatrix_readStatic(false);
    }
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < M; j++) {
            if (dynamic) {
                if (abs(resultMatrixDynamic[i][j] - matrixDynamic[i][j]) > 1) {
                    cout << "Invalid result at line: " << i << " and column: " << j << "\n";
                    cout << "Actual value: " << resultMatrixDynamic[i][j] << "\n";
                    cout << "Real value: " << matrixDynamic[i][j] << "\n";
                }
            }
            else {
                if (abs(resultMatrixStatic[i][j] - matrixStatic[i][j]) > 1) {
                    cout << "Invalid result at line: " << i << " and column: " << j << "\n";
                    cout << "Actual value: " << resultMatrixStatic[i][j] << "\n";
                    cout << "Real value: " << matrixStatic[i][j] << "\n";
                }
            }
        }
    }
}

int main(int argc, char* argv[]) {
    //    p = 16;
    //    withThreads = 0;
    //    MATRIX_INPUT_FILENAME = "testMatrix.txt";
    //    MATRIX_OUTPUT_FILENAME = "testMatrixWith0Threads.txt";
    //    TEST_MATRIX_FILENAME = "testMatrixWith0Threads.txt";
    //    KERNEL_FILENAME = "testKernel.txt";
    //    N = 6;
    //    M = 6;
    //    n = 3;
    //    m = 3;
    //    dynamic = 0;

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
    dynamic = strtol(argv[11], NULL, 10);

    if (dynamic) {
        ReadKernel_readDynamic();
        ReadMatrix_readDynamic(true);
        vector<vector<double>> v(N, vector<double>(M, 0));
        resultMatrixDynamic = v;
    }
    else {
        ReadKernel_readStatic();
        ReadMatrix_readStatic(true);
    }

    auto startTime = chrono::high_resolution_clock::now();

    if (withThreads == 0) {
        sequentially(0, N, 0, M);
    }
    else if (withThreads == 1) {
        parallel();
    }

    auto endTime = chrono::high_resolution_clock::now();

    WriteMatrix_write();

    testResult();

    double duration = chrono::duration<double, milli>(endTime - startTime).count();
    cout << duration;
    return 0;
}
