package ua.edu.dnu.booksales;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class PaneNode {
    private String fileName;
    private Parent root;
    private Object controller;

    public PaneNode(){}

    public PaneNode(String fileName){
        load(fileName);
    }

    public void load(String fileName){
        this.fileName = fileName;
        FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getClassLoader().getResource(fileName));
        try {
            root = fxmlLoader.load();
            controller = fxmlLoader.getController();
            root.setFocusTraversable(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getFileName() {
        return fileName;
    }

    public Parent getRoot() {
        return root;
    }

    public Object getController() {
        return controller;
    }

    public void update(){
        load(fileName);
    }
}
