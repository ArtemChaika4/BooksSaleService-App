package ua.edu.dnu.booksales.customers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.CustomerDAO;
import ua.edu.dnu.booksales.model.Customer;
import ua.edu.dnu.booksales.pdf.PDFConvertable;
import ua.edu.dnu.booksales.pdf.PDFCreator;
import ua.edu.dnu.booksales.selector.CustomerSelector;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomersController implements Initializable, PDFConvertable {
    public TableColumn<Customer, String> nameColumn;
    public TableColumn<Customer, String> surnameColumn;
    public TableColumn<Customer, String> patronymicColumn;
    public TableColumn<Customer, String> addressColumn;
    public TableColumn<Customer, String> phoneColumn;
    public ComboBox<String> sortList;
    public TextField searchField;
    public TableView<Customer> table;
    private CustomerDAO customers;
    private CustomerSelector selector;

    public void onCreate() {
        MainPaneController.getInstance().setNextContent("customer-create.fxml");
    }

    public void onDelete() {
        Customer customer = table.getSelectionModel().getSelectedItem();
        if(customer != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попередження про видалення замовника");
            alert.setContentText("При видаленні замовника видаляються всі його замовлення\n" +
                    "Після виконання операції інформація про замовлення буде назавжди втрачена");
            alert.showAndWait().ifPresent(b -> {
                customers.delete(customer.getId());
                table.getItems().remove(customer);
            });
        }
    }

    @FXML
    private void onEdit(){
        Customer customer = table.getSelectionModel().getSelectedItem();
        if (customer != null) {
            MainPaneController.getInstance().setNextContent("customer-edit.fxml");
            ((CustomerEditController)MainPaneController.getInstance().getContent().getController()).loadCustomer(customer);
        }
    }

    private void update(){
        table.setItems(FXCollections.observableArrayList(
                customers.select(selector.getSqlQuery()))
        );
    }

    public void sort(){
        switch (sortList.getValue()) {
            case "Прізвище" -> selector.setSortedBySurname();
            case "Ім'я" -> selector.setSortedByName();
            case "По батькові" -> selector.setSortedByPatronymic();
        }
        update();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selector = new CustomerSelector();
        customers = new CustomerDAO();

        ObservableList<String> sorts = FXCollections.observableArrayList("Прізвище", "Ім'я", "По батькові");
        sortList.setItems(sorts);
        sortList.setValue("За замовченням");
        ObservableList<Customer> presenterObservableList =
                FXCollections.observableArrayList(customers.getAll());
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        patronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        table.setItems(presenterObservableList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            selector.setContains(newValue);
            update();
        });
    }

    @Override
    public void saveToPDF(File directory) throws Exception {
        PDFCreator pdf = new PDFCreator(directory);
        pdf.create("замовники");
        pdf.addTitle("Замовники");
        pdf.addText("Застосовані параметри фільтрації:\n");
        pdf.addText("Пошук за ключовим словом: ");
        String searchText = searchField.getText().isEmpty() ? "—" :
                "'" + searchField.getText() + "'";
        pdf.addText(searchText + "\n");
        pdf.addText("Параметри сортування: ");
        pdf.addText(sortList.getValue() + "\n");
        pdf.addTable(table);
        pdf.saveAndShow();
    }

}
