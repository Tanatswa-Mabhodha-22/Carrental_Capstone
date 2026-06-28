package za.ac.cput.carrental.factory;

import za.ac.cput.carrental.domain.Insurance;

public class InsuranceFactoryTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {

        testCreateInsurance();
        testCreateComprehensive();
        testCreateThirdParty();
        testCreateCopy();
        testCalculatePremiumTotal();

        System.out.println("\n=============================");
        System.out.println("Tests passed : " + passed);
        System.out.println("Tests failed : " + failed);
        System.out.println("=============================");
    }

    // Test 1: createInsurance() builds object with correct fields
    static void testCreateInsurance() {
        Insurance ins = InsuranceFactory.createInsurance(
                "INS001", "BK001", "Basic", 75.00);

        assertEqual("testCreateInsurance - insuranceId",
                "INS001", ins.getInsuranceId());
        assertEqual("testCreateInsurance - bookingId",
                "BK001", ins.getBookingId());
        assertEqual("testCreateInsurance - type",
                "Basic", ins.getType());
        assertDoubleEqual("testCreateInsurance - dailyPremium",
                75.00, ins.getDailyPremium());
    }

    // Test 2: createComprehensive() sets correct type and premium
    static void testCreateComprehensive() {
        Insurance ins = InsuranceFactory.createComprehensive("INS002", "BK002");

        assertEqual("testCreateComprehensive - type",
                "Comprehensive", ins.getType());
        assertDoubleEqual("testCreateComprehensive - dailyPremium",
                120.00, ins.getDailyPremium());
        assertEqual("testCreateComprehensive - insuranceId",
                "INS002", ins.getInsuranceId());
        assertEqual("testCreateComprehensive - bookingId",
                "BK002", ins.getBookingId());
    }

    // Test 3: createThirdParty() sets correct type and premium
    static void testCreateThirdParty() {
        Insurance ins = InsuranceFactory.createThirdParty("INS003", "BK003");

        assertEqual("testCreateThirdParty - type",
                "Third Party", ins.getType());
        assertDoubleEqual("testCreateThirdParty - dailyPremium",
                55.00, ins.getDailyPremium());
        assertEqual("testCreateThirdParty - insuranceId",
                "INS003", ins.getInsuranceId());
        assertEqual("testCreateThirdParty - bookingId",
                "BK003", ins.getBookingId());
    }

    // Test 4: createCopy() produces identical but separate object
    static void testCreateCopy() {
        Insurance original = InsuranceFactory.createInsurance(
                "INS004", "BK004", "Comprehensive", 120.00);

        Insurance copy = InsuranceFactory.createCopy(original);

        assertEqual("testCreateCopy - insuranceId",
                original.getInsuranceId(), copy.getInsuranceId());
        assertEqual("testCreateCopy - bookingId",
                original.getBookingId(), copy.getBookingId());
        assertEqual("testCreateCopy - type",
                original.getType(), copy.getType());
        assertDoubleEqual("testCreateCopy - dailyPremium",
                original.getDailyPremium(), copy.getDailyPremium());
    }

    // Test 5: calculatePremiumTotal() returns correct amount
    static void testCalculatePremiumTotal() {
        Insurance ins = InsuranceFactory.createInsurance(
                "INS005", "BK005", "Basic", 80.00);

        double total = ins.calculatePremiumTotal(5);
        assertDoubleEqual("testCalculatePremiumTotal - 5 days at R80",
                400.00, total);
    }

    // Helper: assert String equality
    static void assertEqual(String testName, String expected, String actual) {
        if (expected.equals(actual)) {
            System.out.println("PASS : " + testName);
            passed++;
        } else {
            System.out.println("FAIL : " + testName
                    + " | expected: " + expected + " | got: " + actual);
            failed++;
        }
    }

    // Helper: assert double equality
    static void assertDoubleEqual(String testName, double expected, double actual) {
        if (Math.abs(expected - actual) < 0.001) {
            System.out.println("PASS : " + testName);
            passed++;
        } else {
            System.out.println("FAIL : " + testName
                    + " | expected: " + expected + " | got: " + actual);
            failed++;
        }
    }
}
