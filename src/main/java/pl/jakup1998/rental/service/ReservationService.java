package pl.jakup1998.rental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.jakup1998.rental.dto.PaymentDto;
import pl.jakup1998.rental.dto.ReservationDto;
import pl.jakup1998.rental.exception.ApartmentRentedException;
import pl.jakup1998.rental.exception.NotFoundException;
import pl.jakup1998.rental.exception.ResourceNotFoundException;
import pl.jakup1998.rental.mapper.PaymentMapper;
import pl.jakup1998.rental.mapper.ReservationMapper;
import pl.jakup1998.rental.model.Apartment;
import pl.jakup1998.rental.model.Payment;
import pl.jakup1998.rental.model.Reservation;
import pl.jakup1998.rental.model.User;
import pl.jakup1998.rental.model.enums.PaymentStatus;
import pl.jakup1998.rental.repository.ApartmentRepository;
import pl.jakup1998.rental.repository.PaymentRepository;
import pl.jakup1998.rental.repository.ReservationRepository;
import pl.jakup1998.rental.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final ReservationMapper reservationMapper;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, ApartmentRepository apartmentRepository, UserRepository userRepository, ReservationMapper reservationMapper, PaymentMapper paymentMapper) {
        this.reservationRepository = reservationRepository;
        this.apartmentRepository = apartmentRepository;
        this.userRepository = userRepository;
        this.reservationMapper = reservationMapper;
        this.paymentMapper = paymentMapper;
    }

    public List<ReservationDto> getReservationsByUser(User user){
        List<Reservation> reservations = reservationRepository.findByUser(user);
        return reservationMapper.convertToDtoList(reservations);
    }
    public List<ReservationDto> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservationMapper.convertToDtoList(reservations);
    }

    public ReservationDto getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        return reservationMapper.convertToDto(reservation);
    }

    public ReservationDto createReservation(ReservationDto reservationDto) {
        Reservation reservation = reservationMapper.convertToEntity(reservationDto);
        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationMapper.convertToDto(savedReservation);
    }

    public ReservationDto updateReservation(Long id, ReservationDto reservationDto) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        User user = userRepository.getById(reservationDto.getUserId());
        Apartment apartment = apartmentRepository.getById(reservationDto.getApartmentId());
        reservation.setUser(user);
        reservation.setApartment(apartment);
        reservation.setStartDate(reservationDto.getStartDate());
        return reservationMapper.convertToDto(reservationRepository.save(reservation));
    }

    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", "id", id);
        }
        reservationRepository.deleteById(id);
    }

    public ReservationDto confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        if(reservationRepository.existsByApartmentIdAndConfirmedIsTrue(reservation.getApartment().getId())){
            throw new ApartmentRentedException(id);
        }
        reservation.setConfirmed(true);
        Payment payment = new Payment();
        payment.setReservation(reservation);
        payment.setPaymentDate(reservation.getStartDate());
        payment.setAmount(reservation.getApartment().getRent());
        payment.setStatus(PaymentStatus.NOT_PAID);
        reservation.getPayments().add(payment);
        reservationRepository.save(reservation);
        return reservationMapper.convertToDto(reservation);
    }

    public List<PaymentDto> getReservationPaymentsByReservationId(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        return paymentMapper.convertToDtoList(reservation.getPayments());
    }
}
