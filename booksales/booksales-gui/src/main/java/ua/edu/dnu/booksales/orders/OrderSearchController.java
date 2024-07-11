package ua.edu.dnu.booksales.orders;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ua.edu.dnu.booksales.MainPaneController;
import ua.edu.dnu.booksales.dao.OrderDAO;
import ua.edu.dnu.booksales.model.ActiveUser;
import ua.edu.dnu.booksales.model.Order;
import ua.edu.dnu.booksales.model.Position;
import ua.edu.dnu.booksales.model.Post;

public class OrderSearchController {
    public TextField number;
    public Label errorLabel;
    private final OrderDAO orders = new OrderDAO();
    public void searchOrder() {
        try {
            int orderId = Integer.parseInt(number.getText());
            Order order = orders.getOrderById(orderId);
            if(order == null){
                errorLabel.setText("За вказаним номером не знайдено замовлення");
            }else {
                setNextScene(order);
                errorLabel.setText("");
                number.setText("");
            }
        }catch (Exception e){
            errorLabel.setText("Виникла помилка при пошуку замовлення");
        }
    }

    private void setNextScene(Order order){
        Position position = ActiveUser.getInstance().getEmployee().getPosition();
        if(position == null){
            return;
        }
        if(position.getPost() == Post.WAREHOUSEMAN){
            MainPaneController.getInstance().setNextContent("order-close.fxml");
            ((OrderEditController)MainPaneController.getInstance().getContent().getController())
                    .loadOrder(order);
        }else {
            MainPaneController.getInstance().setNextContent("order-view.fxml");
            ((OrderViewController)MainPaneController.getInstance().getContent().getController())
                    .loadOrder(order);
        }
    }
}
