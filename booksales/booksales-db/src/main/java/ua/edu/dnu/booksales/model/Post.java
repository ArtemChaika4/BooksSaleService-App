package ua.edu.dnu.booksales.model;

import java.util.Arrays;

public enum Post {
    ADMINISTRATOR(1),
    WAREHOUSEMAN(2),
    OPERATOR(3);

    private final int id;

    Post(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Post getById(int id){
        return Arrays.stream(values())
                .filter(p -> p.id == id)
                .findFirst()
                .orElse(null);
    }


}
