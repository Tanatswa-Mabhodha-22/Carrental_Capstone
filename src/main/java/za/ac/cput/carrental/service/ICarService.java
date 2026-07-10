package za.ac.cput.carrental.service;

/* ICarService.java
     Car Service Interface
     Author: Malwandla Blessing Mahori (230962963)
     Date: 10 July 2026 */

import za.ac.cput.carrental.domain.Car;

import java.util.List;
import java.util.Optional;

public interface ICarService {

    Car create(Car car);
    Optional<Car> read(String carId);
    Car update(Car car);
    boolean delete(String carId);
    List<Car> getAll();
}
