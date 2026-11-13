package za.ac.cput.carrental.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.carrental.domain.Booking;
import za.ac.cput.carrental.security.AuthInterceptor;
import za.ac.cput.carrental.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    private static boolean isAdmin(HttpServletRequest req) {
        return "ADMIN".equals(req.getAttribute(AuthInterceptor.AUTH_ROLE));
    }

    private static String authId(HttpServletRequest req) {
        return (String) req.getAttribute(AuthInterceptor.AUTH_MEMBER);
    }

    // Not a web endpoint (no mapping). Kept so the existing unit tests still compile and run.
    public Booking create(Booking booking) {
        return this.bookingService.create(booking);
    }

    // Not a web endpoint (no mapping). Kept so the existing unit tests still compile and run.
    public Booking update(Booking booking) {
        return this.bookingService.update(booking);
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking, HttpServletRequest req) {
        // A customer can only book for themselves.
        if (!isAdmin(req) && !authId(req).equals(booking.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only book for your own account");
        }
        try {
            return ResponseEntity.ok(this.bookingService.createChecked(booking));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Admin: every booking
    @GetMapping
    public List<Booking> getAll() {
        return this.bookingService.getAll();
    }

    // Customer: only their own bookings
    @GetMapping("/mine")
    public List<Booking> mine(HttpServletRequest req) {
        return this.bookingService.getByMember(authId(req));
    }

    // Public: which cars are taken, and when (no customer details)
    @GetMapping("/busy")
    public List<BookingService.BusySlot> busy() {
        return this.bookingService.getBusySlots();
    }

    @GetMapping("/{id}")
    public Booking read(@PathVariable String id) {
        return this.bookingService.read(id);
    }

    // Extend a booking: only the end date and total can change
    @PutMapping
    public ResponseEntity<?> extend(@RequestBody Booking booking, HttpServletRequest req) {
        Booking existing = this.bookingService.read(booking.getBookingId());
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        if (!isAdmin(req) && !authId(req).equals(existing.getMemberId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("That isn't your booking");
        }
        try {
            return ResponseEntity.ok(this.bookingService.extend(existing.getBookingId(), booking.getEndDate(), booking.getTotalCost()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public Booking cancelBooking(@PathVariable String id) {
        return this.bookingService.cancelBooking(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        this.bookingService.delete(id);
    }

}