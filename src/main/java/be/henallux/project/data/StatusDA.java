package main.java.be.henallux.project.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.model.Status;
import main.java.be.henallux.project.model.WorkFlowStatusRepository;
import main.java.be.henallux.project.model.exception.DataValidationException;

public class StatusDA extends CRUD<Status> {

    private static volatile StatusDA instance;

    private StatusDA() throws DataBaseException, DataValidationException {
        TABLE_NAME = "Status_";
        IDS_MAPPING_OBJECT = new HashMap<>();

        WorkFlowStatusRepository statusRepository = WorkFlowStatusRepository.getInstance();
        for (Status status: statusRepository.getWorkFlowStatuses())
            checkExist(status);
    }

    public static StatusDA getInstance() {
        synchronized (StatusDA.class) {
            try {
                if (instance == null) {
                    instance = new StatusDA();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
        return instance;
    }

    @Override
    Status mapDataToObject(ResultSet data, boolean mapping)
            throws DataBaseException, DataValidationException {

        try {
            String name = data.getString("name_");

            if (IDS_MAPPING_OBJECT.containsKey(name))
                return IDS_MAPPING_OBJECT.get(name);

            Status status = new Status(name);

            IDS_MAPPING_OBJECT.put(name, status);

            return status;

        } catch (SQLException e) {
            throw new DataBaseException("Error while mapping Status data.", e);
        }
    }

    @Override
    public List<Status> getAll() throws DataBaseException, DataValidationException {

        List<Status> statuses = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;

        try (Connection connection = connector.getConnection()) {
            ResultSet rs = connection.createStatement().executeQuery(query);

            while (rs.next()) {
                statuses.add(mapDataToObject(rs, true));
            }

            return statuses;

        } catch (SQLException e) {
            throw new DataBaseException("Error while retrieving all statuses.", e);
        }
    }

    /**
     * Status utilise une clé primaire String.
     * Cette méthode est donc inutilisable.
     */
    @Override
    public Status getById(int id, boolean mapping)
            throws DataBaseException {

        throw new DataBaseException(
                "Status uses a String primary key. Use getByName(String name)."
        );
    }

    public Status getByName(String name)
            throws DataBaseException, DataValidationException {

        if (IDS_MAPPING_OBJECT.containsKey(name)) {
            return IDS_MAPPING_OBJECT.get(name);
        }

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE name_ = ?";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, name);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return mapDataToObject(rs, true);
            }

            return null;

        } catch (SQLException e) {
            throw new DataBaseException("Error while retrieving Status.", e);
        }
    }

    @Override
    public List<Status> getsByIds(List<Integer> ids, boolean mapping)
            throws DataBaseException {

        throw new DataBaseException(
                "Status uses a String primary key. Use getByNames(...)."
        );
    }

    public List<Status> getByNames(List<String> names)
            throws DataBaseException, DataValidationException {

        List<Status> statuses = new ArrayList<>();

        for (String name : names) {
            Status status = getByName(name);

            if (status != null) {
                statuses.add(status);
            }
        }

        return statuses;
    }

    @Override
    boolean insert(Status model)
            throws DataBaseException {

        String query = "INSERT INTO " + TABLE_NAME + " (name_) VALUES (?)";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, model.getName());

            boolean inserted = statement.executeUpdate() > 0;

            if (inserted) {
                IDS_MAPPING_OBJECT.put(model.getName(), model);
            }

            return inserted;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    @Override
    boolean update(Status model, Status newModel)
            throws DataBaseException {

        String query =
                "UPDATE " + TABLE_NAME +
                        " SET name_ = ? " +
                        "WHERE name_ = ?";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, newModel.getName());
            statement.setString(2, model.getName());

            boolean updated = statement.executeUpdate() > 0;

            if (updated) {
                IDS_MAPPING_OBJECT.remove(model.getName());
                IDS_MAPPING_OBJECT.put(newModel.getName(), newModel);
            }

            return updated;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    /**
     * Update uniquement le nom du Status.
     */
    public boolean updateFieldName(Status model, String newName)
            throws DataBaseException, DataValidationException {

        Status updatedStatus = new Status(newName);

        return update(model, updatedStatus);
    }

    @Override
    boolean delete(Status model)
            throws DataBaseException {

        String query =
                "DELETE FROM " + TABLE_NAME +
                        " WHERE name_ = ?";

        try (Connection connection = connector.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, model.getName());

            boolean deleted = statement.executeUpdate() > 0;

            if (deleted) {
                IDS_MAPPING_OBJECT.remove(model.getName());
            }

            return deleted;
        } catch (SQLException e) {
            throw new DataBaseException(e.getMessage(), e);
        }
    }

    @Override
    boolean checkExist(Status model)
            throws DataBaseException, DataValidationException {
        if (getByName(model.getName()) == null)
            return insert(model);
        return  true;
    }
}