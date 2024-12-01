package com.rentalsystem.manager;

import com.rentalsystem.model.*;
import com.rentalsystem.util.FileHandler;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.rentalsystem.util.FileHandler.DATE_FORMAT;

public class RentalManagerImpl implements RentalManager {
    private final Map<String, RentalAgreement> rentalAgreements;
    private final FileHandler fileHandler;
    private TenantManager tenantManager;
    private PropertyManager propertyManager;
    private HostManager hostManager;
    private OwnerManager ownerManager;

    public RentalManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.rentalAgreements = new HashMap<>();
    }

    public void setDependencies(TenantManager tenantManager, PropertyManager propertyManager, HostManager hostManager, OwnerManager ownerManager) {
        this.tenantManager = tenantManager;
        this.propertyManager = propertyManager;
        this.hostManager = hostManager;
        this.ownerManager = ownerManager;
    }

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

    @Override
    public RentalAgreement get(String agreementId) {
        return rentalAgreements.get(agreementId);
    }

    @Override
    public List<RentalAgreement> getAll() {
        return new ArrayList<>(rentalAgreements.values());
    }

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

    @Override
    public List<RentalAgreement> getActiveRentalAgreements() {
        Date currentDate = new Date();
        return getAll().stream()
                .filter(agreement -> agreement.getEndDate().after(currentDate) && agreement.getStatus() == RentalAgreement.Status.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalAgreement> getExpiredRentalAgreements() {
        Date currentDate = new Date();
        return getAll().stream()
                .filter(agreement -> agreement.getEndDate().before(currentDate) || agreement.getStatus() == RentalAgreement.Status.COMPLETED)
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalRentalIncome() {
        return getAll().stream()
                .filter(agreement -> agreement.getStatus() == RentalAgreement.Status.ACTIVE)
                .mapToDouble(RentalAgreement::getRentAmount)
                .sum();
    }

    @Override
    public int getTotalActiveAgreements() {
        return getActiveRentalAgreements().size();
    }

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

    @Override
    public void extendRentalAgreement(String agreementId, int extensionDays) {
        RentalAgreement agreement = get(agreementId);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(agreement.getEndDate());
        calendar.add(Calendar.DAY_OF_YEAR, extensionDays);
        agreement.setEndDate(calendar.getTime());
        saveToFile();
    }

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

    public RentalAgreement findActiveRentalAgreement(Property property) {
        return getAll().stream()
                .filter(a -> a.getProperty().getPropertyId().equals(property.getPropertyId()) && a.getStatus() == RentalAgreement.Status.ACTIVE)
                .findFirst()
                .orElse(null);
    }

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