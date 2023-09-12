package pl.jakup1998.rental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.jakup1998.rental.exception.ResourceNotFoundException;
import pl.jakup1998.rental.model.Payment;
import pl.jakup1998.rental.model.Reservation;
import pl.jakup1998.rental.model.enums.PaymentPeriod;
import pl.jakup1998.rental.model.enums.PaymentStatus;
import pl.jakup1998.rental.repository.PaymentRepository;
import pl.jakup1998.rental.repository.ReservationRepository;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public void markAsPaid(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found","paymentId",paymentId));

        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void generatePayments() {
        List<Reservation> confirmedReservations = reservationRepository.findAllByConfirmed(true);

        for (Reservation reservation : confirmedReservations) {
            Payment lastPayment = paymentRepository.findTopByReservationOrderByPaymentDateDesc(reservation);

            ZonedDateTime nextPaymentDate = calculateNextPaymentDate(lastPayment.getPaymentDate(), reservation.getPaymentPeriod());

            if (nextPaymentDate.isBefore(reservation.getEndDate())) {
                Payment payment = new Payment();
                payment.setReservation(reservation);
                payment.setPaymentDate(nextPaymentDate);
                payment.setAmount(reservation.getApartment().getRent());
                payment.setStatus(PaymentStatus.NOT_PAID);

                paymentRepository.save(payment);
            }
        }
    }

    private ZonedDateTime calculateNextPaymentDate(ZonedDateTime currentPaymentDate, PaymentPeriod paymentPeriod) {
        switch (paymentPeriod) {
            case WEEKLY:
                return currentPaymentDate.plusWeeks(1);
            case MONTHLY:
                return currentPaymentDate.plusMonths(1);
            case QUARTERLY:
                return currentPaymentDate.plusMonths(3);
            case ANNUALLY:
                return currentPaymentDate.plusYears(1);
            default:
                throw new IllegalArgumentException("Unknown payment period: " + paymentPeriod);
        }
    }
}
