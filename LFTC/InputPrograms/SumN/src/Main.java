import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        double N, i, x, sum;
        sum = 0;
        N = new Scanner(System.in).nextInt();
        i = 0;
        while (i < N) {
            x = new Scanner(System.in).nextDouble();
            sum = sum + x;
            i++;
        }
        System.out.println(sum);
    }
}