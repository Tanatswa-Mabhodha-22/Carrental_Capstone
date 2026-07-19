package za.ac.cput.carrental.factory;

import za.ac.cput.carrental.domain.Payment;
import za.ac.cput.carrental.util.Helper;

public class PaymentFactory {

        public static Payment createPayment(String paymentId, String bookingId,
                                            double amount, String paymentDate, String method) {
            if (Helper.isNullOrEmpty(paymentId) || Helper.isNullOrEmpty(bookingId)
                    || !Helper.isPositiveDouble(amount)
                    || !Helper.isValidDate(paymentDate)
                    || Helper.isNullOrEmpty(method)) {
                return null;
            }
            return new Payment.Builder()
                    .setPaymentId(paymentId)
                    .setBookingId(bookingId)
                    .setAmount(amount)
                    .setPaymentDate(paymentDate)
                    .setMethod(method)
                    .build();
        }
    }
