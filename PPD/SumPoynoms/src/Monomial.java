public class Monomial {
    int coefficient;
    int grad;
    Monomial prev, next;

    public Monomial(int coefficient, int grad) {
        this.coefficient = coefficient;
        this.grad = grad;
        this.prev = null;
        this.next = null;
    }

    @Override
    public String toString() {
        return "Monomial{" +
                "coefficient=" + coefficient +
                ", grad=" + grad +
                '}';
    }
}
