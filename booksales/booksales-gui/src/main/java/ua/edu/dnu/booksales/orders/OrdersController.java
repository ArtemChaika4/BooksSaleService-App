package ua.edu.dnu.booksales.orders;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.OrderDAO;
import ua.edu.dnu.booksales.model.*;
import ua.edu.dnu.booksales.pdf.PDFConvertable;
import ua.edu.dnu.booksales.pdf.PDFCreator;
import ua.edu.dnu.booksales.selector.OrderSelector;

import java.io.File;

public class OrdersController implements PDFConvertable {
    @FXML
    private TableColumn<Order, Integer> idColumn;
    @FXML
    private TableColumn<Order, String> nameColumn;
    @FXML
    private TableColumn<Order, String> phoneColumn;
    @FXML
    private TableColumn<Order, String> dateColumn;
    @FXML
    private TableColumn<Order, Integer> priceColumn;
    @FXML
    private TableView<Order> table;
    @FXML
    private ComboBox<String> sortList;
    @FXML
    private ComboBox<OrderStatus> statusList;
    @FXML
    private TextField searchField;
    OrderSelector selector;
    OrderDAO orders;

    @FXML
    public void sort(){
        switch (sortList.getValue()) {
            case "Ім'я" -> selector.setSortedByName();
            case "Прізвище" -> selector.setSortedBySurname();
            case "Дата" -> selector.setSortedByDate();
            case "Ціна" -> selector.setSortedByPrice();
        }
        update();
    }

    public void setStatus(){
        selector.setOrderStatus(statusList.getValue());
        update();
    }

    private void update(){
        table.setItems(FXCollections.observableArrayList(
                orders.select(selector.getSqlQuery()))
        );
    }

    @FXML
    public void onCreate() {
        MainPaneController.getInstance().setNextContent("order-form.fxml");
    }

    @FXML
    private void onView(){
        Order order = table.getSelectionModel().getSelectedItem();
        if (order != null) {
            MainPaneController.getInstance().setNextContent("order-view.fxml");
            ((OrderViewController)MainPaneController.getInstance().getContent().getController()).
                    loadOrder(order);
        }
    }

    @FXML
    private void onCancel()  {
        Order order = table.getSelectionModel().getSelectedItem();
        if (order != null && !order.getStatus().isCanceled()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попередження про скасування замовлення");
            alert.setContentText("Після виконання операції замовлення буде скасовано");
            alert.showAndWait().ifPresent(b -> {
                orders.changeOrderStatus(order, OrderStatus.CANCELED);
                update();
            });
        }
    }

    @FXML
    private void initialize() {
        selector = new OrderSelector();
        orders = new OrderDAO();
        ObservableList<String> sorts = FXCollections.observableArrayList(
                "Ім'я", "Прізвище", "Дата", "Ціна");
        sortList.setItems(sorts);
        sortList.setValue("За замовченням");

        ObservableList<Order> observableOrderList =
                FXCollections.observableArrayList(orders.getAll());
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(order -> new SimpleStringProperty(
                order.getValue().getCustomer().getFullName()));
        phoneColumn.setCellValueFactory(order -> new SimpleStringProperty(
                order.getValue().getCustomer().getPhone()));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("orderPrice"));
        table.setItems(observableOrderList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            selector.setContains(newValue);
            update();
        });

        ObservableList<OrderStatus> statuses = FXCollections.observableArrayList();
        Position position = ActiveUser.getInstance().getEmployee().getPosition();
        if(position.getPost() == Post.WAREHOUSEMAN){
            statuses.addAll(OrderStatus.CREATED, OrderStatus.PROCESSED, OrderStatus.READY);
            statusList.setValue(OrderStatus.CREATED);
        }else {
            statuses.addAll(OrderStatus.values());
        }
        statusList.setItems(statuses);
        setStatus();
    }


    public void onDelete() {
        Order order = table.getSelectionModel().getSelectedItem();
        if (order != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попередження про видалення замовлення");
            alert.setContentText("Після виконання операції замовлення та товари, що до нього входять " +
                    "будуть назавжди видалені без можливості відновлення");
            alert.showAndWait().ifPresent(b -> {
                orders.delete(order.getId());
                table.getItems().remove(order);
            });
        }
    }


    public void onEdit() {
        Order order = table.getSelectionModel().getSelectedItem();
        if (order != null) {
            MainPaneController.getInstance().setNextContent("order-edit.fxml");
            ((OrderEditController)(MainPaneController.getInstance().getContent().getController()))
                    .loadOrder(order);
        }
    }

    @Override
    public void saveToPDF(File directory) throws Exception {
        PDFCreator pdf = new PDFCreator(directory);
        pdf.create("замовлення");
        pdf.addTitle("Замовлення");
        pdf.addText("Застосовані параметри фільтрації:\n");
        pdf.addText("Пошук за ключовим словом: ");
        String searchText = searchField.getText().isEmpty() ? "—" :
                "'" + searchField.getText() + "'";
        pdf.addText(searchText + "\n");
        pdf.addText("Параметри сортування: ");
        pdf.addText(sortList.getValue() + "\n");
        pdf.addText("Статус замовлення: ");
        String status = statusList.getValue() == null ? "Усі" :
                statusList.getValue().getName();
        pdf.addText(status + "\n");
        pdf.addTable(table);
        pdf.saveAndShow();
    }
}
