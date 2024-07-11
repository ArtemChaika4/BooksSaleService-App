package ua.edu.dnu.booksales.dao;

import java.util.List;

public interface InterfaceDAO<T> {
    void create(T item);
    List<T> getAll();
    void update(T item);
    void delete(int id);
    List<T> select(String sqlQuery);
}
