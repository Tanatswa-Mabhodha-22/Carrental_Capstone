package za.ac.cput.carrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.carrental.domain.Garage;
import za.ac.cput.carrental.service.GarageService;

@RestController
@RequestMapping("/garage")
public class GarageController {

    @Autowired
    private GarageService garageService;

    @PostMapping
    public Garage create(@RequestBody Garage garage) {
        return this.garageService.create(garage);
    }

    @GetMapping("/{id}")
    public Garage read(@PathVariable String id) {
        return this.garageService.read(id);
    }

    @PutMapping
    public Garage update(@RequestBody Garage garage) {
        return this.garageService.update(garage);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        this.garageService.delete(id);
    }
}
