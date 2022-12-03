public class Queue {
    private final int SIZE = 5;
    private final Monomial[] items = new Monomial[SIZE];
    private int front, rear;
    private volatile boolean finished = false;

    public Queue() {
        this.front = -1;
        this.rear = 0;
    }

    public synchronized void finish() {
        this.finished = true;
        notifyAll();
    }

    public synchronized void add(Monomial element) {

        while (isFull()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (front == -1) {
            front = 0;
        }

        items[rear] = element;
        rear++;

        if (rear == SIZE) {
            rear = 0;
        }

        this.notifyAll();
    }

    public synchronized Monomial remove() {

        while (isEmpty() && !finished) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (finished) {
            return null;
        }

        final Monomial element = items[front];
        front++;

        if (front == SIZE) {
            front = 0;
        }

        if (front == rear) {
            front = -1;
            rear = 0;
        }

        this.notifyAll();
        return element;
    }

    private boolean isFull() {
        return front == rear;
    }

    private boolean isEmpty() {
        return front == -1 && rear == 0;
    }
}
