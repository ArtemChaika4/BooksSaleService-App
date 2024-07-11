package ua.edu.dnu.booksales.model;

import java.util.Arrays;

public enum OrderStatus {
    CREATED(1, "Нове замовлення", false),
    PROCESSED(2, "Опрацьовано", false),
    READY(3, "Готове до видачі", false),
    COMPLETED(4, "Виконано", false),
    CANCELED(5, "Скасовано", true),
    RETURNED(6, "Повернуто", true);

    private final int id;
    private final boolean canceled;
    private final String name;

    OrderStatus(int id, String name, boolean canceled) {
        this.id = id;
        this.name = name;
        this.canceled = canceled;
    }

    public int getId() {
        return id;
    }

    public boolean isCanceled() {
        return canceled;
    }


    public String getName() {
        return name;
    }

    @Override
    public String toString() {
       return name;
    }

    public static OrderStatus getById(int id){
        return Arrays.stream(values())
                .filter(s -> s.id == id)
                .findFirst()
                .orElse(null);
    }
}
