public class Main {
    public static void main(String[] args) {
        final ServiceAFD serviceAFD = new ServiceAFD();
        final UIAFD uiafd = new UIAFD(serviceAFD);
        uiafd.run();
    }
}