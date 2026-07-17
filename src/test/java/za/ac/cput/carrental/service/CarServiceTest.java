package za.ac.cput.carrental.service;

/* CarServiceTest.java
     Car Service Test
     Author: Malwandla Blessing Mahori (230962963)
     Date: 10 July 2026 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.domain.Car;
import za.ac.cput.carrental.factory.CarFactory;

import static org.junit.jupiter.api.Assertions.*;

public class CarServiceTest {
    private Car car;

    @BeforeEach
    void setup() {

        car = CarFactory.buildCar("C001", "Toyota", "Corolla", 2022, 45.00, "AVAILABLE", 0);
    }

    @Test
    void testCarIsNotNull() {
        assertNotNull(car);
    }

    @Test
    void testGetCarId() {
        assertEquals("C001", car.getCarId());
    }

    @Test
    void testGetMake() {
        assertEquals("Toyota", car.getMake());
    }

    @Test
    void testGetModel() {
        assertEquals("Corolla", car.getModel());
    }

    @Test
    void testGetYear() {
        assertEquals(2022, car.getYear());
    }

    @Test
    void testGetDailyRate() {
        assertEquals(45.00, car.getDailyRate());
    }

    @Test
    void testGetStatus() {
        assertEquals("AVAILABLE", car.getStatus());
    }

    @Test
    void testGetMileage() {
        assertEquals(0, car.getMileage());
    }

    @Test
    void testMarkForMaintenance() {
        car.markForMaintenance();
        assertEquals("UNDER_MAINTENANCE", car.getStatus());
    }

    @Test
    void testUpdateMileage() {
        car.updateMileage(5000);
        assertEquals(5000, car.getMileage());
    }

    @Test
    void testToString() {
        assertNotNull(car.toString());
    }
}