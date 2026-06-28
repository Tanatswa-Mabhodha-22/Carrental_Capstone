package za.ac.cput.carrental.factory;
import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.domain.Garage;
import static org.junit.jupiter.api.Assertions.*;


public class GarageFactoryTests {

    @Test
        void createGarageShouldNotBeNull() {
            Garage garage = GarageFactory.createGarage(
                    "G001",
                    "Cape Town Central Garage",
                    "101 Main Road",
                    "Cape Town",
                    "Sima Buthelezi"
            );

            assertNotNull(garage);
            System.out.println(garage);
        }

        @Test
        void createGarageShouldSetGarageDetails() {
            Garage garage = GarageFactory.createGarage(
                    "G001",
                    "Cape Town Central Garage",
                    "101 Main Road",
                    "Cape Town",
                    "Sima Buthelezi"
            );

            assertEquals("Cape Town Central Garage", garage.getGarageName());
            assertEquals("101 Main Road", garage.getAddress());
            assertEquals("Cape Town", garage.getCity());
            assertEquals("Sima Buthelezi", garage.getManagerName());
        }

        @Test
        void createGarageShouldGenerateGarageId() {
            Garage garage = GarageFactory.createGarage(
                    "G001",
                    "Cape Town Central Garage",
                    "101 Main Road",
                    "Cape Town",
                    "Sima Buthelezi"
            );

            assertNotNull(garage.getGarageId());
            assertFalse(garage.getGarageId().isEmpty());
        }
    }