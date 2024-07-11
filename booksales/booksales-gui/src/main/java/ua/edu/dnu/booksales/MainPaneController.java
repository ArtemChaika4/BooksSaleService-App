package ua.edu.dnu.booksales;


import javafx.application.HostServices;
import javafx.scene.layout.BorderPane;

public class MainPaneController {
    private BorderPane pane;
    private PaneNode top;
    private PaneNode menu;
    private PaneContent content;
    private static MainPaneController instance;

    static class PaneContent extends PaneNode{
        private PaneContent prev;
        public PaneContent(String fileName){
            super(fileName);
        }
    }

    private MainPaneController(){}

    public void setPane(BorderPane pane){
        this.pane = pane;
    }

    public void setMenu(String fxmlFileName){
        menu = new PaneNode(fxmlFileName);
        pane.setLeft(menu.getRoot());
    }

    public void setTop(String fxmlFileName){
        top = new PaneNode(fxmlFileName);
        pane.setTop(top.getRoot());
    }

    public void setNextContent(String fxmlFileName){
        PaneContent node = new PaneContent(fxmlFileName);
        node.prev = content;
        content = node;
        pane.setCenter(content.getRoot());
    }

    public void setRootContent(String fxmlFileName){
        content = new PaneContent(fxmlFileName);
        pane.setCenter(content.getRoot());
    }

    public void reset(){
        content = null;
        menu = null;
        top = null;
        pane = null;
    }

    public void setRootContent(){
        if(content == null){
            return;
        }
        PaneContent current = content;
        while (current.prev != null){
            current = current.prev;
        }
        content = current;
        update();
    }

    public void setPrevContent(){
        if(content == null || content.prev == null){
            return;
        }
        content = content.prev;
        if(content.prev == null){
            content.update();
        }
        pane.setCenter(content.getRoot());
    }

    public void update(){
        content.update();
        pane.setCenter(content.getRoot());
    }

    public static MainPaneController getInstance() {
        if(instance == null){
            instance = new MainPaneController();
        }
        return instance;
    }

    public PaneNode getContent(){
        return content;
    }

    public PaneNode getTop(){
        return top;
    }

    public PaneNode getMenu(){
        return menu;
    }

}
