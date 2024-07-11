package ua.edu.dnu.booksales.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private Customer customer;
    private Date orderDate;
    private OrderStatus status;
    private int orderPrice;
    private List<OrderItem> items;


    public Order(){
        id = 0;
        customer = new Customer();
        orderDate = new Date(System.currentTimeMillis());
        status = null;
        orderPrice = 0;
        items = new ArrayList<>();
    }

    public Order(Customer customer, Date orderDate, OrderStatus status, int orderPrice) {
        this.customer = customer;
        this.orderDate = orderDate;
        this.status = status;
        this.orderPrice = orderPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customer=" + customer +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", price=" + orderPrice +
                ", items=" + items +
                '}';
    }
}
