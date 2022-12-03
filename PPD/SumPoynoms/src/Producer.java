import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

public class Producer extends Thread {
    private final OrderedLinkedList polynomial;
    private final Queue queue;
    private final List<String> inputFiles;
    private final String outputFile;
    private final Lock lock;
    private final Condition finishedCondition;

    private boolean finished;

    public Producer(
            final OrderedLinkedList polynomial,
            final Queue queue,
            final List<String> inputFiles,
            final String outputFile,
            final Lock lock,
            final Condition finishedCondition
    ) {
        this.polynomial = polynomial;
        this.queue = queue;
        this.inputFiles = inputFiles;
        this.outputFile = outputFile;
        this.lock = lock;
        this.finishedCondition = finishedCondition;
        this.finished = false;
    }

    public void finish() {
        this.finished = true;
    }

    @Override
    public void run() {
        readFiles();
        queue.finish();
        lock.lock();
        try {
            while (!finished) {
                finishedCondition.await();
            }
            writeFile();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
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

    private void writeFile() {
        try {
            final FileWriter fileWriter = new FileWriter(outputFile);
            Monomial current = polynomial.getHead();
            while (current != null) {
                fileWriter.write(current.coefficient + " " + current.grad + "\n");
                current = current.next;
            }
            fileWriter.close();
            System.out.println("Successfully wrote to the file: " + outputFile);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            System.out.println("Can't write into file: " + outputFile);
            e.printStackTrace();
        }
    }
}
