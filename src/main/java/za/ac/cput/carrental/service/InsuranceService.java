/* InsuranceService.java
   Insurance Service Implementation
   Author: Thandeka Chantal Malande (222857005)
   Date: 12 July 2026 */

package za.ac.cput.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.carrental.domain.Insurance;
import za.ac.cput.carrental.repository.InsuranceRepository;

import java.util.List;

@Service
public class InsuranceService implements IInsuranceService {

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Override
    public Insurance create(Insurance insurance) {
        return insuranceRepository.save(insurance);
    }

    @Override
    public Insurance read(String insuranceId) {
        return insuranceRepository.findById(insuranceId).orElse(null);
    }

    @Override
    public Insurance update(Insurance insurance) {
        return insuranceRepository.save(insurance);
    }

    @Override
    public boolean delete(String insuranceId) {
        insuranceRepository.deleteById(insuranceId);
        return true;
    }
}