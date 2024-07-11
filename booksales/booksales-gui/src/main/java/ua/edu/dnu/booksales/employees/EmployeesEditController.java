package ua.edu.dnu.booksales.employees;

import it.negste.peekablepasswordfield.PeekablePasswordField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.EmployeeDAO;
import ua.edu.dnu.booksales.model.Employee;
import ua.edu.dnu.booksales.model.Position;
import ua.edu.dnu.booksales.validator.Validator;

public class EmployeesEditController {
    public TextField name;
    public TextField email;
    public TextField patronymic;
    public TextField surname;
    public PeekablePasswordField password;
    public ComboBox<Position> postList;
    private Employee employee;


    public void loadEmployee(Employee employee) {
        this.employee = employee;
        reset();
    }

    private boolean isEmployeeValid(){
        return Validator.validateTextField(surname, Validator.NSP_REGEX) &&
                Validator.validateTextField(name, Validator.NSP_REGEX) &&
                Validator.validateTextField(patronymic, Validator.NSP_REGEX) &&
                Validator.validateTextField(email, Validator.EMAIL_REGEX) &&
                Validator.validateTextField(password, Validator.PASSWORD_REGEX) &&
                postList.getValue() != null;
    }

    public void saveEmployee() {
        if(isEmployeeValid()){
            EmployeeDAO employees = new EmployeeDAO();
            String newEmail = email.getText();
            if(!newEmail.equals(employee.getEmail()) && employees.getByEmail(newEmail) != null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Вже існує співробітник з електронною адресою: " + newEmail);
                alert.showAndWait();
                return;
            }
            employee.setName(name.getText());
            employee.setSurname(surname.getText());
            employee.setPatronymic(patronymic.getText());
            employee.setEmail(newEmail);
            employee.setPassword(password.getText());
            employee.setPosition(postList.getValue());
            new EmployeeDAO().update(employee);
            MainPaneController.getInstance().setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(surname, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(name, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(patronymic, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(password, Validator.PASSWORD_REGEX);
        Validator.addTextFieldValidation(email, Validator.EMAIL_REGEX);
        ObservableList<Position> positions = FXCollections.observableArrayList(
                new EmployeeDAO().getPositions());
        postList.setItems(positions);
        postList.setConverter(new StringConverter<>() {
            @Override
            public String toString(Position position) {
                if(position == null){
                    return "";
                }
                return position.getName();
            }
            @Override
            public Position fromString(String s) {
                return null;
            }
        });
    }

    public void reset() {
        name.setText(employee.getName());
        surname.setText(employee.getSurname());
        patronymic.setText(employee.getPatronymic());
        email.setText(employee.getEmail());
        password.setText(employee.getPassword());
        postList.setValue(employee.getPosition());
    }
}
