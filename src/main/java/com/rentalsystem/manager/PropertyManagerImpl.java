package com.rentalsystem.manager;


import com.rentalsystem.model.*;
import com.rentalsystem.util.FileHandler;


import java.util.*;
import java.util.stream.Collectors;


public class PropertyManagerImpl implements PropertyManager {
    private Map<String, Property> properties;
    private FileHandler fileHandler;
    private HostManager hostManager;
    private TenantManager tenantManager;
    private OwnerManager ownerManager;
    private RentalManager rentalManager;


    public PropertyManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.properties = new HashMap<>();
    }


    public void setDependencies(HostManager hostManager, TenantManager tenantManager, OwnerManager ownerManager, RentalManager rentalManager) {
        this.hostManager = hostManager;
        this.tenantManager = tenantManager;
        this.ownerManager = ownerManager;
        this.rentalManager = rentalManager;
    }


    @Override
    public void load() {
        if (hostManager == null || tenantManager == null || ownerManager == null || rentalManager == null) {
            throw new IllegalStateException("Dependencies not set for PropertyManager");
        }


        // Load properties
        for (String[] parts : fileHandler.readLines("properties.txt")) {
            Property property = fromString(parts);
            properties.put(property.getPropertyId(), property);


            // Update owner's owned properties
            Owner owner = ownerManager.get(property.getOwner().getId());
            if (owner != null) {
                owner.addOwnedProperty(property);
            }
        }


        // Load property-host relationships
        for (String[] parts : fileHandler.readLines("properties_hosts.txt")) {
            if (parts.length == 2) {
                Property property = get(parts[0]);
                Host host = hostManager.get(parts[1]);
                if (property != null && host != null) {
                    property.addHost(host);
                    host.addManagedProperty(property);


                    // Update owner's managing hosts
                    Owner owner = property.getOwner();
                    if (owner != null) {
                        owner.addManagingHost(host);
                    }
                }
            }
        }


        // Load property-tenant relationships
        for (String[] parts : fileHandler.readLines("properties_tenants.txt")) {
            if (parts.length == 2) {
                Property property = get(parts[0]);
                Tenant tenant = tenantManager.get(parts[1]);
                if (property != null && tenant != null) {
                    property.addTenant(tenant);
                    tenant.addRentedProperty(property);
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
        property.getOwner().addOwnedProperty(property);
        saveToFile();
    }


    @Override
    public void update(Property property) {
        if (!properties.containsKey(property.getPropertyId())) {
            throw new IllegalArgumentException("Property with ID " + property.getPropertyId() + " does not exist.");
        }
        properties.put(property.getPropertyId(), property);
        saveToFile();
    }


    @Override
    public void delete(String propertyId) {
        Property property = properties.remove(propertyId);
        if (property == null) {
            throw new IllegalArgumentException("Property with ID " + propertyId + " does not exist.");
        }
        for (Host host : new ArrayList<>(property.getHosts())) {
            host.removeManagedProperty(property);
        }
        for (Tenant tenant : new ArrayList<>(property.getTenants())) {
            tenant.removeRentedProperty(property);
        }
        property.getOwner().removeOwnedProperty(property);
        saveToFile();
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
        return (int) getAll().stream().filter(p -> p.getStatus() == PropertyStatus.RENTED).count();
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
                    "", "", ""
            ));
        } else if (property instanceof CommercialProperty) {
            CommercialProperty cp = (CommercialProperty) property;
            propertyData.addAll(Arrays.asList(
                    "", "", "",
                    cp.getBusinessType(),
                    String.valueOf(cp.getParkingSpaces()),
                    String.valueOf(cp.getSquareFootage())
            ));
        }


        return propertyData.toArray(new String[0]);
    }


    @Override
    public void saveToFile() {
        List<String[]> propertyLines = new ArrayList<>();
        List<String[]> propertyHostsLines = new ArrayList<>();
        List<String[]> propertyTenantLines = new ArrayList<>();


        for (Property property : getSorted("id")) {
            propertyLines.add(saveProperty(property));


            for (Host host : property.getHosts()) {
                propertyHostsLines.add(new String[]{property.getPropertyId(), host.getId()});
            }


            for (Tenant tenant : property.getTenants()) {
                propertyTenantLines.add(new String[]{property.getPropertyId(), tenant.getId()});
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
        String ownerId = parts[5];


        Owner owner = ownerManager.get(ownerId);


        Property property;
        switch (propertyType) {
            case "RESIDENTIAL":
                property = new ResidentialProperty(
                        propertyId, address, price, propertyStatus, owner,
                        Integer.parseInt(parts[6]),
                        Boolean.parseBoolean(parts[7]),
                        Boolean.parseBoolean(parts[8])
                );
                break;
            case "COMMERCIAL":
                property = new CommercialProperty(
                        propertyId, address, price, propertyStatus, owner,
                        parts[9],
                        Integer.parseInt(parts[10]),
                        Double.parseDouble(parts[11])
                );
                break;
            default:
                throw new RuntimeException("Property of type " + propertyType + " does not exist");
        }


        return property;
    }
}
