package za.ac.cput.carrental.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.domain.Payment;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentFactoryTest {


        @Test
        void createPayment_validData_success() {
            Payment payment = PaymentFactory.createPayment(
                    "PAY01", "BOOK01", 1500.00, "2026-07-15", "Card");

            assertNotNull(payment);
            assertEquals("PAY01", payment.getPaymentId());
            assertEquals("BOOK01", payment.getBookingId());
            assertEquals(1500.00, payment.getAmount());
            assertEquals("2026-07-15", payment.getPaymentDate());
            assertEquals("Card", payment.getMethod());
        }

        @Test
        void createPayment_nullPaymentId_returnsNull() {
            Payment payment = PaymentFactory.createPayment(
                    null, "BOOK01", 1500.00, "2026-07-15", "Card");
            assertNull(payment);
        }

        @Test
        void createPayment_emptyBookingId_returnsNull() {
            Payment payment = PaymentFactory.createPayment(
                    "PAY01", "", 1500.00, "2026-07-15", "Card");
            assertNull(payment);
        }

        @Test
        void createPayment_negativeAmount_returnsNull() {
            Payment payment = PaymentFactory.createPayment(
                    "PAY01", "BOOK01", -50.00, "2026-07-15", "Card");
            assertNull(payment);
        }

        @Test
        void createPayment_zeroAmount_returnsNull() {
            Payment payment = PaymentFactory.createPayment(
                    "PAY01", "BOOK01", 0.00, "2026-07-15", "Card");
            assertNull(payment);
        }

        @Test
        void createPayment_invalidDate_returnsNull() {
            Payment payment = PaymentFactory.createPayment(
                    "PAY01", "BOOK01", 1500.00, "not-a-date", "Card");
            assertNull(payment);
        }

        @Test
        void createPayment_nullMethod_returnsNull() {
            Payment payment = PaymentFactory.createPayment(
                    "PAY01", "BOOK01", 1500.00, "2026-07-15", null);
            assertNull(payment);
        }
    }

