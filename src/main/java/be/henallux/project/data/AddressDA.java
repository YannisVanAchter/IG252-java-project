package be.henallux.project.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.data.CRUD;
import be.henallux.project.data.LocalityDA;
import be.henallux.project.model.exception.DataValidationException;
import be.henallux.project.model.Address;
import be.henallux.project.model.Locality;

public class AddressDA extends CRUD<Address> {
    private static volatile AddressDA instance;
    private final String TABLE_NAME = "Address_";
    private final Map<Integer, Address> dataMappingObject;
    private final LocalityDA locality;

    private AddressDA() {
        super();

        locality = LocalityDA.getInstance();
        dataMappingObject = new HashMap<>();
    }

    public static AddressDA getInstance() {
        synchronized (AddressDA.class) {
            if (instance == null) {
                setInstance(new AddressDA());
            }
        }
        return instance;
    }

    private static void setInstance(AddressDA addressDA) {
        synchronized (AddressDA.class) {
            if (instance == null) {
                instance = addressDA;
            }
        }
    }
    
    public Address mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException, DataValidationException {
        int id;
        try {
            id = data.getInt("id_");
            if (dataMappingObject.get(id) == null) {
                Address address = new Address(
                    id,
                    data.getString("streetName"),
                        data.getInt("streetNumber"),
                    locality.getById(data.getInt("postalId"), mapping)
                );
                dataMappingObject.put(id, address);
            }
        } catch (SQLException e) {
            throw new DataBaseException("Error mapping data to object", e);
        }
        return dataMappingObject.get(id);
    }

    public List<Address> getAll() throws DataBaseException, DataValidationException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " ORDER BY streetName";
        List<Address> addresses = new ArrayList<>();

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                addresses.add(mapDataToObject(resultSet, true));
            }
        } catch (SQLException e) {
            throw new DataBaseException("Error getting all addresses", e);
        }
        return addresses;
    }

    public List<Address> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException, DataValidationException {
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
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE id_ IN (" + placeholders + ") ORDER BY name";

        try (Connection connection = connector.getConnection()) {
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

    public List<Address> getsByIds(List<Integer> ids) throws DataBaseException, DataValidationException {
        return getsByIds(ids, true);
    }

    public Address getById(int id, boolean mapping) throws DataBaseException, DataValidationException {
        // TODO create decorator to automatise this portion of code in the parent class (+- 4 nexts lines)
        Address addresse = dataMappingObject.get(id);

        if (addresse != null)
            return addresse;

        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {
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

    public Address getById(int id) throws  DataBaseException, DataValidationException {
        return getById(id, true);
    }

    public List<Address> getByLocality(Locality locality, boolean mapping) throws DataBaseException, DataValidationException {
        ArrayList<Address> addresses = new ArrayList<>();

        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE postalId=? AND city=?;";

        try (Connection connection = connector.getConnection()) {
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

    public List<Address> getByLocality(Locality locality) throws DataBaseException, DataValidationException {
        return getByLocality(locality, true);
    }

    public boolean insert (Address newAddress) throws DataBaseException, DataValidationException {
        this.locality.checkExist(newAddress.getLocality());
        boolean inserted = false;
        String SQLInstruction = "INSERT INTO " + TABLE_NAME + " (streetName, streetNumber, postalId, city) VALUES (?, ?, ?, ?);";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, newAddress.getStreetName());
            statement.setInt(2, newAddress.getStreetNumber());
            statement.setInt(3, newAddress.getLocality().getPostalCode());
            statement.setString(4, newAddress.getLocality().getCity());

            inserted = 0 < statement.executeUpdate();

            // update newAddress to include the ID set by the DBMS
            SQLInstruction = "SELECT id_ FROM " + TABLE_NAME + " WHERE streetName=? AND streetNumber=? AND postalId=? AND city=?;";
            statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, newAddress.getStreetName());
            statement.setInt(2, newAddress.getStreetNumber());
            statement.setInt(3, newAddress.getLocality().getPostalCode());
            statement.setString(4, newAddress.getLocality().getCity());

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                newAddress.setAddressId(result.getInt("id_"));
                dataMappingObject.put(newAddress.getAddressId(), newAddress);
            }

        } catch (SQLException e) {
            throw new DataBaseException("Insert address imposible", e);
        }
        return inserted;
    }

    public boolean update(Address address, Address newAddress) throws DataBaseException, DataValidationException {
        String SQLInstruction = "UPDATE " + TABLE_NAME + " SET streetName=?, streetNumber=?, postalId=?, city=? WHERE id_=?";


        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
            locality.checkExist(newAddress.getLocality());

            statement.setString(1, newAddress.getStreetName());
            statement.setInt(2, newAddress.getStreetNumber());
            statement.setInt(3, newAddress.getLocality().getPostalCode());
            statement.setString(4, newAddress.getLocality().getCity());
            
            statement.setInt(5, address.getAddressId());

            int affectedRows = statement.executeUpdate();

            dataMappingObject.remove(address.getAddressId());
            dataMappingObject.put(newAddress.getAddressId(), newAddress);

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DataBaseException("Update impossible", e);
        }
    }

    public boolean delete(Address address) throws DataBaseException, DataValidationException {

        int nbClientSupplierUsingAddress =
                ClientSupplierDA.getInstance().getAll().stream()
                        .filter(cs -> cs.getAddress() == address)
                        .toList().size();
        boolean isUsedInDocuments =
                DocumentDA.getInstance().getAll().stream()
                        .filter(doc -> doc.getAddress() == address)
                        .toList().size() > 0;

        if (nbClientSupplierUsingAddress > 1 || isUsedInDocuments)
            return false;

        String SQLInstruction = "DELETE FROM " + TABLE_NAME + " WHERE id_=?;";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setInt(1, address.getAddressId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                if (getByLocality(address.getLocality()).isEmpty())
                    this.locality.delete(address.getLocality());
            }

            dataMappingObject.remove(address.getAddressId());

            return true;
        } catch (SQLException e) {
            throw new DataBaseException("Delete impossible", e);
        }
    }

    public boolean checkExist(Address address) throws DataBaseException, DataValidationException {
        boolean exist = false;

        if (address != null) {
            String  SQLInstruction = "SELECT COUNT(*) as nbAddress FROM " + TABLE_NAME + 
                    " WHERE id_=?";

            try (Connection connection = connector.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(SQLInstruction);

                statement.setInt(1, address.getAddressId());

                ResultSet result = statement.executeQuery();

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
