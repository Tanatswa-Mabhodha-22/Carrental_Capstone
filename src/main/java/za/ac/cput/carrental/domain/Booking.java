/*Booking domain entity
        Author: Tanatswa Mabhodha
        Student No:220637482
        Date: 21 June 2026
*/

package za.ac.cput.carrental.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @Column(name = "booking_id", nullable = false)
    private String bookingId;

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Column(name = "car_id", nullable = false)
    private String carId;

    @Column(name = "start_date",  nullable = false)
    private String startDate;

    @Column(name = "end_date",   nullable = false)
    private String endDate;

    @Column(name = "totalCost",  nullable = false)
    private double totalCost;

    protected Booking() {
    }

    private Booking(Builder builder) {
        this.bookingId = builder.bookingId;
        this.memberId = builder.memberId;
        this.carId = builder.carId;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.totalCost = builder.totalCost;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getCarId() {
        return carId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public int calculateRentalDuration() {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return (int) ChronoUnit.DAYS.between(start, end);
    }

    public void extendRental(String newEndDate) {
        this.endDate = newEndDate;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", carId='" + carId + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", totalCost=" + totalCost +
                '}';
    }

    public static class Builder {
        public String bookingId;
        private String memberId;
        private String carId;
        private String startDate;
        private String endDate;
        private double totalCost;

        public Builder setBookingId(String bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder setMemberId(String memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder setCarId(String carId) {
            this.carId = carId;
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setTotalCost(double totalCost) {
            this.totalCost = totalCost;
            return this;
        }

        public Builder copy(Booking book) {
            this.bookingId = book.bookingId;
            this.memberId = book.memberId;
            this.carId = book.carId;
            this.startDate = book.startDate;
            this.endDate = book.endDate;
            this.totalCost = book.totalCost;
            return this;

        }

        public Booking build() {
            return new Booking(this);

        }
    }
}



