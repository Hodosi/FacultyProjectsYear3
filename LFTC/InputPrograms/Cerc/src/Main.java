import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final double PI;
        PI = 3.14;
        double r, P, A;
        r = new Scanner(System.in).nextDouble();
        P = 2 * PI;
        P = P * r;
        A = PI * r;
        A = A * r;
        System.out.println(P);
        System.out.println(A);
    }
}
