package pl.jakup1998.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.jakup1998.rental.model.Apartment;
import pl.jakup1998.rental.model.Reservation;
import pl.jakup1998.rental.model.User;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByApartment(Apartment apartment);
    boolean existsByApartmentIdAndConfirmedIsTrue(Long apartmentId);
    List<Reservation> findAllByConfirmed(boolean confirmend);
}
