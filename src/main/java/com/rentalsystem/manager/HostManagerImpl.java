package com.rentalsystem.manager;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

import com.rentalsystem.model.Host;
import com.rentalsystem.model.Person;
import com.rentalsystem.util.FileHandler;
import com.rentalsystem.util.InputValidator;

import static com.rentalsystem.util.FileHandler.DATE_FORMAT;

/**
 * Implementation of the HostManager interface.
 * Manages Host entities in the system.
 */
public class HostManagerImpl implements HostManager {
    private final Map<String, Host> hosts;
    private final FileHandler fileHandler;

    /**
     * Constructs a new HostManagerImpl.
     * @param fileHandler The FileHandler to use for data persistence
     */
    public HostManagerImpl(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        this.hosts = new HashMap<>();
    }

    /**
     * Loads hosts from file into the system.
     */
    public void load() {
        fileHandler.loadHosts();
    }

    @Override
    public void add(Host host) {
        if (!InputValidator.isValidEmail(host.getContactInformation())) {
            throw new IllegalArgumentException("Invalid email format for host: " + host.getContactInformation());
        }
        if (isEmailTaken(host.getContactInformation())) {
            throw new IllegalArgumentException("Email already in use: " + host.getContactInformation());
        }
        hosts.put(host.getId(), host);
    }

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
    }

    @Override
    public void delete(String hostId) {
        if (!hosts.containsKey(hostId)) {
            throw new IllegalArgumentException("Host with ID " + hostId + " does not exist.");
        }
        hosts.remove(hostId);
    }

    @Override
    public Host get(String hostId) {
        Host host = hosts.get(hostId);
        if (host == null) {
            throw new IllegalArgumentException("Host with ID " + hostId + " does not exist.");
        }
        return host;
    }

    @Override
    public List<Host> getAll() {
        return new ArrayList<>(hosts.values());
    }

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

    @Override
    public List<Host> search(String keyword) {
        final String lowercaseKeyword = keyword.toLowerCase();
        return getAll().stream()
                .filter(host -> host.getFullName().toLowerCase().contains(lowercaseKeyword) ||
                        host.getId().toLowerCase().contains(lowercaseKeyword) ||
                        host.getContactInformation().toLowerCase().contains(lowercaseKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEmailTaken(String email) {
        return getAll().stream()
                .anyMatch(host -> host.getContactInformation().equalsIgnoreCase(email));
    }

    @Override
    public void saveToFile() {
        List<String[]> lines = getSorted("id").stream()
                .map(Person::toCSV).collect(Collectors.toList());
        fileHandler.saveHosts(lines);
    }

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
            throw new RuntimeException(e);
        }
    }
}