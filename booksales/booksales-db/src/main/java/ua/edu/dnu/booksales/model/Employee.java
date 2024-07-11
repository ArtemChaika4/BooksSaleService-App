package ua.edu.dnu.booksales.model;

public class Employee {
    private int id;
    private String name;
    private String surname;
    private String patronymic;
    private String email;
    private String password;
    private Position position;

    public Employee(){
        id = 0;
        name = "";
        surname = "";
        patronymic = "";
        email = "";
        password = "";
        position = new Position();
    }

    public Employee(String name, String surname, String patronymic, String email, String password,
                    Position position) {
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.email = email;
        this.position = position;
        this.password = password;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getFullName(){
        return surname + " " + name + " " + patronymic;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", email='" + email + '\'' +
                ", position=" + position +
                '}';
    }
}
