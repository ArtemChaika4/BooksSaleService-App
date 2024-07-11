package ua.edu.dnu.booksales.customers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.CustomerDAO;
import ua.edu.dnu.booksales.model.Customer;
import ua.edu.dnu.booksales.validator.Validator;

public class CustomerEditController {
    public TextField name;
    public TextField phone;
    public TextField patronymic;
    public TextField address;
    public TextField surname;

    private Customer customer;

    public void loadCustomer(Customer customer){
        this.customer = customer;
        reset();
    }

    public void reset() {
        name.setText(customer.getName());
        surname.setText(customer.getSurname());
        patronymic.setText(customer.getPatronymic());
        address.setText(customer.getAddress());
        phone.setText(customer.getPhone().substring(3));
    }

    private boolean isCustomerValid(){
        return Validator.validateTextField(surname, Validator.NSP_REGEX) &&
                Validator.validateTextField(name, Validator.NSP_REGEX) &&
                Validator.validateTextField(patronymic, Validator.NSP_REGEX) &&
                Validator.validateTextField(address, Validator.ADDRESS_REGEX) &&
                Validator.validateTextField(phone, Validator.PHONE_REGEX);
    }

    public void saveCustomer() {
        if(isCustomerValid()){
            CustomerDAO customers = new CustomerDAO();
            String newPhone = "+38" + phone.getText();
            if(!newPhone.equals(customer.getPhone()) && customers.getByPhone(newPhone) != null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Вже існує замовник з номером телефону: " + newPhone);
                alert.showAndWait();
                return;
            }
            customer.setName(name.getText());
            customer.setSurname(surname.getText());
            customer.setPatronymic(patronymic.getText());
            customer.setAddress(address.getText());
            customer.setPhone(newPhone);
            customers.update(customer);
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
