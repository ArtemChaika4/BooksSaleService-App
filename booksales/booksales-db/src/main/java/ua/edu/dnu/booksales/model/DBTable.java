package ua.edu.dnu.booksales.model;

import java.util.Arrays;

public enum DBTable {
    ORDERS(1, "Замовлення"),
    CUSTOMERS(2, "Замовники"),
    GOODS(3, "Товари"),
    GOODS_TYPES(4, "Типи літератури"),
    EMPLOYEES(5, "Працівники");

    private final int id;
    private final String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    DBTable(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static DBTable getById(int id){
        return Arrays.stream(values())
                .filter(s -> s.id == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return name;
    }
}
