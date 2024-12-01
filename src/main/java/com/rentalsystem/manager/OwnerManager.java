/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */

package com.rentalsystem.manager;


import com.rentalsystem.model.Owner;
import java.util.List;


/**
 * Interface for managing Owner entities in the system.
 * Extends CrudManager for basic CRUD operations.
 */
public interface OwnerManager extends CrudManager<Owner> {
    /**
     * Adds a new owner to the system.
     * @param owner The owner to be added
     */
    void add(Owner owner);


    /**
     * Updates an existing owner in the system.
     * @param owner The owner to be updated
     */
    void update(Owner owner);


    /**
     * Deletes an owner from the system based on its ID.
     * @param ownerId The ID of the owner to be deleted
     */
    void delete(String ownerId);


    /**
     * Retrieves an owner from the system based on its ID.
     * @param ownerId The ID of the owner to retrieve
     * @return The retrieved owner, or null if not found
     */
    Owner get(String ownerId);


    /**
     * Retrieves all owners from the system.
     * @return A list of all owners
     */
    List<Owner> getAll();


    /**
     * Searches for owners based on a keyword.
     * @param keyword The search keyword
     * @return A list of owners matching the search criteria
     */
    List<Owner> search(String keyword);


    /**
     * Retrieves a sorted list of all owners based on a specified criteria.
     * @param sortBy The criteria to sort by
     * @return A sorted list of all owners
     */
    List<Owner> getSorted(String sortBy);
}
