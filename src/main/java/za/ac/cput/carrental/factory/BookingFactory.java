/*BookingFactory.java
Author: Tanatswa Mabhodha 220637482
Date: 27/06/2026

 */

package za.ac.cput.carrental.factory;


import za.ac.cput.carrental.domain.Booking;
import za.ac.cput.carrental.util.Helper;

public class BookingFactory {
    public static Booking createBooking(String memberId,
                                        String carId,
                                        String startDate,
                                        String endDate,
                                        double totalCost){

        if(memberId ==null || memberId.isEmpty())
            return null;
        if(carId == null || carId.isEmpty())
            return null;
        if(startDate == null || startDate.isEmpty())
            return null;
        if(endDate == null || endDate.isEmpty())
            return null;
        if(!Helper.isDateRangeValid(startDate, endDate))
            return null;
        if(totalCost < 0)
            return null;


        return new Booking.Builder()
                .setBookingId(Helper.generateId())
                .setMemberId(memberId)
                .setCarId(carId)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setTotalCost(totalCost)
                .build();
    }

}
