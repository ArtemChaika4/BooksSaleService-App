package ua.edu.dnu.booksales.model;

public class Position {
    private int id;
    private String name;
    private String description;
    private Post post;

    public Position(){
        id = 0;
        name = "";
        description = "";
    }

    public Position(String name, String description, Post post) {
        this.name = name;
        this.description = description;
        this.post = post;
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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", position=" + post +
                '}';
    }
}
