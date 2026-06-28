/* InsuranceFactory.java
   TDD Test class for InsuranceFactory
   Author: Thandeka Chantal Malande - 222857005
   Date: 28 June 2026 */

package za.ac.cput.carrental.factory;

import za.ac.cput.carrental.domain.Insurance;

public class InsuranceFactory {

    // Factory method: create a basic Insurance object
    public static Insurance createInsurance(String insuranceId,
                                            String bookingId,
                                            String type,
                                            double dailyPremium) {
        return new Insurance.Builder()
                .setInsuranceId(insuranceId)
                .setBookingId(bookingId)
                .setType(type)
                .setDailyPremium(dailyPremium)
                .build();
    }

    // Factory method: create a Comprehensive insurance policy
    public static Insurance createComprehensive(String insuranceId,
                                                String bookingId) {
        return new Insurance.Builder()
                .setInsuranceId(insuranceId)
                .setBookingId(bookingId)
                .setType("Comprehensive")
                .setDailyPremium(120.00)
                .build();
    }

    // Factory method: create a Third Party insurance policy
    public static Insurance createThirdParty(String insuranceId,
                                             String bookingId) {
        return new Insurance.Builder()
                .setInsuranceId(insuranceId)
                .setBookingId(bookingId)
                .setType("Third Party")
                .setDailyPremium(55.00)
                .build();
    }

    // Factory method: create a copy of an existing Insurance object
    public static Insurance createCopy(Insurance existing) {
        return new Insurance.Builder()
                .copy(existing)
                .build();
    }
}



