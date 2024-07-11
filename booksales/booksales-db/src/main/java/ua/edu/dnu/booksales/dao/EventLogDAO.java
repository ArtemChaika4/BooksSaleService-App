package ua.edu.dnu.booksales.dao;


import ua.edu.dnu.booksales.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventLogDAO implements InterfaceDAO<EventLog>{

    @Override
    public void create(EventLog item) {
        final String CREATE_LOG = "INSERT INTO event_log" +
                "(activity, timestamp, employee_id, table_id) VALUES(?, ?, ?, ?)";
        try (Connection connection = Service.getConnection()){
            PreparedStatement preparedStatement =
                    connection.prepareStatement(CREATE_LOG);
            preparedStatement.setString(1, item.getActivity());
            preparedStatement.setTimestamp(2, item.getTimestamp());
            preparedStatement.setInt(3, item.getEmployee().getId());
            preparedStatement.setInt(4, item.getTable().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<EventLog> getAll() {
        List<EventLog> logs = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            Statement statement = connection.createStatement();
            String READ_LOG =
                    "SELECT * FROM event_log " +
                            "LEFT JOIN employees ON event_log.employee_id = employees.employee_id " +
                            "LEFT JOIN positions ON employees.position_id = positions.position_id";
            ResultSet resultSet = statement.executeQuery(READ_LOG);
            while (resultSet.next()) {
                EventLog log = getLogFromResultSet(resultSet);
                logs.add(log);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return logs;
    }

    private EventLog getLogFromResultSet(ResultSet resultSet) throws SQLException {
        EventLog log = new EventLog();
        log.setId(resultSet.getInt("event_log_id"));
        log.setActivity(resultSet.getString("activity"));
        log.setTable(DBTable.getById(
                resultSet.getInt("table_id")));
        log.setTimestamp(resultSet.getTimestamp("timestamp"));
        Employee employee = null;
        resultSet.getInt("employee_id");
        if(!resultSet.wasNull()) {
            employee = EmployeeDAO.getEmployeeFromResultSet(resultSet);
        }
        log.setEmployee(employee);
        return log;
    }


    @Override
    public void update(EventLog item) {
        try (Connection connection = Service.getConnection()) {
            String UPDATE_LOG =
                    "UPDATE event_log SET " +
                            "activity=?, timestamp=?, employee_id=?, table_id=? " +
                            "WHERE event_log_id=?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(UPDATE_LOG);
            preparedStatement.setString(1, item.getActivity());
            preparedStatement.setTimestamp(2, item.getTimestamp());
            preparedStatement.setInt(3, item.getEmployee().getId());
            preparedStatement.setInt(4, item.getTable().getId());
            preparedStatement.setInt(5, item.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = Service.getConnection()) {
            String DELETE_LOG = "DELETE FROM event_log WHERE event_log_id=?";
            PreparedStatement preparedStatement =
                    connection.prepareStatement(DELETE_LOG);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<EventLog> select(String sqlQuery) {
        List<EventLog> logs = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                EventLog log = getLogFromResultSet(resultSet);
                logs.add(log);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return logs;
    }
}
