package ua.edu.dnu.booksales.dao;

import ua.edu.dnu.booksales.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDAO implements InterfaceDAO<Employee> {
    private final EventLogDAO logs = new EventLogDAO();

    private void createLog(String activity){
        logs.create(new EventLog(activity, DBTable.EMPLOYEES));
    }

    private void createLog(String activity, Employee employee){
        EventLog log = new EventLog(activity, DBTable.EMPLOYEES);
        log.setEmployee(employee);
        logs.create(log);
    }

    @Override
    public void create(Employee item) {
        final String CREATE_EMPLOYEE = "INSERT INTO employees" +
                "(name, surname, patronymic, email, password, position_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection connection = Service.getConnection()){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(CREATE_EMPLOYEE);
            preparedStatement.setString(1, item.getName());
            preparedStatement.setString(2, item.getSurname());
            preparedStatement.setString(3, item.getPatronymic());
            preparedStatement.setString(4, item.getEmail());
            preparedStatement.setString(5, item.getPassword());
            preparedStatement.setInt(6, item.getPosition().getId());
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Додано нового співробітника: " + item);
        } catch (SQLException e) {
            createLog("Помилка при додаванні співробітника: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            String READ_EMPLOYEE = "SELECT * FROM employees " +
                    "LEFT JOIN positions ON employees.position_id = positions.position_id";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(READ_EMPLOYEE);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Employee employee = getEmployeeFromResultSet(resultSet);
                employees.add(employee);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }

    public static Employee getEmployeeFromResultSet(ResultSet resultSet) throws SQLException {
        Employee employee = new Employee();
        employee.setId(resultSet.getInt("employee_id"));
        employee.setName(resultSet.getString("name"));
        employee.setSurname(resultSet.getString("surname"));
        employee.setPatronymic(resultSet.getString("patronymic"));
        employee.setEmail(resultSet.getString("email"));
        employee.setPassword(resultSet.getString("password"));
        Position position = null;
        resultSet.getInt("position_id");
        if(!resultSet.wasNull()) {
            position = getPositionFromResultSet(resultSet);
        }
        employee.setPosition(position);
        return employee;
    }

    public List<Position> getPositions() {
        List<Position> positions = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            String READ_EMPLOYEE = "SELECT * FROM positions";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(READ_EMPLOYEE);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Position position = getPositionFromResultSet(resultSet);
                positions.add(position);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return positions;
    }

    public static Position getPositionFromResultSet(ResultSet resultSet) throws SQLException {
        Position position = new Position();
        position.setId(resultSet.getInt("position_id"));
        position.setName(resultSet.getString("post_name"));
        position.setDescription(resultSet.getString("post_description"));
        position.setPost(Post.getById(position.getId()));
        return position;
    }

    public boolean checkPassword(String email, String password){
        try (Connection connection = Service.getConnection()){
            String READ_EMPLOYEE = "SELECT * FROM employees " +
                    "LEFT JOIN positions ON employees.position_id = positions.position_id " +
                    "WHERE email=? AND password=?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(READ_EMPLOYEE);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    public Employee logIn(String email, String password){
        try (Connection connection = Service.getConnection()){
            String READ_EMPLOYEE = "SELECT * FROM employees " +
                    "LEFT JOIN positions ON employees.position_id = positions.position_id " +
                    "WHERE email=? AND password=?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(READ_EMPLOYEE);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Employee employee = getEmployeeFromResultSet(resultSet);
                createLog("Здійснено вхід у систему: " + email, employee);
                return employee;
            }
        } catch (SQLException e) {
            createLog("Помилка при вході у систему: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    public Employee getByEmail(String email){
        try (Connection connection = Service.getConnection()){
            String READ_EMPLOYEE = "SELECT * FROM employees " +
                    "LEFT JOIN positions ON employees.position_id = positions.position_id " +
                    "WHERE email=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(READ_EMPLOYEE);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return getEmployeeFromResultSet(resultSet);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean changePassword(String email, String password){
        try (Connection connection = Service.getConnection()) {
            String UPDATE_EMPLOYEE =
                    "UPDATE employees SET password=? WHERE email=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(UPDATE_EMPLOYEE);
            preparedStatement.setString(1, password);
            preparedStatement.setString(2, email);
            int rowsCount = preparedStatement.executeUpdate();
            connection.commit();
            if(rowsCount > 0){
                createLog("Змінено пароль співробітника: " + email);
                return true;
            }
        } catch (SQLException e) {
            createLog("Помилка при зміні паролю співробітника: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public void update(Employee item) {
        try (Connection connection = Service.getConnection()) {
            String UPDATE_EMPLOYEE =
                    "UPDATE employees SET name=?, surname=?, patronymic=?, " +
                            "email=?, password=?, position_id=? " +
                            "WHERE employee_id=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(UPDATE_EMPLOYEE);
            preparedStatement.setString(1, item.getName());
            preparedStatement.setString(2, item.getSurname());
            preparedStatement.setString(3, item.getPatronymic());
            preparedStatement.setString(4, item.getEmail());
            preparedStatement.setInt(5, item.getId());
            preparedStatement.setString(5, item.getPassword());
            preparedStatement.setInt(6, item.getPosition().getId());
            preparedStatement.setInt(7, item.getId());
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Оновлено дані про співробітника: " + item);
        } catch (SQLException e) {
            createLog("Помилка при оновленні даних співробітника: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = Service.getConnection()) {
            String DELETE_EMPLOYEE = "DELETE FROM employees WHERE employee_id=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(DELETE_EMPLOYEE);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Видалено співробітника: id=" + id);
        } catch (SQLException e) {
            createLog("Помилка при видаленні співробітника: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Employee> select(String sqlQuery) {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                Employee employee = getEmployeeFromResultSet(resultSet);
                employees.add(employee);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return employees;
    }

    public int getEmployeesCount(){
        int count = 0;
        try (Connection connection = Service.getConnection()) {
            String COUNT_QUERY = "SELECT COUNT(employee_id) AS count FROM employees";
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

    public Map<Position, Integer> getEmployeesCountByPosition() {
        Map<Position, Integer> map = new LinkedHashMap<>();
        try (Connection connection = Service.getConnection()) {
            String EMPLOYEE_POSITION_QUERY = "SELECT * FROM " +
                    "(SELECT COUNT(employee_id) AS count, position_id FROM employees " +
                    "GROUP BY position_id) T, positions " +
                    "WHERE T.position_id = positions.position_id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(EMPLOYEE_POSITION_QUERY);
            while (resultSet.next()){
                int count = resultSet.getInt("count");
                Position position = getPositionFromResultSet(resultSet);
                map.put(position, count);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
}
