/* InsuranceService.java
        Insurance Service Implementation
        Author: Thandeka Chanatal Malande (222857005)
        Date: 12 July 2026 */

        package za.ac.cput.carrental.service;

import za.ac.cput.carrental.domain.Insurance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class InsuranceService implements IInsuranceService {
    private HashMap<String, Insurance> store = new HashMap<>();

    // CREATE - adds a new Insurance to the store
    @Override
    public Insurance create(Insurance insurance) {
        if (insurance == null || insurance.getInsuranceId() == null) {
            return null;
        }
        store.put(insurance.getInsuranceId(), insurance);
        return insurance;
    }

    // READ - retrieves an Insurance by its ID, wrapped in Optional
    @Override
    public Optional<Insurance> read(String insuranceId) {
        return Optional.ofNullable(store.get(insuranceId));
    }

    // UPDATE - replaces existing Insurance with updated version
    @Override
    public Insurance update(Insurance insurance) {
        if (insurance == null || !store.containsKey(insurance.getInsuranceId())) {
            return null;
        }
        store.put(insurance.getInsuranceId(), insurance);
        return insurance;
    }

    // DELETE - removes an Insurance from the store by its ID
    @Override
    public boolean delete(String insuranceId) {
        if (!store.containsKey(insuranceId)) {
            return false;
        }
        store.remove(insuranceId);
        return true;
    }

    // GETALL - returns all Insurance objects in the store
    @Override
    public List<Insurance> getAll() {
        return new ArrayList<>(store.values());
    }
}
