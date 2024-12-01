/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */

package com.rentalsystem.manager;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;


import com.rentalsystem.model.Payment;
import com.rentalsystem.model.Tenant;
import com.rentalsystem.util.FileHandler;


import static com.rentalsystem.util.FileHandler.DATE_FORMAT;
import static com.rentalsystem.util.InputValidator.isValidEmail;


/**
 * Implementation of the TenantManager interface.
 * Manages Tenant entities in the system, providing CRUD operations and additional functionalities.
 */
public class TenantManagerImpl implements TenantManager {
    private Map<String, Tenant> tenants;
    private FileHandler fileHandler;


    /**
     * Constructs a new TenantManagerImpl.
     * @param fileHandler The FileHandler to use for data persistence
     */
    public TenantManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.tenants = new HashMap<>();
    }


    /**
     * Loads tenants from file into the system.
     */
    public void load() {
        fileHandler.loadTenants();
    }


    /**
     * Adds a new tenant to the system.
     * @param tenant The Tenant object to be added
     * @throws IllegalArgumentException if the email is invalid or already in use
     */
    @Override
    public void add(Tenant tenant) {
        if (!isValidEmail(tenant.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (isEmailTaken(tenant.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + tenant.getContactInformation());
        }
        tenants.put(tenant.getId(), tenant);
        saveToFile();
    }

    /**
     * Updates an existing tenant in the system.
     * @param tenant The Tenant object to be updated
     * @throws IllegalArgumentException if the tenant doesn't exist, or if the new email is invalid or already in use
     */
    @Override
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
        saveToFile();
    }

    /**
     * Deletes a tenant from the system.
     * @param id The ID of the tenant to be deleted
     */
    @Override
    public void delete(String id) {
        tenants.remove(id);
        saveToFile();
    }

    /**
     * Retrieves a tenant by their ID.
     * @param id The ID of the tenant to retrieve
     * @return The Tenant object, or null if not found
     */
    @Override
    public Tenant get(String id) {
        return tenants.get(id);
    }

    /**
     * Retrieves all tenants in the system.
     * @return A list of all Tenant objects
     */
    @Override
    public List<Tenant> getAll() {
        return new ArrayList<>(tenants.values());
    }

    /**
     * Retrieves a sorted list of tenants based on the specified criteria.
     * @param sortBy The criteria to sort by (id, name, dob, or email)
     * @return A sorted list of Tenant objects
     * @throws IllegalArgumentException if an invalid sort criteria is provided
     */
    @Override
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

    /**
     * Loads payments for all tenants from the file system.
     */
    @Override
    public void loadPayments() {
        for (Payment p : fileHandler.loadPayments()) {
            Tenant tenant = tenants.get(p.getTenant().getId());
            if (tenant != null) {
                tenant.addPayment(p);
            }
        }
    }

    /**
     * Searches for tenants based on a keyword.
     * The search is case-insensitive and looks in the full name, ID, and email.
     * @param keyword The search keyword
     * @return A list of Tenant objects matching the search criteria
     */
    @Override
    public List<Tenant> search(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return tenants.values().stream()
                .filter(tenant ->
                        tenant.getFullName().toLowerCase().contains(lowercaseKeyword) ||
                                tenant.getId().toLowerCase().contains(lowercaseKeyword) ||
                                tenant.getContactInformation().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a tenant by their email address.
     * @param email The email address of the tenant to retrieve
     * @return The Tenant object, or null if not found
     */
    @Override
    public Tenant getByEmail(String email) {
        return tenants.values().stream()
                .filter(tenant -> tenant.getContactInformation().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if an email is already taken by any tenant in the system.
     * @param email The email to check
     * @return true if the email is taken, false otherwise
     */
    @Override
    public boolean isEmailTaken(String email) {
        final String lowercaseEmail = email.toLowerCase();
        return tenants.values().stream()
                .anyMatch(tenant -> tenant.getContactInformation().toLowerCase().equals(lowercaseEmail));
    }


    /**
     * Updates a tenant's email address.
     * @param tenant The tenant to update
     * @param newEmail The new email address
     * @return true if the update was successful, false otherwise
     * @throws IllegalArgumentException if the new email is invalid or already in use by another tenant
     */
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

    /**
     * Saves the current state of tenants and their payments to the file system.
     */
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

    /**
     * Creates a Tenant object from a string array representation.
     * @param parts The string array containing tenant data
     * @return The created Tenant object
     * @throws RuntimeException if there's an error parsing the date
     */
    @Override
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
}
