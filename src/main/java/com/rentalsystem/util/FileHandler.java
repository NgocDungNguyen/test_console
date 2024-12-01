package com.rentalsystem.util;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.rentalsystem.manager.*;
import com.rentalsystem.model.*;

/**
 * Utility class for handling file operations related to the rental system.
 */
public class FileHandler {
    private static final String DATA_DIRECTORY = "resources/data/";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private RentalManager rentalManager;
    private TenantManager tenantManager;
    private OwnerManager ownerManager;
    private HostManager hostManager;
    private PropertyManager propertyManager;

    /**
     * Synchronizes the FileHandler with the various managers in the system.
     */
    public void syncManager(RentalManager rentalManager, TenantManager tenantManager, OwnerManager ownerManager, HostManager hostManager, PropertyManager propertyManager) {
        this.rentalManager = rentalManager;
        this.hostManager = hostManager;
        this.tenantManager = tenantManager;
        this.ownerManager = ownerManager;
        this.propertyManager = propertyManager;
    }

    /**
     * Reads lines from a CSV file.
     * @param filename The name of the file to read
     * @return A list of string arrays, each representing a line in the CSV file
     */
    public List<String[]> readLines(String filename) {
        File file = new File(DATA_DIRECTORY + filename);

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            return reader.readAll();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found: " + filename);
            try {
                if (file.createNewFile()) {
                    System.out.println("File created: " + filename);
                } else {
                    System.err.println("Failed to create file: " + filename);
                }
            } catch (IOException e) {
                System.err.println("Error creating file: " + filename);
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
            e.printStackTrace();
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Writes lines to a CSV file.
     * @param filename The name of the file to write to
     * @param lines The lines to write to the file
     */
    public void writeLines(String filename, List<String[]> lines) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(DATA_DIRECTORY + filename))) {
            writer.writeAll(lines);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filename);
            e.printStackTrace();
        }
    }

    /**
     * Escapes special characters in a string for CSV format.
     * @param data The string to escape
     * @return The escaped string
     */
    private String escapeSpecialCharacters(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    /**
     * Converts a list of strings to a CSV-formatted string.
     * @param data The list of strings to convert
     * @return The CSV-formatted string
     */
    public String convertToCSV(List<String> data) {
        return data.stream()
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    /**
     * Loads rental agreements from file.
     */
    public void loadRentalAgreements() {
        List<String[]> lines = readLines("rental_agreements.txt");
        for (String[] parts : lines) {
            rentalManager.add(rentalManager.fromString(parts));
        }
    }

    /**
     * Loads properties from file.
     */
    public void loadProperties() {
        if (propertyManager == null || hostManager == null || ownerManager == null) {
            throw new IllegalStateException("Dependencies not set for FileHandler");
        }

        for (String[] parts : readLines("properties.txt")) {
            Property property = propertyManager.fromString(parts);
            propertyManager.add(property);
        }
    }

    /**
     * Loads tenants from file.
     */
    public void loadTenants() {
        for (String[] parts : readLines("tenants.txt")) {
            tenantManager.add(tenantManager.fromString(parts));
        }
    }

    /**
     * Loads owners from file.
     */
    public void loadOwners() {
        for (String[] parts : readLines("owners.txt")) {
            ownerManager.add(ownerManager.fromString(parts));
        }
    }

    /**
     * Loads hosts from file.
     */
    public void loadHosts() {
        if (hostManager == null) {
            throw new IllegalStateException("HostManager is not initialized");
        }
        for (String[] parts : readLines("hosts.txt")) {
            hostManager.add(hostManager.fromString(parts));
        }
    }

    /**
     * Saves rental agreements to file.
     * @param lines The lines to save
     */
    public void saveRentalAgreements(List<String[]> lines) {
        writeLines("rental_agreements.txt", lines);
    }

    /**
     * Saves tenants to file.
     * @param lines The lines to save
     */
    public void saveTenants(List<String[]> lines) {
        writeLines("tenants.txt", lines);
    }

    /**
     * Saves owners to file.
     * @param lines The lines to save
     */
    public void saveOwners(List<String[]> lines) {
        writeLines("owners.txt", lines);
    }

    /**
     * Saves hosts to file.
     * @param lines The lines to save
     */
    public void saveHosts(List<String[]> lines) {
        writeLines("hosts.txt", lines);
    }

    /**
     * Saves properties to file.
     * @param propertyLines The lines to save
     */
    public void saveProperties(List<String[]> propertyLines) {
        writeLines("properties.txt", propertyLines);
    }

    /**
     * Saves payments to file.
     * @param lines The lines to save
     */
    public void savePayments(List<String[]> lines) {
        writeLines("payments.txt", lines);
    }

    /**
     * Loads payments from file.
     * @return A list of Payment objects
     */
    public List<Payment> loadPayments() {
        List<Payment> payments = new ArrayList<>();
        for (String[] parts : readLines("payments.txt")) {
            if (parts.length == 6) {
                RentalAgreement agreement = rentalManager.get(parts[1]);
                if (agreement == null) {
                    System.out.println("Rental Agreement id: " + parts[1] + " not found");
                    continue;
                }
                Tenant tenant = tenantManager.get(parts[2]);
                if (tenant == null) {
                    System.out.println("Tenant id: " + parts[2] + " not found");
                    continue;
                }
                try {
                    payments.add(new Payment(
                            parts[0],
                            agreement,
                            tenant,
                            DATE_FORMAT.parse(parts[3]),
                            Double.parseDouble(parts[4]),
                            parts[5]
                    ));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return payments;
    }
}