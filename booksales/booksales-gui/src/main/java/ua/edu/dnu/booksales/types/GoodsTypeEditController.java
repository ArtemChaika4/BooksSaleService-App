package ua.edu.dnu.booksales.types;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.GoodsTypeDAO;
import ua.edu.dnu.booksales.model.GoodsType;
import ua.edu.dnu.booksales.validator.Validator;

public class GoodsTypeEditController {
    public TextField name;
    public TextArea description;
    private GoodsType type;

    public void loadType(GoodsType type){
        this.type = type;
        reset();
    }

    public void reset() {
        name.setText(type.getName());
        description.setText(type.getDescription());
    }

    private boolean isTypeValid(){
        return Validator.validateTextField(name, Validator.TITLE_REGEX);
    }

    public void saveType() {
        if(isTypeValid()){
            type.setName(name.getText());
            type.setDescription(description.getText());
            new GoodsTypeDAO().update(type);
            MainPaneController.getInstance().setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(name, Validator.TITLE_REGEX);
    }
}
