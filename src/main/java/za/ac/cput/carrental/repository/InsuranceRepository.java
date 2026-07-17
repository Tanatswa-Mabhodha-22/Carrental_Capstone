package za.ac.cput.carrental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.carrental.domain.Insurance;

public interface InsuranceRepository extends JpaRepository <Insurance,String>{
}
