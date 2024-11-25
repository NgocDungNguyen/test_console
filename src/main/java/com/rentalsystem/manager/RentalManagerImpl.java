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
    private final TenantManager tenantManager;
    private final PropertyManager propertyManager;
    private final HostManager hostManager;
    private final OwnerManager ownerManager;

    public RentalManagerImpl(FileHandler fileHandler, TenantManager tenantManager, PropertyManager propertyManager, HostManager hostManager, OwnerManager ownerManager) {
        this.fileHandler = fileHandler;
        this.tenantManager = tenantManager;
        this.propertyManager = propertyManager;
        this.hostManager = hostManager;
        this.ownerManager = ownerManager;
        this.rentalAgreements = new HashMap<>();
    }

    public void load() {
        fileHandler.loadRentalAgreements();

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


    @Override
    public void add(RentalAgreement agreement) {
        if (rentalAgreements.get(agreement.getAgreementId()) != null) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreement.getAgreementId() + " already exists.");
        }
        rentalAgreements.put(agreement.getAgreementId(), agreement);
        updateAgreementStatus(agreement);

        Property property = propertyManager.get(agreement.getProperty().getPropertyId());
        Host host = hostManager.get(agreement.getHost().getId());
        Owner owner = ownerManager.get(agreement.getOwner().getId());
        Tenant mainTenant = tenantManager.get(agreement.getMainTenant().getId());

        property.addTenant(agreement.getMainTenant());
        mainTenant.addRentalAgreement(agreement);
        host.addManagedAgreement(agreement);
        owner.addRentalAgreement(agreement);

        for (Tenant subTenant : agreement.getSubTenants()) {
            Tenant actualSubTenant = tenantManager.get(subTenant.getId());
            property.addTenant(subTenant);
            actualSubTenant.addRentalAgreement(agreement);
        }

    }

    @Override
    public void update(RentalAgreement agreement) {
        RentalAgreement existingAgreement = rentalAgreements.get(agreement.getAgreementId());
        if (existingAgreement == null) {
            throw new IllegalArgumentException("Rental agreement with ID " + agreement.getAgreementId() + " does not exist.");
        }

        Property property = propertyManager.get(agreement.getProperty().getPropertyId());
        Host host = hostManager.get(agreement.getHost().getId());
        Owner owner = ownerManager.get(agreement.getOwner().getId());
        Tenant mainTenant = tenantManager.get(agreement.getMainTenant().getId());

        property.removeTenant(mainTenant);
        mainTenant.removeRentalAgreement(existingAgreement);
        host.removeManagedAgreement(existingAgreement);
        owner.removeRentalAgreement(existingAgreement);

        for (Tenant subTenant : existingAgreement.getSubTenants()) {
            Tenant realSubTenant = tenantManager.get(subTenant.getId());
            property.removeTenant(realSubTenant);
            realSubTenant.removeRentalAgreement(existingAgreement);
        }
        updateAgreementStatus(agreement);

        property.addTenant(agreement.getMainTenant());
        mainTenant.addRentalAgreement(agreement);
        host.addManagedAgreement(agreement);
        owner.addRentalAgreement(agreement);

        for (Tenant subTenant : agreement.getSubTenants()) {
            Tenant realSubTenant = tenantManager.get(subTenant.getId());
            property.addTenant(realSubTenant);
            realSubTenant.addRentalAgreement(agreement);
        }

        rentalAgreements.put(agreement.getAgreementId(), agreement);
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
            Tenant realSubTenant = tenantManager.get(agreement.getMainTenant().getId());
            property.removeTenant(realSubTenant);
            realSubTenant.removeRentalAgreement(agreement);
        }
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
        Tenant subTenant = tenantManager.get(subTenantId);
        if (subTenant == null) {
            throw new IllegalArgumentException("Tenant with ID " + subTenantId + " does not exist.");
        }
        agreement.addSubTenant(subTenant);
        propertyManager.get(agreement.getProperty().getPropertyId()).addTenant(subTenant);
        subTenant.addRentalAgreement(agreement);
    }

    @Override
    public void removeSubTenant(String agreementId, String subTenantId) {
        RentalAgreement agreement = get(agreementId);
        Tenant subTenant = tenantManager.get(subTenantId);
        if (subTenant == null) {
            throw new IllegalArgumentException("Tenant with ID " + subTenantId + " does not exist.");
        }
        agreement.removeSubTenant(subTenantId);
        propertyManager.get(agreement.getProperty().getPropertyId()).removeTenant(subTenant);
        subTenant.removeRentalAgreement(agreement);
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
    }

    @Override
    public void terminateRentalAgreement(String agreementId) {
        RentalAgreement agreement = get(agreementId);
        agreement.setEndDate(new Date());
        agreement.setStatus(RentalAgreement.Status.COMPLETED);

        propertyManager.get(agreement.getProperty().getPropertyId()).removeTenant(agreement.getMainTenant());
        for (Tenant subTenant : agreement.getSubTenants()) {
            propertyManager.get(agreement.getProperty().getPropertyId()).removeTenant(subTenant);
        }
    }

    public RentalAgreement findActiveRentalAgreement(Property property) {
        return getAll().stream()
                .filter(a -> a.getProperty().getPropertyId().equals(property.getPropertyId()) && a.getStatus() == RentalAgreement.Status.ACTIVE)
                .findFirst()
                .orElse(null);
    }

    public void saveToFile() {
        List<String[]> rentalAgreementLines = new ArrayList<>();
        List<String[]> rentalAgreementTenantsLines = new ArrayList<>();

        for (RentalAgreement agreement: getSorted("id")) {
            rentalAgreementLines.add(new String[] {
                    agreement.getAgreementId(),
                    agreement.getProperty().getPropertyId(),
                    agreement.getMainTenant().getId(),
                    agreement.getOwner().getId(),
                    agreement.getHost().getId(),
                    DATE_FORMAT.format(agreement.getStartDate()),
                    DATE_FORMAT.format(agreement.getEndDate()),
                    String.valueOf(agreement.getRentAmount()),
                    agreement.getRentalPeriod().toString(),
                    agreement.getStatus().toString()
            });
            for (Tenant subTenant: agreement.getSubTenants()) {
                rentalAgreementTenantsLines.add(new String[] {
                        agreement.getAgreementId(), subTenant.getId()
                });
            }
        }

        fileHandler.saveRentalAgreements(rentalAgreementLines);
        fileHandler.saveRentalAgreementsTenants(rentalAgreementTenantsLines);
    }

    @Override
    public RentalAgreement fromString(String[] parts) {

        Property property = propertyManager.get(parts[1]);
        Tenant mainTenant = tenantManager.get(parts[2]);
        Owner owner = ownerManager.get(parts[3]);
        Host host = hostManager.get(parts[4]);

        try {
            return new RentalAgreement(
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
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
