package ua.edu.dnu.booksales;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public BorderPane mainPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainPaneController mainPaneController = MainPaneController.getInstance();
        mainPaneController.reset();
        mainPaneController.setPane(mainPane);
        mainPaneController.setTop("top.fxml");
    }
}
