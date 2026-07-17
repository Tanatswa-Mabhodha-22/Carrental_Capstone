package za.ac.cput.carrental.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.carrental.domain.Insurance;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InsuranceServiceTest {

    @Autowired
    private InsuranceService insuranceService;
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
        Insurance created = insuranceService.create(insurance);
        assertNotNull(created);
        assertEquals(insurance.getInsuranceId(), created.getInsuranceId());
        System.out.println("Created: " + created);
    }

    @Test
    @Order(2)
    void read() {
        Insurance read = insuranceService.read(insurance.getInsuranceId());
        assertNotNull(read);
        assertEquals(insurance.getInsuranceId(), read.getInsuranceId());
        System.out.println("Read: " + read);
    }

    @Test
    @Order(3)
    void update() {
        Insurance updated = new Insurance.Builder()
                .copy(insurance)
                .setDailyPremium(200.00)
                .setType("Third Party")
                .build();
        Insurance result = insuranceService.update(updated);
        assertNotNull(result);
        assertEquals(200.00, result.getDailyPremium());
        System.out.println("Updated: " + result);
    }

    @Test
    @Order(4)
    void delete() {
        boolean deleted = insuranceService.delete(insurance.getInsuranceId());
        assertTrue(deleted);
        System.out.println("Deleted: " + deleted);
    }
}