package main.java.be.henallux.project.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.data.exception.DataValidationException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.Locality;

public class AddressDA extends CRUD<Address> {
    private static volatile AddressDA instance;
    private final String TABLE_NAME = "Address_";
    private Map<Integer, Address> dataMappingObject;
    private LocalityDA locality;

    private AddressDA() {
        super();

        this.locality = LocalityDA.getInstance();
        this.dataMappingObject = new HashMap<>();
    }

    public static AddressDA getInstance() {
        synchronized (AddressDA.class) {
            if (this.instance == null) {
                setInstance(new AddressDA());
            }
        }
        return this.instance;
    }

    private static void setInstance(AddressDA addressDA) {
        synchronized (AddressDA.class) {
            if (instance == null) {
                instance = addressDA;
            }
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
                    locality.getById(data.getInt("postalId"), mapping)
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
        List<Address> addresses = new ArrayList<>();

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
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
        if (ids == null) {
            return new ArrayList<>();
        }
        List<Address> addresses = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            int id = ids.get(i);
            Address a = dataMappingObject.get(id);
            if (a != null) {
                addresses.add(a);
                ids.remove(i);
                i--;
            }
        }

        if (ids.isEmpty())
            return addresses;

        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE id IN (" + placeholders + ") ORDER BY name";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
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

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next())
                addresse = mapDataToObject(result, mapping);

        } catch (SQLException e) {
            throw new DataBaseException("SQL Exception", e);
        }

        return addresse;
    }

    public Address getById(int id) throws  DataBaseException {
        return getById(id, true);
    }

    public List<Address> getByLocality(Locality locality, boolean mapping) throws DataValidationException {
        ArrayList<Address> addresses = new ArrayList<>();

        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE postalId=? AND city=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setInt(1, locality.getPostalCode());
            statement.setString(2, locality.getCity());

            ResultSet results = statement.executeQuery();

            while (results.next()) {
                addresses.add(mapDataToObject(results, mapping));
            }
        } catch (SQLException e) {
            throw new DataBaseException("Impossible to get address by locality", e);
        }

        return addresses;
    }

    public List<Address> getByLocality(Locality locality) throws DataBaseException {
        return getByLocality(locality, true);
    }

    public boolean insert (Address newAddress) throws DataBaseException {
        this.locality.checkExist(newAddress.getLocation());
        boolean inserted = false;
        String SQLInstruction = "INSERT INTO " + TABLE_NAME + " (streetName, streetNumber, postalId, city VALUES (?, ?, ?, ?);";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
            this.locality.chechExist(newAddress.getLocation());

            statement.setString(1, newAddress.getStreetName());
            statement.setInt(2, newAddress.getStreetNumber());
            statement.setInt(3, newAddress.getLocation().getPostalCode());
            statement.setString(4, newAddress.getLocation().getCity());

            inserted = 0 < statement.executeUpdate();

            // update newAddress to include the ID set by the DBMS
            SQLInstruction = "SELECT id_ FROM " + TABLE_NAME + " WHERE streetName=? AND streetNumber=? AND postalId=? AND city=?;";
            statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, newAddress.getStreetName());
            statement.setInt(2, newAddress.getStreetNumber());
            statement.setInt(3, newAddress.getLocation().getPostalCode());
            statement.setString(4, newAddress.getLocation().getCity());

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                newAddress.setId(result.getInt("id_"));
                dataMappingObject.put(newAddress.getId(), newAddress);
            }

        } catch (SQLException e) {
            throw new DataBaseException("Insert address imposible", e);
        }
        return inserted;
    }

    public boolean update(Address address, Address newAddress) {
        String SQLInstruction = "UPDATE " + TABLE_NAME + " SET streetName=?, streetNumber=?, postalId=?, city=? WHERE id_=?";


        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
            locality.chechExist(newAddress.getLocation());

            statement.setString(1, newAddress.getStreetName());
            statement.setString(2, newAddress.getStreetNumber());
            statement.setString(3, newAddress.getLocation().getPostalCode());
            statement.setString(4, newAddress.getLocation().getCity());
            
            statement.setString(5, address.getId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(address.getId());
            dataMappingObject.put(newAddress.getId(), newAddress);

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
        return false;
    }

    public boolean delete(Address address) throws DataBaseException {
        String SQLInstruction = "DELETE FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = MySQLConnector.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setInt(1, address.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                if (getByLocality(address.getLocality()).size() == 0)
                    this.locality.delete(address.getLocality());
            }

            dataMappingObject.remove(address.getId());

            return true;
        } catch (SQLException e) {
            throw new DataBaseException("Delete impossible", e);
        }
    }

    public boolean checkExist(Address address) throws DataBaseException {
        boolean exist = false;

        if (address != null) {
            String  SQLInstruction = "SELECT COUNT(*) as nbAddress FROM " + TABLE_NAME + 
                    " WHERE id_=?";

            try (Connection connection = MySQLConnector.getInstance().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(SQLInstruction);

                statement.setInt(address.getId());

                ResultSet result = statement.executeQuerry();

                if ( !result.next() )
                    insert(address);
                exist = true;
            } catch (SQLException e) {
                throw new DataBaseException("Check impossible", e);
            }
        }

        return exist;
    }
}
