package za.ac.cput.carrental.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.domain.Payment;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentFactoryTest {

    @Test
    void testCreatePayment_Success() {
        Payment payment = PaymentFactory.createPayment(
                "PAY001",
                "BOOK001",
                1500.00,
                "2026-07-14",
                "Credit Card"
        );
        assertNotNull(payment);
        assertEquals("PAY001", payment.getPaymentId());
        assertEquals("BOOK001", payment.getBookingId());
        assertEquals(1500.00, payment.getAmount());
        assertEquals("2026-07-14", payment.getPaymentDate());
        assertEquals("Credit Card", payment.getMethod());
    }

    @Test
    void testCreatePayment_NotNull() {
        Payment payment = PaymentFactory.createPayment(
                "PAY002",
                "BOOK002",
                800.00,
                "2026-07-15",
                "EFT"
        );
        assertNotNull(payment);
    }

    @Test
    void testCreatePayment_NullPaymentId_ReturnsNull() {
        Payment payment = PaymentFactory.createPayment(
                null,
                "BOOK001",
                1500.00,
                "2026-07-14",
                "Credit Card"
        );
        assertNull(payment);
    }

    @Test
    void testCreatePayment_NullBookingId_ReturnsNull() {
        Payment payment = PaymentFactory.createPayment(
                "PAY001",
                null,
                1500.00,
                "2026-07-14",
                "Credit Card"
        );
        assertNull(payment);
    }

    @Test
    void testCreatePayment_NegativeAmount_ReturnsNull() {
        Payment payment = PaymentFactory.createPayment(
                "PAY001",
                "BOOK001",
                -100.00,
                "2026-07-14",
                "Credit Card"
        );
        assertNull(payment);
    }

    @Test
    void testCreatePayment_NullMethod_ReturnsNull() {
        Payment payment = PaymentFactory.createPayment(
                "PAY001",
                "BOOK001",
                1500.00,
                "2026-07-14",
                null
        );
        assertNull(payment);
    }
}
