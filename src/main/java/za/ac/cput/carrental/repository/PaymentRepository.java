package za.ac.cput.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.carrental.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment,String> {
}
