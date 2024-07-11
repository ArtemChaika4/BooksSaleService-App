package ua.edu.dnu.booksales.dao;

import ua.edu.dnu.booksales.model.*;

import java.sql.*;
import java.sql.Date;
import java.time.Month;
import java.util.*;

public class OrderDAO implements InterfaceDAO<Order> {
    private final EventLogDAO logs = new EventLogDAO();

    private void createLog(String activity){
        logs.create(new EventLog(activity, DBTable.ORDERS));
    }

    @Override
    public void create(Order item) {
        try (Connection connection = Service.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            createOrder(item, connection);
            createOrderItems(item, connection);
            createOrderLog(new OrderLog(item, OrderStatus.CREATED), connection);
            connection.commit();
            connection.setAutoCommit(autoCommit);
            createLog("Додано нове замовлення: " + item);
        } catch (SQLException e) {
            createLog("Помилка при додаванні замовлення: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<OrderLog> getOrderLogs(Order order){
        List<OrderLog> orderLogs = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String READ_LOGS =
                    "SELECT * FROM order_log " +
                            "LEFT JOIN employees ON order_log.employee_id = employees.employee_id " +
                            "LEFT JOIN positions ON employees.position_id = positions.position_id " +
                            "WHERE order_id=?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(READ_LOGS);
            preparedStatement.setInt(1, order.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                OrderLog log = getOrderLogFromResultSet(resultSet);
                log.setOrder(order);
                orderLogs.add(log);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orderLogs;
    }

    public static OrderLog getOrderLogFromResultSet(ResultSet resultSet) throws SQLException {
        OrderLog log = new OrderLog();
        log.setId(resultSet.getInt("order_log_id"));
        log.setStatus(OrderStatus.getById(
                resultSet.getInt("order_status_id")));
        log.setTimestamp(resultSet.getTimestamp("timestamp"));
        Employee employee = null;
        resultSet.getInt("employee_id");
        if(!resultSet.wasNull()) {
            employee = EmployeeDAO.getEmployeeFromResultSet(resultSet);
        }
        log.setEmployee(employee);
        return log;
    }

    private void createOrderLog(OrderLog orderLog, Connection connection) throws SQLException {
        final String CREATE_ORDER_LOG = "INSERT INTO order_log" +
                "(order_id, order_status_id, employee_id, timestamp) " +
                "VALUES(?, ?, ?, ?)";
        PreparedStatement preparedStatement =
                connection.prepareStatement(CREATE_ORDER_LOG);
        preparedStatement.setInt(1, orderLog.getOrder().getId());
        preparedStatement.setInt(2, orderLog.getStatus().getId());
        preparedStatement.setInt(3, orderLog.getEmployee().getId());
        preparedStatement.setTimestamp(4, orderLog.getTimestamp());
        preparedStatement.executeUpdate();
    }

    private void returnOrderItems(Order order, Connection connection) throws SQLException {
        final String UPDATE_GOODS_AMOUNT = "UPDATE goods SET amount+=? WHERE goods_id=?";
        PreparedStatement preparedStatement =
                connection.prepareStatement(UPDATE_GOODS_AMOUNT);
        for (OrderItem item: order.getItems()) {
            preparedStatement.setInt(1, item.getNumber());
            preparedStatement.setInt(2, item.getGoods().getId());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
    }

    private void updateGoodsAmount(OrderItem item, Connection connection) throws SQLException {
        int goodsId = item.getGoods().getId();
        int statusId = GoodsStatus.AVAILABLE.getId();
        final String SELECT_GOODS_AMOUNT = "SELECT amount, goods_status_id FROM goods " +
                "WHERE goods_id=" + goodsId + " AND goods_status_id=" + statusId;
        Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = stmt.executeQuery(SELECT_GOODS_AMOUNT);
        boolean isAvailable = resultSet.next();
        if(!isAvailable){
            connection.rollback();
            throw new SQLException("Не вдалося знайти товар: id=" + goodsId);
        }
        int amount = resultSet.getInt("amount");
        int updateAmount = amount - item.getNumber();
        if(updateAmount < 0){
            connection.rollback();
            throw new SQLException("Кількість товару при оновленні менше нуля: id=" + goodsId);
        }
        resultSet.updateInt("amount", updateAmount);
        if(updateAmount == 0){
            resultSet.updateInt("goods_status_id", GoodsStatus.EXPECTED.getId());
        }
        resultSet.updateRow();
    }

    private void createOrderItems(Order order, Connection connection) throws SQLException {
        final String CREATE_ORDER_ITEM = "INSERT INTO order_list" +
                "(order_id, goods_id, number) VALUES(?, ?, ?)";
        PreparedStatement preparedStatement =
                connection.prepareStatement(CREATE_ORDER_ITEM);
        for (OrderItem item: order.getItems()) {
            updateGoodsAmount(item, connection);
            preparedStatement.setInt(1, order.getId());
            preparedStatement.setInt(2, item.getGoods().getId());
            preparedStatement.setInt(3, item.getNumber());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
    }

    private void createOrder(Order item, Connection connection) throws SQLException {
        final String CREATE_ORDER = "INSERT INTO orders" +
                "(customer_id, order_status_id, order_price, order_date) " +
                "VALUES(?, ?, ?, ?)";
        PreparedStatement preparedStatement =
                connection.prepareStatement(CREATE_ORDER, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, item.getCustomer().getId());
        preparedStatement.setInt(2, item.getStatus().getId());
        preparedStatement.setDouble(3, item.getOrderPrice());
        preparedStatement.setDate(4, item.getOrderDate());
        int rowCount = preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        if (rowCount > 0 && generatedKeys.next()) {
            int orderId = generatedKeys.getInt(1);
            item.setId(orderId);
        }else {
            connection.rollback();
            throw new SQLException("Не вдалося створити нове замовлення");
        }
    }

    @Override
    public List<Order> getAll() {
        List<Order> orders = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement statement = connection.createStatement();
            String READ_ORDERS = "SELECT * FROM orders " +
                    "LEFT JOIN customers ON orders.customer_id = customers.customer_id";
            ResultSet resultSet = statement.executeQuery(READ_ORDERS);
            while (resultSet.next()) {
                Order order = getOrderFromResultSet(resultSet);
                List<OrderItem> items = getOrderItems(order, connection);
                order.setItems(items);
                orders.add(order);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }

    public Order getOrderById(int id){
        try (Connection connection = Service.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String READ_ORDER =
                    "SELECT * FROM orders, customers " +
                            "WHERE orders.customer_id = customers.customer_id " +
                            "AND orders.order_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(READ_ORDER);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                Order order = getOrderFromResultSet(resultSet);
                List<OrderItem> items = getOrderItems(order, connection);
                order.setItems(items);
                return order;
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private List<OrderItem> getOrderItems(Order order, Connection connection) throws SQLException {
        final String SELECT_ORDER_LIST = "SELECT * FROM order_list " +
                "LEFT JOIN goods ON order_list.goods_id = goods.goods_id " +
                "LEFT JOIN goods_type ON goods.goods_type_id = goods_type.goods_type_id " +
                "WHERE order_id=?";
        List<OrderItem> items = new ArrayList<>();
        PreparedStatement preparedStatement =
                connection.prepareStatement(SELECT_ORDER_LIST);
        preparedStatement.setInt(1, order.getId());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            OrderItem item = getOrderItemFromResultSet(resultSet);
            items.add(item);
        }
        return items;
    }

    private OrderItem getOrderItemFromResultSet(ResultSet resultSet) throws SQLException {
        OrderItem item = new OrderItem();
        Goods goods = GoodsDAO.getGoodsFromResultSet(resultSet);
        item.setGoods(goods);
        item.setNumber(resultSet.getInt("number"));
        return item;
    }

    private Order getOrderFromResultSet(ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getInt("order_id"));
        order.setOrderDate(resultSet.getDate("order_date"));
        order.setOrderPrice(resultSet.getInt("order_price"));
        order.setStatus(OrderStatus.getById(
                resultSet.getInt("order_status_id")));
        Customer customer = CustomerDAO.getCustomerFromResultSet(resultSet);
        order.setCustomer(customer);
        return order;
    }

    public void changeOrderStatus(Order item, OrderStatus status){
        try (Connection connection = Service.getConnection()) {
            String UPDATE_ORDER_STATUS =
                    "UPDATE orders SET order_status_id=? WHERE order_id=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(UPDATE_ORDER_STATUS);
            preparedStatement.setInt(1, status.getId());
            preparedStatement.setInt(2, item.getId());
            preparedStatement.executeUpdate();
            if(!item.getStatus().isCanceled() && status.isCanceled()){
                returnOrderItems(item, connection);
            }
            createOrderLog(new OrderLog(item, status), connection);
            connection.commit();
            createLog("Змінено статус замовлення: " + item);
        } catch (SQLException e) {
            createLog("Помилка при зміні статусу замовлення: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Order item) {
        try (Connection connection = Service.getConnection()) {
            String UPDATE_ORDER =
                    "UPDATE orders SET " +
                            "customer_id=?, order_status_id=?, order_price=?, order_date=? " +
                            "WHERE order_id=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(UPDATE_ORDER);
            preparedStatement.setInt(1, item.getCustomer().getId());
            preparedStatement.setInt(2, item.getStatus().getId());
            preparedStatement.setDouble(3, item.getOrderPrice());
            preparedStatement.setDate(4, item.getOrderDate());
            preparedStatement.setInt(5, item.getId());
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Оновлено дані про замовлення: " + item);
        } catch (SQLException e) {
            createLog("Помилка при оновленні даних про замовлення: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = Service.getConnection()) {
            String DELETE_ORDER = "DELETE FROM orders WHERE order_id=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(DELETE_ORDER);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Видалено замовлення: id=" + id);
        } catch (SQLException e) {
            createLog("Помилка при видаленні замовлення: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> select(String sqlQuery) {
        List<Order> orders = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                Order order = getOrderFromResultSet(resultSet);
                List<OrderItem> items = getOrderItems(order, connection);
                order.setItems(items);
                orders.add(order);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orders;
    }

    public int getTotalProfit(){
        int total = 0;
        try (Connection connection = Service.getConnection()) {
            String TOTAL_QUERY = "SELECT SUM(order_price) AS total FROM orders " +
                    "WHERE order_status_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(TOTAL_QUERY);
            OrderStatus status = OrderStatus.COMPLETED;
            preparedStatement.setInt(1, status.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                total = resultSet.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return total;
    }

    public double getAverageOrdersPrice(){
        double averagePrice = 0;
        try (Connection connection = Service.getConnection()) {
            Statement statement = connection.createStatement();
            String AVG_PRICE_QUERY = "SELECT AVG(order_price) AS average_price FROM orders";
            ResultSet resultSet = statement.executeQuery(AVG_PRICE_QUERY);
            if(resultSet.next()) {
                averagePrice = resultSet.getDouble("average_price");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return averagePrice;
    }

    public int getCompletedCount(){
        int count = 0;
        try (Connection connection = Service.getConnection()) {
            String COMPLETED_COUNT_QUERY = "SELECT COUNT(order_id) AS count FROM orders " +
                    "WHERE order_status_id = ?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(COMPLETED_COUNT_QUERY);
            OrderStatus status = OrderStatus.COMPLETED;
            preparedStatement.setInt(1, status.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    public int getOrdersCount(){
        int count = 0;
        try (Connection connection = Service.getConnection()) {
            String COUNT_QUERY = "SELECT COUNT(order_id) AS count FROM orders";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(COUNT_QUERY);
            if(resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    public Map<OrderStatus, Integer> getOrderByStatus(){
        Map<OrderStatus, Integer> map = new HashMap<>();
        try (Connection connection = Service.getConnection()) {
            String ORDER_STATUS_QUERY = "SELECT COUNT(order_id) AS count, " +
                    "order_status_id as status_id FROM orders " +
                    "GROUP BY order_status_id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(ORDER_STATUS_QUERY);
            while (resultSet.next()){
                int statusId = resultSet.getInt("status_id");
                OrderStatus status = OrderStatus.getById(statusId);
                int count = resultSet.getInt("count");
                map.put(status, count);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public Map<Goods, Integer> getOrderTopGoods(){
        Map<Goods, Integer> map = new LinkedHashMap<>();
        try (Connection connection = Service.getConnection()) {
            String ORDER_GOODS_QUERY = "SELECT * FROM " +
                    "(SELECT SUM(number) AS total_number, goods_id " +
                    "FROM order_list, orders " +
                    "WHERE order_list.order_id = orders.order_id AND order_status_id = ? " +
                    "GROUP BY goods_id) T " +
                    "LEFT JOIN goods ON T.goods_id = goods.goods_id " +
                    "LEFT JOIN goods_type ON goods.goods_type_id = goods_type.goods_type_id " +
                    "ORDER BY total_number DESC";
            PreparedStatement preparedStatement = connection.prepareStatement(ORDER_GOODS_QUERY);
            OrderStatus status = OrderStatus.COMPLETED;
            preparedStatement.setInt(1, status.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int totalNumber = resultSet.getInt("total_number");
                Goods goods = GoodsDAO.getGoodsFromResultSet(resultSet);
                map.put(goods, totalNumber);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public Map<Date, Integer> getStatisticByMonth(Month month){ 
        Map<Date, Integer> map = new LinkedHashMap<>();
        try (Connection connection = Service.getConnection()) {
            String ORDER_DATE_QUERY = "SELECT COUNT(order_id) AS count, " +
                    "order_date FROM orders " +
                    "WHERE MONTH(order_date)=? AND order_status_id=? " +
                    "GROUP BY order_date";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(ORDER_DATE_QUERY);
            OrderStatus status = OrderStatus.COMPLETED;
            preparedStatement.setInt(1, month.getValue());
            preparedStatement.setInt(2, status.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Date date = resultSet.getDate("order_date");
                int count = resultSet.getInt("count");
                map.put(date, count);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

}
