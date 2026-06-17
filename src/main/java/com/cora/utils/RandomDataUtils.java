package com.cora.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates unique values for form fields (appointments, etc.).
 * Do not use for login — keep valid credentials in config.properties.
 */
public final class RandomDataUtils {

    private RandomDataUtils() {
    }

    public static String suffix() {
        return System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    /** e.g. "awfawf-x7k2" when prefix comes from config */
    public static String withSuffix(String prefix) {
        return prefix + "-" + suffix();
    }

    public static String clientName() {
        return "Client-" + suffix();
    }

    public static String phoneNumber() {
        int lastSeven = ThreadLocalRandom.current().nextInt(1_000_000, 9_999_999);
        return "555" + lastSeven;
    }

    public static String notes() {
        return "Automated test notes " + suffix();
    }

    public static String streetAddress() {
        return "123 Automation Lane " + suffix();
    }

    public static String city() {
        return "Orlando";
    }

    public static String zipCode() {
        return "32804";
    }

    public static String listingPrice() {
        int price = ThreadLocalRandom.current().nextInt(200_000, 900_000);
        return String.valueOf(price);
    }

    public static String bedrooms() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(2, 6));
    }

    public static String bathrooms() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(1, 4));
    }

    public static String squareFootage() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(1200, 3500));
    }

    public static String unitNumber() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(1, 999));
    }

    public static String county() {
        return "Orange County";
    }

    public static String yearBuilt() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(1985, 2024));
    }

    public static String lotSize() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(5000, 15000));
    }

    public static String propertyDescription() {
        return "Automated property description " + suffix();
    }

    public static String listingDescription() {
        return "Spacious automated test listing with updated kitchen, fenced yard, and great schools. Ref: " + suffix();
    }

    public static String totalRooms() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(5, 12));
    }

    public static String parkingSpaces() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(1, 4));
    }

    public static String annualPropertyTax() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(2000, 12000));
    }

    public static String taxAssessment() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(250000, 750000));
    }

    public static String parcelApn() {
        return "APN-" + ThreadLocalRandom.current().nextInt(100000, 999999);
    }

    public static String zoning() {
        return "R-" + ThreadLocalRandom.current().nextInt(1, 5);
    }

    public static String schoolName() {
        return "School-" + suffix();
    }

    public static String schoolDistrict() {
        return "District-" + suffix();
    }

    public static String legalDescription() {
        return "Lot 12 Block 4 automated legal description " + suffix();
    }

    public static String daysOnMarket() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(1, 30));
    }

    public static String showingContact() {
        return "Agent-" + suffix() + "@test.com";
    }

    public static String accessInstructions() {
        return "Lockbox on front door. Call " + phoneNumber() + " before showing.";
    }

    public static String bestShowingTime() {
        return "Weekdays after 5 PM or weekends 10 AM – 4 PM";
    }
}
