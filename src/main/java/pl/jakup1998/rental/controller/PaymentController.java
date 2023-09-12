package pl.jakup1998.rental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;
import pl.jakup1998.rental.model.PaymentTransaction;
import pl.jakup1998.rental.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PutMapping("/{paymentId}/pay")
    public ResponseEntity<String> payPayment(@PathVariable Long paymentId){
        paymentService.markAsPaid(paymentId);
        return ResponseEntity.ok("Payment paid!");
    }
    @PostMapping("/{paymentId}")
    public ResponseEntity<?> processPayment(@PathVariable Long paymentId, @RequestBody PaymentTransaction paymentTransaction) {
        if (paymentTransaction.isSuccessful()) {
            paymentService.markAsPaid(paymentId);
            return ResponseEntity.ok("Payment processed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment failed");
        }
    }


}
