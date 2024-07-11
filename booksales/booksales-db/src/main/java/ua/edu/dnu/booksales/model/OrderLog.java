package ua.edu.dnu.booksales.model;

import java.sql.Timestamp;

public class OrderLog {
    private int id;
    private Order order;
    private OrderStatus status;
    private Employee employee;
    private Timestamp timestamp;

    public OrderLog(){
        id = 0;
        order = new Order();
        employee = new Employee();
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    public OrderLog(Order order, OrderStatus status){
        this.order = order;
        this.status = status;
        employee = ActiveUser.getInstance().getEmployee();
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    public OrderLog(Order order, OrderStatus status, Employee employee, Timestamp timestamp) {
        this.order = order;
        this.status = status;
        this.employee = employee;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "OrderLog{" +
                "id=" + id +
                ", order=" + order +
                ", status=" + status +
                ", employee=" + employee +
                ", timestamp=" + timestamp +
                '}';
    }
}
