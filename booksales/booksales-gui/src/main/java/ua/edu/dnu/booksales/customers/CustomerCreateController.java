package ua.edu.dnu.booksales.customers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.CustomerDAO;
import ua.edu.dnu.booksales.model.Customer;
import ua.edu.dnu.booksales.validator.Validator;

public class CustomerCreateController {
    public TextField name;
    public TextField phone;
    public TextField patronymic;
    public TextField address;
    public TextField surname;

    private boolean isCustomerValid(){
        return Validator.validateTextField(surname, Validator.NSP_REGEX) &&
                Validator.validateTextField(name, Validator.NSP_REGEX) &&
                Validator.validateTextField(patronymic, Validator.NSP_REGEX) &&
                Validator.validateTextField(address, Validator.ADDRESS_REGEX) &&
                Validator.validateTextField(phone, Validator.PHONE_REGEX);
    }

    @FXML
    private void addCustomer() {
        if (isCustomerValid()) {
            Customer customer = new Customer(name.getText(), surname.getText(),
                    patronymic.getText(), address.getText(), "+38" + phone.getText());
            CustomerDAO customers = new CustomerDAO();
            if(customers.getByPhone(customer.getPhone()) != null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Вже існує замовник з номером телефону: " + customer.getPhone());
                alert.showAndWait();
                return;
            }
            customers.create(customer);
            MainPaneController.getInstance().setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(surname, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(name, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(patronymic, Validator.NSP_REGEX);
        Validator.addTextFieldValidation(address, Validator.ADDRESS_REGEX);
        Validator.addTextFieldValidation(phone, Validator.PHONE_REGEX);
    }
}
