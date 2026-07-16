package za.ac.cput.carrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.carrental.domain.Insurance;
import za.ac.cput.carrental.service.InsuranceService;
@RestController
@RequestMapping("/insurance")
public class InsuranceController {

    @Autowired
    private InsuranceService insuranceService;

    @PostMapping
    public Insurance create(@RequestBody Insurance insurance) {
        return this.insuranceService.create(insurance);
    }

    @GetMapping("/{id}")
    public Insurance read(@PathVariable String id) {
        return this.insuranceService.read(id);
    }

    @PutMapping
    public Insurance update(@RequestBody Insurance insurance) {
        return this.insuranceService.update(insurance);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        this.insuranceService.delete(id);
    }
}
