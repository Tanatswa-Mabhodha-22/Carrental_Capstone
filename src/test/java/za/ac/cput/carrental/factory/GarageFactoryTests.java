package za.ac.cput.carrental.factory;
import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.domain.Garage;
import static org.junit.jupiter.api.Assertions.*;


public class GarageFactoryTests {
    @Test
    void createGarage() {
        Garage garage = GarageFactory.createGarage(
                "G001",
                "Cape Town Central Garage",
                "10 Main Road",
                "Cape Town",
                "Sima Buthelezi"
        );

        assertNotNull(garage);
        assertNotNull(garage.getGarageId());
        assertEquals("Cape Town Central Garage", garage.getGarageName());
        assertEquals("10 Main Road", garage.getAddress());
        assertEquals("Cape Town", garage.getCity());
        assertEquals("Sima Buthelezi", garage.getManagerName());

        System.out.println(garage);
    }
}
