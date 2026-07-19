/*BookingService.java
Author: Tanatswa Mabhodha 220637482
07 July 2026
 */

package za.ac.cput.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.carrental.domain.Booking;
import za.ac.cput.carrental.factory.BookingFactory;
import za.ac.cput.carrental.repository.BookingRepository;

import java.util.List;

@Service
 public class BookingService implements IBookingService {
@Autowired
private  BookingRepository booking ;

    @Override
    public Booking create(Booking booking) {
        return this.booking.save(booking);
    }

    @Override
    public Booking read(String id) {
        return this.booking.findById(id).orElse(null);
    }

    @Override
    public Booking update(Booking booking) {
        return this.booking.save(booking);
    }

    @Override
    public boolean delete(String id) {
        this.booking.deleteById(id);
        return true;
    }
}

