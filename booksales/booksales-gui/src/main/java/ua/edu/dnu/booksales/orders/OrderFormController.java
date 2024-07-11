package ua.edu.dnu.booksales.orders;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.goods.GoodsController;
import ua.edu.dnu.booksales.model.Goods;
import ua.edu.dnu.booksales.model.GoodsStatus;
import ua.edu.dnu.booksales.model.GoodsType;
import ua.edu.dnu.booksales.model.OrderItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderFormController {
    public TableView<Goods> table;
    public TableColumn<Goods, String> titleColumn;
    public TableColumn<Goods, String> authorColumn;
    public TableColumn<Goods, String> typeColumn;
    public TableColumn<Goods, Integer> amountColumn;
    public TableColumn<Goods, Integer> priceColumn;
    public TextField searchField;
    public ComboBox<String> sortList;
    public ComboBox<GoodsType> typeList;
    public TableView<OrderItem> goodsTable;
    public TableColumn<OrderItem, String> goodsTitleColumn;
    public TableColumn<OrderItem, String> goodsAuthorColumn;
    public TableColumn<OrderItem, String> goodsTypeColumn;
    public TableColumn<OrderItem, String> goodsAmountColumn;
    public TableColumn<OrderItem, String> goodsPriceColumn;
    public TextField goodsTitle;
    public TextField goodsAuthor;
    public TextField goodsType;
    public TextField goodsPrice;
    public TextField goodsAmount;
    public Spinner<Integer> goodsNumber;
    public TextField goodsTotal;
    public TextField total;
    private GoodsController goodsController;
    private Goods selectedGoods;
    private Map<Integer, OrderItem> goodsItemMap;
    @FXML
    private void initialize() {
        goodsItemMap = new HashMap<>();
        goodsController = new GoodsController();
        goodsController.table = table;
        goodsController.titleColumn = titleColumn;
        goodsController.authorColumn = authorColumn;
        goodsController.typeColumn = typeColumn;
        goodsController.amountColumn = amountColumn;
        goodsController.priceColumn = priceColumn;
        goodsController.searchField = searchField;
        goodsController.typeList = typeList;
        goodsController.sortList = sortList;
        goodsController.statusList = new ComboBox<>();
        goodsController.initialize();
        goodsController.statusList.setValue(GoodsStatus.AVAILABLE);
        goodsController.setStatus();
        goodsTitleColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getTitle()));
        goodsAuthorColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getAuthor()));
        goodsPriceColumn.setCellValueFactory(item -> new SimpleStringProperty(
                String.valueOf(item.getValue().getTotalPrice())));
        goodsAmountColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        goodsTypeColumn.setCellValueFactory(goods -> new SimpleStringProperty(
                goods.getValue().getGoods().getType() == null ? "—" :
                        goods.getValue().getGoods().getType().getName()));
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> setGoods(newValue));
        goodsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> setGoods(newValue));
        goodsNumber.valueProperty().addListener(
                (obs, oldValue, newValue) -> setGoodsTotal());
    }

    private void setGoodsTotal() {
        int total = goodsNumber.getValue() * selectedGoods.getPrice();
        goodsTotal.setText(String.valueOf(total));
    }

    private void setGoods(OrderItem item){
        if(item == null){
            return;
        }
        setGoods(item.getGoods());
        table.getSelectionModel().clearSelection();
    }

    private void setGoods(Goods goods){
        if(goods == null){
            return;
        }
        selectedGoods = goods;
        goodsTitle.setText(goods.getTitle());
        goodsAuthor.setText(goods.getAuthor());
        goodsType.setText(goods.getType().getName());
        goodsPrice.setText(String.valueOf(goods.getPrice()));

        OrderItem item = goodsItemMap.get(goods.getId());
        int number = item == null ? 1 : item.getNumber();
        goodsAmount.setText(String.valueOf(goods.getAmount()));
        goodsNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, goods.getAmount()));
        goodsNumber.getValueFactory().setValue(number);
    }

    public void onRemove() {
        if(selectedGoods == null){
            return;
        }
        int goodsId = selectedGoods.getId();
        if(goodsItemMap.containsKey(goodsId)) {
            goodsItemMap.remove(goodsId);
            update();
            clear();
        }
    }

    private void clear() {
        selectedGoods = null;
        goodsTitle.setText(null);
        goodsAuthor.setText(null);
        goodsType.setText(null);
        goodsPrice.setText(null);
        goodsAmount.setText(null);
        goodsNumber.getEditor().clear();
        goodsNumber.setValueFactory(null);
        goodsTotal.setText(null);
    }

    public void onUpdate() {
        if(selectedGoods == null){
            return;
        }
        int goodsId = selectedGoods.getId();
        if(goodsItemMap.containsKey(goodsId)){
            int number = goodsNumber.getValue();
            OrderItem item = goodsItemMap.get(goodsId);
            item.setNumber(number);
            update();
        }
    }

    public void onAdd() {
        if(selectedGoods == null){
            return;
        }
        int goodsId = selectedGoods.getId();
        int number = goodsNumber.getValue();
        OrderItem item = goodsItemMap.get(goodsId);
        if(item == null){
            item = new OrderItem(selectedGoods, number);
            goodsItemMap.put(goodsId, item);
        }else {
            int amount = selectedGoods.getAmount() - item.getNumber();
            int maxNumber = Math.min(number, amount);
            item.addNumber(maxNumber);
            goodsNumber.getValueFactory().setValue(item.getNumber());
        }
        update();
    }

    private void update(){
        goodsTable.getItems().clear();
        goodsTable.setItems(FXCollections.observableArrayList(
                goodsItemMap.values()));
        total.setText(String.valueOf(getTotal()));
    }

    private int getTotal(){
        return goodsItemMap.values().stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }

    public void onConfirm() {
        if(goodsItemMap.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Список товарів порожній. Додайте товари до замовлення");
            alert.showAndWait();
            return;
        }
        List<OrderItem> items = new ArrayList<>(goodsItemMap.values());
        MainPaneController.getInstance().setNextContent("order-creation.fxml");
        ((OrderCreateController)MainPaneController.getInstance().getContent().getController())
                .setItems(items);
    }

    public void setType() {
        goodsController.setType();
    }

    public void sort() {
        goodsController.sort();
    }
}
