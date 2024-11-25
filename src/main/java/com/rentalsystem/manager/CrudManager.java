package com.rentalsystem.manager;

import java.util.List;

public interface CrudManager<T> {
    void add(T object);
    void update(T object);
    void delete(String id);
    T get(String id);
    List<T> getAll();
    List<T> getSorted(String sortBy);


    void saveToFile();
    T fromString(String[] parts);
    void load();
}
