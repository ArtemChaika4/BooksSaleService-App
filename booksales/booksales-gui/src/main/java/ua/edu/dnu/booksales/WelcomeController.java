package ua.edu.dnu.booksales;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    @FXML
    private void changeScene(ActionEvent event){
        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getClassLoader().getResource("login.fxml"));
        try {
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            double width = root.prefWidth(-1);
            double height = root.prefHeight(-1);
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
