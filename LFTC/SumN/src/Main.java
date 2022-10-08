import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final Scanner IN = new Scanner(System.in);
        final int N = IN.nextInt();
        double i, x, sum = 0;
        for(i = 0; i < N; i++){
            x = IN.nextDouble();
            sum = sum + x;
        }
        System.out.println(sum);
    }
}