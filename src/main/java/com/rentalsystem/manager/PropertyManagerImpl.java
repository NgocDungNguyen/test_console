/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */

package com.rentalsystem.manager;

import com.rentalsystem.model.*;
import com.rentalsystem.util.FileHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the PropertyManager interface.
 * Manages Property entities in the system, providing CRUD operations and additional functionalities.
 */
public class PropertyManagerImpl implements PropertyManager {
    private Map<String, Property> properties;
    private FileHandler fileHandler;
    private HostManager hostManager;
    private TenantManager tenantManager;
    private OwnerManager ownerManager;
    private RentalManager rentalManager;

    /**
     * Constructs a new PropertyManagerImpl with the given FileHandler.
     * Initializes the properties map.
     * @param fileHandler The FileHandler to use for data persistence
     */
    public PropertyManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.properties = new HashMap<>();
    }

    /**
     * Sets the dependencies required for the PropertyManager.
     * @param hostManager The HostManager instance
     * @param tenantManager The TenantManager instance
     * @param ownerManager The OwnerManager instance
     * @param rentalManager The RentalManager instance
     */
    public void setDependencies(HostManager hostManager, TenantManager tenantManager, OwnerManager ownerManager, RentalManager rentalManager) {
        this.hostManager = hostManager;
        this.tenantManager = tenantManager;
        this.ownerManager = ownerManager;
        this.rentalManager = rentalManager;
    }

    /**
     * Loads property data from the file system and sets up relationships with owners and hosts.
     * Throws an IllegalStateException if dependencies are not set.
     */
    @Override
    public void load() {
        if (hostManager == null || tenantManager == null || ownerManager == null || rentalManager == null) {
            throw new IllegalStateException("Dependencies not set for PropertyManager");
        }

        for (String[] parts : fileHandler.readLines("properties.txt")) {
            Property property = fromString(parts);
            properties.put(property.getPropertyId(), property);

            Owner owner = ownerManager.get(property.getOwner().getId());
            if (owner != null) {
                owner.addOwnedProperty(property);
            }

            // Load hosts for the property if the information is available
            if (parts.length > 12 && parts[12] != null && !parts[12].isEmpty()) {
                String[] hostIds = parts[12].split(";");
                for (String hostId : hostIds) {
                    if (!hostId.isEmpty()) {
                        Host host = hostManager.get(hostId);
                        if (host != null) {
                            property.addHost(host);
                            host.addManagedProperty(property);
                            owner.addManagingHost(host);
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds a new property to the system.
     * @param property The Property object to be added
     * @throws IllegalArgumentException if a property with the same ID already exists
     */
    @Override
    public void add(Property property) {
        if (properties.containsKey(property.getPropertyId())) {
            throw new IllegalArgumentException("Property with ID " + property.getPropertyId() + " already exists.");
        }
        properties.put(property.getPropertyId(), property);
        property.getOwner().addOwnedProperty(property);
        saveToFile();
    }

    /**
     * Updates an existing property in the system.
     * @param property The Property object to be updated
     * @throws IllegalArgumentException if the property doesn't exist
     */

    @Override
    public void update(Property property) {
        if (!properties.containsKey(property.getPropertyId())) {
            throw new IllegalArgumentException("Property with ID " + property.getPropertyId() + " does not exist.");
        }
        properties.put(property.getPropertyId(), property);
        saveToFile();
    }

    /**
     * Deletes a property from the system and updates related entities.
     * @param propertyId The ID of the property to be deleted
     * @throws IllegalArgumentException if the property doesn't exist
     */
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


    /**
     * Retrieves a property by its ID.
     * @param propertyId The ID of the property to retrieve
     * @return The Property object, or null if not found
     */
    @Override
    public Property get(String propertyId) {
        return properties.get(propertyId);
    }

    /**
     * Retrieves all properties in the system.
     * @return A list of all Property objects
     */

    @Override
    public List<Property> getAll() {
        return new ArrayList<>(properties.values());
    }

    /**
     * Retrieves a sorted list of properties based on the specified criteria.
     * @param sortBy The criteria to sort by (id, type, address, price, status, or owner)
     * @return A sorted list of Property objects
     * @throws IllegalArgumentException if an invalid sort criteria is provided
     */

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

    /**
     * Gets the total number of properties in the system.
     * @return The total number of properties
     */

    @Override
    public int getTotalProperties() {
        return properties.size();
    }

    /**
     * Gets the number of occupied (rented) properties in the system.
     * @return The number of occupied properties
     */
    @Override
    public int getOccupiedProperties() {
        return (int) getAll().stream().filter(p -> p.getStatus() == PropertyStatus.RENTED).count();
    }

    /**
     * Searches for properties based on a keyword.
     * The search is case-insensitive and looks in the property ID, address, and owner's full name.
     * @param keyword The search keyword
     * @return A list of Property objects matching the search criteria
     */

    @Override
    public List<Property> search(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return getAll().stream()
                .filter(property -> property.getPropertyId().toLowerCase().contains(lowercaseKeyword) ||
                        property.getAddress().toLowerCase().contains(lowercaseKeyword) ||
                        property.getOwner().getFullName().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of all available properties.
     * @return A list of Property objects with AVAILABLE status
     */

    @Override
    public List<Property> getAvailableProperties() {
        return getAll().stream()
                .filter(property -> property.getStatus() == PropertyStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    /**
     * Converts a Property object to a string array for saving to file.
     * @param property The Property object to convert
     * @return A string array representation of the property
     */

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

        // Add hosts
        String hostIds = property.getHosts().stream()
                .map(Host::getId)
                .collect(Collectors.joining(";"));
        propertyData.add(hostIds);

        return propertyData.toArray(new String[0]);
    }

    /**
     * Saves the current state of properties to the file system.
     */
    @Override
    public void saveToFile() {
        List<String[]> propertyLines = new ArrayList<>();

        for (Property property : getSorted("id")) {
            propertyLines.add(saveProperty(property));
        }

        fileHandler.saveProperties(propertyLines);
    }


    /**
     * Creates a Property object from a string array representation.
     * @param parts The string array containing property data
     * @return The created Property object
     * @throws RuntimeException if there's an error parsing the property type
     */

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

        // Load hosts if the information is available
        if (parts.length > 12 && parts[12] != null && !parts[12].isEmpty()) {
            String[] hostIds = parts[12].split(";");
            for (String hostId : hostIds) {
                Host host = hostManager.get(hostId);
                if (host != null) {
                    property.addHost(host);
                }
            }
        }

        return property;
    }
}