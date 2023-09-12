package pl.jakup1998.rental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.jakup1998.rental.dto.PaymentDto;
import pl.jakup1998.rental.dto.ReservationDto;
import pl.jakup1998.rental.dto.UserDto;
import pl.jakup1998.rental.model.Reservation;
import pl.jakup1998.rental.model.User;
import pl.jakup1998.rental.service.ReservationService;
import pl.jakup1998.rental.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    @Autowired
    private UserService userService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        List<ReservationDto> reservationDtos = reservationService.getAllReservations();
        return ResponseEntity.ok(reservationDtos);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReservationDto>> getCurrentUserReservations(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<ReservationDto> reservationDtos = reservationService.getReservationsByUser(user);
        return ResponseEntity.ok(reservationDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        ReservationDto reservationDto = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationDto);
    }

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationDto reservationDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username= userDetails.getUsername();
        User user = userService.findByUsername(username);
        reservationDto.setUserId(user.getId());
        ReservationDto reservation = reservationService.createReservation(reservationDto);
        reservationService.createReservation(reservation);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationDto> confirmReservation(@PathVariable Long id){
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<PaymentDto>> getPayments(@PathVariable Long id){
        return ResponseEntity.ok(reservationService.getReservationPaymentsByReservationId(id));
    }


    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
    }
}
