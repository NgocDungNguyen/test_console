package com.rentalsystem.manager;

import com.rentalsystem.model.Host;
import java.util.List;

public interface HostManager extends CrudManager<Host> {
    void add(Host host);
    void update(Host host);
    void delete(String hostId);
    Host get(String hostId);
    List<Host> getAll();
    List<Host> search(String keyword);
    boolean isEmailTaken(String email);
    List<Host> getSorted(String sortBy);
    void saveToFile();
}
