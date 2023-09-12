package pl.jakup1998.rental.exception;

public class ApartmentRentedException extends RuntimeException{
    public ApartmentRentedException(Long apartmentId) {
        super("Apartment with ID " + apartmentId + " already has a confirmed reservation.");
    }
}
