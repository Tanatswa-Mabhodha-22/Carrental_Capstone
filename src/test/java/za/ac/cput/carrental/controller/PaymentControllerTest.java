package za.ac.cput.carrental.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.carrental.domain.Payment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentControllerTest {

    @Autowired
    private PaymentController paymentController;
    private static Payment payment;

    @BeforeAll
    static void setUp() {
        payment = new Payment.Builder()
                .setPaymentId("PAY001")
                .setBookingId("BOOK001")
                .setAmount(1500.00)
                .setPaymentDate("2026-07-14")
                .setMethod("Credit Card")
                .build();
    }

    @Test
    @Order(1)
    void create() {
        Payment created = paymentController.create(payment);
        assertNotNull(created);
        assertEquals(payment.getPaymentId(), created.getPaymentId());
        System.out.println("Created: " + created);
    }

    @Test
    @Order(2)
    void read() {
        Payment read = paymentController.read(payment.getPaymentId());
        assertNotNull(read);
        assertEquals(payment.getPaymentId(), read.getPaymentId());
        System.out.println("Read: " + read);
    }

    @Test
    @Order(3)
    void update() {
        Payment updated = new Payment.Builder()
                .copy(payment)
                .setAmount(2000.00)
                .setMethod("EFT")
                .build();
        Payment result = paymentController.update(updated);
        assertNotNull(result);
        assertEquals(2000.00, result.getAmount());
        System.out.println("Updated: " + result);
    }

    @Test
    @Order(4)
    void getAll() {
        List<Payment> payments = paymentController.getAll();
        assertNotNull(payments);
        assertFalse(payments.isEmpty());
        System.out.println("All payments: " + payments);
    }

    @Test
    @Order(5)
    void delete() {
        paymentController.delete(payment.getPaymentId());
        Payment deleted = paymentController.read(payment.getPaymentId());
        assertNull(deleted);
        System.out.println("Deleted successfully");
    }
}