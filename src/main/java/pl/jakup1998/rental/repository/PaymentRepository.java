package pl.jakup1998.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.jakup1998.rental.model.Payment;
import pl.jakup1998.rental.model.Reservation;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Payment findTopByReservationOrderByPaymentDateDesc(Reservation reservation);
}
