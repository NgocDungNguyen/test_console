package com.rentalsystem.manager;


import com.rentalsystem.model.Property;


import java.util.List;


/**
 * Interface for managing Property entities in the system.
 * Extends CrudManager for basic CRUD operations.
 */
public interface PropertyManager extends CrudManager<Property> {
    /**
     * Adds a new property to the system.
     * @param property The property to be added
     */
    void add(Property property);


    /**
     * Updates an existing property in the system.
     * @param property The property to be updated
     */
    void update(Property property);


    /**
     * Deletes a property from the system based on its ID.
     * @param propertyId The ID of the property to be deleted
     */
    void delete(String propertyId);


    /**
     * Retrieves a property from the system based on its ID.
     * @param propertyId The ID of the property to retrieve
     * @return The retrieved property, or null if not found
     */
    Property get(String propertyId);


    /**
     * Retrieves all available properties from the system.
     * @return A list of all available properties
     */
    List<Property> getAvailableProperties();


    /**
     * Retrieves all properties from the system.
     * @return A list of all properties
     */
    List<Property> getAll();


    /**
     * Searches for properties based on a keyword.
     * @param keyword The search keyword
     * @return A list of properties matching the search criteria
     */
    List<Property> search(String keyword);


    /**
     * Gets the total number of properties in the system.
     * @return The total number of properties
     */
    int getTotalProperties();


    /**
     * Gets the number of occupied properties in the system.
     * @return The number of occupied properties
     */
    int getOccupiedProperties();


    /**
     * Retrieves a sorted list of all properties based on a specified criteria.
     * @param sortBy The criteria to sort by
     * @return A sorted list of all properties
     */
    List<Property> getSorted(String sortBy);


    /**
     * Loads properties from file into the system.
     */
    void load();
}
