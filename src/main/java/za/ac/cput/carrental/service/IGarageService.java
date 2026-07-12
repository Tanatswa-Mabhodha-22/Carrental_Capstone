package za.ac.cput.carrental.service;
import za.ac.cput.carrental.domain.Garage;
import java.util.List;


public interface IGarageService {

    Garage create(String garageId,
                  String garageName,
                  String address,
                  String city,
                  String managerName);

    Garage read(String garageId);

    Garage update(Garage garage);

    boolean delete(String garageId);

    List<Garage> getAll();
    }
