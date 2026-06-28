package za.ac.cput.carrental.factory;

/* CarFactoryTests.java
     Car Class
     Author: Malwandla Blessing Mahori (230962963)
     Date: 21 June 2026 */

import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.domain.Car;

import static org.junit.jupiter.api.Assertions.*;

public class CarFactoryTests {

    @Test
    void testBuildCar_Success(){
        Car car = CarFactory.buildCar("C001", "Toyota", "Camry", 2020, 100.0, "Available", 50000);
        assertNotNull(car);
        assert car.getCarId().equals("C001");
        assert car.getMake().equals("Toyota");
        assert car.getModel().equals("Camry");
        assert car.getYear() == 2020;
        assert car.getDailyRate() == 100.0;
        assert car.getStatus().equals("Available");
        assert car.getMileage() == 50000;
    }

    @Test
    void testBuildCar_NullCarId(){
        Car car = CarFactory.buildCar(null, "Toyota", "Camry", 2020, 100.0, "Available", 50000);
        assertNull(car);
    }

    @Test
    void testBuildCar_EmptyMake_ShouldFail(){
        Car car = CarFactory.buildCar("C001", "", "Camry", 2020, 100.0, "Available", 50000);
        assertNull(car);
    }

    @Test
    void testBuildCar_NullModel_ShouldFail(){
        Car car = CarFactory.buildCar("C001", "Toyota", null, 2020, 100.0, "Available", 50000);
        assertNull(car);
    }

    @Test
    void testBuildCar_InvalidYear_ShouldFail(){
        Car car = CarFactory.buildCar("C001", "Toyota", "Camry", 0, 100.0, "Available", 50000);
        assertNull(car);
    }

    @Test
    void testBuildCar_NegativeDailyRate_ShouldFail(){
        Car car = CarFactory.buildCar("C001", "Toyota", "Camry", 2020, -10.0, "Available", 50000);
        assertNull(car);
    }

    @Test
    void testBuildCar_NullStatus_ShouldFail(){
        Car car = CarFactory.buildCar("C001", "Toyota", "Camry", 2020, 100.0, null, 50000);
        assertNull(car);
    }

    @Test
    void testBuildCar_NegativeMileage_ShouldFail(){
        Car car = CarFactory.buildCar("C001", "Toyota", "Camry", 2020, 100.0, "Available", -100);
        assertNull(car);
    }
}
