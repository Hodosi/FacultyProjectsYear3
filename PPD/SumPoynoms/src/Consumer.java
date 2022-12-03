public class Consumer extends Thread {
    private final Queue queue;
    private final OrderedLinkedList polynomial;

    public Consumer(final OrderedLinkedList polynomial, final Queue queue) {
        this.polynomial = polynomial;
        this.queue = queue;
    }

    @Override
    public void run() {
        computeSum();
    }

    private void computeSum() {
        while (true) {
            final Monomial monomial = queue.remove();
            if (monomial == null) {
                break;
            }
            polynomial.insert(monomial);
        }
    }
}
