package ua.edu.dnu.booksales.model;

public class GoodsType {
    private int id;
    private String name;
    private String description;

    public GoodsType(){
        id = 0;
        name = "";
        description = "";
    }

    public GoodsType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "GoodsType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
