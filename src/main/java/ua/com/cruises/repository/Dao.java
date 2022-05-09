package ua.com.cruises.repository;

import java.util.Collection;
import java.util.Optional;

public interface Dao<T> {

    boolean insert(T obj);

    Optional<T> find(int id);

    Collection<T> findAll();

    boolean update(T obj);

    boolean remove(int id);
}
