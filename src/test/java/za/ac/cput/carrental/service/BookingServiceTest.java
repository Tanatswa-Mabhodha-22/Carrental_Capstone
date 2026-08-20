package za.ac.cput.carrental.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.carrental.domain.Booking;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;
    private static Booking booking;

    @BeforeAll
    static void setUp() {
        booking = new Booking.Builder()
                .setBookingId("BOOK001")
                .setMemberId("M001")
                .setCarId("C001")
                .setStartDate("2026-09-01")
                .setEndDate("2026-09-05")
                .setTotalCost(1800.00)
                .build();
    }

    @Test
    @Order(1)
    void create() {
        Booking created = bookingService.create(booking);
        assertNotNull(created);
        assertEquals(booking.getBookingId(), created.getBookingId());
        System.out.println("Created: " + created);
    }

    @Test
    @Order(2)
    void read() {
        Booking read = bookingService.read(booking.getBookingId());
        assertNotNull(read);
        assertEquals(booking.getBookingId(), read.getBookingId());
        System.out.println("Read: " + read);
    }

    @Test
    @Order(3)
    void update() {
        Booking updated = new Booking.Builder()
                .copy(booking)
                .setEndDate("2026-09-10")
                .setTotalCost(2250.00)
                .build();
        Booking result = bookingService.update(updated);
        assertNotNull(result);
        assertEquals("2026-09-10", result.getEndDate());
        System.out.println("Updated: " + result);
    }

    @Test
    @Order(4)
    void getAll() {
        List<Booking> bookings = bookingService.getAll();
        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
        System.out.println("All bookings: " + bookings);
    }

    @Test
    @Order(5)
    void delete() {
        boolean deleted = bookingService.delete(booking.getBookingId());
        assertTrue(deleted);
        System.out.println("Deleted: " + deleted);
    }
}