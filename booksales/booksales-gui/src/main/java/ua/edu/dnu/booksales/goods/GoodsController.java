package ua.edu.dnu.booksales.goods;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.GoodsDAO;
import ua.edu.dnu.booksales.dao.GoodsTypeDAO;
import ua.edu.dnu.booksales.model.Goods;
import ua.edu.dnu.booksales.model.GoodsStatus;
import ua.edu.dnu.booksales.model.GoodsType;
import ua.edu.dnu.booksales.pdf.PDFConvertable;
import ua.edu.dnu.booksales.pdf.PDFCreator;
import ua.edu.dnu.booksales.selector.GoodsSelector;

import java.io.File;

public class GoodsController implements PDFConvertable {
    public TableView<Goods> table;
    public TableColumn<Goods, String> titleColumn;
    public TableColumn<Goods, String> authorColumn;
    public TableColumn<Goods, String> typeColumn;
    public TableColumn<Goods, Integer> amountColumn;
    public TableColumn<Goods, Integer> priceColumn;
    public TextField searchField;
    public ComboBox<String> sortList;
    public ComboBox<GoodsType> typeList;
    public ComboBox<GoodsStatus> statusList;
    private GoodsDAO goodsDAO;
    private GoodsSelector selector;

    public void onCreate() {
        MainPaneController.getInstance().setNextContent("goods-create.fxml");
    }

    public void onDelete() {
        Goods goods = table.getSelectionModel().getSelectedItem();
        if(goods != null && goods.getStatus() != GoodsStatus.DELETED) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попередження про видалення товару");
            alert.setContentText("Після виконання операції література зникне з переліку наявних товарів");
            alert.showAndWait().ifPresent(b -> {
                goods.setStatus(GoodsStatus.DELETED);
                goodsDAO.update(goods);
                table.getItems().remove(goods);
            });
        }
    }
    @FXML
    private void onEdit(){
        Goods goods = table.getSelectionModel().getSelectedItem();
        if (goods != null) {
            MainPaneController.getInstance().setNextContent("goods-edit.fxml");
            ((GoodsEditController)MainPaneController.getInstance().getContent().getController())
                    .loadGoods(goods);
        }
    }

    private void update(){
        table.setItems(FXCollections.observableArrayList(
                goodsDAO.select(selector.getSqlQuery()))
        );
    }

    public void setType() {
        selector.setGoodsType(typeList.getValue());
        update();
    }

    public void setStatus(){
        selector.setGoodsStatus(statusList.getValue());
        update();
    }

    public void sort(){
        switch (sortList.getValue()) {
            case "Назва" -> selector.setSortedByTitle();
            case "Автор" -> selector.setSortedByAuthor();
            case "Ціна" -> selector.setSortedByPrice();
            case "Кількість" -> selector.setSortedByAmount();
        }
        update();
    }


    @FXML
    public void initialize() {
        selector = new GoodsSelector();
        goodsDAO = new GoodsDAO();

        ObservableList<String> sorts =
                FXCollections.observableArrayList("Назва", "Автор", "Ціна", "Кількість");
        sortList.setItems(sorts);
        sortList.setValue("За замовченням");
        ObservableList<Goods> goodsObservableList =
                FXCollections.observableArrayList(goodsDAO.getAll());
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        typeColumn.setCellValueFactory(goods -> new SimpleStringProperty(
                goods.getValue().getType() == null ? "—" :
                        goods.getValue().getType().getName() + " (" + goods.getValue().getType().getDescription() + ")"));
        table.setItems(goodsObservableList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            selector.setContains(newValue);
            update();
        });

        ObservableList<GoodsType> types = FXCollections.observableArrayList(
                new GoodsTypeDAO().getAll());
        typeList.setItems(types);
        typeList.setConverter(new StringConverter<>() {
            @Override
            public String toString(GoodsType type) {
                if(type == null){
                    return "";
                }
                return type.getName();
            }
            @Override
            public GoodsType fromString(String s) {
                return null;
            }
        });

        ObservableList<GoodsStatus> statuses = FXCollections.observableArrayList(GoodsStatus.values());
        statusList.setItems(statuses);
    }

    @Override
    public void saveToPDF(File directory) throws Exception {
        PDFCreator pdf = new PDFCreator(directory);
        pdf.create("література");
        pdf.addTitle("Література");
        pdf.addText("Застосовані параметри фільтрації:\n");
        pdf.addText("Пошук за ключовим словом: ");
        String searchText = searchField.getText().isEmpty() ? "—" :
                "'" + searchField.getText() + "'";
        pdf.addText(searchText + "\n");
        pdf.addText("Параметри сортування: ");
        pdf.addText(sortList.getValue() + "\n");
        pdf.addText("Тип літератури: ");
        String type = typeList.getValue() == null ? "Усі" :
                typeList.getValue().getName();
        pdf.addText(type + "\n");
        pdf.addText("Статус товару: ");
        String status = statusList.getValue() == null ? "Усі" :
                statusList.getValue().getName();
        pdf.addText(status + "\n");
        pdf.addTable(table);
        pdf.saveAndShow();
    }
}
