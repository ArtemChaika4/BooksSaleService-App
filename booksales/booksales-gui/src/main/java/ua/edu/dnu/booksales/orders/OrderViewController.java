package ua.edu.dnu.booksales.orders;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.edu.dnu.booksales.dao.OrderDAO;
import ua.edu.dnu.booksales.model.Order;
import ua.edu.dnu.booksales.model.OrderItem;
import ua.edu.dnu.booksales.model.OrderLog;
import ua.edu.dnu.booksales.pdf.PDFConvertable;
import ua.edu.dnu.booksales.pdf.PDFCreator;

import java.io.File;
import java.util.List;


public class OrderViewController implements PDFConvertable {
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
    public TableView<OrderLog> logTable;
    public TableColumn<OrderLog, String> idColumn;
    public TableColumn<OrderLog, String> statusColumn;
    public TableColumn<OrderLog, String> timestampColumn;
    public TableColumn<OrderLog, String> emailColumn;
    public TableColumn<OrderLog, String> positionColumn;
    public TextField number;
    public TextField date;
    public TextField status;
    private Order order;

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
        OrderDAO orders = new OrderDAO();
        List<OrderLog> logs = orders.getOrderLogs(order);
        int id = 0;
        for (OrderLog log : logs) {
            log.setId(++id);
        }
        logTable.setItems(FXCollections.observableArrayList(logs));
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

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        statusColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getStatus() == null ? "—" :
                        log.getValue().getStatus().getName()));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        emailColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getEmployee() == null ? "—" :
                        log.getValue().getEmployee().getEmail()));
        positionColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getEmployee() == null ||
                        log.getValue().getEmployee().getPosition() == null ? "—" :
                        log.getValue().getEmployee().getPosition().getName()));
    }

    @Override
    public void saveToPDF(File directory) throws Exception {
        PDFCreator pdf = new PDFCreator(directory);
        pdf.create("замовлення №" + number.getText());
        OrderUtil.addOrderToPDF(pdf, order, table);
        pdf.addTitle("Історія замовлення");
        pdf.addTable(logTable);
        pdf.saveAndShow();
    }
}
