package ua.edu.dnu.booksales.dao;

import ua.edu.dnu.booksales.model.Customer;
import ua.edu.dnu.booksales.model.DBTable;
import ua.edu.dnu.booksales.model.EventLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO implements InterfaceDAO<Customer> {
    private final EventLogDAO logs = new EventLogDAO();
    private void createLog(String activity){
        logs.create(new EventLog(activity, DBTable.CUSTOMERS));
    }

    @Override
    public void create(Customer item) {
        final String CREATE_CUSTOMER = "INSERT INTO customers" +
                "(name, surname, patronymic, address, phone) " +
                "VALUES(?, ?, ?, ?, ?)";
        try (Connection connection = Service.getConnection()){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(CREATE_CUSTOMER);
            preparedStatement.setString(1, item.getName());
            preparedStatement.setString(2, item.getSurname());
            preparedStatement.setString(3, item.getPatronymic());
            preparedStatement.setString(4, item.getAddress());
            preparedStatement.setString(5, item.getPhone());
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Додано нового замовника: " + item);
        } catch (SQLException e) {
            createLog("Помилка при реєстрації замовника: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Customer> getAll() {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement statement = connection.createStatement();
            String READ_CUSTOMER = "SELECT * FROM customers";
            ResultSet resultSet = statement.executeQuery(READ_CUSTOMER);
            while (resultSet.next()) {
                Customer customer = getCustomerFromResultSet(resultSet);
                customers.add(customer);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }

    public Customer getByPhone(String phone) {
        try (Connection connection = Service.getConnection()){
            String READ_CUSTOMER = "SELECT * FROM customers WHERE phone=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(READ_CUSTOMER);
            preparedStatement.setString(1, phone);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return getCustomerFromResultSet(resultSet);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Customer getCustomerFromResultSet(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();
        customer.setId(resultSet.getInt("customer_id"));
        customer.setName(resultSet.getString("name"));
        customer.setSurname(resultSet.getString("surname"));
        customer.setPatronymic(resultSet.getString("patronymic"));
        customer.setAddress(resultSet.getString("address"));
        customer.setPhone(resultSet.getString("phone"));
        return customer;
    }

    @Override
    public void update(Customer item) {
        try (Connection connection = Service.getConnection()) {
            String UPDATE_CUSTOMER =
                    "UPDATE customers SET name=?, surname=?, patronymic=?, address=?, phone=? " +
                            "WHERE customer_id=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(UPDATE_CUSTOMER);
            preparedStatement.setString(1, item.getName());
            preparedStatement.setString(2, item.getSurname());
            preparedStatement.setString(3, item.getPatronymic());
            preparedStatement.setString(4, item.getAddress());
            preparedStatement.setString(5, item.getPhone());
            preparedStatement.setInt(6, item.getId());
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Оновлено дані про замовника: " + item);
        } catch (SQLException e) {
            createLog("Помилка при оновленні даних замовника: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = Service.getConnection()) {
            String DELETE_CUSTOMER = "DELETE FROM customers WHERE customer_id=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(DELETE_CUSTOMER);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Видалено замовника: id=" + id);
        } catch (SQLException e) {
            createLog("Помилка при видаленні замовника: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Customer> select(String sqlQuery) {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                Customer customer = getCustomerFromResultSet(resultSet);
                customers.add(customer);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customers;
    }

    public int getCustomersCount(){
        int count = 0;
        try (Connection connection = Service.getConnection()) {
            String COMPLETED_COUNT_QUERY = "SELECT COUNT(customer_id) AS count FROM customers";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(COMPLETED_COUNT_QUERY);
            if(resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }
}
