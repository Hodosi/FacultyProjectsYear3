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


void readN1(short a[]) {
    int n, x;
    ifstream fin1(INPUT_NR_1);
    fin1 >> n;
    for (int i = 0; i < n; i++) {
        fin1 >> a[i];
    }
    for (int i = n; i < MAX; i++) {
        a[i] = 0;
    }
    fin1.close();
}

void readN2(short b[]) {
    int n, x;
    ifstream fin2(INPUT_NR_2);
    fin2 >> n;
    for (int i = 0; i < n; i++) {
        fin2 >> b[i];
    }
    for (int i = n; i < MAX; i++) {
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
    
    short *a = new short[MAX];
    short *b = new short[MAX];
    short *c = new short[MAX + 1];

    readN1(a);
    readN2(b);
    suma(a, b, c);
    writeN3(c);

    auto endTime = chrono::high_resolution_clock::now();
    double duration = chrono::duration<double, milli>(endTime - startTime).count();
    cout << duration;
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
