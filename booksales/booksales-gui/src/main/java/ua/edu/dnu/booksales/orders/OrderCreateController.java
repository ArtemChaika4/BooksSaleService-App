package ua.edu.dnu.booksales.orders;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.CustomerDAO;
import ua.edu.dnu.booksales.dao.OrderDAO;
import ua.edu.dnu.booksales.model.Customer;
import ua.edu.dnu.booksales.model.Order;
import ua.edu.dnu.booksales.model.OrderItem;
import ua.edu.dnu.booksales.model.OrderStatus;
import ua.edu.dnu.booksales.pdf.PDFCreator;
import ua.edu.dnu.booksales.validator.Validator;

import java.io.File;
import java.sql.Date;
import java.util.List;

public class OrderCreateController {
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
    public VBox nameBox;
    public VBox addressBox;
    private List<OrderItem> items;
    private int orderPrice;
    private Customer customer;


    public void setItems(List<OrderItem> items){
        this.items = items;
        orderPrice = items.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
        table.setItems(FXCollections.observableArrayList(items));
        total.setText(String.valueOf(orderPrice));
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
        Validator.addTextFieldValidation(phone, Validator.PHONE_REGEX);
    }

    public void onCreateOrder() {
        if(customer == null){
            showAlert("Замовлення не містить замовника.\n" +
                    "Введіть номер телефону замовника та натисніть кнопку пошуку");
            return;
        }
        Order order = new Order(customer,
                new Date(System.currentTimeMillis()),
                OrderStatus.CREATED, orderPrice);
        order.setItems(items);
        OrderDAO orders = new OrderDAO();
        try {
            orders.create(order);
            createOrderPDF(order);
        }catch (Exception e){
            showAlert("При виконанні операції виникла помилка." +
                    "Замовлення не було створено: " + e.getMessage());
        }
        MainPaneController.getInstance().setRootContent();
    }

    private void createOrderPDF(Order order){
        String path = System.getProperty("user.home") + "/Downloads";
        PDFCreator pdf = new PDFCreator(new File(path));
        try {
            pdf.create("замовлення №" + order.getId());
            OrderUtil.addOrderToPDF(pdf, order, table);
            pdf.saveAndShow();
        } catch (Exception e) {
            showAlert("Помилка при створенні квитанції на замовлення");
        }
    }

    public void onCreateCustomer() {
        MainPaneController.getInstance().setNextContent("customer-create.fxml");
    }

    private boolean isValidPhone(){
        return Validator.validateTextField(phone, Validator.PHONE_REGEX);
    }

    private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void searchCustomer() {
        if(!isValidPhone()){
            showAlert("Некоректно введений номер телефону");
            reset();
            return;
        }
        CustomerDAO customers = new CustomerDAO();
        String newPhone = "+38" + phone.getText();
        Customer newCustomer = customers.getByPhone(newPhone);
        if(newCustomer == null){
            showAlert("Не знайдено замовника з вказаним номером телефону: " + newPhone +
                    "\nНатисніть кнопку 'Зареєструвати замовника' для реєстрації нового замовника");
            reset();
            return;
        }
        customer = newCustomer;
        nameBox.setDisable(false);
        addressBox.setDisable(false);
        name.setText(customer.getFullName());
        address.setText(customer.getAddress());
    }

    private void reset(){
        if(customer != null){
            phone.setText(customer.getPhone().substring(3));
            name.setText(customer.getFullName());
            address.setText(customer.getAddress());
        }
    }
}
