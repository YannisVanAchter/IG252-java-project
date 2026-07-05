package be.henallux.project.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.data.CRUD;
import be.henallux.project.model.exception.DataValidationException;
import be.henallux.project.model.LocationProduct;
import be.henallux.project.model.QuantityProduct;

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
            String shelf = data.getString("shelf");
            String floor_ = String.format("%d", data.getInt("floor_"));
            Boolean isStock = data.getBoolean("isStock");

            id = LocationProduct.hashCode(shelf, floor_, isStock);
            if (dataMappingObject.get(id) == null) {
                LocationProduct locationProduct = new LocationProduct(
                        shelf,
                        floor_,
                        isStock,
                        data.getBoolean("isFreezer")
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
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " ORDER BY shelf";
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
        if (!dataMappingObject.isEmpty() && dataMappingObject.containsKey(id)) {
            return dataMappingObject.get(id);
        }
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
                " (shelf, floor_, isStock, isFreezer) VALUES (?, ?, ?, ?);";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, newLocationProduct.getShelf());
            statement.setString(2, newLocationProduct.getFloor());
            statement.setBoolean(3, newLocationProduct.getIsStock());
            statement.setBoolean(4, newLocationProduct.getIsFreezer());

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
                " SET shelf=?, floor_=?, isStock=?, isFreezer=? WHERE shelf=? AND floor_=? AND isStock=?;";

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

        for (QuantityProduct qp: QuantityProductDA.getInstance().getAll())
                if (qp.getLocationProduct() == locationProduct)
                    QuantityProductDA.getInstance().delete(qp);

        String SQLInstruction = "DELETE FROM " + TABLE_NAME + " WHERE shelf=? AND floor_=? AND isStock=?";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, locationProduct.getShelf());
            statement.setString(2, locationProduct.getFloor());
            statement.setBoolean(3, locationProduct.getIsStock());

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
                    "SELECT COUNT(*) as nbLocationProduct FROM " + TABLE_NAME +
                            " WHERE shelf=? AND floor_=? AND isStock=?";

            try (Connection connection = connector.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(SQLInstruction);

                statement.setString(1, locationProduct.getShelf());
                statement.setString(2, locationProduct.getFloor());
                statement.setBoolean(3, locationProduct.getIsStock());

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