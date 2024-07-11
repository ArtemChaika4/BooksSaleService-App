package ua.edu.dnu.booksales.orders;

import com.itextpdf.text.DocumentException;
import javafx.scene.control.TableView;
import ua.edu.dnu.booksales.model.Customer;
import ua.edu.dnu.booksales.model.Order;
import ua.edu.dnu.booksales.model.OrderItem;
import ua.edu.dnu.booksales.pdf.PDFCreator;

public class OrderUtil {
    public static void addOrderToPDF(PDFCreator pdf, Order order, TableView<OrderItem> table)
            throws DocumentException
    {
        pdf.addTitle("Інформація про замовника");
        Customer customer = order.getCustomer();
        pdf.addText("Номер телефону: ");
        pdf.addText(customer.getPhone() + "\n");
        pdf.addText("ПІБ: ");
        pdf.addText(customer.getFullName() + "\n");
        pdf.addText("Адреса: ");
        pdf.addText(customer.getAddress() + "\n");
        pdf.addTitle("Інформація про замовлення");
        pdf.addText("Номер замовлення: ");
        pdf.addText(order.getId() + "\n");
        pdf.addText("Дата створення: ");
        pdf.addText(order.getOrderDate() + "\n");
        pdf.addText("Статус замовлення: ");
        pdf.addText(order.getStatus().getName() + "\n");
        pdf.addTitle("Товари, що входять до замовлення");
        pdf.addTable(table);
        pdf.addText("Сума до сплати: ");
        pdf.addText(order.getOrderPrice() + " грн\n");
    }
}
