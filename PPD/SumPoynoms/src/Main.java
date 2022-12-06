import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
//    private static final String testOutputFilename = "/output/test_output_g1000_m50.txt";
//    private static final String outputFilename = "./resources/output/output_g1000_m50.txt";
//    private static final String inputFilenamesPrefix = "./resources/input/polynom_g1000_m50_";
//    private static final int noFiles = 10;


//    private static final String testOutputFilename = "./resources/output/test_output_g10000_m100.txt";
//    private static final String outputFilename = "./resources/output/output_g10000_m100.txt";
//    private static final String inputFilenamesPrefix = "./resources/input/polynom_g10000_m100.txt";
//    private static final int noFiles = 5;

    private static final String testOutputFilename = "C:\\UserDisk\\FacultyProjectsYear3\\PPD\\SumPoynoms\\resources\\output\\test_output_g10000_m100.txt";
    private static final String outputFilename = "C:\\UserDisk\\FacultyProjectsYear3\\PPD\\SumPoynoms\\resources\\output\\output_g10000_m100.txt";
    private static final String inputFilenamesPrefix = "C:\\UserDisk\\FacultyProjectsYear3\\PPD\\SumPoynoms\\resources\\input\\polynom_g10000_m100_";
    private static final int noFiles = 5;

    public static void main(String[] args) {
//        final int p = 4;
        final int p = Integer.parseInt(args[1]);
        final List<String> inputFiles = new ArrayList<>();
        IntStream.range(1, noFiles + 1).forEach(i -> inputFiles.add(inputFilenamesPrefix + i + ".txt"));

        if (p == 0) {
            long startTime = System.nanoTime();

            final SequentiallySum sequentiallySum = new SequentiallySum(inputFiles, testOutputFilename);
            sequentiallySum.run();

            long endTime = System.nanoTime();

            double time = (endTime - startTime) / 1e6;
            System.out.println(time);

            return;
        }

        long startTime = System.nanoTime();

        final Lock lock = new ReentrantLock();
        final Condition finishedCondition = lock.newCondition();

        final OrderedLinkedList polynomial = new OrderedLinkedList();

        final Queue queue = new Queue();

        final Producer producer = new Producer(polynomial, queue, inputFiles, outputFilename, lock, finishedCondition);
        final Thread producerThread = new Thread(producer);

        final List<Thread> consumers = IntStream.range(0, p)
                .mapToObj(i -> new Consumer(polynomial, queue))
                .map(Thread::new)
                .collect(Collectors.toList());

        producerThread.start();
        consumers.forEach(Thread::start);

        consumers.forEach(consumer -> {
            try {
                consumer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        lock.lock();
        try {
            producer.finish();
            finishedCondition.signal();
        } finally {
            lock.unlock();
        }

        try {
            producerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        double time = (endTime - startTime) / 1e6;

        compareResults();

        System.out.println(time);
    }

    private static void compareResults() {
        try {
            final File file1 = new File(testOutputFilename);
            final File file2 = new File(outputFilename);

            final Scanner reader1 = new Scanner(file1);
            final Scanner reader2 = new Scanner(file2);

            int noLine = 0;
            while (reader1.hasNextLine()) {
                noLine++;
                final String line1 = reader1.nextLine();
                final String line2 = reader2.nextLine();

                if (!line1.equals(line2)) {
                    throw new RuntimeException("Incorrect results at line " + noLine);
                }
            }

            reader1.close();
            reader2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
