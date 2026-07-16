package za.ac.cput.carrental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.carrental.domain.Payment;
import za.ac.cput.carrental.service.PaymentService;

@RestController
@RequestMapping
public class PaymentController {
    @Autowired
    private PaymentService payment;

    @PostMapping
    public Payment create(@RequestBody Payment payment) {
        return this.payment.create(payment);
    }

    @GetMapping("/{id}")
    public Payment read(@PathVariable String id) {
        return this.payment.read(id);
    }

    @PutMapping
    public Payment update(@RequestBody Payment payment) {
        return this.payment.update(payment);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        this.payment.delete(id);
    }

    }

