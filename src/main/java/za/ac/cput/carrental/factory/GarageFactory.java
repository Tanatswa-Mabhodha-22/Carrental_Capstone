package za.ac.cput.carrental.factory;

import za.ac.cput.carrental.domain.Garage;
import za.ac.cput.carrental.util.Helper;

/*
 * GarageFactory.java
 * Garage Factory class
 * Author: 222359366
 * Date: 28 June 2026
 */

public class GarageFactory {

    public static Garage createGarage(String garageId,
                                      String garageName,
                                      String address,
                                      String city,
                                      String managerName) {

        if (Helper.isNullOrEmpty(garageId) ||
                Helper.isNullOrEmpty(garageName) ||
                Helper.isNullOrEmpty(address) ||
                Helper.isNullOrEmpty(city) ||
                Helper.isNullOrEmpty(managerName)) {
            return null;
        }

        return new Garage.Builder()
                .setGarageId(garageId)
                .setGarageName(garageName)
                .setAddress(address)
                .setCity(city)
                .setManagerName(managerName)
                .build();
    }
}