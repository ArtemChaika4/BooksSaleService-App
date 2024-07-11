package ua.edu.dnu.booksales;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ua.edu.dnu.booksales.dao.EmployeeDAO;
import ua.edu.dnu.booksales.model.ActiveUser;
import ua.edu.dnu.booksales.model.Employee;
import ua.edu.dnu.booksales.model.Position;
import ua.edu.dnu.booksales.validator.Validator;

import java.io.IOException;

public class LoginController {
    public TextField emailField;
    public TextField passwordField;
    public Label errorLabel;

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(emailField, Validator.EMAIL_REGEX);
        errorLabel.setVisible(false);
    }

    public void logIn(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        EmployeeDAO employees = new EmployeeDAO();
        Employee employee = employees.logIn(email, password);
        if(employee == null){
            errorLabel.setVisible(true);
            return;
        }
        ActiveUser.getInstance().setEmployee(employee);
        changeScene(event);
        setEmployeeScene(employee);
    }

    private void setEmployeeScene(Employee employee){
        Position position = employee.getPosition();
        if(position == null || position.getPost() == null){
            return;
        }
        String menu = switch (position.getPost()){
            case OPERATOR -> "operator-menu.fxml";
            case WAREHOUSEMAN -> "warehouseman-menu.fxml";
            case ADMINISTRATOR -> "admin-menu.fxml";
        };
        MainPaneController.getInstance().setMenu(menu);
        MainPaneController.getInstance().setRootContent("profile.fxml");
    }

    private void changeScene(ActionEvent event){
        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getClassLoader().getResource("main.fxml"));
        try {
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            double width = root.prefWidth(-1);
            double height = root.prefHeight(-1);
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
