package za.ac.cput.carrental.controller;

/* CarController.java
     CarController Class
     Author: Malwandla Blessing Mahori (230962963)
     Date 17 July 2026 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.carrental.domain.Car;
import za.ac.cput.carrental.service.CarService;

import java.util.List;

@RestController
@RequestMapping("/car")
public class CarController {
    @Autowired
    private CarService carService;

    @PostMapping
    public Car create(@RequestBody Car car) {
        return this.carService.create(car);
    }

    @GetMapping("/{id}")
    public Car read(@PathVariable String id) {
        return this.carService.read(id);
    }

    @PutMapping
    public Car update(@RequestBody Car car) {
        return this.carService.update(car);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        this.carService.delete(id);
    }

    @GetMapping
    public List<Car> getAll() {
        return this.carService.getAll();
    }

}