package com.rentalsystem.manager;


import com.rentalsystem.model.Host;
import java.util.List;


/**
 * Interface for managing Host entities in the system.
 * Extends CrudManager for basic CRUD operations.
 */
public interface HostManager extends CrudManager<Host> {
    /**
     * Adds a new host to the system.
     * @param host The host to be added
     */
    void add(Host host);


    /**
     * Updates an existing host in the system.
     * @param host The host to be updated
     */
    void update(Host host);


    /**
     * Deletes a host from the system based on its ID.
     * @param hostId The ID of the host to be deleted
     */
    void delete(String hostId);


    /**
     * Retrieves a host from the system based on its ID.
     * @param hostId The ID of the host to retrieve
     * @return The retrieved host, or null if not found
     */
    Host get(String hostId);


    /**
     * Retrieves all hosts from the system.
     * @return A list of all hosts
     */
    List<Host> getAll();


    /**
     * Searches for hosts based on a keyword.
     * @param keyword The search keyword
     * @return A list of hosts matching the search criteria
     */
    List<Host> search(String keyword);


    /**
     * Checks if an email is already taken by any host.
     * @param email The email to check
     * @return true if the email is taken, false otherwise
     */
    boolean isEmailTaken(String email);


    /**
     * Retrieves a sorted list of all hosts based on a specified criteria.
     * @param sortBy The criteria to sort by
     * @return A sorted list of all hosts
     */
    List<Host> getSorted(String sortBy);


    /**
     * Saves the current state of hosts to a file.
     */
    void saveToFile();
}
