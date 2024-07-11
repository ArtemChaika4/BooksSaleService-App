package ua.edu.dnu.booksales.orders;

import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.OrderDAO;
import ua.edu.dnu.booksales.model.Order;
import ua.edu.dnu.booksales.model.OrderItem;
import ua.edu.dnu.booksales.model.OrderStatus;
import ua.edu.dnu.booksales.pdf.PDFConvertable;
import ua.edu.dnu.booksales.pdf.PDFCreator;

import java.io.File;

public class OrderEditController implements PDFConvertable {
    public TableView<OrderItem> table;
    public TableColumn<OrderItem, String> titleColumn;
    public TableColumn<OrderItem, String> authorColumn;
    public TableColumn<OrderItem, String> typeColumn;
    public TableColumn<OrderItem, String> amountColumn;
    public TableColumn<OrderItem, String> priceColumn;
    public TextField phone;
    public TextField name;
    public TextField address;
    public TextField total;
    public TextField number;
    public TextField date;
    public TextField status;
    public ToggleGroup statusGroup;
    public JFXRadioButton createdToggle;
    public JFXRadioButton processedToggle;
    public JFXRadioButton readyToggle;
    public Button completeButton;
    public Button returnButton;
    private Order order;

    public OrderEditController(){
        statusGroup = new ToggleGroup();
        completeButton = new Button();
        returnButton = new Button();
    }

    public void loadOrder(Order order){
        this.order = order;
        table.setItems(FXCollections.observableArrayList(order.getItems()));
        total.setText(String.valueOf(order.getOrderPrice()));
        phone.setText(order.getCustomer().getPhone());
        name.setText(order.getCustomer().getFullName());
        address.setText(order.getCustomer().getAddress());
        number.setText(String.valueOf(order.getId()));
        date.setText(order.getOrderDate().toString());
        status.setText(order.getStatus().getName());
        OrderStatus orderStatus = order.getStatus();
        statusGroup.selectToggle(getStatusToggle(orderStatus));
        if(orderStatus == OrderStatus.READY){
            completeButton.setDisable(false);
        }
        if(!orderStatus.isCanceled()){
            returnButton.setDisable(false);
        }
    }

    @FXML
    public void initialize(){
        titleColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getTitle()));
        authorColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getAuthor()));
        priceColumn.setCellValueFactory(item -> new SimpleStringProperty(
                String.valueOf(item.getValue().getTotalPrice())));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        typeColumn.setCellValueFactory(goods -> new SimpleStringProperty(
                goods.getValue().getGoods().getType() == null ? "—" :
                        goods.getValue().getGoods().getType().getName()));
    }

    private Toggle getStatusToggle(OrderStatus status){
        return switch (status) {
            case CREATED -> createdToggle;
            case PROCESSED -> processedToggle;
            case READY -> readyToggle;
            default -> null;
        };
    }

    private OrderStatus getSelectedStatus(){
        JFXRadioButton button = (JFXRadioButton) statusGroup.getSelectedToggle();
        if(button == null) {
            return null;
        }
        return switch (button.getId()){
            case "createdToggle" -> OrderStatus.CREATED;
            case "processedToggle" -> OrderStatus.PROCESSED;
            case "readyToggle" -> OrderStatus.READY;
            default -> null;
        };
    }

    public void onEditOrder() {
        OrderStatus status = getSelectedStatus();
        if(status == null){
            return;
        }
        if(status == order.getStatus()){
            MainPaneController.getInstance().setPrevContent();
        }else {
            changeOrderStatus(status);
        }
    }

    private final OrderDAO orders = new OrderDAO();

    private void changeOrderStatus(OrderStatus status){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Попередження про оновлення замовлення");
        alert.setContentText("Після виконання операції статус замовлення буде змінено на: " +
                status.getName());
        alert.showAndWait().ifPresent(b -> {
            orders.changeOrderStatus(order, status);
            MainPaneController.getInstance().setPrevContent();
        });
    }

    public void onCompleteOrder() {
        changeOrderStatus(OrderStatus.COMPLETED);
    }

    public void onReturnOrder() {
        changeOrderStatus(OrderStatus.RETURNED);
    }

    @Override
    public void saveToPDF(File directory) throws Exception {
        PDFCreator pdf = new PDFCreator(directory);
        pdf.create("замовлення №" + number.getText());
        OrderUtil.addOrderToPDF(pdf, order, table);
        pdf.saveAndShow();
    }
}
