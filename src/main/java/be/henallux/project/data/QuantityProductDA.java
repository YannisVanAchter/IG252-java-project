package main.java.be.henallux.project.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.QuantityProduct;
import main.java.be.henallux.project.model.exception.DataValidationException;

public class QuantityProductDA extends CRUD<QuantityProduct> {

    private static volatile QuantityProductDA instance;
    private final String TABLE_NAME = "QuantityProduct";
    private ProductDA productDA;
    private LocationProductDA locationDA; 

    private Map<String, QuantityProduct> dataMappingObject;

    private QuantityProductDA() {
        super();
        this.dataMappingObject = new HashMap<>();
        productDA = ProductDA.getInstance();
        locationDA = LocationProductDA.getInstance();
    }

    public static QuantityProductDA getInstance() {
        synchronized (QuantityProductDA.class) {    
            if (instance == null) {
                setInstance(new QuantityProductDA());
            }
        }
        return instance;
    }

    private static void setInstance(QuantityProductDA dao) {
        synchronized (QuantityProductDA.class) { 
            if (instance == null) {
                instance = dao;
            }
        }
    }

    @Override
    public QuantityProduct mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException, DataValidationException {
        String id;

        try {
            Product product = productDA.getById(data.getInt("productId"));
            LocationProduct location = locationDA.getById(
                LocationProduct.hashCode(
                    data.getString("shelf"),
                    data.getString("floor_"),
                    data.getBoolean("isStock")
                )
            );

            id = QuantityProductDA.hashCode(location, product);

            if (dataMappingObject.get(id) == null) {

                QuantityProduct quantityProduct = new QuantityProduct(
                    location,
                    product,
                    data.getInt("quantity")
                );

                id = quantityProduct.hashCode();

                dataMappingObject.put(id, quantityProduct);
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error mapping QuantityProduct", e);
        }

        return dataMappingObject.get(id);
    }

    @Override
    public List<QuantityProduct> getAll() throws DataBaseException {
        String sql = "SELECT * FROM " + TABLE_NAME + ";";
        List<QuantityProduct> list = new ArrayList<>();

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                list.add(mapDataToObject(rs, true));
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error getting all QuantityProduct", e);
        }

        return list;
    }

    @Override
    public List<QuantityProduct> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException {
        return getAll().streams().filter(quantity -> 
            ids.contains(quantity.hashCode())
        );
    }

    @Override
    public List<QuantityProduct> getsByIds(List<Integer> ids) throws DataBaseException {
        return getsByIds(ids, true);
    }

    public QuantityProduct getById(int id, boolean mapping) throws DataBaseException {
        getAll();
        return dataMappingObject.get(id);
    }

    public QuantityProduct getById(int id) throws DataBaseException {
        return getById(id, true);
    }

    @Override
    public boolean insert(QuantityProduct qp) throws DataBaseException {
        String sql = String.format("""
            INSERT INTO %s 
            (shelf, floor_, isStock, productId, quantity) VALUES 
            (?, ?, ?, ?, ?);
        """, TABLE_NAME);
        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, qp.getLocationProduct().getShelf());
            statement.setInt(2, qp.getLocationProduct().getfloor());
            statement.setBoolean(3, qp.getLocationProduct().getIsStock());
            statement.setInt(4, qp.getProduct().getId());
            statement.setInt(5, qp.getQuantity());

            boolean inserted = statement.executeUpdate() > 0;

            if (inserted) {
                dataMappingObject.put(qp.getId(), qp);
            }

            return inserted;

        } catch (SQLException e) {
            throw new DataBaseException("Insert QuantityProduct impossible", e);
        }
    }

    @Override
    public boolean update(QuantityProduct qp, QuantityProduct newQp) throws DataBaseException {

        String sql =String.format("""
            UPDATE %s SET
            shelf=?, floor_=?, isStock=?, productId=?, quantity=? WHERE  
            shelf=? AND floor_=? AND isStock=? AND productId=?;
        """, TABLE_NAME);

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, newQp.getLocationProduct().getShelf());
            statement.setInt(2, newQp.getLocationProduct().getfloor());
            statement.setBoolean(3, newQp.getLocationProduct().getIsStock());
            statement.setInt(4, newQp.getProduct().getId());
            statement.setInt(5, newQp.getQuantity());
            
            statement.setString(6, qp.getLocationProduct().getShelf());
            statement.setInt(7, qp.getLocationProduct().getfloor());
            statement.setBoolean(8, qp.getLocationProduct().getIsStock());
            statement.setInt(9, qp.getProduct().getId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(qp.getId());
            dataMappingObject.put(qp.getId(), qp);

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update QuantityProduct impossible", e);
        }
    }

    public boolean update(QuantityProduct qp, int newQuantity) throws DataBaseException {

        String sql =String.format("""
            UPDATE %s SET
            quantity=? WHERE  
            shelf=? AND floor_=? AND isStock=? AND productId=?;
        """, TABLE_NAME);

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, newQuantity);
            
            statement.setString(2, qp.getLocationProduct().getShelf());
            statement.setInt(3, qp.getLocationProduct().getfloor());
            statement.setBoolean(4, qp.getLocationProduct().getIsStock());
            statement.setInt(5, qp.getProduct().getId());

            int affectedRows = statement.executeUpdate();

            qp.setQuantity(newQuantity);

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update QuantityProduct impossible", e);
        }
    }

    @Override
    public boolean delete(QuantityProduct qp) throws DataBaseException {

        String sql = "DELETE FROM " + TABLE_NAME + " WHERE shelf=? AND floor_=? AND isStock=? AND productId=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, qp.getLocationProduct().getShelf());
            statement.setInt(2, qp.getLocationProduct().getfloor());
            statement.setBoolean(3, qp.getLocationProduct().getIsStock());
            statement.setInt(4, qp.getProduct().getId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(qp.hashCode());

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Delete QuantityProduct impossible", e);
        }
    }

    @Override
    public boolean checkExist(QuantityProduct qp) throws DataBaseException {

        if (qp == null) return false;

        String sql =
                "SELECT COUNT(*) as nb FROM " +
                TABLE_NAME +
                " WHERE shelf=? AND floor_=? AND isStock=? AND productId=?";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, qp.getLocationProduct().getShelf());
            statement.setInt(2, qp.getLocationProduct().getfloor());
            statement.setBoolean(3, qp.getLocationProduct().getIsStock());
            statement.setInt(4, qp.getProduct().getId());

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                boolean exist = rs.getInt("nb") > 0;

                return true;
            }

        } catch (SQLException e) {
            throw new DataBaseException("Check exist QuantityProduct impossible", e);
        }

        return false;
    }
}