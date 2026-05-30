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
import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.LocationProduct;

public class LocationProductDA extends CRUD<LocationProduct> {
    private static volatile LocationProductDA instance;
    private final String TABLE_NAME = "LocationProduct";
    private final Map<Integer, LocationProduct> dataMappingObject;

    private LocationProductDA() {
        super();
        this.dataMappingObject = new HashMap<>();
    }

    public static  LocationProductDA getInstance() {
        synchronized (LocationProductDA.class) {
            if (instance == null) {
                setInstance(new LocationProductDA());
            }
        }

        return instance;
    }

    private static void setInstance(LocationProductDA locationProductDA) {
        synchronized (LocationProductDA.class) {
            if (instance == null) {
                instance = locationProductDA;
            }
        }
    }

    @Override
    public LocationProduct mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException, DataValidationException {
        int id;

        try {
            String shelf = data.getString("shelf_");
            String floor = String.format("%d", data.getInt("floor_"));
            Boolean isStock = data.getBoolean("isStock_");

            id = LocationProduct.hashCode(shelf, floor, isStock);
            if (dataMappingObject.get(id) == null) {
                LocationProduct locationProduct = new LocationProduct(
                        shelf,
                        floor,
                        isStock,
                        data.getBoolean("isFreezer_")
                );

                dataMappingObject.put(id, locationProduct);
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error mapping LocationProduct", e);
        }

        return dataMappingObject.get(id);
    }

    @Override
    public List<LocationProduct> getAll() throws DataBaseException, DataValidationException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " ORDER BY id_";
        List<LocationProduct> locationProducts = new ArrayList<>();

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                locationProducts.add(mapDataToObject(resultSet, true));
            }

        } catch (SQLException e) {
            throw new DataBaseException("Error getting all location products", e);
        }

        return locationProducts;
    }

    @Override
    public List<LocationProduct> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException, DataValidationException {
        return getAll().stream().filter( l ->
                ids.contains(l.hashCode())
                ).toList();
    }

    @Override
    public List<LocationProduct> getsByIds(List<Integer> ids) throws DataBaseException, DataValidationException {
        return getsByIds(ids, true);
    }

    public LocationProduct getById(int id, boolean mapping) throws DataBaseException, DataValidationException {
        getAll();
        return dataMappingObject.get(id);
    }

    public LocationProduct getById(int id) throws DataBaseException, DataValidationException {
        return getById(id, true);
    }

    @Override
    public boolean insert(LocationProduct newLocationProduct) throws DataBaseException, DataValidationException {
        boolean inserted = false;

        String SQLInstruction =
                "INSERT INTO " + TABLE_NAME +
                " (id_, shelf_, floor_, isStock_, isFreezer_) VALUES (?, ?, ?, ?, ?);";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, newLocationProduct.getLocationProductId());
            statement.setString(2, newLocationProduct.getShelf());
            statement.setString(3, newLocationProduct.getFloor());
            statement.setBoolean(4, newLocationProduct.getIsStock());
            statement.setBoolean(5, newLocationProduct.getIsFreezer());

            inserted = 0 < statement.executeUpdate();

            if (inserted) {
                dataMappingObject.put(
                        newLocationProduct.hashCode(),
                        newLocationProduct
                );
            }

        } catch (SQLException e) {
            throw new DataBaseException("Insert location product impossible", e);
        }

        return inserted;
    }

    @Override
    public boolean update(LocationProduct locationProduct, LocationProduct newLocationProduct) throws DataBaseException, DataValidationException {
        String SQLInstruction =
                "UPDATE " + TABLE_NAME +
                " SET shelf_=?, floor_=?, isStock_=?, isFreezer_=? WHERE shelf_=? AND floor_=? AND isStock_=?;";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, newLocationProduct.getShelf());
            statement.setString(2, newLocationProduct.getFloor());
            statement.setBoolean(3, newLocationProduct.getIsStock());
            statement.setBoolean(4, newLocationProduct.getIsFreezer());
            statement.setString(5, locationProduct.getShelf());
            statement.setString(6, locationProduct.getFloor());
            statement.setBoolean(7, locationProduct.getIsStock());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(locationProduct.hashCode());
            dataMappingObject.put(
                    newLocationProduct.hashCode(),
                    newLocationProduct
            );

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    @Override
    public boolean delete(LocationProduct locationProduct) throws DataBaseException, DataValidationException {
        String SQLInstruction = "DELETE FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, locationProduct.getLocationProductId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(locationProduct.hashCode());

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Delete impossible", e);
        }
    }

    @Override
    public boolean checkExist(LocationProduct locationProduct) throws DataBaseException, DataValidationException {
        boolean exist = false;

        if (locationProduct != null) {
            String SQLInstruction =
                    "SELECT COUNT(*) as nbLocationProduct FROM " +
                    TABLE_NAME +
                    " WHERE id_=?;";

            try (Connection connection = connector.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(SQLInstruction);

                statement.setString(1, locationProduct.getLocationProductId());

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    exist = result.getInt("nbLocationProduct") > 0;
                }

                if (!exist) {
                    insert(locationProduct);
                    exist = true;
                }

            } catch (SQLException e) {
                throw new DataBaseException("Check impossible", e);
            }
        }

        return exist;
    }
}