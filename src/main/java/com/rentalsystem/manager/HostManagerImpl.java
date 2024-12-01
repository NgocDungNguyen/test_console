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

import com.rentalsystem.model.Host;
import com.rentalsystem.util.FileHandler;
import com.rentalsystem.util.InputValidator;

import static com.rentalsystem.util.FileHandler.DATE_FORMAT;


/**
 * Implementation of the HostManager interface.
 * Manages Host entities in the system, providing CRUD operations and additional functionalities.
 */
public class HostManagerImpl implements HostManager {
    private final Map<String, Host> hosts;
    private final FileHandler fileHandler;
    private PropertyManager propertyManager;
    private OwnerManager ownerManager;

    /**
     * Constructs a new HostManagerImpl with the given FileHandler.
     * Initializes the hosts map.
     * @param fileHandler The FileHandler to use for data persistence
     */

    public HostManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.hosts = new HashMap<>();
    }

    /**
     * Sets the dependencies required for the HostManager.
     * @param propertyManager The PropertyManager instance
     * @param ownerManager The OwnerManager instance
     */

    public void setDependencies(PropertyManager propertyManager, OwnerManager ownerManager) {
        this.propertyManager = propertyManager;
        this.ownerManager = ownerManager;
    }

    /**
     * Loads host data from the file system.
     * Throws an IllegalStateException if dependencies are not set.
     */
    @Override
    public void load() {
        if (propertyManager == null || ownerManager == null) {
            throw new IllegalStateException("Dependencies not set for HostManager");
        }

        for (String[] parts : fileHandler.readLines("hosts.txt")) {
            Host host = fromString(parts);
            hosts.put(host.getId(), host);
        }
    }

    /**
     * Adds a new host to the system.
     * Validates the email format and checks for email uniqueness before adding.
     * @param host The Host object to be added
     * @throws IllegalArgumentException if the email is invalid or already in use
     */

    @Override
    public void add(Host host) {
        if (!InputValidator.isValidEmail(host.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format for host: " + host.getContactInformation());
        }
        if (isEmailTaken(host.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + host.getContactInformation());
        }
        hosts.put(host.getId(), host);
        saveToFile();
    }

    /**
     * Updates an existing host in the system.
     * Validates the email format and checks for email uniqueness before updating.
     * @param host The Host object to be updated
     * @throws IllegalArgumentException if the host doesn't exist, or if the new email is invalid or already in use
     */

    @Override
    public void update(Host host) {
        if (!InputValidator.isValidEmail(host.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format for host: " + host.getContactInformation());
        }
        Host existingHost = hosts.get(host.getId());
        if (existingHost == null) {
            throw new IllegalArgumentException("Host with ID " + host.getId() + " does not exist.");
        }
        if (!existingHost.getContactInformation().equals(host.getContactInformation()) && isEmailTaken(host.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + host.getContactInformation());
        }
        hosts.put(host.getId(), host);
        saveToFile();
    }

    /**
     * Deletes a host from the system.
     * @param hostId The ID of the host to be deleted
     * @throws IllegalArgumentException if the host doesn't exist
     */

    @Override
    public void delete(String hostId) {
        if (!hosts.containsKey(hostId)) {
            throw new IllegalArgumentException("Host with ID " + hostId + " does not exist.");
        }
        hosts.remove(hostId);
        saveToFile();
    }

    /**
     * Retrieves a host by their ID.
     * @param id The ID of the host to retrieve
     * @return The Host object, or null if no host with the given ID exists
     */
    @Override
    public Host get(String id) {
        return hosts.get(id);
    }

    /**
     * Retrieves all hosts in the system.
     * @return A list of all Host objects
     */

    @Override
    public List<Host> getAll() {
        return new ArrayList<>(hosts.values());
    }

    /**
     * Retrieves a sorted list of hosts based on the specified criteria.
     * @param sortBy The criteria to sort by (id, name, dob, or email)
     * @return A sorted list of Host objects
     * @throws IllegalArgumentException if an invalid sort criteria is provided
     */

    @Override
    public List<Host> getSorted(String sortBy) {
        List<Host> sortedList = getAll();
        switch (sortBy.toLowerCase()) {
            case "id":
                sortedList.sort(Comparator.comparing(Host::getId));
                break;
            case "name":
                sortedList.sort(Comparator.comparing(Host::getFullName));
                break;
            case "dob":
                sortedList.sort(Comparator.comparing(Host::getDateOfBirth));
                break;
            case "email":
                sortedList.sort(Comparator.comparing(Host::getContactInformation));
                break;
            default:
                throw new IllegalArgumentException("Invalid sort criteria: " + sortBy);
        }
        return sortedList;
    }

    /**
     * Searches for hosts based on a keyword.
     * The search is case-insensitive and looks in the full name, ID, and email.
     * @param keyword The search keyword
     * @return A list of Host objects matching the search criteria
     */

    @Override
    public List<Host> search(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return getAll().stream()
                .filter(host -> host.getFullName().toLowerCase().contains(lowercaseKeyword) ||
                        host.getId().toLowerCase().contains(lowercaseKeyword) ||
                        host.getContactInformation().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }


    /**
     * Checks if an email is already taken by any host in the system.
     * @param email The email to check
     * @return true if the email is taken, false otherwise
     */

    @Override
    public boolean isEmailTaken(String email) {
        return getAll().stream()
                .anyMatch(host -> host.getContactInformation().equalsIgnoreCase(email));
    }

    /**
     * Saves the current state of hosts to the file system.
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
        fileHandler.saveHosts(lines);
    }

    /**
     * Creates a Host object from a string array representation.
     * @param parts The string array containing host data
     * @return The created Host object
     * @throws RuntimeException if there's an error parsing the date
     */
    @Override
    public Host fromString(String[] parts) {
        try {
            return new Host(
                    parts[0],
                    parts[1],
                    DATE_FORMAT.parse(parts[2]),
                    parts[3]
            );
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + parts[2], e);
        }
    }
}
