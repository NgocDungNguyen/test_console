package com.rentalsystem.manager;

import com.rentalsystem.model.Property;
import com.rentalsystem.model.RentalAgreement;
import java.util.List;

public interface RentalManager extends CrudManager<RentalAgreement> {
    void add(RentalAgreement agreement);
    void update(RentalAgreement agreement);
    void delete(String agreementId);
    RentalAgreement get(String agreementId);
    List<RentalAgreement> getAll();
    List<RentalAgreement> getSorted(String sortBy);
    void saveToFile();
    void addSubTenant(String agreementId, String subTenantId);
    void removeSubTenant(String agreementId, String subTenantId);
    List<RentalAgreement> getActiveRentalAgreements();
    List<RentalAgreement> getExpiredRentalAgreements();
    double getTotalRentalIncome();
    int getTotalActiveAgreements();
    RentalAgreement findActiveRentalAgreement(Property property);
    List<RentalAgreement> searchRentalAgreements(String keyword);
    void extendRentalAgreement(String agreementId, int extensionDays);
    void terminateRentalAgreement(String agreementId);
    void load();
}