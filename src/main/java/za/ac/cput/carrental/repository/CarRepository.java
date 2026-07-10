package za.ac.cput.carrental.repository;

/* CarRepository.java
     Car Repository Interface
     Author: Malwandla Blessing Mahori (230962963)
     Date: 10 July 2026 */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.carrental.domain.Car;

public interface CarRepository extends JpaRepository<Car, String> {
}
