package com.rentalsystem.manager;

import com.rentalsystem.model.*;
import com.rentalsystem.util.FileHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class PropertyManagerImpl implements PropertyManager {
    private Map<String, Property> properties;
    private FileHandler fileHandler;
    private HostManager hostManager;
    private TenantManager tenantManager;
    private OwnerManager ownerManager;
    private RentalManager rentalManager;

    public PropertyManagerImpl(FileHandler fileHandler, HostManager hostManager, TenantManager tenantManager, OwnerManager ownerManager, RentalManager rentalManager) {
        this.fileHandler = fileHandler;
        this.hostManager = hostManager;
        this.tenantManager = tenantManager;
        this.ownerManager = ownerManager;
        this.rentalManager = rentalManager;

        this.properties = new HashMap<>();
    }

    public void load() {
        // fileHandler will modify the existing this.properties and call this.properties.add() on each saved line;
        fileHandler.loadProperties();

        for (Property property : getAll()) {
            properties.put(property.getPropertyId(), property);

            if (property.getOwner() != null) {
                Owner owner = ownerManager.get(property.getOwner().getId());
                if (owner != null) {
                    property.setOwner(owner);
                }
            }

            if (!property.getHosts().isEmpty()) {

                property.getHosts().forEach(host -> {
                    Host realHost = hostManager.get(host.getId());
                    realHost.addManagedProperty(property);
                });
            }

            if (!property.getTenants().isEmpty()) {

                property.getTenants().forEach(t -> {
                    Tenant rt = tenantManager.get(t.getId());
                    rt.addRentedProperty(property);
                });
            }

            if (property.getStatus() == PropertyStatus.RENTED) {
                RentalAgreement agreement = rentalManager.findActiveRentalAgreement(property);
                if (agreement != null) {
                    Tenant tenant = tenantManager.get(agreement.getMainTenant().getId());
                    if (tenant != null) {
                        property.addTenant(tenant);
                    }
                }
            }
        }
    }

    @Override
    public void add(Property property) {
        if (properties.containsKey(property.getPropertyId())) {
            throw new IllegalArgumentException("Property with ID " + property.getPropertyId() + " already exists.");
        }
        properties.put(property.getPropertyId(), property);
    }

    @Override
    public void update(Property property) {
        if (!property.getTenants().isEmpty()) {
            property.setStatus(PropertyStatus.RENTED);
        } else if (property.getStatus() != PropertyStatus.UNDER_MAINTENANCE) {
            property.setStatus(PropertyStatus.AVAILABLE);
        }
        properties.put(property.getPropertyId(), property);
    }

    @Override
    public void delete(String propertyId) {
        Property property = properties.remove(propertyId);
        if (property == null) {
            throw new IllegalArgumentException("Property with ID " + propertyId + " does not exist.");
        }
        if (property.getHosts() != null) {
            property.getHosts().forEach(host -> host.removeManagedProperty(property));
        }
        for (Tenant tenant : property.getTenants()) {
            tenant.removeRentedProperty(property);
        }
        if (property.getOwner() != null) {
            property.getOwner().removeOwnedProperty(property);
        }
    }

    @Override
    public Property get(String propertyId) {
        return properties.get(propertyId);
    }

    @Override
    public List<Property> getAll() {
        return new ArrayList<>(properties.values());
    }

    @Override
    public List<Property> getSorted(String sortBy) {
        List<Property> sortedList = getAll();
        switch (sortBy.toLowerCase()) {
            case "id":
                sortedList.sort(Comparator.comparing(Property::getPropertyId));
                break;
            case "type":
                sortedList.sort(Comparator.comparing(p -> p instanceof ResidentialProperty ? "Residential" : "Commercial"));
                break;
            case "address":
                sortedList.sort(Comparator.comparing(Property::getAddress));
                break;
            case "price":
                sortedList.sort(Comparator.comparing(Property::getPrice));
                break;
            case "status":
                sortedList.sort(Comparator.comparing(Property::getStatus));
                break;
            case "owner":
                sortedList.sort(Comparator.comparing(p -> p.getOwner().getFullName()));
                break;
            default:
                throw new IllegalArgumentException("Invalid sort criteria: " + sortBy);
        }
        return sortedList;
    }

    @Override
    public int getTotalProperties() {
        return properties.size();
    }

    @Override
    public int getOccupiedProperties() {
        return (int) getAll().stream()
                .filter(p -> p.getStatus() == PropertyStatus.RENTED)
                .count();
    }

    @Override
    public List<Property> search(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return getAll().stream()
                .filter(property -> property.getPropertyId().toLowerCase().contains(lowercaseKeyword) ||
                        property.getAddress().toLowerCase().contains(lowercaseKeyword) ||
                        property.getOwner().getFullName().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<Property> getAvailableProperties() {
        return getAll().stream()
                .filter(property -> property.getStatus() == PropertyStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    private String[] saveProperty(Property property) {
        List<String> propertyData = new ArrayList<>(Arrays.asList(
                property.getPropertyId(),
                property instanceof ResidentialProperty ? "RESIDENTIAL" : "COMMERCIAL",
                property.getAddress(),
                String.valueOf(property.getPrice()),
                property.getStatus().toString(),
                property.getOwner().getId()
        ));

        if (property instanceof ResidentialProperty) {
            ResidentialProperty rp = (ResidentialProperty) property;
            propertyData.addAll(Arrays.asList(
                    String.valueOf(rp.getNumberOfBedrooms()),
                    String.valueOf(rp.hasGarden()),
                    String.valueOf(rp.isPetFriendly()),
                    "",
                    "",
                    ""
            ));
        }
        if (property instanceof CommercialProperty) {
            CommercialProperty cp = (CommercialProperty) property;
            propertyData.addAll(Arrays.asList(
                    "",
                    "",
                    "",
                    cp.getBusinessType(),
                    String.valueOf(cp.getParkingSpaces()),
                    String.valueOf(cp.getSquareFootage())
            ));
        }

        return propertyData.toArray(new String[0]);
    }

    private String[] savePropertyPerson(Property property, Person host) {
        return new String[]{
                property.getPropertyId(),
                host.getId()
        };
    }

    public void saveToFile() {
        List<String[]> propertyLines = new ArrayList<>();
        List<String[]> propertyHostsLines = new ArrayList<>();
        List<String[]> propertyTenantLines = new ArrayList<>();


        for (Property property : getSorted("id")) {

            propertyLines.add(saveProperty(property));

            for (Host host : property.getHosts()) {
                propertyHostsLines.add(savePropertyPerson(property, host));
            }

            for (Tenant tenant : property.getTenants()) {
                propertyTenantLines.add(savePropertyPerson(property, tenant));
            }
        }


        fileHandler.saveProperties(propertyLines);
        fileHandler.savePropertiesHosts(propertyHostsLines);
        fileHandler.savePropertiesTenants(propertyTenantLines);
    }

    @Override
    public Property fromString(String[] parts) {
        String propertyId = parts[0];
        String propertyType = parts[1];
        String address = parts[2];
        double price = Double.parseDouble(parts[3]);
        PropertyStatus propertyStatus = PropertyStatus.valueOf(parts[4]);
        // 5: ownerId
        String ownerId = parts[5];

        String numberOfRoom = parts[6];
        String hasGarden = parts[7];
        String petFriendly = parts[8];
        String businessType = parts[9];
        String numberOfParking = parts[10];
        String squareFootage = parts[11];

        Owner owner = ownerManager.get(ownerId);

        Property property = new Property(propertyId, address, price, propertyStatus, owner);
        switch (propertyType) {
            case "RESIDENTIAL":
                property = new ResidentialProperty(property,
                        Integer.parseInt(numberOfRoom),
                        Boolean.parseBoolean(hasGarden),
                        Boolean.parseBoolean(petFriendly)
                );
                break;
            case "COMMERCIAL":
                property = new CommercialProperty(
                        property,
                        businessType,
                        Integer.parseInt(numberOfParking),
                        Double.parseDouble(squareFootage)
                );
                break;
            default:
                throw new RuntimeException("Property of type " + propertyType + "does not exist");
        }

        return property;
    }
}
