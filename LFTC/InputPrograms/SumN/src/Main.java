import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final Scanner IN;
        IN = new Scanner(System.in);
        double N, i, x, sum;
        sum = 0;
        N = IN.nextInt();
        i = 0;
        while (i < N) {
            x = IN.nextDouble();
            sum = sum + x;
            i++;
        }
        System.out.println(sum);
    }
}