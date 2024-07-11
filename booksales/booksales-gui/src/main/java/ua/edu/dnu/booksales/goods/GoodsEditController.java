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

public class GoodsEditController {
    public TextField title;
    public TextField author;
    public TextField amount;
    public TextField price;
    public ComboBox<GoodsType> typeList;
    public ComboBox<GoodsStatus> statusList;
    private Goods goods;

    public void loadGoods(Goods goods){
        this.goods = goods;
        reset();
    }

    public void reset(){
        title.setText(goods.getTitle());
        author.setText(goods.getAuthor());
        amount.setText(String.valueOf(goods.getAmount()));
        price.setText(String.valueOf(goods.getPrice()));
        typeList.setValue(goods.getType());
        statusList.setValue(goods.getStatus());
    }

    private boolean isGoodsValid(){
        return Validator.validateTextField(title, Validator.TITLE_REGEX) &&
                Validator.validateTextField(author, Validator.TITLE_REGEX) &&
                Validator.validateTextField(amount, Validator.AMOUNT_REGEX) &&
                Validator.validateTextField(price, Validator.PRICE_REGEX) &&
                typeList.getValue() != null && statusList.getValue() != null;
    }

    @FXML
    private void saveGoods() {
        if (isGoodsValid()) {
            goods.setTitle(title.getText());
            goods.setAuthor(author.getText());
            goods.setAmount(Integer.parseInt(amount.getText()));
            goods.setPrice(Integer.parseInt(price.getText()));
            goods.setStatus(statusList.getValue());
            goods.setType(typeList.getValue());
            new GoodsDAO().update(goods);
            MainPaneController.getInstance().setPrevContent();
        }
    }

    @FXML
    private void initialize() {
        Validator.addTextFieldValidation(title, Validator.TITLE_REGEX);
        Validator.addTextFieldValidation(author, Validator.TITLE_REGEX);
        Validator.addTextFieldValidation(amount, Validator.AMOUNT_REGEX);
        Validator.addTextFieldValidation(price, Validator.PRICE_REGEX);
        ObservableList<GoodsStatus> statusObservableList =
                FXCollections.observableArrayList(GoodsStatus.values());
        statusList.setItems(statusObservableList);
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
