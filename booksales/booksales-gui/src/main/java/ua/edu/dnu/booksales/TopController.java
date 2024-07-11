package ua.edu.dnu.booksales;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ua.edu.dnu.booksales.model.ActiveUser;
import ua.edu.dnu.booksales.model.Employee;
import ua.edu.dnu.booksales.pdf.PDFConvertable;

import java.io.File;
import java.io.IOException;

public class TopController {
    public Label fullNameLabel;
    public Label emailLabel;

    @FXML
    private void initialize() {
        Employee employee = ActiveUser.getInstance().getEmployee();
        fullNameLabel.setText(employee.getFullName());
        emailLabel.setText(employee.getEmail());
    }

    public void saveToPDF() {
        Object controller = MainPaneController.getInstance().getContent().getController();
        if(controller instanceof PDFConvertable pdfConvertable){
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directory = directoryChooser.showDialog(null);
            if(directory != null){
                try {
                    pdfConvertable.saveToPDF(directory);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void logOut(ActionEvent event) {
        changeScene(event);
    }

    private void changeScene(ActionEvent event){
        FXMLLoader fxmlLoader = new FXMLLoader(TopController.class.getClassLoader().getResource("login.fxml"));
        try {
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            double width = root.prefWidth(-1);
            double height = root.prefHeight(-1);
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void goBack() {
        MainPaneController.getInstance().setPrevContent();
    }

    public void onProfileClicked() {
        MainPaneController.getInstance().setNextContent("profile.fxml");
    }
}
