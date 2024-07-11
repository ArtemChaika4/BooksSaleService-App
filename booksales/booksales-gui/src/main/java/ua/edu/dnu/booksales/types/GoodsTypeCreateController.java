package ua.edu.dnu.booksales.types;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.GoodsTypeDAO;
import ua.edu.dnu.booksales.model.GoodsType;
import ua.edu.dnu.booksales.validator.Validator;

public class GoodsTypeCreateController {
    public TextField name;
    public TextArea description;

    private boolean isTypeValid(){
        return Validator.validateTextField(name, Validator.TITLE_REGEX);
    }

    public void addType() {
        if(isTypeValid()){
            GoodsType type = new GoodsType(name.getText(), description.getText());
            new GoodsTypeDAO().create(type);
            MainPaneController.getInstance().setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(name, Validator.TITLE_REGEX);
    }
}
