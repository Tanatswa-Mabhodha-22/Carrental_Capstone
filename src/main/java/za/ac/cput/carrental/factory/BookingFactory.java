/*BookingFactory.java
Author: Tanatswa Mabhodha 220637482
Date: 05 July 2026 */

package za.ac.cput.carrental.factory;

import za.ac.cput.carrental.domain.Booking;

public class BookingFactory {

    public static Booking createBooking(String bookingId,
                                        String memberId,
                                        String carId,
                                        String startDate,
                                        String endDate,
                                        double totalCost) {
        return new Booking.Builder()
                .setBookingId(bookingId)
                .setMemberId(memberId)
                .setCarId(carId)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setTotalCost(totalCost)
                .build();
    }
}


