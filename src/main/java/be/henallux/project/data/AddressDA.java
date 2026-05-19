package main.java.be.henallux.project.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.Locality;

public class AddressDA implements CRUD<Address> {
    private static volatile AddressDA instance;
    private final String TABLE_NAME = "Address_";
    private Map<Integer, Address> dataMappingObject;

    private AddressDA() {
        super();

        this.dataMappingModel.entry(
            Address.class, this
        );

        this.dataMappingObject = new HashMap<>();
    }

    public static synchronized  AddressDA getInstance() {
        if (instance == null) {
            setInstance(new AddressDA());
        }
        return instance;
    }

    private static synchronized void setInstance(AddressDA localityDA) {
        if (instance == null) {
            instance = localityDA;
        }
    }
    
    public Address mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException {
        int id;
        try {
            id = data.getInt("id");
            if (dataMappingObject.get(id) == null) {
                Address address = new Address(
                    id,
                    data.getInt("streetNumber"),
                    data.getString("streetName"),
                    LocalityDA.getInstance().getById(data.getInt("postalId"), mapping)
                );
                dataMappingObject.put(id, address);
            }
        } catch (SQLException e) {
            throw new DataBaseException("Error mapping data to object", e);
        }
        return dataMappingObject.get(id);
    }

    public List<Address> getAll() throws DataBaseException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " ORDER BY name";
        Connection connection = MySQLConnector.getInstance().getConnection();
        List<Address> addresses = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(SQLInstruction)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                addresses.add(mapDataToObject(resultSet));
            }
        } catch (SQLException e) {
            throw new DataBaseException("Error getting all addresses", e);
        }
        return addresses;
    }

    public List<Address> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException {
        // TODO create decorator to automatise this portion of code in the parent class (+- 15 nexts lines)
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<Address> addresses = new ArrayList<>();

        for (int id: ids) {
            Address a = dataMappingObject.get(id);
            if (a != null) {
                ids.remove(id);
                addresses.add(a);
            }
        }

        if (ids.isEmpty())
            return addresses;

        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE id IN (" + placeholders + ") ORDER BY name";
        Connection connection = MySQLConnector.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SQLInstruction)) {
            for (int i = 1; i <= ids.size(); i++) {
                statement.setInt(i, ids.get(i));
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                addresses.add(mapDataToObject(resultSet, mapping));
            }
        } catch (SQLException e) {
            throw new DataBaseException("Error getting addresses by IDs", e);
        }
        return addresses;
    }

    public List<Address> getsByIds(List<Integer> ids) throws DataBaseException {
        return getsByIds(ids, true);
    }

    public Address getById(int id, boolean mapping) throws DataBaseException {
        // TODO create decorator to automatise this portion of code in the parent class (+- 4 nexts lines)
        Address addresse = dataMappingObject.get(id);

        if (addresse != null)
            return addresse;

        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE id=?;";
        Conneciton conneciton = MySQLConnector.getInstance().getConnection();

        try (PreparedStatement statement = connection.prepareStatement(SQLInstruction)) {
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next())
                addresse = mapDataToObject(result, mapping);

        } catch (SQLException e) {
            throw new DataBaseException("SQL Exception", e);
        }

        return addresse;
    }

    public Address getById(int id) throws  DataBaseExcepiton {
        return getById(id, true);
    }

    public boolean insert (Address newAddress) throws DataBaseException {
        LocalityDA.getInstance().checkExist(newAddress.getLocation());
        boolean inserted = false;
        String SQLInstruction = "INSERT INTO " + TABLE_NAME + " (streetName, streetNumber, postalId, city VALUES (?, ?, ?, ?);";

        Connection connection = MySQLConnector.getInstance().getConnection();

        try (PreparedStatement statement connection.prepareStatement(SQLInstruction);) {
            statement.setString(1, newAddress.getStreetName());
            statement.setInt(2, newAddress.getStreetNumber());
            statement.setInt(3, newAddress.getLocation().getPostalCode());
            statement.setString(4, newAddress.getLocation().getCity());

            inserted = 0 < statement.executeUpdate();

            // TODO update newAddress to include the ID set by the DB
        } catch (SQLException e) {
            throw new DataBaseException("Insert address imposible", e);
        }
        return inserted;
    }
}
