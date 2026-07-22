/* InsuranceFactoryTest.java
   Insurance Factory Test class
   Author: Thandeka Chantal Malande (222857005)
   Date: 26 June 2026 */

package za.ac.cput.carrental.factory;

import org.junit.jupiter.api.Test;
import za.ac.cput.carrental.domain.Insurance;

import static org.junit.jupiter.api.Assertions.*;

public class InsuranceFactoryTest {

    @Test
    void testCreateInsurance_Success() {
        Insurance ins = InsuranceFactory.createInsurance(
                "INS001", "BK001", "Basic", 75.00);
        assertNotNull(ins);
        assertEquals("INS001", ins.getInsuranceId());
        assertEquals("BK001", ins.getBookingId());
        assertEquals("Basic", ins.getType());
        assertEquals(75.00, ins.getDailyPremium());
        System.out.println("Created: " + ins);
    }

    @Test
    void testCreateComprehensive_Success() {
        Insurance ins = InsuranceFactory.createComprehensive("INS002", "BK002");
        assertNotNull(ins);
        assertEquals("Comprehensive", ins.getType());
        assertEquals(120.00, ins.getDailyPremium());
        System.out.println("Comprehensive: " + ins);
    }

    @Test
    void testCreateThirdParty_Success() {
        Insurance ins = InsuranceFactory.createThirdParty("INS003", "BK003");
        assertNotNull(ins);
        assertEquals("Third Party", ins.getType());
        assertEquals(55.00, ins.getDailyPremium());
        System.out.println("Third Party: " + ins);
    }

    @Test
    void testCreateCopy_Success() {
        Insurance original = InsuranceFactory.createInsurance(
                "INS004", "BK004", "Comprehensive", 120.00);
        Insurance copy = InsuranceFactory.createCopy(original);
        assertNotNull(copy);
        assertEquals(original.getInsuranceId(), copy.getInsuranceId());
        assertEquals(original.getType(), copy.getType());
        assertEquals(original.getDailyPremium(), copy.getDailyPremium());
        System.out.println("Copy: " + copy);
    }

    @Test
    void testCalculatePremiumTotal_Success() {
        Insurance ins = InsuranceFactory.createInsurance(
                "INS005", "BK005", "Basic", 80.00);
        double total = ins.calculatePremiumTotal(5);
        assertEquals(400.00, total);
        System.out.println("Premium total: " + total);
    }
}