package za.ac.cput.carrental.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.carrental.domain.Booking;
import za.ac.cput.carrental.domain.Payment;
import za.ac.cput.carrental.security.AuthInterceptor;
import za.ac.cput.carrental.service.BookingService;
import za.ac.cput.carrental.service.PaymentService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService payment;

    @Autowired
    private BookingService bookingService;

    // Not a web endpoint (no mapping). Kept so the existing unit tests still compile and run.
    public Payment create(Payment payment) {
        return this.payment.create(payment);
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody Payment payment, HttpServletRequest req) {
        boolean admin = "ADMIN".equals(req.getAttribute(AuthInterceptor.AUTH_ROLE));
        if (!admin) {
            // A customer can only pay for their own booking.
            Booking b = this.bookingService.read(payment.getBookingId());
            String me = (String) req.getAttribute(AuthInterceptor.AUTH_MEMBER);
            if (b == null || !b.getMemberId().equals(me)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("That isn't your booking");
            }
        }
        return ResponseEntity.ok(this.payment.create(payment));
    }

    @GetMapping("/{id}")
    public Payment read(@PathVariable String id) {
        return this.payment.read(id);
    }

    @PutMapping
    public Payment update(@RequestBody Payment payment) {
        return this.payment.update(payment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        this.payment.delete(id);
    }

    // Admin: every payment
    @GetMapping
    public List<Payment> getAll() {
        return this.payment.getAll();
    }

    // Customer: payments for their own bookings
    @GetMapping("/mine")
    public List<Payment> mine(HttpServletRequest req) {
        String me = (String) req.getAttribute(AuthInterceptor.AUTH_MEMBER);
        Set<String> myBookingIds = this.bookingService.getByMember(me).stream().map(Booking::getBookingId).collect(Collectors.toSet());
        return this.payment.getAll().stream().filter(p -> myBookingIds.contains(p.getBookingId())).toList();
    }

}