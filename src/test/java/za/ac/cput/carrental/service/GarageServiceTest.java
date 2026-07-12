package za.ac.cput.carrental.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.domain.Garage;
import za.ac.cput.carrental.factory.GarageFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
/* GarageServiceTest
 * Author: SE MACHAULE (222359366)
 * Date: 12 June 2026
 */

public class GarageServiceTest {
    private Garage garage;

    @BeforeEach
    void setup() {
        garage = GarageFactory.createGarage(
                "G001",
                "Cape Town Central Garage",
                "103 Main Road",
                "Cape Town",
                "Sima Buthelezi"
        );
    }

    @Test
    void testGarageIsNotNull() {
        assertNotNull(garage);
    }

    @Test
    void testGetGarageId() {
        assertNotNull(garage.getGarageId());
    }

    @Test
    void testGetGarageName() {
        assertEquals("Cape Town Central Garage", garage.getGarageName());
    }

    @Test
    void testGetAddress() {
        assertEquals("103 Main Road", garage.getAddress());
    }

    @Test
    void testGetCity() {
        assertEquals("Cape Town", garage.getCity());
    }

    @Test
    void testGetManagerName() {
        assertEquals("Sima Buthelezi", garage.getManagerName());
    }

    @Test
    void testToString() {
        assertNotNull(garage.toString());
    }
}
