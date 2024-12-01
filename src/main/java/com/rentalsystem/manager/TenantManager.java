/**
 * @author <Nguyen Ngoc Dung - s3978535>
 */
package com.rentalsystem.manager;

import java.util.List;
import com.rentalsystem.model.Tenant;


/**
 * Interface for managing Tenant entities in the system.
 * Extends CrudManager for basic CRUD operations.
 */
public interface TenantManager extends CrudManager<Tenant> {
    /**
     * Adds a new tenant to the system.
     * @param tenant The tenant to be added
     */
    void add(Tenant tenant);


    /**
     * Updates an existing tenant in the system.
     * @param tenant The tenant to be updated
     */
    void update(Tenant tenant);


    /**
     * Deletes a tenant from the system based on its ID.
     * @param id The ID of the tenant to be deleted
     */
    void delete(String id);


    /**
     * Retrieves a tenant from the system based on its ID.
     * @param id The ID of the tenant to retrieve
     * @return The retrieved tenant, or null if not found
     */
    Tenant get(String id);


    /**
     * Retrieves all tenants from the system.
     * @return A list of all tenants
     */
    List<Tenant> getAll();


    /**
     * Searches for tenants based on a keyword.
     * @param keyword The search keyword
     * @return A list of tenants matching the search criteria
     */
    List<Tenant> search(String keyword);


    /**
     * Checks if an email is already taken by any tenant.
     * @param email The email to check
     * @return true if the email is taken, false otherwise
     */
    boolean isEmailTaken(String email);


    /**
     * Retrieves a tenant from the system based on their email.
     * @param email The email of the tenant to retrieve
     * @return The retrieved tenant, or null if not found
     */
    Tenant getByEmail(String email);


    /**
     * Retrieves a sorted list of all tenants based on a specified criteria.
     * @param sortBy The criteria to sort by
     * @return A sorted list of all tenants
     */
    List<Tenant> getSorted(String sortBy);


    /**
     * Loads payments for all tenants from file.
     */
    void loadPayments();
}
