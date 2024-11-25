package com.rentalsystem.manager;

import com.rentalsystem.model.Property;
import com.rentalsystem.model.RentalAgreement;
import java.util.List;

/**
 * Interface for managing RentalAgreement entities in the system.
 * Extends CrudManager for basic CRUD operations.
 */
public interface RentalManager extends CrudManager<RentalAgreement> {
    /**
     * Adds a new rental agreement to the system.
     * @param agreement The rental agreement to be added
     */
    void add(RentalAgreement agreement);

    /**
     * Updates an existing rental agreement in the system.
     * @param agreement The rental agreement to be updated
     */
    void update(RentalAgreement agreement);

    /**
     * Deletes a rental agreement from the system based on its ID.
     * @param agreementId The ID of the rental agreement to be deleted
     */
    void delete(String agreementId);

    /**
     * Retrieves a rental agreement from the system based on its ID.
     * @param agreementId The ID of the rental agreement to retrieve
     * @return The retrieved rental agreement, or null if not found
     */
    RentalAgreement get(String agreementId);

    /**
     * Retrieves all rental agreements from the system.
     * @return A list of all rental agreements
     */
    List<RentalAgreement> getAll();

    /**
     * Retrieves a sorted list of all rental agreements based on a specified criteria.
     * @param sortBy The criteria to sort by
     * @return A sorted list of all rental agreements
     */
    List<RentalAgreement> getSorted(String sortBy);

    /**
     * Saves the current state of rental agreements to a file.
     */
    void saveToFile();

    /**
     * Adds a sub-tenant to a rental agreement.
     * @param agreementId The ID of the rental agreement
     * @param subTenantId The ID of the sub-tenant to be added
     */
    void addSubTenant(String agreementId, String subTenantId);

    /**
     * Removes a sub-tenant from a rental agreement.
     * @param agreementId The ID of the rental agreement
     * @param subTenantId The ID of the sub-tenant to be removed
     */
    void removeSubTenant(String agreementId, String subTenantId);

    /**
     * Retrieves all active rental agreements from the system.
     * @return A list of all active rental agreements
     */
    List<RentalAgreement> getActiveRentalAgreements();

    /**
     * Retrieves all expired rental agreements from the system.
     * @return A list of all expired rental agreements
     */
    List<RentalAgreement> getExpiredRentalAgreements();

    /**
     * Calculates the total rental income from all active agreements.
     * @return The total rental income
     */
    double getTotalRentalIncome();

    /**
     * Gets the total number of active rental agreements.
     * @return The total number of active rental agreements
     */
    int getTotalActiveAgreements();

    /**
     * Finds an active rental agreement for a given property.
     * @param property The property to find an active rental agreement for
     * @return The active rental agreement, or null if not found
     */
    RentalAgreement findActiveRentalAgreement(Property property);

    /**
     * Searches for rental agreements based on a keyword.
     * @param keyword The search keyword
     * @return A list of rental agreements matching the search criteria
     */
    List<RentalAgreement> searchRentalAgreements(String keyword);

    /**
     * Extends the duration of a rental agreement.
     * @param agreementId The ID of the rental agreement to extend
     * @param extensionDays The number of days to extend the agreement by
     */
    void extendRentalAgreement(String agreementId, int extensionDays);

    /**
     * Terminates a rental agreement.
     * @param agreementId The ID of the rental agreement to terminate
     */
    void terminateRentalAgreement(String agreementId);

    /**
     * Loads rental agreements from file into the system.
     */
    void load();
}