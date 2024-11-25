package com.rentalsystem.manager;

import com.rentalsystem.model.Owner;
import java.util.List;

public interface OwnerManager extends CrudManager<Owner> {
    void add(Owner owner);
    void update(Owner owner);
    void delete(String ownerId);
    Owner get(String ownerId);
    List<Owner> getAll();
    List<Owner> search(String keyword);
    List<Owner> getSorted(String sortBy);

}
