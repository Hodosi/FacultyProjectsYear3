import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final String outputFilename = "./resources/output/testOutput.txt";
    private static final String inputFilenamesPrefix = "./resources/input/testInput_";
//    private static final String outputFilename = "./resources/output/output.txt";
//    private static final String inputFilenamesPrefix = "./resources/input/polynom_g1000_m50_";
    private static final int noFiles = 3;

    public static void main(String[] args) {
        final Lock lock = new ReentrantLock();
        final Condition finishedCondition = lock.newCondition();

        final int p = 4;

        final List<String> inputFiles = new ArrayList<>();
        IntStream.range(1, noFiles + 1).forEach(i -> inputFiles.add(inputFilenamesPrefix + i + ".txt"));

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
    }
}
