package za.ac.cput.carrental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.carrental.domain.Car;
import za.ac.cput.carrental.repository.CarRepository;

import java.util.List;
import java.util.Optional;


@Service
public class CarService implements ICarService{

    private final CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public Car create(Car car) {
        return carRepository.save(car);
    }

    @Override
    public Optional<Car> read(String carId) {
        return carRepository.findById(carId);
    }

    @Override
    public Car update(Car car) {
        return carRepository.save(car);
    }

    @Override
    public boolean delete(String carId) {
        if (carRepository.existsById(carId)) {
            carRepository.deleteById(carId);
            return true;
        }
        return false;
    }

    @Override
    public List<Car> getAll() {
        return carRepository.findAll();
    }
}
