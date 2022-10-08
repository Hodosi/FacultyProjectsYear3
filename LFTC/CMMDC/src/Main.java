import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final Scanner IN = new Scanner(System.in);
        int a, b, r;
        a = IN.nextInt();
        b = IN.nextInt();
        while (b != 0) {
            r = a % b;
            a = b;
            b = r;
        }
        System.out.println(a);
    }
}