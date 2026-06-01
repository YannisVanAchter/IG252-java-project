package main.java.be.henallux.project.data;

import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.WorkFlowType;
import main.java.be.henallux.project.model.WorkFlowTypeRepository;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkFlowTypeDA extends CRUD<WorkFlowType> {
    private static volatile WorkFlowTypeDA instance;

    public WorkFlowTypeDA() {
        this.TABLE_NAME = "WorkFlowType";
        this.IDS_MAPPING_OBJECT = new HashMap<>();

        WorkFlowTypeRepository workFlowTypeRepository = WorkFlowTypeRepository.getInstance();
        for (WorkFlowType workFlowType: workFlowTypeRepository.getWorkFLowTypes()) {
            checkExist(workFlowType);
        }
    }

    public static WorkFlowTypeDA getInstance() {
        synchronized (WorkFlowTypeDA.class) {
            if (instance == null)
                instance = new WorkFlowTypeDA();
        }
        return instance;
    }

    /**
     * Map SQL ResultSet to WorkFlowType object
     */
    @Override
    WorkFlowType mapDataToObject(ResultSet data, boolean mapping)
            throws DataBaseException, DataValidationException {

        try {
            int id = data.getInt("id_");

            if (IDS_MAPPING_OBJECT.containsKey(id)) {
                return IDS_MAPPING_OBJECT.get(id);
            }

            WorkFlowType workflowType = new WorkFlowType(
                    id,
                    data.getString("name_"),
                    data.getBoolean("isBuy"),
                    data.getBoolean("isSupplier"),
                    data.getBoolean("isInternal")
            );

            IDS_MAPPING_OBJECT.put(id, workflowType);

            return workflowType;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while mapping WorkFlowType data : " + e.getMessage()
            );
        }
    }

    /**
     * Retrieve all workflow types
     */
    @Override
    public List<WorkFlowType> getAll()
            throws DataBaseException, DataValidationException {

        List<WorkFlowType> workflowTypes = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query);

                ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {
                workflowTypes.add(mapDataToObject(result, true));
            }

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while retrieving WorkFlowTypes : " + e.getMessage()
            );
        }

        return workflowTypes;
    }

    /**
     * Retrieve workflow type by id
     */
    @Override
    public WorkFlowType getById(int id, boolean mapping)
            throws DataBaseException, DataValidationException {

        if (IDS_MAPPING_OBJECT.containsKey(id)) {
            return IDS_MAPPING_OBJECT.get(id);
        }

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE id_ = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return mapDataToObject(result, mapping);
                }

            }

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while retrieving WorkFlowType : " + e.getMessage()
            );
        }

        return null;
    }

    /**
     * Retrieve multiple workflow types by ids
     */
    @Override
    public List<WorkFlowType> getsByIds(List<Integer> ids, boolean mapping)
            throws DataBaseException, DataValidationException {

        List<WorkFlowType> workflowTypes = new ArrayList<>();

        for (Integer id : ids) {
            WorkFlowType workflowType = getById(id, mapping);

            if (workflowType != null) {
                workflowTypes.add(workflowType);
            }
        }

        return workflowTypes;
    }

    public WorkFlowType getByName(String name, boolean mapping) {
        return getAll().stream().filter(wf -> wf.getName().equals(name)).findFirst().orElse(null);
    }

    public WorkFlowType getByName(String name) {
        return getByName(name, true);
    }

    /**
     * Insert new workflow type
     */
    @Override
    boolean insert(WorkFlowType model)
            throws DataBaseException {

        String query = String.format("""
                INSERT INTO %s
                (name_, isBuy, isSupplier, isInternal)
                VALUES (?, ?, ?, ?)
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setString(1, model.getName());
            statement.setBoolean(2, model.getIsBuy());
            statement.setBoolean(3, model.getIsSell());
            statement.setBoolean(4, model.getIsInternal());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while inserting WorkFlowType : " + e.getMessage()
            );
        }
    }

    /**
     * Update workflow type
     */
    @Override
    boolean update(WorkFlowType model, WorkFlowType newModel)
            throws DataBaseException {

        String query = String.format("""
                UPDATE %s
                SET name_ = ?,
                    isBuy = ?,
                    isSupplier = ?,
                    isInternal = ?
                WHERE id_ = ?
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setString(1, newModel.getName());
            statement.setBoolean(2, newModel.getIsBuy());
            statement.setBoolean(3, newModel.getIsSell());
            statement.setBoolean(4, newModel.getIsInternal());
            statement.setInt(5, model.getId());

            boolean isUpdated = statement.executeUpdate() > 0;

            if (isUpdated) {
                IDS_MAPPING_OBJECT.remove(model.getId());
                IDS_MAPPING_OBJECT.put(newModel.getId(), newModel);
            }

            return isUpdated;
        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating WorkFlowType : " + e.getMessage()
            );
        }
    }

    /**
     * Delete workflow type
     */
    @Override
    boolean delete(WorkFlowType model)
            throws DataBaseException {

        String query = "DELETE FROM " + TABLE_NAME + " WHERE id_ = ?";

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, model.getId());

            boolean deleted = statement.executeUpdate() > 0;

            if (deleted) {
                IDS_MAPPING_OBJECT.remove(model.getId());
            }

            return deleted;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while deleting WorkFlowType : " + e.getMessage()
            );
        }
    }

    /**
     * Check if workflow type already exists
     */
    @Override
    boolean checkExist(WorkFlowType model)
            throws DataBaseException, DataValidationException {

        WorkFlowType existing = getByName(model.getName(), false);

        if (existing == null) {
            return insert(model);
        }

        return true;
    }

    /* ========================================================= */
    /* UPDATE FIELD METHODS                                      */
    /* ========================================================= */

    public boolean updateFieldName(WorkFlowType model, String newName)
            throws DataBaseException {

        String query = String.format("""
                UPDATE %s
                SET name_ = ?
                WHERE id_ = ?
                """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setString(1, newName);
            statement.setInt(2, model.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataBaseException(
                    "Error while updating name : " + e.getMessage()
            );
        }
    }
}