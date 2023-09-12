package pl.jakup1998.rental.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
    public NotFoundException() {
        super("Resource not found");
    }
}

