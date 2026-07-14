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


    @Autowired
    private GarageRepository garage ;


    @Override
    public Garage craete(Garage garage) {
        return this.garage.save(garage);
    }

    @Override
    public Garage read(String id) {
        return this.garage.findById(id).orElse(null);
    }

    @Override
    public Garage update(Garage garage) {
        return this.garage.save(garage);
    }

    @Override
    public boolean delete(String id) {
        this.garage.deleteById(id);
        return true;
    }
}
