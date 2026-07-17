/* Insurance.java
   Insurance entity class - Builder Pattern
   Author: Thandeka Chantal Malande - 222857005
   Date: 21 June 2026 */

package za.ac.cput.carrental.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "insurance")
public class Insurance {

    @Id
    @Column(name = "insurance_id")
    private String insuranceId;
    private String bookingId;
    private String type;
    private double dailyPremium;

    protected Insurance() {
    }

    private Insurance(Builder builder) {
        this.insuranceId  = builder.insuranceId;
        this.bookingId    = builder.bookingId;
        this.type         = builder.type;
        this.dailyPremium = builder.dailyPremium;
    }


    public String getInsuranceId() {
        return insuranceId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getType() {
        return type;
    }

    public double getDailyPremium() {
        return dailyPremium;
    }


    public double calculatePremiumTotal(int days) {
        return dailyPremium * days;
    }


    @Override
    public String toString() {
        return "Insurance{" +
                "insuranceId='" + insuranceId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", type='" + type + '\'' +
                ", dailyPremium=" + dailyPremium +
                '}';
    }


    public static class Builder {


        private String insuranceId;
        private String bookingId;
        private String type;
        private double dailyPremium;


        public Builder setInsuranceId(String insuranceId) {
            this.insuranceId = insuranceId;
            return this;
        }

        public Builder setBookingId(String bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }


        public Builder setDailyPremium(double dailyPremium) {
            this.dailyPremium = dailyPremium;
            return this;
        }

        public Builder copy(Insurance insurance) {
            this.insuranceId  = insurance.getInsuranceId();
            this.bookingId    = insurance.getBookingId();
            this.type         = insurance.getType();
            this.dailyPremium = insurance.getDailyPremium();
            return this;

        }
        public Insurance build() {
            return new Insurance(this);
        }
    }
}
