import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Queue;

public class SequentiallySum {
    private final OrderedLinkedList polynomial;
    private final Queue<Monomial> queue;
    private final List<String> inputFiles;
    private final String outputFile;

    public SequentiallySum(final List<String> inputFiles, final String outputFile) {
        this.inputFiles = inputFiles;
        this.outputFile = outputFile;
        this.polynomial = new OrderedLinkedList();
        this.queue = new LinkedList<>();
    }


    public void run(){
        readFiles();
        computeSum();
        writeFile();
    }

    private void readFiles() {
        List<Scanner> scanners = inputFiles.stream()
                .map(File::new)
                .map(file -> {
                    try {
                        return new Scanner(file);
                    } catch (FileNotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(Scanner::hasNextInt)
                .collect(Collectors.toList());

        while (!scanners.isEmpty()) {

            final List<Scanner> activeScanners = new ArrayList<>();

            for (final Scanner scanner : scanners) {

                final int coefficient = scanner.nextInt();
                final int grad = scanner.nextInt();

                final Monomial monomial = new Monomial(coefficient, grad);

                this.queue.add(monomial);

                if (scanner.hasNextInt()) {
                    activeScanners.add(scanner);
                }
            }

            scanners = activeScanners;
        }
    }

    private void computeSum() {
        while (!queue.isEmpty()) {
            final Monomial monomial = queue.remove();
            polynomial.insert(monomial);
        }
    }

    private void writeFile() {
        try {
            final FileWriter fileWriter = new FileWriter(outputFile);
            Monomial current = polynomial.getHead();
            while (current != null) {
                fileWriter.write(current.coefficient + " " + current.grad + "\n");
                current = current.next;
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            System.out.println("Can't write into file: " + outputFile);
            e.printStackTrace();
        }
    }
}
