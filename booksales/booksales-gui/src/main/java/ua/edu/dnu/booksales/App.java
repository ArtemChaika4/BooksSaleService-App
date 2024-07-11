package ua.edu.dnu.booksales;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import ua.edu.dnu.booksales.dao.CustomerDAO;
import ua.edu.dnu.booksales.dao.EmployeeDAO;
import ua.edu.dnu.booksales.model.ActiveUser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class App extends Application
{
    public static void main( String[] args )
    {
        launch();
    }


    @Override
    public void start(Stage stage) throws Exception {
        stage.setUserData(getHostServices());
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getClassLoader().getResource("welcome.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, root.prefWidth(-1), root.prefHeight(-1));
        stage.setTitle("Курсова робота");
        stage.setScene(scene);
        stage.show();
    }

}
