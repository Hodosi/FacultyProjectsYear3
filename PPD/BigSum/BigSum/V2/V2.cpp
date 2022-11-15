// BigSum.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>
#include <fstream>
#include <chrono>
#include <mpi.h>

using namespace std;

string INPUT_NR_1 = "C:\\UserDisk\\FacultyProjectsYear3\\PPD\\BigSum\\BigSum\\BigSum\\Numar1.txt";
string INPUT_NR_2 = "C:\\UserDisk\\FacultyProjectsYear3\\PPD\\BigSum\\BigSum\\BigSum\\Numar2.txt";
string OUTPUT_NR_3 = "C:\\UserDisk\\FacultyProjectsYear3\\PPD\\BigSum\\BigSum\\BigSum\\Numar3.txt";

#define MAX 100000

void readN1(short a[], int MAX_LOCAL) {
    int n, x;
    ifstream fin1(INPUT_NR_1);
    fin1 >> n;
    for (int i = 0; i < n; i++) {
        fin1 >> a[i];
    }
    for (int i = n; i < MAX_LOCAL; i++) {
        a[i] = 0;
    }
    fin1.close();
}

void readN2(short b[], int MAX_LOCAL) {
    int n, x;
    ifstream fin2(INPUT_NR_2);
    fin2 >> n;
    for (int i = 0; i < n; i++) {
        fin2 >> b[i];
    }
    for (int i = n; i < MAX_LOCAL; i++) {
        b[i] = 0;
    }
    fin2.close();
}

void writeN3(short c[]) {
    ofstream fout(OUTPUT_NR_3);
    for (int i = 0; i <= MAX; i++) {
        fout << c[i] << " ";
    }
    fout.close();
}

void suma(short a[], short b[], short c[]) {
    short carry = 0;
    for (int i = 0; i < MAX; i++) {
        c[i] = (a[i] + b[i] + carry) % 10;
        carry = (a[i] + b[i] + carry) / 10;
    }
    c[MAX] = carry;
}

int main()
{
    auto startTime = chrono::high_resolution_clock::now();

    int MAX_LOCAL = MAX;
    int id, nr_procese, nr_elem = 0;
    int rc = MPI_Init(NULL, NULL);

    if (rc != MPI_SUCCESS) {
        cout << "MPI Init Error";
        MPI_Abort(MPI_COMM_WORLD, rc);
    }

    MPI_Comm_rank(MPI_COMM_WORLD, &id);
    MPI_Comm_size(MPI_COMM_WORLD, &nr_procese);

    // printf("Procesul %d din %d\n", id, nr_procese);

    while (MAX_LOCAL % nr_procese)
    {
        MAX_LOCAL++;
    }

    short* a = new short[MAX_LOCAL];
    short* b = new short[MAX_LOCAL];
    short* c = new short[MAX_LOCAL + 1];


    if (id == 0) {
        readN1(a, MAX_LOCAL);
        readN2(b, MAX_LOCAL);
        nr_elem = MAX_LOCAL / nr_procese;
    }

    MPI_Bcast(&nr_elem, 1, MPI_INT, 0, MPI_COMM_WORLD);

    
    short* a_local = new short[nr_elem];
    short* b_local = new short[nr_elem];
    short* c_local = new short[nr_elem];
    
    MPI_Scatter(a, nr_elem, MPI_SHORT, a_local, nr_elem, MPI_SHORT, 0, MPI_COMM_WORLD);
    MPI_Scatter(b, nr_elem, MPI_SHORT, b_local, nr_elem, MPI_SHORT, 0, MPI_COMM_WORLD);

    short carry = 0;
    for (int i = 0; i < nr_elem; i++) {
        c_local[i] = (a_local[i] + b_local[i] + carry) % 10;
        carry = (a_local[i] + b_local[i] + carry) / 10;
    }

    if (id == nr_procese - 1) {
        MPI_Send(&carry, 1, MPI_SHORT, 0, 0, MPI_COMM_WORLD);
    }
    else {
        MPI_Send(&carry, 1, MPI_SHORT, id + 1, 0, MPI_COMM_WORLD);
    }

    if (id == 0) {
        MPI_Recv(&carry, 1, MPI_SHORT, nr_procese - 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    }
    else {
        MPI_Recv(&carry, 1, MPI_SHORT, id - 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    }
    
    c[MAX] = 0;
    // cout << "\ncarry " << carry << "\n";
    if (carry) {
        if (id == 0) {
            c[MAX] = carry;
        }
        else {
            c_local[0] = c_local[0] + carry;
        }
    }

    // printf(" id = %d; nr_elem = %d", id, nr_elem);

    MPI_Gather(c_local, nr_elem, MPI_SHORT, c, nr_elem, MPI_SHORT, 0, MPI_COMM_WORLD);

    if (id == 0) {
        auto endTime = chrono::high_resolution_clock::now();
        double duration = chrono::duration<double, milli>(endTime - startTime).count();
        cout << duration;

        writeN3(c);
    }    

    MPI_Finalize();
}

// Run program: Ctrl + F5 or Debug > Start Without Debugging menu
// Debug program: F5 or Debug > Start Debugging menu

// Tips for Getting Started: 
//   1. Use the Solution Explorer window to add/manage files
//   2. Use the Team Explorer window to connect to source control
//   3. Use the Output window to see build output and other messages
//   4. Use the Error List window to view errors
//   5. Go to Project > Add New Item to create new code files, or Project > Add Existing Item to add existing code files to the project
//   6. In the future, to open this project again, go to File > Open > Project and select the .sln file
