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

#define MAX 18

int main()
{
    /*
    short* a = new short[MAX];
    short* b = new short[MAX];
    short* c = new short[MAX + 1];

    auto startTime = chrono::high_resolution_clock::now();
    readN1(a);
    readN2(b);
    suma(a, b, c);
    writeN3(c);
    auto endTime = chrono::high_resolution_clock::now();

    double duration = chrono::duration<double, milli>(endTime - startTime).count();
    cout << duration;
    */

    int id, nr_procese, nr_elem = 0;
    short* a = new short[MAX];
    short* b = new short[MAX];
    short* c = new short[MAX + 1];

    int rc = MPI_Init(NULL, NULL);

    if (rc != MPI_SUCCESS) {
        cout << "MPI Init Error";
        MPI_Abort(MPI_COMM_WORLD, rc);
    }

    MPI_Comm_rank(MPI_COMM_WORLD, &id);
    MPI_Comm_size(MPI_COMM_WORLD, &nr_procese);

    printf("Procesul %d din %d", id, nr_procese);

    if (id == 0) {
        ifstream fin1(INPUT_NR_1);
        ifstream fin2(INPUT_NR_2);

        int n_1, n_2;
        int id_proces_curent = 1;
        int chunk, rest, indexStart, indexEnd;
        
        chunk = MAX / (nr_procese - 1);
        rest = MAX % (nr_procese - 1);
        indexStart = 0;
       
        fin1 >> n_1;
        fin2 >> n_2;

        for (int i = 0; i < nr_procese - 1; i++) {
            indexEnd = indexStart + chunk;
            if (rest > 0) {
                indexEnd++;
                rest--;
            }

            for (int j = indexStart; j < indexEnd; j++) {
                if (j >= n_1) {
                    a[j] = 0;
                }
                else {
                    fin1 >> a[j];
                }

                if (j >= n_2) {
                    b[j] = 0;
                }
                else {
                    fin2 >> b[j];
                }
            }
            
            int nr_elemente = indexEnd - indexStart;
            MPI_Send(&indexStart, 1, MPI_INT, id_proces_curent, 0, MPI_COMM_WORLD);
            MPI_Send(&nr_elemente, 1, MPI_INT, id_proces_curent, 0, MPI_COMM_WORLD);

            MPI_Send(a + indexStart, nr_elemente, MPI_SHORT, id_proces_curent, 0, MPI_COMM_WORLD);
            MPI_Send(b + indexStart, nr_elemente, MPI_SHORT, id_proces_curent, 0, MPI_COMM_WORLD);

            indexStart = indexEnd;
            id_proces_curent++;
        }

        fin1.close();
        fin2.close();

        indexStart = 0;
        rest = MAX % (nr_procese - 1);
        id_proces_curent = 1;
        for (int i = 0; i < nr_procese - 1; i++) {
            indexEnd = indexStart + chunk;
            if (rest > 0) {
                indexEnd++;
                rest--;
            }
            int nr_elemente = indexEnd - indexStart;
            if (id_proces_curent == nr_procese - 1) {
                MPI_Recv(c + indexStart, nr_elemente + 1, MPI_SHORT, id_proces_curent, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            }
            else {
                MPI_Recv(c + indexStart, nr_elemente, MPI_SHORT, id_proces_curent, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            }
            
            indexStart = indexEnd;
            id_proces_curent++;
        }

        ofstream fout(OUTPUT_NR_3);
        for (int i = 0; i <= MAX; i++) {
            fout << c[i] << " ";
        }
    }
    else {
        int indexStart, nr_elemente;
        MPI_Recv(&indexStart, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(&nr_elemente, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

        MPI_Recv(a + indexStart, nr_elemente, MPI_SHORT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        MPI_Recv(b + indexStart, nr_elemente, MPI_SHORT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        
        short carry = 0;
        for (int i = indexStart; i < indexStart + nr_elemente; i++) {
            c[i] = (a[i] + b[i] + carry) % 10;
            carry = (a[i] + b[i] + carry) / 10;
        }
        
        if (id == nr_procese - 1) {
            c[MAX] = carry;
        }
        else {
            MPI_Send(&carry, 1, MPI_SHORT, id + 1, 0, MPI_COMM_WORLD);
        }
        
        if (id == 1) {
            carry = 0;
        }
        else {
            MPI_Recv(&carry, 1, MPI_SHORT, id - 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        }
        if (carry) {
            c[indexStart] = c[indexStart] + carry;
        }

        if (id == nr_procese - 1) {
            MPI_Send(c + indexStart, nr_elemente + 1, MPI_SHORT, 0, 0, MPI_COMM_WORLD);
        }
        else {
            MPI_Send(c + indexStart, nr_elemente, MPI_SHORT, 0, 0, MPI_COMM_WORLD);
        }
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
