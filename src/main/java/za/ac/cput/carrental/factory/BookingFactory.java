/*BookingFactory.java
Author: Tanatswa Mabhodha 220637482
Date: 05 July 2026 */

package za.ac.cput.carrental.factory;

import za.ac.cput.carrental.domain.Booking;
import za.ac.cput.carrental.util.Helper;

public class BookingFactory {

    public static Booking createBooking(String bookingId,
                                        String memberId,
                                        String carId,
                                        String startDate,
                                        String endDate,
                                        double totalCost) {

        if(Helper.isNullOrEmpty(bookingId)
                || Helper.isNullOrEmpty(memberId)
                || Helper.isNullOrEmpty(carId)
                 || !Helper.isValidDate(startDate)
                 || !Helper.isValidDate(endDate)
                 || !Helper.isDateRangeValid(startDate, endDate)
                || !Helper.isPositiveDouble(totalCost)){
                return null;
        }



        return new Booking.Builder()
                .setBookingId(bookingId)
                .setMemberId(memberId)
                .setCarId(carId)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setTotalCost(totalCost)
                .setStatus("confirmed")
                .build();
    }
}


