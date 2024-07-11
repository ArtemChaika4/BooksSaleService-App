package ua.edu.dnu.booksales.analytics;

import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import ua.edu.dnu.booksales.dao.CustomerDAO;
import ua.edu.dnu.booksales.dao.EmployeeDAO;
import ua.edu.dnu.booksales.dao.GoodsDAO;
import ua.edu.dnu.booksales.dao.GoodsTypeDAO;
import ua.edu.dnu.booksales.pdf.PDFConvertable;
import ua.edu.dnu.booksales.pdf.PDFCreator;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AnalyticController implements Initializable, PDFConvertable {
    public Label averagePriceLabel;
    public Label goodsCountLabel;
    public Label typeCountLabel;
    public Label customerCountLabel;
    public Label employeeCountLabel;
    public PieChart typeChart;
    public PieChart statusChart;
    public PieChart positionChart;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GoodsDAO goods = new GoodsDAO();
        GoodsTypeDAO types = new GoodsTypeDAO();
        CustomerDAO customers = new CustomerDAO();
        EmployeeDAO employees = new EmployeeDAO();

        double averagePrice = goods.getAverageGoodsPrice();
        int goodsCount = goods.getGoodsCount();
        int typeCount = types.getGoodsTypeCount();
        int customerCount = customers.getCustomersCount();
        int employeeCount = employees.getEmployeesCount();

        averagePriceLabel.setText(String.format("%.2f", averagePrice));
        goodsCountLabel.setText(String.valueOf(goodsCount));
        typeCountLabel.setText(String.valueOf(typeCount));
        customerCountLabel.setText(String.valueOf(customerCount));
        employeeCountLabel.setText(String.valueOf(employeeCount));

        goods.getGoodsCountByTypes().forEach((k, v) ->
                typeChart.getData().add(new PieChart.Data(k.getName() + " - " + v,  v)));
        goods.getGoodsCountByStatus().forEach((k, v) ->
                statusChart.getData().add(new PieChart.Data(k.getName() + " - " + v, v)));
        employees.getEmployeesCountByPosition().forEach((k, v) ->
                positionChart.getData().add(new PieChart.Data(k.getName() + " - " + v, v)));
        typeChart.setTitle("Діаграма розподілу літератури за типом");
        statusChart.setTitle("Діаграма розподілу літератури за статусом");
        positionChart.setTitle("Діаграма розподілу працівників за посадою");
    }

    @Override
    public void saveToPDF(File directory) throws Exception {
        PDFCreator pdf = new PDFCreator(directory);
        pdf.create("розділ аналітики");
        pdf.addTitle("Управління підприємством");
        pdf.addText("Середня ціна товарів: ");
        pdf.addText(averagePriceLabel.getText() + " грн\n");
        pdf.addText("Загальна кількість товарів: ");
        pdf.addText(goodsCountLabel.getText() + "\n");
        pdf.addText("Загальна кількість типів літератури: ");
        pdf.addText(typeCountLabel.getText() + "\n");
        pdf.addText("Загальна кількість замовників: ");
        pdf.addText(customerCountLabel.getText() + "\n");
        pdf.addText("Загальна кількість працівників: ");
        pdf.addText(employeeCountLabel.getText() + "\n");
        pdf.addTitle("Аналітика розділу літератури");
        pdf.addChart(typeChart);
        pdf.addChart(statusChart);
        pdf.addTitle("Аналітика розділу працівників");
        pdf.addChart(positionChart);
        pdf.saveAndShow();
    }
}
