package ua.edu.dnu.booksales.employees;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.EmployeeDAO;
import ua.edu.dnu.booksales.model.*;
import ua.edu.dnu.booksales.pdf.PDFConvertable;
import ua.edu.dnu.booksales.pdf.PDFCreator;
import ua.edu.dnu.booksales.selector.EmployeeSelector;

import java.io.File;

public class EmployeesController implements PDFConvertable {
    @FXML
    private TableColumn<Employee, String> nameColumn;
    @FXML
    private TableColumn<Employee, String> surnameColumn;
    @FXML
    private TableColumn<Employee, String> patronymicColumn;
    @FXML
    private TableColumn<Employee, String> emailColumn;
    @FXML
    private TableColumn<Employee, String> postColumn;
    @FXML
    private TableView<Employee> table;
    @FXML
    private ComboBox<String> sortList;
    @FXML
    private ComboBox<Position> postList;
    @FXML
    private TextField searchField;
    EmployeeSelector selector;
    EmployeeDAO employees;

    @FXML
    public void sort(){
        switch (sortList.getValue()) {
            case "Ім'я" -> selector.setSortedByName();
            case "Прізвище" -> selector.setSortedBySurname();
            case "По батькові" -> selector.setSortedByPatronymic();
        }
        update();
    }

    public void setPost(){
        selector.setPosition(postList.getValue());
        update();
    }

    private void update(){
        table.setItems(FXCollections.observableArrayList(
                employees.select(selector.getSqlQuery()))
        );
    }

    @FXML
    public void onCreate() {
        MainPaneController.getInstance().setNextContent("employee-create.fxml");
    }

    @FXML
    private void onEdit(){
        Employee employee = table.getSelectionModel().getSelectedItem();
        if (employee != null && employee.getId() != ActiveUser.getInstance().getEmployee().getId()) {
            MainPaneController.getInstance().setNextContent("employee-edit.fxml");
            ((EmployeesEditController)MainPaneController.getInstance().getContent().getController()).
                    loadEmployee(employee);
        }
    }

    @FXML
    private void onDelete()  {
        Employee employee = table.getSelectionModel().getSelectedItem();
        if (employee != null && employee.getId() != ActiveUser.getInstance().getEmployee().getId()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попередження про видалення працівника");
            alert.setContentText("Після виконання операції працівника буде видалено без можливості відновлення");
            alert.showAndWait().ifPresent(b -> {
                employees.delete(employee.getId());
                table.getItems().remove(employee);
            });
        }
    }

    @FXML
    private void initialize() {
        selector = new EmployeeSelector();
        employees = new EmployeeDAO();

        ObservableList<String> sorts = FXCollections.observableArrayList(
                "Ім'я", "Прізвище", "По батькові");
        sortList.setItems(sorts);
        sortList.setValue("За замовченням");

        ObservableList<Employee> observableEmployeeList =
                FXCollections.observableArrayList(employees.getAll());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        patronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        postColumn.setCellValueFactory(employee -> new SimpleStringProperty(
                employee.getValue().getPosition() == null ? "—" :
                        employee.getValue().getPosition().getName()));
        table.setItems(observableEmployeeList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            selector.setContains(newValue);
            update();
        });

        ObservableList<Position> positions = FXCollections.observableArrayList(employees.getPositions());
        postList.setItems(positions);
        postList.setConverter(new StringConverter<>() {
            @Override
            public String toString(Position position) {
                if(position == null){
                    return "";
                }
                return position.getName();
            }
            @Override
            public Position fromString(String s) {
                return null;
            }
        });
    }

    @Override
    public void saveToPDF(File directory) throws Exception {
        PDFCreator pdf = new PDFCreator(directory);
        pdf.create("працівники");
        pdf.addTitle("Працівники");
        pdf.addText("Застосовані параметри фільтрації:\n");
        pdf.addText("Пошук за ключовим словом: ");
        String searchText = searchField.getText().isEmpty() ? "—" :
                "'" + searchField.getText() + "'";
        pdf.addText(searchText + "\n");
        pdf.addText("Параметри сортування: ");
        pdf.addText(sortList.getValue() + "\n");
        pdf.addText("Посада працівника: ");
        String position = postList.getValue() == null ? "Усі" :
                postList.getValue().getName();
        pdf.addText(position + "\n");
        pdf.addTable(table);
        pdf.saveAndShow();
    }
}
