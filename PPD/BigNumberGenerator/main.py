import random


def main():
    f = open("ID_3_N_2.txt", "w")

    n = 100000
    f.write(str(n) + "\n")
    for i in range(n - 1):
        x = random.randint(0, 9)
        f.write(str(x) + " ")

    x = random.randint(1, 9)
    f.write(str(x) + " ")

    f.close()


main()
