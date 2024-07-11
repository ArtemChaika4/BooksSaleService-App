package ua.edu.dnu.booksales.logs;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.edu.dnu.booksales.dao.EventLogDAO;
import ua.edu.dnu.booksales.model.DBTable;
import ua.edu.dnu.booksales.model.EventLog;
import ua.edu.dnu.booksales.pdf.PDFConvertable;
import ua.edu.dnu.booksales.pdf.PDFCreator;
import ua.edu.dnu.booksales.selector.EventLogSelector;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

public class LogsController implements PDFConvertable {
    public TextField searchField;
    public TableView<EventLog> table;
    public TableColumn<EventLog, Integer> idColumn;
    public TableColumn<EventLog, String> activityColumn;
    public TableColumn<EventLog, Timestamp> timestampColumn;
    public TableColumn<EventLog, String> emailColumn;
    public TableColumn<EventLog, String> tableColumn;
    public TableColumn<EventLog, String> positionColumn;
    public DatePicker afterDate;
    public DatePicker beforeDate;
    public ComboBox<DBTable> tableList;
    private EventLogDAO logs;
    private EventLogSelector selector;

    private void update(){
        table.setItems(FXCollections.observableArrayList(
                logs.select(selector.getSqlQuery()))
        );
    }

    public void initialize() {
        selector = new EventLogSelector();
        logs = new EventLogDAO();

        ObservableList<EventLog> logObservableList = FXCollections.observableArrayList(logs.getAll());
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        activityColumn.setCellValueFactory(new PropertyValueFactory<>("activity"));
        tableColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getTable() == null ? "—" :
                        log.getValue().getTable().getName()));
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        emailColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getEmployee() == null ? "—" :
                        log.getValue().getEmployee().getEmail()));
        positionColumn.setCellValueFactory(log -> new SimpleStringProperty(
                log.getValue().getEmployee() == null ||
                        log.getValue().getEmployee().getPosition() == null ? "—" :
                        log.getValue().getEmployee().getPosition().getName()));
        table.setItems(logObservableList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            selector.setContains(newValue);
            update();
        });
        afterDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            Date date = newValue == null ? null : Date.valueOf(newValue);
            selector.setAfterDate(date);
            update();
        });
        beforeDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            Date date = newValue == null ? null : Date.valueOf(newValue);
            selector.setBeforeDate(date);
            update();
        });
        ObservableList<DBTable> tables = FXCollections.observableArrayList(DBTable.values());
        tableList.setItems(tables);
        afterDate.setValue(LocalDate.now());
    }

    public void reset() {
        afterDate.setValue(null);
        beforeDate.setValue(null);
        searchField.setText(null);
        tableList.setValue(null);
    }

    public void setTable() {
        selector.setTable(tableList.getValue());
        update();
    }

    @Override
    public void saveToPDF(File directory) throws Exception {
        PDFCreator pdf = new PDFCreator(directory);
        pdf.create("журнал подій");
        pdf.addTitle("Журнал подій");
        pdf.addText("Застосовані параметри фільтрації:\n");
        pdf.addText("Пошук за ключовим словом: ");
        String searchText = searchField.getText().isEmpty() ? "—" :
                "'" + searchField.getText() + "'";
        pdf.addText(searchText + "\n");
        pdf.addText("Фільтрація за датою:\n");
        pdf.addText("   від: ");
        pdf.addText(getDateValue(afterDate.getValue()) + "\n");
        pdf.addText("   до: ");
        pdf.addText(getDateValue(beforeDate.getValue()) + "\n");
        pdf.addText("Розділ (таблиці) де відбулися зміни: ");
        String section = tableList.getValue() == null ? "Усі" :
                tableList.getValue().getName();
        pdf.addText(section + "\n");
        pdf.addTable(table);
        pdf.saveAndShow();
    }

    private String getDateValue(LocalDate date){
        return date == null ? "—" : Date.valueOf(date).toString();
    }
}
