package ua.edu.dnu.booksales.validator;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;


public class Validator {
    public static final String DEFAULT_STYLE = "-fx-border-color: none;";
    public static final String ERROR_STYLE = "-fx-border-color: red;";
    public static final String VALID_STYLE = "-fx-border-color: green;";
    public static final String PRICE_REGEX = "^[1-9]\\d{1,3}$";
    public static final String AMOUNT_REGEX = "^[1-9]\\d{0,3}$";
    public static final String TITLE_REGEX = "^[А-ЯЇҐЄІа-яїієґ0-9 ]{3,50}$";
    public static final String NSP_REGEX = "^[А-ЯЇҐЄІ]('?[а-яїієґ]){1,30}$";
    public static final String ADDRESS_REGEX = "^[А-ЯЇҐЄІа-яїієґ0-9\\s]{5,50}$";
    public static final String PHONE_REGEX = "^[0-9]{10}$";
    public static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    public static final String PASSWORD_REGEX = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[_@#$%^&])(?=\\S+$).{8,256}";

    public static boolean validateDatePicker(DatePicker firstDate, DatePicker secondDate){
        LocalDate firstValue = firstDate.getValue();
        LocalDate secondValue = secondDate.getValue();
        if(firstValue == null || secondValue == null){
            firstDate.setStyle(DEFAULT_STYLE);
            secondDate.setStyle(DEFAULT_STYLE);
            return false;
        }
        boolean isBefore = firstValue.isBefore(secondValue);
        if(isBefore){
            firstDate.setStyle(VALID_STYLE);
            secondDate.setStyle(VALID_STYLE);
        }else {
            firstDate.setStyle(ERROR_STYLE);
            secondDate.setStyle(ERROR_STYLE);
        }
        return isBefore;
    }

    public static boolean validateTextField(TextField textField, String regex){
        String value = textField.getText();
        if(value == null || value.trim().isEmpty()){
            textField.setStyle(DEFAULT_STYLE);
            return false;
        }
        boolean isMatch = value.matches(regex);
        if(isMatch){
            textField.setStyle(VALID_STYLE);
        }else {
            textField.setStyle(ERROR_STYLE);
        }
        return isMatch;
    }

    public static void addTextFieldValidation(TextField textField, String regex){
        textField.textProperty()
                .addListener((observableValue, oldValue, newValue) -> validateTextField(textField, regex));
    }

    public static void addDatePickerValidation(DatePicker first, DatePicker second){
        first.valueProperty()
                .addListener((observableValue, oldValue, newValue) -> validateDatePicker(first, second));
        second.valueProperty()
                .addListener((observableValue, oldValue, newValue) -> validateDatePicker(first, second));
    }
}
