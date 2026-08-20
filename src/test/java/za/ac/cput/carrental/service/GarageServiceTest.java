package za.ac.cput.carrental.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.carrental.domain.Garage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GarageServiceTest {

    @Autowired
    private GarageService garageService;
    private static Garage garage;

    @BeforeAll
    static void setUp() {
        garage = new Garage.Builder()
                .setGarageId("G001")
                .setGarageName("Cape Town Central Garage")
                .setAddress("103 Main Road")
                .setCity("Cape Town")
                .setManagerName("Sima Buthelezi")
                .build();
    }

    @Test
    @Order(1)
    void create() {
        Garage created = garageService.create(garage);
        assertNotNull(created);
        assertEquals(garage.getGarageId(), created.getGarageId());
        System.out.println("Created: " + created);
    }

    @Test
    @Order(2)
    void read() {
        Garage read = garageService.read(garage.getGarageId());
        assertNotNull(read);
        assertEquals(garage.getGarageId(), read.getGarageId());
        System.out.println("Read: " + read);
    }

    @Test
    @Order(3)
    void update() {
        Garage updated = new Garage.Builder()
                .copy(garage)
                .setManagerName("John Dlamini")
                .build();
        Garage result = garageService.update(updated);
        assertNotNull(result);
        assertEquals("John Dlamini", result.getManagerName());
        System.out.println("Updated: " + result);
    }

    @Test
    @Order(4)
    void getAll() {
        List<Garage> garages = garageService.getAll();
        assertNotNull(garages);
        assertFalse(garages.isEmpty());
        System.out.println("All garages: " + garages);
    }

    @Test
    @Order(5)
    void delete() {
        boolean deleted = garageService.delete(garage.getGarageId());
        assertTrue(deleted);
        System.out.println("Deleted: " + deleted);
    }
}