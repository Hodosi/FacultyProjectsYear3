import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int a, b, r;
        a = new Scanner(System.in).nextInt();
        b = new Scanner(System.in).nextInt();
        while (b != 0) {
            r = a % b;
            a = b;
            b = r;
        }
        System.out.println(a);
    }
}