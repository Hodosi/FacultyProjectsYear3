public class OrderedLinkedList {
    private Monomial head = null;

    synchronized public void insert(final Monomial monomial) {

        // add
        if (head == null) {
            head = monomial;
            head.prev = null;
            head.next = null;
            return;
        }

        Monomial previous = null;
        Monomial current = head;

        while (current != null && monomial.grad >= current.grad) {
            previous = current;
            current = current.next;
        }

        // add
        if (previous == null) {
            monomial.prev = null;
            monomial.next = head;
            head = monomial;
            return;
        }

        if (previous.grad == monomial.grad) {
            // update
            previous.coefficient += monomial.coefficient;

            // delete
            if (previous.coefficient == 0) {

                if (previous.prev == null && current == null) {
                    head = null;
                    return;
                }
                if (previous.prev == null) {
                    current.prev = null;
                    head = current;
                    return;
                }

                if (current == null) {
                    previous.prev.next = null;
                    return;
                }

                previous.prev.next = current;
                current.prev = previous.prev;
            }

            return;
        }

        // add
        monomial.prev = previous;
        monomial.next = current;
        previous.next = monomial;
        if (current != null) {
            current.prev = monomial;
        }
    }

    public Monomial getHead() {
        return head;
    }
}
