package ua.edu.dnu.booksales.types;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.GoodsTypeDAO;
import ua.edu.dnu.booksales.model.GoodsType;
import ua.edu.dnu.booksales.pdf.PDFConvertable;
import ua.edu.dnu.booksales.pdf.PDFCreator;
import ua.edu.dnu.booksales.selector.GoodsTypeSelector;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class GoodsTypeController implements Initializable, PDFConvertable {
    public TextField searchField;
    public TableView<GoodsType> table;
    public TableColumn<GoodsType, String> nameColumn;
    public TableColumn<GoodsType, String> descriptionColumn;
    public ComboBox<String> sortList;
    private GoodsTypeDAO types;
    private GoodsTypeSelector selector;

    public void onCreate() {
        MainPaneController.getInstance().setNextContent("type-create.fxml");
    }

    public void sort() {
        selector.setSortedByName();
        update();
    }

    private void update(){
        table.setItems(FXCollections.observableArrayList(
                types.select(selector.getSqlQuery()))
        );
    }

    public void onEdit() {
        GoodsType type = table.getSelectionModel().getSelectedItem();
        if(type != null) {
            MainPaneController.getInstance().setNextContent("type-edit.fxml");
            ((GoodsTypeEditController)MainPaneController.getInstance().getContent().getController())
                    .loadType(type);
        }
    }

    public void onDelete() {
        GoodsType type = table.getSelectionModel().getSelectedItem();
        if(type != null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Попередження про видалення типу літератури");
            alert.setContentText("Після виконання операції тип літератури буде видалено без можливості відновлення");
            alert.showAndWait().ifPresent(b -> {
                types.delete(type.getId());
                table.getItems().remove(type);
            });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selector = new GoodsTypeSelector();
        types = new GoodsTypeDAO();

        ObservableList<String> sorts = FXCollections.observableArrayList("Назва");
        sortList.setItems(sorts);
        sortList.setValue("За замовченням");
        ObservableList<GoodsType> typeObservableList =
                FXCollections.observableArrayList(types.getAll());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        table.setItems(typeObservableList);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            selector.setContains(newValue);
            update();
        });
    }

    @Override
    public void saveToPDF(File directory) throws Exception {
        PDFCreator pdf = new PDFCreator(directory);
        pdf.create("типи літератури");
        pdf.addTitle("Типи літератури");
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
