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

import com.rentalsystem.model.Owner;
import com.rentalsystem.util.FileHandler;
import com.rentalsystem.util.InputValidator;
import com.rentalsystem.model.Property;
import com.rentalsystem.model.Host;

import static com.rentalsystem.util.FileHandler.DATE_FORMAT;
import static com.rentalsystem.util.InputValidator.isEmailTaken;


/**
 * Implementation of the OwnerManager interface.
 * Manages Owner entities in the system, providing CRUD operations and additional functionalities.
 */
public class OwnerManagerImpl implements OwnerManager {
    private final Map<String, Owner> owners;
    private final FileHandler fileHandler;
    private PropertyManager propertyManager;
    private HostManager hostManager;

    /**
     * Constructs a new OwnerManagerImpl with the given FileHandler.
     * Initializes the owners map.
     * @param fileHandler The FileHandler to use for data persistence
     */

    public OwnerManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.owners = new HashMap<>();
    }

    /**
     * Sets the dependencies required for the OwnerManager.
     * @param propertyManager The PropertyManager instance
     * @param hostManager The HostManager instance
     */

    public void setDependencies(PropertyManager propertyManager, HostManager hostManager) {
        this.propertyManager = propertyManager;
        this.hostManager = hostManager;
    }

    /**
     * Loads owner data from the file system and updates their properties and hosts.
     * Throws an IllegalStateException if dependencies are not set.
     */
    @Override
    public void load() {
        if (propertyManager == null || hostManager == null) {
            throw new IllegalStateException("Dependencies not set for OwnerManager");
        }


        for (String[] parts : fileHandler.readLines("owners.txt")) {
            Owner owner = fromString(parts);
            owners.put(owner.getId(), owner);
        }


        // After loading all owners, update their properties and hosts
        for (Property property : propertyManager.getAll()) {
            Owner owner = get(property.getOwner().getId());
            if (owner != null) {
                owner.addOwnedProperty(property);
                for (Host host : property.getHosts()) {
                    owner.addManagingHost(host);
                }
            }
        }
    }

    /**
     * Adds a new owner to the system.
     * Validates the email format and checks for email uniqueness before adding.
     * @param owner The Owner object to be added
     * @throws IllegalArgumentException if the email is invalid or already in use
     */
    @Override
    public void add(Owner owner) {
        if (!InputValidator.isValidEmail(owner.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format for owner: " + owner.getContactInformation());
        }
        if (isEmailTaken(getAll(), owner.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + owner.getContactInformation());
        }
        owners.put(owner.getId(), owner);
        saveToFile();
    }

    /**
     * Updates an existing owner in the system.
     * Validates the email format and checks for email uniqueness before updating.
     * @param owner The Owner object to be updated
     * @throws IllegalArgumentException if the owner doesn't exist, or if the new email is invalid or already in use
     */
    @Override
    public void update(Owner owner) {
        if (!InputValidator.isValidEmail(owner.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format for owner: " + owner.getContactInformation());
        }
        Owner existingOwner = owners.get(owner.getId());
        if (existingOwner == null) {
            throw new IllegalArgumentException("Owner with ID " + owner.getId() + " does not exist.");
        }
        if (!existingOwner.getContactInformation().equals(owner.getContactInformation()) && isEmailTaken(getAll(), owner.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + owner.getContactInformation());
        }
        owners.put(owner.getId(), owner);
        saveToFile();
    }

    /**
     * Deletes an owner from the system.
     * @param ownerId The ID of the owner to be deleted
     * @throws IllegalArgumentException if the owner doesn't exist
     */
    @Override
    public void delete(String ownerId) {
        if (!owners.containsKey(ownerId)) {
            throw new IllegalArgumentException("Owner with ID " + ownerId + " does not exist.");
        }
        owners.remove(ownerId);
        saveToFile();
    }

    /**
     * Retrieves an owner by their ID.
     * @param ownerId The ID of the owner to retrieve
     * @return The Owner object, or null if not found
     */
    @Override
    public Owner get(String ownerId) {
        return owners.get(ownerId);
    }

    /**
     * Retrieves all owners in the system.
     * @return A list of all Owner objects
     */
    @Override
    public List<Owner> getAll() {
        return new ArrayList<>(owners.values());
    }

    /**
     * Retrieves a sorted list of owners based on the specified criteria.
     * @param sortBy The criteria to sort by (id, name, dob, or email)
     * @return A sorted list of Owner objects
     * @throws IllegalArgumentException if an invalid sort criteria is provided
     */
    @Override
    public List<Owner> getSorted(String sortBy) {
        List<Owner> sortedList = getAll();
        switch (sortBy.toLowerCase()) {
            case "id":
                sortedList.sort(Comparator.comparing(Owner::getId));
                break;
            case "name":
                sortedList.sort(Comparator.comparing(Owner::getFullName));
                break;
            case "dob":
                sortedList.sort(Comparator.comparing(Owner::getDateOfBirth));
                break;
            case "email":
                sortedList.sort(Comparator.comparing(Owner::getContactInformation));
                break;
            default:
                throw new IllegalArgumentException("Invalid sort criteria: " + sortBy);
        }
        return sortedList;
    }

    /**
     * Searches for owners based on a keyword.
     * The search is case-insensitive and looks in the full name, ID, and email.
     * @param keyword The search keyword
     * @return A list of Owner objects matching the search criteria
     */
    @Override
    public List<Owner> search(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return getAll().stream()
                .filter(owner -> owner.getFullName().toLowerCase().contains(lowercaseKeyword) ||
                        owner.getId().toLowerCase().contains(lowercaseKeyword) ||
                        owner.getContactInformation().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Saves the current state of owners to the file system.
     */
    @Override
    public void saveToFile() {
        List<String[]> lines = getSorted("id").stream()
                .map(entity -> new String[]{
                        entity.getId(),
                        entity.getFullName(),
                        DATE_FORMAT.format(entity.getDateOfBirth()),
                        entity.getContactInformation()
                })
                .collect(Collectors.toList());
        fileHandler.saveOwners(lines); // Use appropriate method name for each entity type
    }

    /**
     * Creates an Owner object from a string array representation.
     * @param parts The string array containing owner data
     * @return The created Owner object
     * @throws RuntimeException if there's an error parsing the date
     */
    @Override
    public Owner fromString(String[] parts) {
        try {
            return new Owner(parts[0], parts[1], DATE_FORMAT.parse(parts[2]), parts[3]);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
