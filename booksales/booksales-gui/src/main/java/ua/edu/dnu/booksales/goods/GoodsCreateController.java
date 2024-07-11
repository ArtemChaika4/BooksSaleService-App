package ua.edu.dnu.booksales.goods;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.GoodsDAO;
import ua.edu.dnu.booksales.dao.GoodsTypeDAO;
import ua.edu.dnu.booksales.model.Goods;
import ua.edu.dnu.booksales.model.GoodsStatus;
import ua.edu.dnu.booksales.model.GoodsType;
import ua.edu.dnu.booksales.validator.Validator;

public class GoodsCreateController {
    public TextField title;
    public TextField author;
    public TextField amount;
    public TextField price;
    public ComboBox<GoodsType> typeList;

    private boolean isGoodsValid(){
        return Validator.validateTextField(title, Validator.TITLE_REGEX) &&
                Validator.validateTextField(author, Validator.TITLE_REGEX) &&
                Validator.validateTextField(amount, Validator.AMOUNT_REGEX) &&
                Validator.validateTextField(price, Validator.PRICE_REGEX) &&
                typeList.getValue() != null;
    }

    @FXML
    private void addGoods() {
        if (isGoodsValid()) {
            Goods goods = new Goods(title.getText(), author.getText(),
                    Integer.parseInt(amount.getText()), Integer.parseInt(price.getText()),
                    GoodsStatus.AVAILABLE, typeList.getValue());
            new GoodsDAO().create(goods);
            MainPaneController.getInstance().setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(title, Validator.TITLE_REGEX);
        Validator.addTextFieldValidation(author, Validator.TITLE_REGEX);
        Validator.addTextFieldValidation(amount, Validator.AMOUNT_REGEX);
        Validator.addTextFieldValidation(price, Validator.PRICE_REGEX);
        ObservableList<GoodsType> typeObservableList =
                FXCollections.observableArrayList(new GoodsTypeDAO().getAll());
        typeList.setItems(typeObservableList);
        typeList.setConverter(new StringConverter<>() {
            @Override
            public String toString(GoodsType type) {
                if(type == null){
                    return "";
                }
                return type.getName() + " (" + type.getDescription() + ")";
            }
            @Override
            public GoodsType fromString(String s) {
                return null;
            }
        });
    }
}
