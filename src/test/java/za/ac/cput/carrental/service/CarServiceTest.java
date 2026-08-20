package za.ac.cput.carrental.service;



import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.carrental.domain.Car;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CarServiceTest {

    @Autowired
    private CarService carService;
    private static Car car;

    @BeforeAll
    static void setUp() {
        car = new Car.Builder()
                .carId("C001")
                .make("Toyota")
                .model("Corolla")
                .year(2022)
                .dailyRate(45.00)
                .status("AVAILABLE")
                .mileage(0)
                .build();
    }

    @Test
    @Order(1)
    void create() {
        Car created = carService.create(car);
        assertNotNull(created);
        assertEquals(car.getCarId(), created.getCarId());
        System.out.println("Created: " + created);
    }

    @Test
    @Order(2)
    void read() {
        Car read = carService.read(car.getCarId());
        assertNotNull(read);
        assertEquals(car.getCarId(), read.getCarId());
        System.out.println("Read: " + read);
    }

    @Test
    @Order(3)
    void update() {
        Car updated = new Car.Builder()
                .copy(car)
                .status("UNDER_MAINTENANCE")
                .mileage(5000)
                .build();
        Car result = carService.update(updated);
        assertNotNull(result);
        assertEquals("UNDER_MAINTENANCE", result.getStatus());
        assertEquals(5000, result.getMileage());
        System.out.println("Updated: " + result);
    }

    @Test
    @Order(4)
    void getAll() {
        List<Car> cars = carService.getAll();
        assertNotNull(cars);
        assertFalse(cars.isEmpty());
        System.out.println("All cars: " + cars);
    }

    @Test
    @Order(5)
    void delete() {
        boolean deleted = carService.delete(car.getCarId());
        assertTrue(deleted);
        System.out.println("Deleted: " + deleted);
    }
}