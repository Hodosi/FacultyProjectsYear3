import random


def main():
    filename = "polynom_g10000_m100_"
    grad_maxim = 10000
    nr_monoame = 100
    nr_polinoame = 5

    for i in range(nr_polinoame):
        f = open(filename + str(i + 1) + ".txt", "w")

        exp = grad_maxim // nr_monoame
        for j in range(nr_monoame):
            grad = random.randint(exp * j, exp * (j + 1) - 1)
            coefficient = random.randint(-100, 100)
            while coefficient == 0:
                coefficient = random.randint(-100, 100)

            f.write(str(coefficient) + " " + str(grad) + "\n")

        f.close()


main()
