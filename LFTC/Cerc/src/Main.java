import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final Scanner IN = new Scanner(System.in);
        final double PI = 3.14;
        final double r, P, A;
        r = IN.nextDouble();
        P = 2 * PI * r;
        A = PI * r * r;
        System.out.println(P);
        System.out.println(A);
    }
}
