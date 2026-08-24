package za.ac.cput.carrental.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.carrental.domain.Insurance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InsuranceControllerTest {

    @Autowired
    private InsuranceController insuranceController;
    private static Insurance insurance;

    @BeforeAll
    static void setUp() {
        insurance = new Insurance.Builder()
                .setInsuranceId("INS001")
                .setBookingId("BOOK001")
                .setType("Comprehensive")
                .setDailyPremium(150.00)
                .build();
    }

    @Test
    @Order(1)
    void create() {
        Insurance created = insuranceController.create(insurance);
        assertNotNull(created);
        assertEquals(insurance.getInsuranceId(), created.getInsuranceId());
        System.out.println("Created: " + created);
    }

    @Test
    @Order(2)
    void read() {
        Insurance read = insuranceController.read(insurance.getInsuranceId());
        assertNotNull(read);
        assertEquals(insurance.getInsuranceId(), read.getInsuranceId());
        System.out.println("Read: " + read);
    }

    @Test
    @Order(3)
    void update() {
        Insurance updated = new Insurance.Builder()
                .copy(insurance)
                .setType("Third Party")
                .setDailyPremium(80.00)
                .build();
        Insurance result = insuranceController.update(updated);
        assertNotNull(result);
        assertEquals("Third Party", result.getType());
        System.out.println("Updated: " + result);
    }

    @Test
    @Order(4)
    void getAll() {
        List<Insurance> insurances = insuranceController.getAll();
        assertNotNull(insurances);
        assertFalse(insurances.isEmpty());
        System.out.println("All insurances: " + insurances);
    }

    @Test
    @Order(5)
    void delete() {
        insuranceController.delete(insurance.getInsuranceId());
        Insurance deleted = insuranceController.read(insurance.getInsuranceId());
        assertNull(deleted);
        System.out.println("Deleted successfully");
    }
}
