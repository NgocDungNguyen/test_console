/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */

package com.rentalsystem.manager;

import java.util.List;


/**
 * Generic interface for CRUD (Create, Read, Update, Delete) operations.
 * @param <T> The type of object being managed
 */
public interface CrudManager<T> {
    /**
     * Adds a new object to the system.
     * @param object The object to be added
     */
    void add(T object);


    /**
     * Updates an existing object in the system.
     * @param object The object to be updated
     */
    void update(T object);


    /**
     * Deletes an object from the system based on its ID.
     * @param id The ID of the object to be deleted
     */
    void delete(String id);


    /**
     * Retrieves an object from the system based on its ID.
     * @param id The ID of the object to retrieve
     * @return The retrieved object, or null if not found
     */
    T get(String id);


    /**
     * Retrieves all objects of this type from the system.
     * @return A list of all objects
     */
    List<T> getAll();


    /**
     * Retrieves a sorted list of all objects based on a specified criteria.
     * @param sortBy The criteria to sort by
     * @return A sorted list of all objects
     */
    List<T> getSorted(String sortBy);


    /**
     * Saves the current state of objects to a file.
     */
    void saveToFile();


    /**
     * Creates an object from a string array representation.
     * @param parts The string array containing object data
     * @return The created object
     */
    T fromString(String[] parts);


    /**
     * Loads objects from a file into the system.
     */
    void load();
}
