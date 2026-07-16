package za.ac.cput.carrental.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.carrental.domain.Booking;
import za.ac.cput.carrental.service.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public Booking create(@RequestBody Booking booking) {
        return this.bookingService.create(booking);
    }

    @GetMapping("/{id}")
    public Booking read(@PathVariable String id) {
        return this.bookingService.read(id);
    }

    @PutMapping
    public Booking update(@RequestBody Booking booking) {
        return this.bookingService.update(booking);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        this.bookingService.delete(id);
    }

}
