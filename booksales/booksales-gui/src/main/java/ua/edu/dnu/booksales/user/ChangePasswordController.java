package ua.edu.dnu.booksales.user;

import it.negste.peekablepasswordfield.PeekablePasswordField;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.EmployeeDAO;
import ua.edu.dnu.booksales.model.ActiveUser;
import ua.edu.dnu.booksales.model.Employee;
import ua.edu.dnu.booksales.validator.Validator;

public class ChangePasswordController {

    public TextField emailField;
    public Label oldErrorLabel;
    public Label newErrorLabel;
    public Label confirmErrorLabel;
    public PeekablePasswordField oldPassword;
    public PeekablePasswordField newPassword;
    public PeekablePasswordField confirmPassword;
    private final EmployeeDAO employees = new EmployeeDAO();
    private final Employee employee = ActiveUser.getInstance().getEmployee();

    @FXML
    public void initialize() {
        emailField.setText(employee.getEmail());
        Validator.addTextFieldValidation(oldPassword, Validator.PASSWORD_REGEX);
        Validator.addTextFieldValidation(newPassword, Validator.PASSWORD_REGEX);
    }

    private boolean validatePassword(TextField password, Label error){
        boolean isValid = Validator.validateTextField(password, Validator.PASSWORD_REGEX);
        error.setVisible(!isValid);
        return isValid;
    }

    private boolean checkOldPassword(){
        if(!validatePassword(oldPassword, oldErrorLabel)){
            return false;
        }
        String email = emailField.getText();
        String password = oldPassword.getText();
        boolean isCorrect = employees.checkPassword(email, password);
        oldErrorLabel.setVisible(!isCorrect);
        return isCorrect;
    }

    private boolean checkConfirmPassword(){
        String newPass = newPassword.getText();
        String confirmPass = confirmPassword.getText();
        boolean isConfirmed = confirmPass.equals(newPass);
        confirmErrorLabel.setVisible(!isConfirmed);
        return isConfirmed;
    }

    private boolean validate(){
        oldErrorLabel.setVisible(false);
        newErrorLabel.setVisible(false);
        confirmErrorLabel.setVisible(false);
        return checkOldPassword() &&
                validatePassword(newPassword, newErrorLabel) &&
                checkConfirmPassword();
    }

    private void showResultAlert(boolean status){
        String message = status ? "Пароль успішно змінено" : "Не вдалося змінити пароль";
        Alert.AlertType type = status ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING;
        Alert alert = new Alert(type);
        alert.setTitle("Повідомлення про зміну пароля");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void changePassword() {
        if(validate()){
            String email = emailField.getText();
            String password = newPassword.getText();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попередження про зміну пароля");
            alert.setContentText("Після виконання операції пароль буде змінено");
            alert.showAndWait().ifPresent(b -> {
                boolean isChanged = employees.changePassword(email, password);
                showResultAlert(isChanged);
                MainPaneController.getInstance().setPrevContent();
            });
        }
    }
}
