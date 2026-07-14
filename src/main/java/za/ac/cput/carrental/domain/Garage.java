/*
 * Garage.java
 * Garage POJO class
 * Author: SE MACHAULE (222359366)
 * Date: 21 June 2026
 */

package za.ac.cput.carrental.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "garages")
public class Garage {

    @Id
    @Column(name = "garage_id")
    private String garageId;
    private String garageName;
    private String address;
    private String city;
    private String managerName;

    @Transient
    private List<Car> cars;

    private Garage() {
    }

    private Garage(Builder builder) {
        this.garageId = builder.garageId;
        this.garageName = builder.garageName;
        this.address = builder.address;
        this.city = builder.city;
        this.managerName = builder.managerName;
        this.cars = builder.cars;
    }

    public String getGarageId() {
        return garageId;
    }

    public String getGarageName() {
        return garageName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getManagerName() {
        return managerName;
    }

    public List<Car> listAvailableCars() {
        return cars;
    }

    @Override
    public String toString() {
        return "Garage{" +
                "garageId='" + garageId + '\'' +
                ", garageName='" + garageName + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", managerName='" + managerName + '\'' +
                ", cars=" + cars +
                '}';
    }

    public static class Builder {
        private String garageId;
        private String garageName;
        private String address;
        private String city;
        private String managerName;
        private List<Car> cars = new ArrayList<>();

        public Builder setGarageId(String garageId) {
            this.garageId = garageId;
            return this;
        }

        public Builder setGarageName(String garageName) {
            this.garageName = garageName;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setManagerName(String managerName) {
            this.managerName = managerName;
            return this;
        }

        public Builder setCars(List<Car> cars) {
            this.cars = cars;
            return this;
        }

        public Builder copy(Garage garage) {
            this.garageId = garage.garageId;
            this.garageName = garage.garageName;
            this.address = garage.address;
            this.city = garage.city;
            this.managerName = garage.managerName;
            this.cars = garage.cars;
            return this;
        }

        public Garage build() {
            return new Garage(this);
        }
    }
}