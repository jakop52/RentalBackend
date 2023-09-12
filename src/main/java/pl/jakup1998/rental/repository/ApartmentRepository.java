package pl.jakup1998.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.jakup1998.rental.model.Apartment;
import pl.jakup1998.rental.model.User;

import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    List<Apartment> findByOwner(User owner);
    List<Apartment> findByOwnerId(Long id);
    @Query("SELECT DISTINCT a FROM Apartment a LEFT JOIN a.reservations r WHERE r.confirmed = false OR r IS NULL")
    List<Apartment> findAvailableApartments();

    List<Apartment> findAllByReservationsUserAndReservationsConfirmedTrue(User user);
}