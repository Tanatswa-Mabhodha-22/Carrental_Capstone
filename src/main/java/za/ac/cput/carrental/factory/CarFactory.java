package za.ac.cput.carrental.factory;

/* CarFactory.java
     Car Class
     Author: Malwandla Blessing Mahori (230962963)
     Date: 25 June 2026 */

import za.ac.cput.carrental.domain.Car;

public class CarFactory {

    public static Car buildCar(String carId,String make, String model,int year,
                               double dailyRate, String status, int mileage){
        if (carId == null || carId.isEmpty())
            return null;

        if(make == null || make.isEmpty())
            return null;
        if(model == null || model.isEmpty())
            return null;
        if(year <= 0)
            return null;
        if(dailyRate <= 0)
            return null;
        if(status == null || status.isEmpty())
            return null;
        if(mileage < 0)
            return null;

        return new Car.Builder()
                .carId(carId)
                .make(make)
                .model(model)
                .year(year)
                .dailyRate(dailyRate)
                .status(status)
                .mileage(mileage)
                .build();
    }
}
