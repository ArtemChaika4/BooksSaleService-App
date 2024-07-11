package ua.edu.dnu.booksales.dao;



import ua.edu.dnu.booksales.model.DBTable;
import ua.edu.dnu.booksales.model.EventLog;
import ua.edu.dnu.booksales.model.GoodsType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GoodsTypeDAO implements InterfaceDAO<GoodsType> {
    private final EventLogDAO logs = new EventLogDAO();

    private void createLog(String activity){
        logs.create(new EventLog(activity, DBTable.GOODS_TYPES));
    }

    @Override
    public void create(GoodsType item) {
        final String CREATE_TYPE = "INSERT INTO goods_type" +
                "(type_name, type_description) VALUES(?, ?)";
        try (Connection connection = Service.getConnection()){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(CREATE_TYPE);
            preparedStatement.setString(1, item.getName());
            preparedStatement.setString(2, item.getDescription());
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Додано новий тип літератури: " + item);
        } catch (SQLException e) {
            createLog("Помилка при додаванні типу літератури: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GoodsType> getAll() {
        List<GoodsType> goodsTypes = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement statement = connection.createStatement();
            String READ_TYPE = "SELECT * FROM goods_type";
            ResultSet resultSet = statement.executeQuery(READ_TYPE);
            while (resultSet.next()) {
                GoodsType type = getTypeFromResultSet(resultSet);
                goodsTypes.add(type);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return goodsTypes;
    }

    public static GoodsType getTypeFromResultSet(ResultSet resultSet) throws SQLException {
        GoodsType type = new GoodsType();
        type.setId(resultSet.getInt("goods_type_id"));
        type.setName(resultSet.getString("type_name"));
        type.setDescription(resultSet.getString("type_description"));
        return type;
    }

    @Override
    public void update(GoodsType item) {
        try (Connection connection = Service.getConnection()) {
            String UPDATE_TYPE =
                    "UPDATE goods_type SET type_name=?, type_description=? " +
                            "WHERE goods_type_id=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(UPDATE_TYPE);
            preparedStatement.setString(1, item.getName());
            preparedStatement.setString(2, item.getDescription());
            preparedStatement.setInt(3, item.getId());
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Оновлено дані про тип літератури: " + item);
        } catch (SQLException e) {
            createLog("Помилка при оновленні даних про тип літератури: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = Service.getConnection()) {
            String DELETE_TYPE = "DELETE FROM goods_type WHERE goods_type_id=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(DELETE_TYPE);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Видалено тип літератури: id=" + id);
        } catch (SQLException e) {
            createLog("Помилка при видаленні типу літератури: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GoodsType> select(String sqlQuery) {
        List<GoodsType> goodsTypes = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                GoodsType type = getTypeFromResultSet(resultSet);
                goodsTypes.add(type);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return goodsTypes;
    }

    public int getGoodsTypeCount(){
        int count = 0;
        try (Connection connection = Service.getConnection()) {
            String COUNT_QUERY = "SELECT COUNT(goods_type_id) AS count FROM goods_type";
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

}
