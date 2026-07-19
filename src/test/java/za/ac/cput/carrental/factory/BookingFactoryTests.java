package za.ac.cput.carrental.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.domain.Booking;
import za.ac.cput.carrental.util.Helper;

import static org.junit.jupiter.api.Assertions.*;

public class BookingFactoryTests {

    @Test
    void testCreateBooking_Success() {
        Booking booking = BookingFactory.createBooking(
                "BOOK001",
                "M001",
                "C001",
                "2026-09-01",
                "2026-09-05",
                1700.00
        );
        assertNotNull(booking);
        assertEquals("BOOK001", booking.getBookingId());
        assertEquals("M001", booking.getMemberId());
        assertEquals("C001", booking.getCarId());
        assertEquals("2026-09-01", booking.getStartDate());
        assertEquals("2026-09-05", booking.getEndDate());
        assertEquals(1700.00, booking.getTotalCost());
    }

    @Test
    void testCreateBooking_NotNull() {
        Booking booking = BookingFactory.createBooking(
                "BOOK002",
                "M002",
                "C002",
                "2026-10-01",
                "2026-10-05",
                1300.00
        );
        assertNotNull(booking);
    }

    @Test
    void testCalculateRentalDuration() {
        Booking booking = BookingFactory.createBooking(
                "BOOK003",
                "M003",
                "C001",
                "2026-09-01",
                "2026-09-05",
                1800.00
        );
        assertEquals(4, booking.calculateRentalDuration());
    }

    @Test
    void testExtendRental() {
        Booking booking = BookingFactory.createBooking(
                "BOOK004",
                "M004",
                "C001",
                "2026-09-01",
                "2026-09-05",
                1800.00
        );
        booking.extendRental("2026-09-10");
        assertEquals("2026-09-10", booking.getEndDate());
    }

    // ===== Helper class tests =====

    @Test
    void testStartDateIsValid() {
        assertTrue(Helper.isValidDate("2026-09-01"));
    }

    @Test
    void testEndDateIsValid() {
        assertTrue(Helper.isValidDate("2026-09-05"));
    }

    @Test
    void testInvalidDateFails() {
        assertFalse(Helper.isValidDate("01-09-2026"));
    }

    @Test
    void testDateRangeIsValid() {
        assertTrue(Helper.isDateRangeValid("2026-09-01", "2026-09-05"));
    }

    @Test
    void testDateRangeInvalid_EndBeforeStart() {
        assertFalse(Helper.isDateRangeValid("2026-09-05", "2026-09-01"));
    }

    @Test
    void testTotalCostIsPositive() {
        assertTrue(Helper.isPositiveDouble(1700.00));
    }

    @Test
    void testTotalCostZeroFails() {
        assertFalse(Helper.isPositiveDouble(0.0));
    }

    @Test
    void testMemberIdNotEmpty() {
        assertFalse(Helper.isNullOrEmpty("M001"));
    }

    @Test
    void testCarIdNotEmpty() {
        assertFalse(Helper.isNullOrEmpty("C001"));
    }

    @Test
    void testNullStringFails() {
        assertTrue(Helper.isNullOrEmpty(null));
    }
}

