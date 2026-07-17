/* InsuranceService.java
        Insurance Service Implementation
        Author: Thandeka Chanatal Malande (222857005)
        Date: 11 July 2026 */

        package za.ac.cput.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.carrental.domain.Insurance;
import za.ac.cput.carrental.repository.InsuranceRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
@Service
public class InsuranceService implements IInsuranceService {

    @Autowired
private InsuranceRepository insurance;


    @Override
    public Insurance create(Insurance insurance) {
        return this.insurance.save(insurance);
    }

    @Override
    public Insurance read(String id) {
        return this.insurance.findById(id).orElse(null);
    }

    @Override
    public Insurance update(Insurance insurance) {
        return this.insurance.save(insurance);
    }

    @Override
    public boolean delete(String id) {
        this.insurance.deleteById(id);
        return true;
    }
}