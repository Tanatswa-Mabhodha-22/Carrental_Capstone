package za.ac.cput.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.carrental.domain.Payment;
import za.ac.cput.carrental.repository.PaymentRepository;

@Service
public class PaymentService implements IPaymentService{
    @Autowired
    private PaymentRepository payment ;


    @Override
    public Payment create(Payment payment) {
        return this.payment.save(payment);
    }

    @Override
    public Payment read(String id) {
        return this.payment.findById(id).orElse(null);
    }

    @Override
    public Payment update(Payment payment) {
        return this.payment.save(payment);
    }

    @Override
    public boolean delete(String id) {
        this.payment.deleteById(id);
        return true;
    }
}
