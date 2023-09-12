package pl.jakup1998.rental.model;

public class PaymentTransaction {
    private boolean successful;

    public PaymentTransaction(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
