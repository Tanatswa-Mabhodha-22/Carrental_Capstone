/* InsuranceServiceTest.java
   TDD Test class for InsuranceService
   Author: Thandeka Chantal Malande: 222857005
   Date: 12 July 2026 */

package za.ac.cput.carrental.service;

import za.ac.cput.carrental.domain.Insurance;
import za.ac.cput.carrental.factory.InsuranceFactory;

import java.util.List;
import java.util.Optional;

public class InsuranceServiceTest {

    static int passed = 0;
    static int failed = 0;

    static InsuranceService service = new InsuranceService();

    public static void main(String[] args) {

        testCreate();
        testRead();
        testUpdate();
        testGetAll();
        testDelete();
        testReadAfterDelete();
        testCreateNull();

        System.out.println("\n=============================");
        System.out.println("Tests passed : " + passed);
        System.out.println("Tests failed : " + failed);
        System.out.println("=============================");
    }

    // Test 1: create() stores and returns the Insurance object
    static void testCreate() {
        Insurance ins = InsuranceFactory.createInsurance(
                "INS001", "BK001", "Comprehensive", 120.00);

        Insurance result = service.create(ins);

        assertNotNull("testCreate - result not null", result);
        assertEqual("testCreate - insuranceId", "INS001", result.getInsuranceId());
        assertEqual("testCreate - type", "Comprehensive", result.getType());
    }

    // Test 2: read() retrieves the correct Insurance wrapped in Optional
    static void testRead() {
        Optional<Insurance> result = service.read("INS001");

        assertTrue("testRead - Optional is present", result.isPresent());
        assertEqual("testRead - insuranceId", "INS001", result.get().getInsuranceId());
        assertEqual("testRead - bookingId", "BK001", result.get().getBookingId());
    }

    // Test 3: update() replaces the existing Insurance
    static void testUpdate() {
        Insurance updated = InsuranceFactory.createInsurance(
                "INS001", "BK001", "Third Party", 55.00);

        Insurance result = service.update(updated);

        assertNotNull("testUpdate - result not null", result);
        assertEqual("testUpdate - type changed", "Third Party", result.getType());
        assertDoubleEqual("testUpdate - dailyPremium changed", 55.00, result.getDailyPremium());
    }

    // Test 4: getAll() returns all Insurance objects in the store
    static void testGetAll() {
        Insurance ins2 = InsuranceFactory.createInsurance(
                "INS002", "BK002", "Basic", 75.00);
        service.create(ins2);

        List<Insurance> all = service.getAll();

        assertTrue("testGetAll - list not empty", !all.isEmpty());
        assertTrue("testGetAll - contains 2 records", all.size() == 2);
    }

    // Test 5: delete() removes the Insurance and returns true
    static void testDelete() {
        boolean result = service.delete("INS002");
        assertTrue("testDelete - returns true", result);
    }

    // Test 6: read() returns empty Optional after deletion
    static void testReadAfterDelete() {
        Optional<Insurance> result = service.read("INS002");
        assertTrue("testReadAfterDelete - Optional is empty", result.isEmpty());
    }

    // Test 7: create() with null returns null safely
    static void testCreateNull() {
        Insurance result = service.create(null);
        assertNull("testCreateNull - null input returns null", result);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    static void assertEqual(String name, String expected, String actual) {
        if (expected.equals(actual)) {
            System.out.println("PASS : " + name);
            passed++;
        } else {
            System.out.println("FAIL : " + name + " | expected: " + expected + " | got: " + actual);
            failed++;
        }
    }

    static void assertDoubleEqual(String name, double expected, double actual) {
        if (Math.abs(expected - actual) < 0.001) {
            System.out.println("PASS : " + name);
            passed++;
        } else {
            System.out.println("FAIL : " + name + " | expected: " + expected + " | got: " + actual);
            failed++;
        }
    }

    static void assertTrue(String name, boolean condition) {
        if (condition) {
            System.out.println("PASS : " + name);
            passed++;
        } else {
            System.out.println("FAIL : " + name + " | expected: true | got: false");
            failed++;
        }
    }

    static void assertNotNull(String name, Object obj) {
        if (obj != null) {
            System.out.println("PASS : " + name);
            passed++;
        } else {
            System.out.println("FAIL : " + name + " | expected: not null | got: null");
            failed++;
        }
    }

    static void assertNull(String name, Object obj) {
        if (obj == null) {
            System.out.println("PASS : " + name);
            passed++;
        } else {
            System.out.println("FAIL : " + name + " | expected: null | got: " + obj);
            failed++;
        }
    }
}
