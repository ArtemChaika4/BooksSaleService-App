package ua.edu.dnu.booksales.model;

import java.util.Arrays;

public enum GoodsStatus {
    AVAILABLE(1, "У наявності"),
    EXPECTED(2, "Очікується"),
    DELETED(3, "Видалено");

    private final int id;
    private final String name;

    GoodsStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public static GoodsStatus getById(int id){
        return Arrays.stream(values())
                .filter(s -> s.id == id)
                .findFirst()
                .orElse(null);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
