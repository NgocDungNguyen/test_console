package com.rentalsystem.manager;

import java.util.List;

import com.rentalsystem.model.Payment;
import com.rentalsystem.model.Tenant;

public interface TenantManager extends CrudManager<Tenant> {
    void add(Tenant tenant);
    void update(Tenant tenant);
    void delete(String id);
    Tenant get(String id);
    List<Tenant> getAll();
    List<Tenant> search(String keyword);
    boolean isEmailTaken(String email);
    Tenant getByEmail(String email);
    List<Tenant> getSorted(String sortBy);
    void loadPayments();
}
