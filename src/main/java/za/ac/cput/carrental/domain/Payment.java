package za.ac.cput.carrental.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @Column(name = "payment_id")
    private String paymentId;
    private String bookingId;
    private double amount;
    private String paymentDate;
    private String method;

    protected Payment() {
    }

    private Payment(Builder builder) {
        this.paymentId = builder.paymentId;
        this.bookingId = builder.bookingId;
        this.amount = builder.amount;
        this.paymentDate = builder.paymentDate;
        this.method = builder.method;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", amount=" + amount +
                ", paymentDate='" + paymentDate + '\'' +
                ", method='" + method + '\'' +
                '}';
    }

    public static class Builder {
        private String paymentId;
        private String bookingId;
        private double amount;
        private String paymentDate;
        private String method;

        public Builder setPaymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder setAmount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder setBookingId(String bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder setPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder copy(Payment payment) {
            this.paymentId = payment.paymentId;
            this.bookingId = payment.bookingId;
            this.amount = payment.amount;
            this.paymentDate = payment.paymentDate;
            this.method = payment.method;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}