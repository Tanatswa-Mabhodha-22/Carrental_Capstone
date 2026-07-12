package za.ac.cput.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.carrental.domain.Garage;
import za.ac.cput.carrental.factory.GarageFactory;
import za.ac.cput.carrental.repository.GarageRepository;

import java.util.List;
/*
 * GarageService
 * Author: SE MACHAULE (222359366)
 * Date: 12 June 2026
 */

@Service
public class GarageService implements IGarageService {
    private final GarageRepository garageRepository;

    @Autowired
    public GarageService(GarageRepository garageRepository) {
        this.garageRepository = garageRepository;
    }

    @Override
    public Garage create(String garageId,
                         String garageName,
                         String address,
                         String city,
                         String managerName) {
        Garage garage = GarageFactory.createGarage(garageId, garageName, address, city, managerName);
        return garageRepository.save(garage);
    }

    @Override
    public Garage read(String garageId) {
        return garageRepository.findById(garageId).orElse(null);
    }

    @Override
    public Garage update(Garage garage) {
        return garageRepository.save(garage);
}
    @Override
    public boolean delete(String garageId) {
        if (garageRepository.existsById(garageId)) {
            garageRepository.deleteById(garageId);
            return true;
        }
        return false;
    }

    @Override
    public List<Garage> getAll() {
        return garageRepository.findAll();
    }
}
