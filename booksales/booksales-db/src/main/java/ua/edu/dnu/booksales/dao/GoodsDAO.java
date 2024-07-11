package ua.edu.dnu.booksales.dao;

import ua.edu.dnu.booksales.model.*;

import java.sql.*;
import java.util.*;

public class GoodsDAO implements InterfaceDAO<Goods> {
    private final EventLogDAO logs = new EventLogDAO();

    private void createLog(String activity){
        logs.create(new EventLog(activity, DBTable.GOODS));
    }

    @Override
    public void create(Goods item) {
        final String CREATE_GOODS = "INSERT INTO goods" +
                "(title, author, amount, price, goods_type_id, goods_status_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection connection = Service.getConnection()){
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(CREATE_GOODS);
            preparedStatement.setString(1, item.getTitle());
            preparedStatement.setString(2, item.getAuthor());
            preparedStatement.setInt(3, item.getAmount());
            preparedStatement.setDouble(4, item.getPrice());
            preparedStatement.setInt(5, item.getType().getId());
            preparedStatement.setInt(6, item.getStatus().getId());
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Додано новий товар: " + item);
        } catch (SQLException e) {
            createLog("Помилка при додаванні товару: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Goods> getAll() {
        List<Goods> goods = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement statement = connection.createStatement();
            String READ_GOODS =
                    "SELECT * FROM goods " +
                            "LEFT JOIN goods_type ON goods.goods_type_id = goods_type.goods_type_id";
            ResultSet resultSet = statement.executeQuery(READ_GOODS);
            while (resultSet.next()) {
                Goods item = getGoodsFromResultSet(resultSet);
                goods.add(item);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return goods;
    }

    public static Goods getGoodsFromResultSet(ResultSet resultSet) throws SQLException {
        Goods goods = new Goods();
        goods.setId(resultSet.getInt("goods_id"));
        goods.setTitle(resultSet.getString("title"));
        goods.setAuthor(resultSet.getString("author"));
        goods.setAmount(resultSet.getInt("amount"));
        goods.setPrice(resultSet.getInt("price"));
        goods.setStatus(GoodsStatus.getById(
                resultSet.getInt("goods_status_id")));
        GoodsType type = null;
        resultSet.getInt("goods_type_id");
        if(!resultSet.wasNull()) {
            type = GoodsTypeDAO.getTypeFromResultSet(resultSet);
        }
        goods.setType(type);
        return goods;
    }

    @Override
    public void update(Goods item) {
        try (Connection connection = Service.getConnection()) {
            String UPDATE_GOODS =
                    "UPDATE goods SET " +
                            "title=?, author=?, amount=?, price=?, goods_type_id=?, goods_status_id=? " +
                            "WHERE goods_id=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(UPDATE_GOODS);
            preparedStatement.setString(1, item.getTitle());
            preparedStatement.setString(2, item.getAuthor());
            preparedStatement.setInt(3, item.getAmount());
            preparedStatement.setDouble(4, item.getPrice());
            preparedStatement.setInt(5, item.getType().getId());
            preparedStatement.setInt(6, item.getStatus().getId());
            preparedStatement.setInt(7, item.getId());
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Оновлено дані про товар: " + item);
        } catch (SQLException e) {
            createLog("Помилка при оновленні даних про товар: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection connection = Service.getConnection()) {
            String DELETE_GOODS = "DELETE FROM goods WHERE goods_id=?";
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            PreparedStatement preparedStatement =
                    connection.prepareStatement(DELETE_GOODS);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            connection.commit();
            createLog("Видалено товар: id=" + id);
        } catch (SQLException e) {
            createLog("Помилка при видаленні товару: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Goods> select(String sqlQuery) {
        List<Goods> goods = new ArrayList<>();
        try (Connection connection = Service.getConnection()) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                Goods item = getGoodsFromResultSet(resultSet);
                goods.add(item);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return goods;
    }

    public double getAverageGoodsPrice() {
        double averagePrice = 0;
        try (Connection connection = Service.getConnection()) {
            Statement statement = connection.createStatement();
            String AVG_PRICE_QUERY = "SELECT AVG(price) AS average_price FROM goods";
            ResultSet resultSet = statement.executeQuery(AVG_PRICE_QUERY);
            if(resultSet.next()) {
                averagePrice = resultSet.getDouble("average_price");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return averagePrice;
    }

    public int getGoodsCount(){
        int count = 0;
        try (Connection connection = Service.getConnection()) {
            String COUNT_QUERY = "SELECT COUNT(goods_id) AS count FROM goods";
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

    public Map<GoodsStatus, Integer> getGoodsCountByStatus() {
        Map<GoodsStatus, Integer> map = new LinkedHashMap<>();
        try (Connection connection = Service.getConnection()) {
            String GOODS_STATUS_QUERY = "SELECT COUNT(goods_id) AS count, " +
                    "goods_status_id as status_id FROM goods " +
                    "GROUP BY goods_status_id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GOODS_STATUS_QUERY);
            while (resultSet.next()){
                int statusId = resultSet.getInt("status_id");
                GoodsStatus status = GoodsStatus.getById(statusId);
                int count = resultSet.getInt("count");
                map.put(status, count);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public Map<GoodsType, Integer> getGoodsCountByTypes() {
        Map<GoodsType, Integer> map = new LinkedHashMap<>();
        try (Connection connection = Service.getConnection()) {
            String GOODS_TYPE_QUERY = "SELECT * FROM " +
                    "(SELECT COUNT(goods_id) AS count, goods_type_id FROM goods " +
                    "GROUP BY goods_type_id) T, goods_type " +
                    "WHERE T.goods_type_id = goods_type.goods_type_id";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GOODS_TYPE_QUERY);
            while (resultSet.next()){
                int count = resultSet.getInt("count");
                GoodsType type = GoodsTypeDAO.getTypeFromResultSet(resultSet);
                map.put(type, count);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
}
