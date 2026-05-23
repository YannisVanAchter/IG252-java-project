package main.java.be.henallux.project.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import com.mysql.cj.xdevapi.PreparableStatement;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.Locality;

public class LocalityDA extends CRUD<Locality> {
    private static volatile LocalityDA instance;
    private final String TABLE_NAME = "Locality";
    private Map<Integer, Locality> dataMappingObject;

    private LocalityDA() {
        super();

        this.dataMappingModel.entry(
            Locality.class, this
        );

        this.dataMappingObject = new HashMap<>();
    }

    public static synchronized  LocalityDA getInstance() {
        if (instance == null) {
            setInstance(new LocalityDA());
        }
        return instance;
    }

    private static synchronized void setInstance(LocalityDA localityDA) {
        if (instance == null) {
            instance = localityDA;
        }
    }

    public Locality mapDataToObject(ResultSet data, boolean mapping) throws DataBaseException {
        int id;
        try {
            id = data.getInt("id");
            if (dataMappingObject.get(id) == null) {
                Locality locality = new Locality(
                    id,
                    data.getString("city"),
                    data.getInt("postalId")
                );
                dataMappingObject.put(id, locality);
            }
        } catch (SQLException e) {
            throw new DataBaseException("Error mapping data to object", e);
        }

        return dataMappingObject.get(id);
    }

    public Locality mapDataToObject(ResultSet data) throws DataBaseException {
        return mapDataToObject(data, true);
    }

    public Locality mapDataToObject(ResultSet data) throws DataBaseException {
        return mapDataToObject(data, true);
    }

    public List<Locality> getAll() throws DataBaseException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " ORDER BY name";

        Connection connection = MySQLConnector.getInstance().getConnection();

        PreparedStatement statement = connection.prepareStatement(SQLInstruction);

        ResultSet resultSet = statement.executeQuery();

        List<Locality> localities = new ArrayList<>();

        while (resultSet.next()) {
            localities.add(mapDataToObject(resultSet));
        }

        return localities;
    }

    public List<Locality> getsByIds(List<Integer> ids, boolean mapping) throws DataBaseException {
        List<Locality> localities = new ArrayList<>();

        for (Integer id : ids) {
            Locality locality = dataMappingObject.get(id);
            if (locality != null) {
                localities.add(locality);
                ids.remove(id);
            }
        }

        if (ids.isEmpty()) {
            return localities;
        }
        
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE id IN (?) ORDER BY name;";

        Connection connection = MySQLConnector.getInstance().getConnection();

        PreparedStatement statement = connection.prepareStatement(SQLInstruction);

        statement.setArray(1, ids.toArray());

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            localities.add(mapDataToObject(resultSet));
        }

        return localities;
    }

    public List<Locality> getsByIds(List<Integer> postalCodes) throws DataBaseException {
        return getsByIds(postalCodes, true);
    }

    public Locality getById(int postalCode, boolean mapping) throws DataBaseException {
        String SQLInstruction = "SELECT * FROM " + TABLE_NAME + " WHERE postalCode = ?;";

        Connection connection = MySQLConnector.getInstance().getConnection();

        PreparedStatement statement = connection.prepareStatement(SQLInstruction);

        statement.setInt(1, postalCode);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return mapDataToObject(resultSet);
        }

        return null;
    }

    public Locality getById(int postalCode) throws DataBaseException {
        return getById(postalCode, true);
    }

    public boolean insert(Locality locality) throws DataBaseException {
        String SQLInstruction = "INSERT INTO " + TABLE_NAME + " (city, postalId) VALUES (?, ?);";

        Connection connection = MySQLConnector.getInstance().getConnection();

        PreparedStatement statement = connection.prepareStatement(SQLInstruction);

        statement.setString(1, locality.getCity());
        statement.setInt(2, locality.getPostalCode());

        int affectedRows = statement.executeUpdate();

        dataMappingObject.put(locality.hashCode(), locality);

        return affectedRows > 0;
    }

    public boolean update(Locality locality, Locality newLocality) throws DataBaseException {
        String SQLInstruction = "UPDATE " + TABLE_NAME + " SET city = ?, postalId = ? WHERE city = ? AND postalId = ?;";

        Connection connection = MySQLConnector.getInstance().getConnection();

        PreparedStatement statement = connection.prepareStatement(SQLInstruction);

        statement.setString(1, locality.getCity());
        statement.setInt(2, locality.getPostalCode());
        statement.setString(3, newLocality.getCity());
        statement.setInt(4, newLocality.getPostalCode());

        int affectedRows = statement.executeUpdate();

        dataMappingObject.remove(locality.hashCode());
        dataMappingObject.put(newLocality.hashCode(), newLocality);

        return affectedRows > 0;
    }

    public boolean delete(Locality locality) throws DataBaseException {
        String SQLInstruction = "DELETE FROM " + TABLE_NAME + " WHERE city = ? AND postalId = ?;";

        Connection connection = MySQLConnector.getInstance().getConnection();

        PreparedStatement statement = connection.prepareStatement(SQLInstruction);

        statement.setString(1, locality.getCity());
        statement.setInt(2, locality.getPostalCode());

        int affectedRows = statement.executeUpdate();

        dataMappingObject.remove(locality.hashCode());

        return affectedRows > 0;
    }

    public boolean checkExist(Locality l) throws DataBaseException {
        boolean exist = false;
        if (l != null) {
            String SQLInstruction = "SELECT COUNT(*) as nbLocality FROM " + TABLE_NAME + " WHERE postalId=? AND city=?;";

            Connection c = MySQLConnection.getInstance().getConnection();

            try (PreparableStatement statement = c.prepareStatement(SQLInstruction);) {

                statement.setInt(1, l.getPostalCode());
                statement.setString(2, l.getCity());

                ResultSet result = statement.executeQuery();

                if (!result.next()) {
                    insert(l);
                }                
                exist = true;
                
            } catch (SQLException e) {
                throw new DataBaseException("Check imposible", e);
            }
        }

        return exist;
    }
}
