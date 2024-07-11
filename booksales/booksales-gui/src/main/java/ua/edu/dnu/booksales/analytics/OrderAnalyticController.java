package ua.edu.dnu.booksales.analytics;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import ua.edu.dnu.booksales.dao.OrderDAO;
import ua.edu.dnu.booksales.model.OrderItem;
import ua.edu.dnu.booksales.pdf.PDFConvertable;
import ua.edu.dnu.booksales.pdf.PDFCreator;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderAnalyticController implements Initializable, PDFConvertable {
    public PieChart pieChart;
    public StackPane chartPane;
    public BarChart<String, Number> chart;
    public Label totalLabel;
    public Label averagePriceLabel;
    public Label completedCountLabel;
    public Label ordersCountLabel;
    public TableView<OrderItem> table;
    public TableColumn<OrderItem, Integer> idColumn;
    public TableColumn<OrderItem, String> titleColumn;
    public TableColumn<OrderItem, String> authorColumn;
    public TableColumn<OrderItem, String> typeColumn;
    public TableColumn<OrderItem, String> priceColumn;
    public TableColumn<OrderItem, Integer> numberColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getTitle()));
        authorColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getAuthor()));
        typeColumn.setCellValueFactory(item -> new SimpleStringProperty(
                item.getValue().getGoods().getType() == null ? "—" :
                        item.getValue().getGoods().getType().getName()));
        priceColumn.setCellValueFactory(item -> new SimpleStringProperty(
                String.valueOf(item.getValue().getGoods().getPrice())));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));

        OrderDAO orders = new OrderDAO();
        AtomicInteger id = new AtomicInteger();
        orders.getOrderTopGoods().forEach((k, v) -> {
            OrderItem item = new OrderItem(id.incrementAndGet(), k, v);
            table.getItems().add(item);
        });
        orders.getOrderByStatus().forEach((k, v) ->
                pieChart.getData().add(new PieChart.Data(k.getName() + " - " + v, v)));
        pieChart.setTitle("Діаграма розподілу замовлень за статусом");
        DateFormat dateFormat = new SimpleDateFormat("MM-dd");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Кількість виконаних замовлень за поточний місяць");
        orders.getStatisticByMonth(LocalDate.now().getMonth())
                .forEach((k, v) -> series.getData().add(new XYChart.Data<>(dateFormat.format(k), v)));
        chart.getData().add(series);
        chart.setTitle("Гістограма продажів за місяць");
        int totalProfit = orders.getTotalProfit();
        double averagePrice = orders.getAverageOrdersPrice();
        int completedCount = orders.getCompletedCount();
        int ordersCount = orders.getOrdersCount();
        totalLabel.setText(String.valueOf(totalProfit));
        averagePriceLabel.setText(String.format("%.2f", averagePrice));
        completedCountLabel.setText(String.valueOf(completedCount));
        ordersCountLabel.setText(String.valueOf(ordersCount));
    }

    @Override
    public void saveToPDF(File directory) throws Exception {
        PDFCreator pdf = new PDFCreator(directory);
        pdf.create("розділ реалізації");
        pdf.addTitle("Статистична інформація за замовленнями");
        pdf.addText("");
        pdf.addText("Загальний прибуток від продажу літератури: ");
        pdf.addText(totalLabel.getText() + " грн\n");
        pdf.addText("Середня ціна замовлень: ");
        pdf.addText(averagePriceLabel.getText() + "грн\n");
        pdf.addText("Кількість виконаних замовлень: ");
        pdf.addText(completedCountLabel.getText() + "\n");
        pdf.addText("Загальна кількість замовлень: ");
        pdf.addText(ordersCountLabel.getText() + "\n");
        pdf.addChart(pieChart);
        pdf.addTitle("Топ товарів за кількістю виконаних замовлень\n");
        pdf.addText("");
        pdf.addTable(table);
        pdf.addTitle("Аналітика за реалізацією літератури");
        pdf.addChart(chart);
        pdf.saveAndShow();
    }
}
