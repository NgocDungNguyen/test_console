package com.rentalsystem.manager;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;


import com.rentalsystem.model.Owner;
import com.rentalsystem.model.Person;
import com.rentalsystem.util.FileHandler;
import com.rentalsystem.util.InputValidator;


import com.rentalsystem.model.Property;
import com.rentalsystem.model.Host;




import static com.rentalsystem.util.FileHandler.DATE_FORMAT;
import static com.rentalsystem.util.InputValidator.isEmailTaken;


/**
 * Implementation of the OwnerManager interface.
 * Manages Owner entities in the system.
 */
public class OwnerManagerImpl implements OwnerManager {
    private final Map<String, Owner> owners;
    private final FileHandler fileHandler;
    private PropertyManager propertyManager;
    private HostManager hostManager;


    public OwnerManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.owners = new HashMap<>();
    }


    public void setDependencies(PropertyManager propertyManager, HostManager hostManager) {
        this.propertyManager = propertyManager;
        this.hostManager = hostManager;
    }


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


    @Override
    public void delete(String ownerId) {
        if (!owners.containsKey(ownerId)) {
            throw new IllegalArgumentException("Owner with ID " + ownerId + " does not exist.");
        }
        owners.remove(ownerId);
        saveToFile();
    }


    @Override
    public Owner get(String ownerId) {
        return owners.get(ownerId);
    }


    @Override
    public List<Owner> getAll() {
        return new ArrayList<>(owners.values());
    }


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


    @Override
    public List<Owner> search(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return getAll().stream()
                .filter(owner -> owner.getFullName().toLowerCase().contains(lowercaseKeyword) ||
                        owner.getId().toLowerCase().contains(lowercaseKeyword) ||
                        owner.getContactInformation().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }


    @Override
    public void saveToFile() {
        List<String[]> lines = getSorted("id").stream()
                .map(Owner::toCSV).collect(Collectors.toList());
        fileHandler.saveOwners(lines);
    }


    @Override
    public Owner fromString(String[] parts) {
        try {
            return new Owner(parts[0], parts[1], DATE_FORMAT.parse(parts[2]), parts[3]);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
