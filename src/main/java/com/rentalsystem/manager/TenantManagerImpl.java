package com.rentalsystem.manager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;


import com.rentalsystem.model.Payment;
import com.rentalsystem.model.Person;
import com.rentalsystem.model.Tenant;
import com.rentalsystem.model.RentalAgreement;
import com.rentalsystem.util.FileHandler;
import com.rentalsystem.util.InputValidator;

import static com.rentalsystem.util.FileHandler.DATE_FORMAT;
import static com.rentalsystem.util.InputValidator.isValidEmail;

public class TenantManagerImpl implements TenantManager {
    private Map<String, Tenant> tenants;
    private FileHandler fileHandler;

    public TenantManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.tenants = new HashMap<>();
    }

    public void load() {
        fileHandler.loadTenants();
    }

    @Override
    public void saveToFile() {
        List<String[]> lines = new ArrayList<>();
        List<String[]> paymentLines = new ArrayList<>();

        for (Tenant t : getSorted("id")) {
            lines.add(t.toCSV());

            for (Payment payment : t.getPayments()) {
                String[] pl = new String[] {
                        payment.getPaymentId(),
                        payment.getRentalAgreement().getAgreementId(),
                        t.getId(),
                        DATE_FORMAT.format(payment.getPaymentDate()),
                        String.valueOf(payment.getAmount()),
                        payment.getPaymentMethod()
                };
                paymentLines.add(pl);
            }

        }

        fileHandler.saveTenants(lines);
    }

    public Tenant fromString(String[] parts) {
        try {
            return new Tenant(
                    parts[0],
                    parts[1],
                    DATE_FORMAT.parse(parts[2]),
                    parts[3]
            );
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public void add(Tenant tenant) {
        if (!isValidEmail(tenant.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (isEmailTaken(tenant.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + tenant.getContactInformation());
        }
        tenants.put(tenant.getId(), tenant);
    }

    public void update(Tenant tenant) {
        if (!isValidEmail(tenant.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format for tenant: " + tenant.getContactInformation());
        }
        Tenant existingTenant = tenants.get(tenant.getId());
        if (existingTenant == null) {
            throw new IllegalArgumentException("Tenant with ID " + tenant.getId() + " does not exist.");
        }
        if (!existingTenant.getContactInformation().equals(tenant.getContactInformation()) && isEmailTaken(tenant.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + tenant.getContactInformation());
        }
        tenants.put(tenant.getId(), tenant);
    }

    public void delete(String id) {
        tenants.remove(id);
    }

    public Tenant get(String id) {
        return tenants.get(id);
    }

    public List<Tenant> getAll() {
        return new ArrayList<>(tenants.values());
    }

    public List<Tenant> getSorted(String sortBy) {
        List<Tenant> sortedList = new ArrayList<>(tenants.values());
        switch (sortBy.toLowerCase()) {
            case "id":
                sortedList.sort(Comparator.comparing(Tenant::getId));
                break;
            case "name":
                sortedList.sort(Comparator.comparing(Tenant::getFullName));
                break;
            case "dob":
                sortedList.sort(Comparator.comparing(Tenant::getDateOfBirth));
                break;
            case "email":
                sortedList.sort(Comparator.comparing(Tenant::getContactInformation));
                break;
            default:
                throw new IllegalArgumentException("Invalid sort criteria: " + sortBy);
        }
        return sortedList;
    }

    @Override
    public void loadPayments() {
        for (Payment p : fileHandler.loadPayments()) {
            Tenant tenant = tenants.get(p.getTenant().getId());
            tenant.addPayment(p);
            System.out.println("Tenant " + tenant.getId() + " payment " + p.getPaymentId() + " added");
        }
    }

    public List<Tenant> search(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return tenants.values().stream()
                .filter(tenant ->
                        tenant.getFullName().toLowerCase().contains(lowercaseKeyword) ||
                                tenant.getId().toLowerCase().contains(lowercaseKeyword) ||
                                tenant.getContactInformation().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    public Tenant getByEmail(String email) {
        return tenants.values().stream()
                .filter(tenant -> tenant.getContactInformation().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public boolean isEmailTaken(String email) {
        final String lowercaseEmail = email.toLowerCase();
        return tenants.values().stream()
                .anyMatch(tenant -> tenant.getContactInformation().toLowerCase().equals(lowercaseEmail));
    }

    public boolean update(Tenant tenant, String newEmail) {
        if (!isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Invalid email format for tenant: " + newEmail);
        }
        Tenant existingTenant = getByEmail(newEmail);
        if (existingTenant != null && !existingTenant.getId().equals(tenant.getId())) {
            throw new IllegalArgumentException("Email is already in use by another tenant.");
        }
        tenant.setContactInformation(newEmail);
        tenants.put(tenant.getId(), tenant);
        return true;
    }
}
