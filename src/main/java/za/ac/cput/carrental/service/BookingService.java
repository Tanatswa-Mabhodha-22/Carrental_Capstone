/*BookingService.java
Author: Tanatswa Mabhodha 220637482
07 July 2026
 */

package za.ac.cput.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.carrental.domain.Booking;
import za.ac.cput.carrental.domain.Car;
import za.ac.cput.carrental.repository.BookingRepository;
import za.ac.cput.carrental.repository.CarRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class BookingService implements IBookingService {

    /** Just the dates a car is taken. Contains no customer details, so it is safe to show publicly. */
    public record BusySlot(String carId, String startDate, String endDate) {
    }

    // Daily insurance prices. Keep in step with INSURANCE_TYPES in the frontend (shared.jsx).
    private static final Map<String, Integer> INSURANCE_PREMIUMS = Map.of("NONE", 0, "BASIC", 45, "STANDARD", 85, "PREMIUM", 150);

    @Autowired
    private  BookingRepository booking ;

    @Autowired
    private CarRepository cars;

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

    @Override
    public Booking cancelBooking(String bookingId) {
        Booking existingBooking = booking.findById(bookingId).orElse(null);
        if (existingBooking == null) {
            return null;
        }
        Booking cancelledBooking = existingBooking.cancel();
        return booking.save(cancelledBooking);
    }

    @Override
    public List<Booking> getAll() {
        return this.booking.findAll();
    }

    // ---------- Availability and checked booking ----------

    public List<Booking> getByMember(String memberId) {
        return this.booking.findAll().stream().filter(b -> memberId != null && memberId.equals(b.getMemberId())).toList();
    }

    /** Confirmed bookings that have not finished yet. */
    public List<BusySlot> getBusySlots() {
        String today = LocalDate.now().toString();
        return this.booking.findAll().stream()
                .filter(b -> isActive(b) && b.getEndDate().compareTo(today) >= 0)
                .map(b -> new BusySlot(b.getCarId(), b.getStartDate(), b.getEndDate()))
                .toList();
    }

    /** Creates a booking after checking dates, the car and clashes. IllegalArgumentException = bad input, IllegalStateException = clash. */
    public Booking createChecked(Booking req) {
        validateDates(req.getStartDate(), req.getEndDate(), true);
        Car car = this.cars.findById(req.getCarId()).orElseThrow(() -> new IllegalArgumentException("That car doesn't exist"));
        if ("UNDER_MAINTENANCE".equalsIgnoreCase(car.getStatus())) {
            throw new IllegalArgumentException("That car is under maintenance and can't be booked");
        }
        String insurance = req.getInsuranceType() == null ? "NONE" : req.getInsuranceType().toUpperCase();
        if (!INSURANCE_PREMIUMS.containsKey(insurance)) {
            throw new IllegalArgumentException("Unknown insurance option");
        }
        long days = ChronoUnit.DAYS.between(LocalDate.parse(req.getStartDate()), LocalDate.parse(req.getEndDate()));
        if (req.getTotalCost() < (car.getDailyRate() + INSURANCE_PREMIUMS.get(insurance)) * days) {
            throw new IllegalArgumentException("The total cost is too low for this car, insurance and these dates");
        }
        assertNoClash(req.getCarId(), req.getStartDate(), req.getEndDate(), null);
        return this.booking.save(new Booking.Builder().copy(req).setInsuranceType(insurance).setStatus("CONFIRMED").build());
    }

    /** Moves the end date later (and raises the total). Returns null when the booking doesn't exist. */
    public Booking extend(String bookingId, String newEndDate, double newTotal) {
        Booking existing = this.booking.findById(bookingId).orElse(null);
        if (existing == null) return null;
        if (!isActive(existing)) {
            throw new IllegalArgumentException("Only confirmed bookings can be extended");
        }
        validateDates(existing.getStartDate(), newEndDate, false);
        if (newEndDate.compareTo(existing.getEndDate()) <= 0) {
            throw new IllegalArgumentException("The new end date must be after the current end date");
        }
        if (newTotal < existing.getTotalCost()) {
            throw new IllegalArgumentException("The new total can't be lower than the current total");
        }
        assertNoClash(existing.getCarId(), existing.getStartDate(), newEndDate, bookingId);
        Booking updated = new Booking.Builder().copy(existing)
                .setEndDate(newEndDate)
                .setTotalCost(newTotal)
                .setStatus(existing.getStatus())
                .build();
        return this.booking.save(updated);
    }

    private boolean isActive(Booking b) {
        return "CONFIRMED".equalsIgnoreCase(b.getStatus());
    }

    private void validateDates(String start, String end, boolean mustNotBePast) {
        LocalDate s;
        LocalDate e;
        try {
            s = LocalDate.parse(start);
            e = LocalDate.parse(end);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("Dates must be in the format YYYY-MM-DD");
        }
        if (!e.isAfter(s)) {
            throw new IllegalArgumentException("The end date must be after the start date");
        }
        if (mustNotBePast && s.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The start date can't be in the past");
        }
    }

    // Back-to-back bookings are allowed (one ends the day the next starts).
    private void assertNoClash(String carId, String start, String end, String ignoreBookingId) {
        for (Booking b : this.booking.findAll()) {
            if (!b.getCarId().equals(carId) || !isActive(b)) continue;
            if (ignoreBookingId != null && ignoreBookingId.equals(b.getBookingId())) continue;
            if (start.compareTo(b.getEndDate()) < 0 && end.compareTo(b.getStartDate()) > 0) {
                throw new IllegalStateException("This car is already booked from " + b.getStartDate() + " to "
                        + b.getEndDate() + ". Please choose different dates.");
            }
        }
    }
}