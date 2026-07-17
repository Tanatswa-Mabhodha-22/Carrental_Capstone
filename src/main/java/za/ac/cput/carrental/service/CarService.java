package za.ac.cput.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.carrental.domain.Car;
import za.ac.cput.carrental.repository.CarRepository;

import java.util.List;
import java.util.Optional;


@Service
public class CarService implements ICarSerivce{
@Autowired
private  CarRepository CAR ;

    @Override
    public Car create(Car car) {
        return this.CAR.save(car);
    }

    @Override
    public Car read(String id) {
        return this.CAR.findById(id).orElse(null);
    }

    @Override
    public Car update(Car car) {
        return this.CAR.save(car);
    }

    @Override
    public boolean delete(String id) {
        this.CAR.deleteById(id);
        return true;
    }
}
