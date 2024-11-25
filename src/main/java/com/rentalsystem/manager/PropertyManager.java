package com.rentalsystem.manager;

import com.rentalsystem.model.Property;

import java.util.List;

public interface PropertyManager extends CrudManager<Property> {
    void add(Property property);
    void update(Property property);
    void delete(String propertyId);
    Property get(String propertyId);
    List<Property> getAvailableProperties();
    List<Property> getAll();
    List<Property> search(String keyword);
    int getTotalProperties();
    int getOccupiedProperties();
    List<Property> getSorted(String sortBy);
    void load();
}
