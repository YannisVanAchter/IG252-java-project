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
import main.java.be.henallux.project.data.CRUD;
import main.java.be.henallux.project.data.ProductDA;
import main.java.be.henallux.project.data.LocationProductDA;
import main.java.be.henallux.project.model.QuantityProduct;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.LocationProduct;
import main.java.be.henallux.project.model.exception.DataValidationException;

public class QuantityProductDA extends CRUD<QuantityProduct> {

    private static volatile QuantityProductDA instance;
    private final String TABLE_NAME = "QuantityProduct";
    private final ProductDA productDA;
    private final LocationProductDA locationDA;

    private final Map<Integer, QuantityProduct> dataMappingObject;

    private QuantityProductDA() {
        super();
        dataMappingObject = new HashMap<>();
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
        int id;

        try {
            Product product = productDA.getById(data.getInt("productId"));
            LocationProduct location = locationDA.getById(
                LocationProduct.hashCode(
                    data.getString("shelf"),
                    data.getString("floor_"),
                    data.getBoolean("isStock")
                )
            );

            id = QuantityProduct.hashCode(location, product);

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
    public List<QuantityProduct> getAll() throws DataBaseException, DataValidationException {
        String sql = "SELECT * FROM " + TABLE_NAME + ";";
        List<QuantityProduct> list = new ArrayList<>();

        try (Connection connection = connector.getConnection()) {

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
    public List<QuantityProduct> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException, DataValidationException {
        return getAll().stream().filter(quantity ->
            ids.contains(quantity.hashCode())
        ).toList();
    }

    @Override
    public List<QuantityProduct> getsByIds(List<Integer> ids) throws DataBaseException, DataValidationException {
        return getsByIds(ids, true);
    }

    public QuantityProduct getById(int id, boolean mapping) throws DataBaseException, DataValidationException {
        getAll();
        return dataMappingObject.get(id);
    }

    public QuantityProduct getById(int id) throws DataBaseException, DataValidationException {
        return getById(id, true);
    }

    @Override
    public boolean insert(QuantityProduct qp) throws DataBaseException, DataValidationException {
        String sql = String.format("""
            INSERT INTO %s 
            (shelf, floor_, isStock, productId, quantity) VALUES 
            (?, ?, ?, ?, ?);
        """, TABLE_NAME);
        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, qp.getLocationProduct().getShelf());
            statement.setInt(2, Integer.parseInt(qp.getLocationProduct().getFloor()));
            statement.setBoolean(3, qp.getLocationProduct().getIsStock());
            statement.setInt(4, qp.getProduct().getId());
            statement.setInt(5, qp.getQuantity());

            boolean inserted = statement.executeUpdate() > 0;

            if (inserted) {
                dataMappingObject.put(qp.hashCode(), qp);
            }

            return inserted;

        } catch (SQLException e) {
            throw new DataBaseException("Insert QuantityProduct impossible", e);
        }
    }

    @Override
    public boolean update(QuantityProduct qp, QuantityProduct newQp) throws DataBaseException, DataValidationException {

        String sql =String.format("""
            UPDATE %s SET
            shelf=?, floor_=?, isStock=?, productId=?, quantity=? WHERE 
            shelf=? AND floor_=? AND isStock=? AND productId=?;
        """, TABLE_NAME);

        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, newQp.getLocationProduct().getShelf());
            statement.setInt(2, Integer.parseInt(newQp.getLocationProduct().getFloor()));
            statement.setBoolean(3, newQp.getLocationProduct().getIsStock());
            statement.setInt(4, newQp.getProduct().getId());
            statement.setInt(5, newQp.getQuantity());
            
            statement.setString(6, qp.getLocationProduct().getShelf());
            statement.setInt(7, Integer.parseInt(qp.getLocationProduct().getFloor()));
            statement.setBoolean(8, qp.getLocationProduct().getIsStock());
            statement.setInt(9, qp.getProduct().getId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.put(qp.hashCode(), qp);

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update QuantityProduct impossible", e);
        }
    }

    public boolean update(QuantityProduct qp, int newQuantity) throws DataBaseException, DataValidationException {

        String sql =String.format("""
            UPDATE %s SET
            quantity=? WHERE 
            shelf=? AND floor_=? AND isStock=? AND productId=?;
        """, TABLE_NAME);

        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, newQuantity);
            
            statement.setString(2, qp.getLocationProduct().getShelf());
            statement.setInt(3, Integer.parseInt(qp.getLocationProduct().getFloor()));
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
    public boolean delete(QuantityProduct qp) throws DataBaseException, DataValidationException {

        String sql = "DELETE FROM " + TABLE_NAME + " WHERE shelf=? AND floor_=? AND isStock=? AND productId=?;";

        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, qp.getLocationProduct().getShelf());
            statement.setInt(2, Integer.parseInt(qp.getLocationProduct().getFloor()));
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
    public boolean checkExist(QuantityProduct qp) throws DataBaseException, DataValidationException {

        if (qp == null) return false;

        String sql =
                "SELECT COUNT(*) as nb FROM " +
                TABLE_NAME +
                " WHERE shelf=? AND floor_=? AND isStock=? AND productId=?";

        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, qp.getLocationProduct().getShelf());
            statement.setInt(2, Integer.parseInt(qp.getLocationProduct().getFloor()));
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