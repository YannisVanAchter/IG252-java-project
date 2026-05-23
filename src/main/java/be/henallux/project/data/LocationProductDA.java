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
import main.java.be.henallux.project.model.LocationProduct;

public class LocationProductDA extends CRUD<LocationProduct> {
    private static volatile LocationProductDA instance;
    private final String TABLE_NAME = "LocationProduct";
    private Map<String, LocationProduct> dataMappingObject;

    private LocationProductDA() {
        super();
        this.dataMappingObject = new HashMap<>();
    }

    public static synchronized LocationProductDA getInstance() {
        if (instance == null) {
            setInstance(new LocationProductDA());
        }

        return instance;
    }

    private static synchronized void setInstance(LocationProductDA locationProductDA) {
        if (instance == null) {
            instance = locationProductDA;
        }
    }

    @Override
    public LocationProduct mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException {
        String id;

        try {
            id = data.getString("id_");

            if (dataMappingObject.get(id) == null) {
                LocationProduct locationProduct = new LocationProduct(
                        id,
                        data.getString("shelf_"),
                        data.getString("floor_"),
                        data.getBoolean("isStock_"),
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
    public List<LocationProduct> getAll() throws DataBaseException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " ORDER BY id_";
        List<LocationProduct> locationProducts = new ArrayList<>();

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
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
    public List<LocationProduct> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException {
        throw new UnsupportedOperationException("LocationProduct uses String ids");
    }

    @Override
    public List<LocationProduct> getsByIds(List<Integer> ids) throws DataBaseException {
        return getsByIds(ids, true);
    }

    public LocationProduct getById(String id, boolean mapping) throws DataBaseException {
        LocationProduct locationProduct = dataMappingObject.get(id);

        if (locationProduct != null) {
            return locationProduct;
        }

        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
            statement.setString(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                locationProduct = mapDataToObject(result, mapping);
            }

        } catch (SQLException e) {
            throw new DataBaseException("SQL Exception", e);
        }

        return locationProduct;
    }

    public LocationProduct getById(String id) throws DataBaseException {
        return getById(id, true);
    }

    @Override
    public LocationProduct getById(int id, boolean mapping) throws DataBaseException {
        return getById(String.valueOf(id), mapping);
    }

    @Override
    public LocationProduct getById(int id) throws DataBaseException {
        return getById(String.valueOf(id), true);
    }

    @Override
    public boolean insert(LocationProduct newLocationProduct) throws DataBaseException {
        boolean inserted = false;

        String SQLInstruction =
                "INSERT INTO " + TABLE_NAME +
                " (id_, shelf_, floor_, isStock_, isFreezer_) VALUES (?, ?, ?, ?, ?);";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, newLocationProduct.getLocationProductId());
            statement.setString(2, newLocationProduct.getShelf());
            statement.setString(3, newLocationProduct.getFloor());
            statement.setBoolean(4, newLocationProduct.getIsStock());
            statement.setBoolean(5, newLocationProduct.getIsFreezer());

            inserted = 0 < statement.executeUpdate();

            if (inserted) {
                dataMappingObject.put(
                        newLocationProduct.getLocationProductId(),
                        newLocationProduct
                );
            }

        } catch (SQLException e) {
            throw new DataBaseException("Insert location product impossible", e);
        }

        return inserted;
    }

    @Override
    public boolean update(LocationProduct locationProduct) throws DataBaseException {
        String SQLInstruction =
                "UPDATE " + TABLE_NAME +
                " SET shelf_=?, floor_=?, isStock_=?, isFreezer_=? WHERE id_=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, locationProduct.getShelf());
            statement.setString(2, locationProduct.getFloor());
            statement.setBoolean(3, locationProduct.getIsStock());
            statement.setBoolean(4, locationProduct.getIsFreezer());
            statement.setString(5, locationProduct.getLocationProductId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(locationProduct.getLocationProductId());
            dataMappingObject.put(
                    locationProduct.getLocationProductId(),
                    locationProduct
            );

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    @Override
    public boolean delete(LocationProduct locationProduct) throws DataBaseException {
        String SQLInstruction = "DELETE FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, locationProduct.getLocationProductId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(locationProduct.getLocationProductId());

            return affectedRows > 0;

        } catch (SQLException e) {
            throw new DataBaseException("Delete impossible", e);
        }
    }

    @Override
    public boolean checkExist(LocationProduct locationProduct) throws DataBaseException {
        boolean exist = false;

        if (locationProduct != null) {
            String SQLInstruction =
                    "SELECT COUNT(*) as nbLocationProduct FROM " +
                    TABLE_NAME +
                    " WHERE id_=?;";

            try (Connection connection = MySQLConnector.getInstance().getConnection()) {
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