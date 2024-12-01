/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */

package com.rentalsystem.manager;

import com.rentalsystem.model.*;
import com.rentalsystem.util.FileHandler;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.rentalsystem.util.FileHandler.DATE_FORMAT;

/**
 * Implementation of the RentalManager interface.
 * Manages RentalAgreement entities in the system, providing CRUD operations and additional functionalities.
 */
public class RentalManagerImpl implements RentalManager {
    private final Map<String, RentalAgreement> rentalAgreements;
    private final FileHandler fileHandler;
    private TenantManager tenantManager;
    private PropertyManager propertyManager;
    private HostManager hostManager;
    private OwnerManager ownerManager;

    /**
     * Constructs a new RentalManagerImpl with the given FileHandler.
     * Initializes the rentalAgreements map.
     * @param fileHandler The FileHandler to use for data persistence
     */
    public RentalManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.rentalAgreements = new HashMap<>();
    }

    /**
     * Sets the dependencies required for the RentalManager.
     * @param tenantManager The TenantManager instance
     * @param propertyManager The PropertyManager instance
     * @param hostManager The HostManager instance
     * @param ownerManager The OwnerManager instance
     */
    public void setDependencies(TenantManager tenantManager, PropertyManager propertyManager, HostManager hostManager, OwnerManager ownerManager) {
        this.tenantManager = tenantManager;
        this.propertyManager = propertyManager;
        this.hostManager = hostManager;
        this.ownerManager = ownerManager;
    }


    /**
     * Loads rental agreement data from the file system.
     * Throws an IllegalStateException if dependencies are not set.
     */
    @Override
    public void load() {
        if (tenantManager == null || propertyManager == null || hostManager == null || ownerManager == null) {
            throw new IllegalStateException("Dependencies not set for RentalManager");
        }

        for (String[] parts : fileHandler.readLines("rental_agreements.txt")) {
            RentalAgreement agreement = fromString(parts);
            rentalAgreements.put(agreement.getAgreementId(), agreement);
        }
    }


    /**
     * Updates the status of a rental agreement based on its dates.
     * @param agreement The RentalAgreement to update
     */
    private void updateAgreementStatus(RentalAgreement agreement) {
        Date currentDate = new Date();
        if (agreement.getStartDate().after(currentDate)) {
            agreement.setStatus(RentalAgreement.Status.NEW);
        } else if (agreement.getEndDate().before(currentDate)) {
            agreement.setStatus(RentalAgreement.Status.COMPLETED);
        } else {
            agreement.setStatus(RentalAgreement.Status.ACTIVE);
        }
    }


    /**
     * Updates the statuses of all rental agreements in the system.
     */
    public void updateAgreementStatuses() {
        Date currentDate = new Date();
        for (RentalAgreement agreement : rentalAgreements.values()) {
            if (currentDate.after(agreement.getEndDate())) {
                agreement.setStatus(RentalAgreement.Status.COMPLETED);
            } else if (currentDate.after(agreement.getStartDate()) && agreement.getStatus() == RentalAgreement.Status.NEW) {
                agreement.setStatus(RentalAgreement.Status.ACTIVE);
            }
        }
        saveToFile();
    }


    /**
     * Adds a new rental agreement to the system.
     * @param agreement The RentalAgreement to be added
     * @throws IllegalArgumentException if an agreement with the same ID already exists
     */
    @Override
    public void add(RentalAgreement agreement) {
        if (rentalAgreements.containsKey(agreement.getAgreementId())) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreement.getAgreementId() + " already exists.");
        }
        rentalAgreements.put(agreement.getAgreementId(), agreement);
        updateAgreementStatus(agreement);

        Property property = propertyManager.get(agreement.getProperty().getPropertyId());
        Host host = hostManager.get(agreement.getHost().getId());
        Owner owner = ownerManager.get(agreement.getOwner().getId());
        Tenant mainTenant = tenantManager.get(agreement.getMainTenant().getId());

        property.addTenant(mainTenant);
        mainTenant.addRentalAgreement(agreement);
        host.addManagedAgreement(agreement);
        owner.addRentalAgreement(agreement);

        for (Tenant subTenant : agreement.getSubTenants()) {
            property.addTenant(subTenant);
            subTenant.addRentalAgreement(agreement);
        }
        saveToFile();
    }


    /**
     * Updates an existing rental agreement in the system.
     * @param agreement The RentalAgreement to be updated
     * @throws IllegalArgumentException if the agreement doesn't exist
     */

    @Override
    public void update(RentalAgreement agreement) {
        if (!rentalAgreements.containsKey(agreement.getAgreementId())) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreement.getAgreementId() + " does not exist.");
        }

        RentalAgreement existingAgreement = rentalAgreements.get(agreement.getAgreementId());

        Property property = propertyManager.get(agreement.getProperty().getPropertyId());
        Host host = hostManager.get(agreement.getHost().getId());
        Owner owner = ownerManager.get(agreement.getOwner().getId());
        Tenant mainTenant = tenantManager.get(agreement.getMainTenant().getId());

        // Remove old associations
        property.removeTenant(existingAgreement.getMainTenant());
        existingAgreement.getMainTenant().removeRentalAgreement(existingAgreement);
        host.removeManagedAgreement(existingAgreement);
        owner.removeRentalAgreement(existingAgreement);

        for (Tenant subTenant : existingAgreement.getSubTenants()) {
            property.removeTenant(subTenant);
            subTenant.removeRentalAgreement(existingAgreement);
        }

        // Add new associations
        updateAgreementStatus(agreement);
        property.addTenant(mainTenant);
        mainTenant.addRentalAgreement(agreement);
        host.addManagedAgreement(agreement);
        owner.addRentalAgreement(agreement);

        for (Tenant subTenant : agreement.getSubTenants()) {
            property.addTenant(subTenant);
            subTenant.addRentalAgreement(agreement);
        }

        rentalAgreements.put(agreement.getAgreementId(), agreement);
        saveToFile();
    }

    /**
     * Deletes a rental agreement from the system.
     * @param agreementId The ID of the agreement to be deleted
     * @throws IllegalArgumentException if the agreement doesn't exist
     */
    @Override
    public void delete(String agreementId) {
        RentalAgreement agreement = rentalAgreements.remove(agreementId);
        if (agreement == null) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreementId + " does not exist.");
        }

        Property property = propertyManager.get(agreement.getProperty().getPropertyId());
        Host host = hostManager.get(agreement.getHost().getId());
        Owner owner = ownerManager.get(agreement.getOwner().getId());
        Tenant mainTenant = tenantManager.get(agreement.getMainTenant().getId());

        property.removeTenant(mainTenant);
        mainTenant.removeRentalAgreement(agreement);
        host.removeManagedAgreement(agreement);
        owner.removeRentalAgreement(agreement);

        for (Tenant subTenant : agreement.getSubTenants()) {
            property.removeTenant(subTenant);
            subTenant.removeRentalAgreement(agreement);
        }

        saveToFile();
    }


    /**
     * Retrieves a rental agreement by its ID.
     * @param agreementId The ID of the agreement to retrieve
     * @return The RentalAgreement object, or null if not found
     */

    @Override
    public RentalAgreement get(String agreementId) {
        return rentalAgreements.get(agreementId);
    }

    /**
     * Retrieves all rental agreements in the system.
     * @return A list of all RentalAgreement objects
     */
    @Override
    public List<RentalAgreement> getAll() {
        return new ArrayList<>(rentalAgreements.values());
    }

    /**
     * Retrieves a sorted list of rental agreements based on the specified criteria.
     * @param sortBy The criteria to sort by
     * @return A sorted list of RentalAgreement objects
     * @throws IllegalArgumentException if an invalid sort criteria is provided
     */
    @Override
    public List<RentalAgreement> getSorted(String sortBy) {
        List<RentalAgreement> sortedList = getAll();
        switch (sortBy.toLowerCase()) {
            case "id":
                sortedList.sort(Comparator.comparing(RentalAgreement::getAgreementId));
                break;
            case "propertyid":
                sortedList.sort(Comparator.comparing(a -> a.getProperty().getPropertyId()));
                break;
            case "tenantname":
                sortedList.sort(Comparator.comparing(a -> a.getMainTenant().getFullName()));
                break;
            case "ownername":
                sortedList.sort(Comparator.comparing(a -> a.getOwner().getFullName()));
                break;
            case "hostname":
                sortedList.sort(Comparator.comparing(a -> a.getHost().getFullName()));
                break;
            case "startdate":
                sortedList.sort(Comparator.comparing(RentalAgreement::getStartDate));
                break;
            case "enddate":
                sortedList.sort(Comparator.comparing(RentalAgreement::getEndDate));
                break;
            case "rentamount":
                sortedList.sort(Comparator.comparing(RentalAgreement::getRentAmount));
                break;
            case "status":
                sortedList.sort(Comparator.comparing(RentalAgreement::getStatus));
                break;
            default:
                throw new IllegalArgumentException("Invalid sort criteria: " + sortBy);
        }
        return sortedList;
    }

    /**
     * Adds a sub-tenant to an existing rental agreement.
     * @param agreementId The ID of the rental agreement
     * @param subTenantId The ID of the sub-tenant to be added
     */
    @Override
    public void addSubTenant(String agreementId, String subTenantId) {
        RentalAgreement agreement = get(agreementId);
        if (agreement == null) {
            System.out.println("Rental agreement with ID " + agreementId + " not found.");
            return;
        }

        Tenant subTenant = tenantManager.get(subTenantId);
        if (subTenant == null) {
            System.out.println("Tenant with ID " + subTenantId + " not found.");
            return;
        }

        agreement.addSubTenant(subTenant);
        update(agreement);
        System.out.println("Sub-tenant added successfully to rental agreement " + agreementId);
    }

    /**
     * Removes a sub-tenant from an existing rental agreement.
     * @param agreementId The ID of the rental agreement
     * @param subTenantId The ID of the sub-tenant to be removed
     */

    @Override
    public void removeSubTenant(String agreementId, String subTenantId) {
        RentalAgreement agreement = get(agreementId);
        if (agreement == null) {
            System.out.println("Rental agreement with ID " + agreementId + " not found.");
            return;
        }

        agreement.removeSubTenant(subTenantId);
        update(agreement);
        System.out.println("Sub-tenant with ID " + subTenantId + " removed from rental agreement " + agreementId);
    }

    /**
     * Retrieves all active rental agreements.
     * @return A list of active RentalAgreement objects
     */
    @Override
    public List<RentalAgreement> getActiveRentalAgreements() {
        Date currentDate = new Date();
        return getAll().stream()
                .filter(agreement -> agreement.getEndDate().after(currentDate) && agreement.getStatus() == RentalAgreement.Status.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all expired rental agreements.
     * @return A list of expired RentalAgreement objects
     */
    @Override
    public List<RentalAgreement> getExpiredRentalAgreements() {
        Date currentDate = new Date();
        return getAll().stream()
                .filter(agreement -> agreement.getEndDate().before(currentDate) || agreement.getStatus() == RentalAgreement.Status.COMPLETED)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total rental income from all active agreements.
     * @return The total rental income
     */
    @Override
    public double getTotalRentalIncome() {
        return getAll().stream()
                .filter(agreement -> agreement.getStatus() == RentalAgreement.Status.ACTIVE)
                .mapToDouble(RentalAgreement::getRentAmount)
                .sum();
    }

    /**
     * Gets the total number of active rental agreements.
     * @return The number of active agreements
     */
    @Override
    public int getTotalActiveAgreements() {
        return getActiveRentalAgreements().size();
    }

    /**
     * Searches for rental agreements based on a keyword.
     * @param keyword The search keyword
     * @return A list of RentalAgreement objects matching the search criteria
     */
    @Override
    public List<RentalAgreement> searchRentalAgreements(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return getAll().stream()
                .filter(agreement -> agreement.getAgreementId().toLowerCase().contains(lowercaseKeyword) ||
                        agreement.getProperty().getAddress().toLowerCase().contains(lowercaseKeyword) ||
                        agreement.getMainTenant().getFullName().toLowerCase().contains(lowercaseKeyword) ||
                        agreement.getOwner().getFullName().toLowerCase().contains(lowercaseKeyword) ||
                        agreement.getHost().getFullName().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }



    /**
     * Extends the end date of a rental agreement.
     * @param agreementId The ID of the agreement to extend
     * @param extensionDays The number of days to extend the agreement by
     */
    @Override
    public void extendRentalAgreement(String agreementId, int extensionDays) {
        RentalAgreement agreement = get(agreementId);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(agreement.getEndDate());
        calendar.add(Calendar.DAY_OF_YEAR, extensionDays);
        agreement.setEndDate(calendar.getTime());
        saveToFile();
    }

    /**
     * Terminates a rental agreement by setting its end date to the current date and status to COMPLETED.
     * @param agreementId The ID of the agreement to terminate
     */

    @Override
    public void terminateRentalAgreement(String agreementId) {
        RentalAgreement agreement = get(agreementId);
        agreement.setEndDate(new Date());
        agreement.setStatus(RentalAgreement.Status.COMPLETED);

        Property property = propertyManager.get(agreement.getProperty().getPropertyId());
        property.removeTenant(agreement.getMainTenant());
        for (Tenant subTenant : agreement.getSubTenants()) {
            property.removeTenant(subTenant);
        }
        saveToFile();
    }

    /**
     * Finds an active rental agreement for a given property.
     * @param property The property to find an active agreement for
     * @return The active RentalAgreement, or null if not found
     */

    public RentalAgreement findActiveRentalAgreement(Property property) {
        return getAll().stream()
                .filter(a -> a.getProperty().getPropertyId().equals(property.getPropertyId()) && a.getStatus() == RentalAgreement.Status.ACTIVE)
                .findFirst()
                .orElse(null);
    }


    /**
     * Saves the current state of rental agreements to the file system.
     */
    @Override
    public void saveToFile() {
        List<String[]> rentalAgreementLines = new ArrayList<>();

        for (RentalAgreement agreement : getSorted("id")) {
            String tenantIds = agreement.getMainTenant().getId() + ";" +
                    agreement.getSubTenants().stream()
                            .map(Tenant::getId)
                            .collect(Collectors.joining(";"));

            rentalAgreementLines.add(new String[] {
                    agreement.getAgreementId(),
                    agreement.getProperty().getPropertyId(),
                    tenantIds,
                    agreement.getOwner().getId(),
                    agreement.getHost().getId(),
                    DATE_FORMAT.format(agreement.getStartDate()),
                    DATE_FORMAT.format(agreement.getEndDate()),
                    String.valueOf(agreement.getRentAmount()),
                    agreement.getRentalPeriod().toString(),
                    agreement.getStatus().toString()
            });
        }

        fileHandler.saveRentalAgreements(rentalAgreementLines);
    }

    /**
     * Creates a RentalAgreement object from a string array representation.
     * @param parts The string array containing rental agreement data
     * @return The created RentalAgreement object
     * @throws RuntimeException if there's an error parsing the date
     */
    @Override
    public RentalAgreement fromString(String[] parts) {
        Property property = propertyManager.get(parts[1]);
        String[] tenantIds = parts[2].split(";");
        Tenant mainTenant = tenantManager.get(tenantIds[0]);
        List<Tenant> subTenants = Arrays.stream(tenantIds).skip(1)
                .map(tenantManager::get)
                .collect(Collectors.toList());
        Owner owner = ownerManager.get(parts[3]);
        Host host = hostManager.get(parts[4]);

        try {
            RentalAgreement agreement = new RentalAgreement(
                    parts[0],
                    property,
                    mainTenant,
                    owner,
                    host,
                    DATE_FORMAT.parse(parts[5]),
                    DATE_FORMAT.parse(parts[6]),
                    Double.parseDouble(parts[7]),
                    RentalAgreement.RentalPeriod.valueOf(parts[8])
            );
            agreement.setStatus(RentalAgreement.Status.valueOf(parts[9]));
            subTenants.forEach(agreement::addSubTenant);
            return agreement;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}