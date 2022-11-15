import java.util.Scanner;

public class UIAFD {
//    private final String INPUT_FILE_RULES = "./AFD/resources/input/ConstAF.txt";
//    private final String INPUT_FILE_RULES = "./AFD/resources/input/AAAA.txt";
//    private final String INPUT_FILE_RULES = "./AFD/resources/input/IdentifAF.txt";
    private final String INPUT_FILE_RULES = "./AFD/resources/input/KeywordAF.txt";
    private final Scanner scanner = new Scanner(System.in);
    private final ServiceAFD serviceAFD;

    public UIAFD(ServiceAFD serviceAFD) {
        this.serviceAFD = serviceAFD;
    }

    public void run() {
        String __ = "";
        showInitOptions();

        String sysInputLine = scanner.nextLine();

        StringBuilder rules;
        if (sysInputLine.strip().equals("1")) {
            rules = new StringBuilder(FileRead.readFile(INPUT_FILE_RULES));
        } else {
            rules = new StringBuilder();
            String line = scanner.nextLine();
            while (!line.equals("end")){
                rules.append(line).append("\n");
                line = scanner.nextLine();
            }
            rules = new StringBuilder();
        }
        serviceAFD.buildAF(rules.toString());

        boolean running = true;
        while (running) {
            showMainOptions();
            String option = scanner.nextLine();
            switch (option.strip()) {
                case "1":
                    System.out.println(serviceAFD.getStari());
                    break;
                case "2":
                    System.out.println(serviceAFD.getStariInitiale());
                    break;
                case "3":
                    System.out.println(serviceAFD.getStariFinale());
                    break;
                case "4":
                    System.out.println(serviceAFD.getAlfabet());
                    break;
                case "5":
                    System.out.println(serviceAFD.getTranzitii());
                    break;
                case "6":
                    System.out.println("Introduceti o secventa: ");
                    String secventaDeValidat = scanner.nextLine();
                    System.out.println(serviceAFD.secventaValida(secventaDeValidat));
                    break;
                case "7":
                    System.out.println("Introduceti o secventa: ");
                    String secventa = scanner.nextLine();
                    String prefixMaxim = serviceAFD.getPrefixMaxim(secventa);
                    System.out.println(prefixMaxim);
                    break;
                case "exit":
                    running = false;
                    break;
            }
        }
    }

    private void showInitOptions() {
        System.out.println("Choose a number!");
        System.out.println("1. Read AF rules from file.");
        System.out.println("2. Read AF rules from console.");
    }

    private void showMainOptions() {
        System.out.println("");
        System.out.println("1. Multimea starilor!");
        System.out.println("2. Multimea starilor initiale!");
        System.out.println("3. Multimea starilor finale!");
        System.out.println("4. Alfabetul!");
        System.out.println("5. Multimea tranzitiilor!");
        System.out.println("6. Secventa valida?");
        System.out.println("7. Prefix valid!");
        System.out.println("exit");
    }
}
