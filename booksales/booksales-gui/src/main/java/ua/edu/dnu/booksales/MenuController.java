package ua.edu.dnu.booksales;

public class MenuController {
    public void openGoods() {
        MainPaneController.getInstance().setRootContent("goods.fxml");
    }

    public void openOrdersOperator() {
        MainPaneController.getInstance().setRootContent("orders-operator.fxml");
    }

    public void openOrdersAdmin() {
        MainPaneController.getInstance().setRootContent("orders-admin.fxml");
    }

    public void openOrdersWarehouse(){
        MainPaneController.getInstance().setRootContent("orders-warehouse.fxml");
    }
    public void openAnalytic() {
        MainPaneController.getInstance().setRootContent("analytic.fxml");
    }

    public void openOrderAnalytic() {
        MainPaneController.getInstance().setRootContent("order-analytic.fxml");
    }

    public void openCustomers() {
        MainPaneController.getInstance().setRootContent("customers.fxml");
    }

    public void openEmployees() {
        MainPaneController.getInstance().setRootContent("employees.fxml");
    }

    public void openProfile() {
        MainPaneController.getInstance().setRootContent("profile.fxml");
    }

    public void openLogs() {
        MainPaneController.getInstance().setRootContent("logs.fxml");
    }

    public void openSettings() {
        MainPaneController.getInstance().setRootContent("settings.fxml");
    }

    public void openGoodsTypes() {
        MainPaneController.getInstance().setRootContent("types.fxml");
    }

    public void openOrderSearch() {
        MainPaneController.getInstance().setRootContent("order-search.fxml");
    }
}
