/* Author: Owen Makene
Student No: 
Date: 28/06/2026
 */

package za.ac.cput.carrental.util;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


public class Helper {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }



    public static boolean isValidDate(String date) {
        if (isNullOrEmpty(date)) return false;
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isDateRangeValid(String startDate, String endDate) {
        if (!isValidDate(startDate) || !isValidDate(endDate)) return false;
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end   = LocalDate.parse(endDate);
        return !end.isBefore(start);
    }

    public static boolean isValidYear(int year) {
        int currentYear = LocalDate.now().getYear();
        return year >= 1886 && year <= currentYear + 1;
    }

    public static boolean isPositiveDouble(double value) {
        return value > 0;
    }

    public static boolean isNonNegativeInt(int value) {
        return value >= 0;
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (isNullOrEmpty(phone)) return false;
        return phone.matches("^[\\+]?[0-9]{10,15}$");
    }

    public static boolean isValidLicense(String license) {
        if (isNullOrEmpty(license)) return false;
        return license.matches("^[A-Z0-9]{6,15}$");
    }

    public static boolean isValidList(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
