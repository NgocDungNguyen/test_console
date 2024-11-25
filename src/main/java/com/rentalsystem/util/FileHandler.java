package com.rentalsystem.util;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.rentalsystem.manager.HostManager;
import com.rentalsystem.manager.OwnerManager;
import com.rentalsystem.manager.PropertyManager;
import com.rentalsystem.manager.RentalManager;
import com.rentalsystem.manager.TenantManager;
import com.rentalsystem.model.*;

public class FileHandler {
    private static final String DATA_DIRECTORY = "resources/data/";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private RentalManager rentalManager;
    private TenantManager tenantManager;
    private OwnerManager ownerManager;
    private HostManager hostManager;
    private PropertyManager propertyManager;

    public void syncManager(RentalManager rentalManager, TenantManager tenantManager, OwnerManager ownerManager, HostManager hostManager, PropertyManager propertyManager) {
        this.rentalManager = rentalManager;
        this.hostManager = hostManager;
        this.tenantManager = tenantManager;
        this.ownerManager = ownerManager;
        this.propertyManager = propertyManager;
    }

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

    public void writeLines(String filename, List<String[]> lines) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(DATA_DIRECTORY + filename))) {
            writer.writeAll(lines);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filename);
            e.printStackTrace();
        }
    }

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

    public String convertToCSV(List<String> data) {
        return data.stream()
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }



    // TODO: add list of owner, host, tenant
    public void loadRentalAgreements() {
        List<String[]> lines = readLines("rental_agreements.txt");
        for (String[] parts : lines) {
            rentalManager.add(rentalManager.fromString(parts));
        }

        loadRentalAgreementsTenants();
    }
    private void loadRentalAgreementsTenants() {
        List<String[]> lines = readLines("rental_agreements_tenants.txt");
        for (String[] parts : lines) {
            rentalManager.addSubTenant(parts[0], parts[1]);
        }
    }

    public void saveRentalAgreementsTenants(List<String[]> lines) {
        writeLines("rental_agreements_tenants.txt", lines);
    }



    public void savePropertiesHosts(List<String[]> lines) {
        writeLines("properties_hosts.txt", lines);
    }

    private void loadPropertiesHosts() {
        for (String[] parts : readLines("properties_hosts.txt")) {

            if (parts.length == 2) {
                Property property = propertyManager.get(parts[0]);
                if (property == null) {
                    System.out.println("Error loading property id: " + parts[0]);
                    continue;
                }
                Host host = hostManager.get(parts[1]);
                if (host == null) {
                    System.out.println("Error loading host id: " + parts[1]);
                    continue;
                }

                property.addHost(host);
            }
        }
    }


    private void loadPropertiesTenants() {
        for (String[] parts : readLines("properties_tenants.txt")) {

            if (parts.length == 2) {
                Property property = propertyManager.get(parts[0]);
                if (property == null) {
                    System.out.println("Error loading property id: " + parts[0]);
                    continue;
                }
                Tenant tenant = tenantManager.get(parts[1]);
                if (tenant == null) {
                    System.out.println("Error loading tenant id: " + parts[1]);
                    continue;
                }
                property.addTenant(tenant);
            }
        }
    }





    public void loadProperties() {
        if (hostManager == null) {
            throw new RuntimeException("hostManager not init");
        }

        for (String[] parts : readLines("properties.txt")) {
            Property property = propertyManager.fromString(parts);
            propertyManager.add(property);
        }

        if (propertyManager.getTotalProperties() > 0) {
            loadPropertiesHosts();
            loadPropertiesTenants();
        }

    }

    public void loadTenants() {
        for (String[] parts : readLines("tenants.txt")) {
            tenantManager.add(tenantManager.fromString(parts));
        }
    }

    public void loadOwners() {
        for (String[] parts : readLines("owners.txt")) {
            ownerManager.add(ownerManager.fromString(parts));
        }
    }

    public void loadHosts() {
        if (hostManager == null) {
            throw new IllegalStateException("HostManager is not initialized");
        }
        for (String[] parts : readLines("hosts.txt")) {
            hostManager.add(hostManager.fromString(parts));
        }
    }

    public void saveRentalAgreements(List<String[]> lines) {
        writeLines("rental_agreements.txt", lines);
    }

    public void saveTenants(List<String[]> lines) {
        writeLines("tenants.txt", lines);
    }

    public void saveOwners(List<String[]> lines) {
        writeLines("owners.txt", lines);
    }

    public void saveHosts(List<String[]> lines) {
        writeLines("hosts.txt", lines);
    }

    public void saveProperties(List<String[]> lines) {
        writeLines("properties.txt", lines);
    }

    public void savePropertiesTenants(List<String[]> lines) {
        writeLines("properties_tenants.txt", lines);
    }



    public void savePayments(List<String[]> lines) {
        writeLines("payments.txt", lines);
    }

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
