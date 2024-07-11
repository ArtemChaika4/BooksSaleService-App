package ua.edu.dnu.booksales.user;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.model.ActiveUser;
import ua.edu.dnu.booksales.model.Employee;
import ua.edu.dnu.booksales.model.Position;

public class ProfileController {
    public TextField surname;
    public TextField patronymic;
    public TextField name;
    public TextField position;
    public TextField email;

    @FXML
    public void initialize() {
        Employee employee = ActiveUser.getInstance().getEmployee();
        surname.setText(employee.getSurname());
        name.setText(employee.getName());
        patronymic.setText(employee.getPatronymic());
        email.setText(employee.getEmail());
        Position post = employee.getPosition();
        position.setText(post == null ? "—" : post.getName());
    }

    public void changePassword() {
        MainPaneController.getInstance().setNextContent("change-password.fxml");
    }
}
