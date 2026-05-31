package main.java.be.henallux.project.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.data.CRUD;
import main.java.be.henallux.project.model.Locality;

public class LocalityDA extends  CRUD<Locality> {
    private static volatile LocalityDA instance;
    private final String TABLE_NAME = "Locality";
    private final Map<Integer, Locality> IDS_MAPPING_OBJECT;

    private LocalityDA() {
        super();
        this.IDS_MAPPING_OBJECT = new HashMap<>();
    }

    public static LocalityDA getInstance() {
        synchronized (LocalityDA.class) {
            if (instance == null) {
                setInstance(new LocalityDA());
            }
        }
        return instance;
    }

    private static void setInstance(LocalityDA localityDA) {
        synchronized (LocalityDA.class) {
            if (instance == null) {
                instance = localityDA;
            }
        }
    }

    public Locality mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException, DataValidationException {
        int id;
        try {
            id = data.getInt("id");
            Locality locality;
            if (IDS_MAPPING_OBJECT.get(id) == null) {
                locality = new Locality(
                    data.getString("city"),
                    data.getInt("postalId")
                );
                IDS_MAPPING_OBJECT.put(locality.hashCode(), locality);
            }
        } catch (SQLException e) {
            throw new DataBaseException("Error mapping data to object", e);
        }

        return IDS_MAPPING_OBJECT.get(id);
    }

    public Locality mapDataToObject(ResultSet data) throws DataBaseException, DataValidationException {
        return mapDataToObject(data, true);
    }

    public List<Locality> getAll() throws DataBaseException, DataValidationException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " ORDER BY name";
        List<Locality> localities = new ArrayList<>();

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                localities.add(mapDataToObject(resultSet));
            }
        } catch (SQLException e) {
            throw new DataBaseException("Impossible to get all objects", e);
        }

        return localities;
    }

    public List<Locality> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException, DataValidationException {

        if (ids == null)
            return new ArrayList<>();

        return getAll().stream().filter( l ->
                ids.contains(l.hashCode())
            ).toList();
    }

    public List<Locality> getsByIds(List<Integer> postalCodes) throws DataBaseException, DataValidationException {
        return getsByIds(postalCodes, true);
    }

    public Locality getById(int postalCode, boolean mapping) throws DataBaseException, DataValidationException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE postalCode = ?;";

        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setInt(1, postalCode);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapDataToObject(resultSet);
            }
        } catch (SQLException e) {
            throw new DataBaseException("Get by ID impossible", e);
        }

        return null;
    }

    public Locality getById(int postalCode) throws DataBaseException, DataValidationException {
        return getById(postalCode, true);
    }

    public boolean insert(Locality locality) throws DataBaseException, DataValidationException {
        String SQLInstruction = "INSERT INTO " + TABLE_NAME + " (city, postalId) VALUES (?, ?);";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, locality.getCity());
            statement.setInt(2, locality.getPostalCode());

            int affectedRows = statement.executeUpdate();

            IDS_MAPPING_OBJECT.put(locality.hashCode(), locality);

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DataBaseException("Insert impossible ", e);
        }
    }

    public boolean update(Locality locality, Locality newLocality) throws DataBaseException {
        String SQLInstruction = "UPDATE " + TABLE_NAME + " SET city = ?, postalId = ? WHERE city = ? AND postalId = ?;";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, newLocality.getCity());
            statement.setInt(2, newLocality.getPostalCode());
            statement.setString(3, locality.getCity());
            statement.setInt(4, locality.getPostalCode());

            int affectedRows = statement.executeUpdate();

            IDS_MAPPING_OBJECT.remove(locality.hashCode());
            IDS_MAPPING_OBJECT.put(newLocality.hashCode(), newLocality);

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DataBaseException("Impossible update", e);
        }
    }

    public boolean delete(Locality locality) throws DataBaseException {

        boolean isUsedInAddress = AddressDA.getInstance().getAll().stream()
                .filter(address -> address.getLocality() == locality)
                .toList().size() > 0;

        if (isUsedInAddress)
            return false;

        String SQLInstruction = "DELETE FROM " + TABLE_NAME + " WHERE city = ? AND postalId = ?;";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQLInstruction);

            statement.setString(1, locality.getCity());
            statement.setInt(2, locality.getPostalCode());

            int affectedRows = statement.executeUpdate();

            IDS_MAPPING_OBJECT.remove(locality.hashCode());

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DataBaseException("Impossible to delete the object", e);
        }
    }

    public boolean checkExist(Locality locality) throws DataBaseException, DataValidationException {
        boolean exist = false;
        
        if (locality != null) {
            String SQLInstruction = "SELECT COUNT(*) as nbLocality FROM " + TABLE_NAME + " WHERE postalId=? AND city=?;";

            try (Connection connection = connector.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(SQLInstruction);

                statement.setInt(1, locality.getPostalCode());
                statement.setString(2, locality.getCity());

                ResultSet result = statement.executeQuery();

                if ( !result.next() )
                    insert(locality);
                exist = true;
                
            } catch (SQLException e) {
                throw new DataBaseException("Check impossible", e);
            }
        }

        return exist;
    }
}
