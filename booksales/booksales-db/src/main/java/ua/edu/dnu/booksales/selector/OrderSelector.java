package ua.edu.dnu.booksales.selector;

import ua.edu.dnu.booksales.model.GoodsStatus;
import ua.edu.dnu.booksales.model.GoodsType;
import ua.edu.dnu.booksales.model.OrderStatus;

public class OrderSelector implements Selector{
    private static final String ORDER_SELECT = "SELECT * FROM orders " +
            "LEFT JOIN customers ON orders.customer_id = customers.customer_id";
    private String likeParameter;
    private String orderBy;
    private OrderStatus status;

    public OrderSelector setOrderStatus(OrderStatus status){
        this.status = status;
        return this;
    }

    public OrderSelector setContains(String value){
        likeParameter = value;
        return this;
    }

    public OrderSelector setSortedBySurname(){
        return setSortedBy("surname");
    }

    public OrderSelector setSortedByName(){
        return setSortedBy("name");
    }

    public OrderSelector setSortedByDate(){
        return setSortedBy("order_date");
    }

    public OrderSelector setSortedByPrice(){
        return setSortedBy("order_price");
    }

    private OrderSelector setSortedBy(String columnName){
        orderBy = columnName;
        return this;
    }

    @Override
    public String getSqlQuery() {
        StringBuilder sql = new StringBuilder();
        if(status != null){
            String pref = Selector.pref(sql);
            sql.append(pref)
                    .append("order_status_id = ").append(status.getId());
        }
        if(likeParameter != null){
            String pref = Selector.pref(sql);
            sql.append(pref)
                    .append("(order_id LIKE '%").append(likeParameter).append("%' OR ")
                    .append("surname LIKE '%").append(likeParameter).append("%' OR ")
                    .append("name LIKE '%").append(likeParameter).append("%' OR ")
                    .append("phone LIKE '%").append(likeParameter).append("%' OR ")
                    .append("CONVERT(VARCHAR, order_date, 101) LIKE '%").append(likeParameter).append("%' OR ")
                    .append("order_price LIKE '%").append(likeParameter).append("%')");
        }
        if(orderBy != null){
            sql.append(" ORDER BY ").append(orderBy);
        }

        return ORDER_SELECT + sql;
    }
}
