package be.henallux.project.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.henallux.project.data.exception.DataBaseException;

import be.henallux.project.model.ClientSupplier;
import be.henallux.project.model.Document;
import be.henallux.project.model.Status;
import be.henallux.project.model.WorkFlow;
import be.henallux.project.model.WorkFlowType;

import be.henallux.project.model.exception.DataValidationException;

public class WorkFlowDA extends CRUD<WorkFlow> {

    private static WorkFlowDA instance;

    private final WorkFlowTypeDA workflowTypeDA;
    private final StatusDA statusDA;
    private final ClientSupplierDA clientSupplierDA;

    private WorkFlowDA() {
        this.TABLE_NAME = "WorkFlow";
        this.IDS_MAPPING_OBJECT = new HashMap<>();

        this.workflowTypeDA = WorkFlowTypeDA.getInstance();
        this.statusDA = StatusDA.getInstance();
        this.clientSupplierDA = ClientSupplierDA.getInstance();
    }

    public static WorkFlowDA getInstance() {
        synchronized (WorkFlowDA.class) {
            if (instance == null) {
                instance = new WorkFlowDA();
            }
        }
        return instance;
    }

    @Override
    WorkFlow mapDataToObject(ResultSet data, boolean mapping)
            throws DataBaseException, DataValidationException {

        try {
            int id = data.getInt("id_");

            if (IDS_MAPPING_OBJECT.containsKey(id)) {
                return IDS_MAPPING_OBJECT.get(id);
            }

            int workflowTypeId = data.getInt("workFlowTypeId");
            String statusId = data.getString("statusId");
            int usId = data.getInt("usId");

            Integer otherId = data.getObject("otherId", Integer.class) ;

            WorkFlowType workflowType = workflowTypeDA.getById(workflowTypeId, mapping);
            Status status = statusDA.getByName(statusId);
            ClientSupplier us = clientSupplierDA.getById(usId, mapping);

            ClientSupplier otherParty = null;

            if (otherId != null) {
                otherParty = clientSupplierDA.getById(otherId, mapping);
            }

            WorkFlow workflow;

            if (otherParty != null) {
                workflow = new WorkFlow(
                        id,
                        status,
                        workflowType,
                        us,
                        otherParty
                );
            } else {
                workflow = new WorkFlow(
                        id,
                        status,
                        workflowType,
                        us
                );
            }

            IDS_MAPPING_OBJECT.put(id, workflow);

            if (mapping)
                DocumentDA.getInstance().getAll();

            return workflow;

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while mapping workflow data to object",
                    e
            );
        }
    }

    @Override
    public List<WorkFlow> getAll()
            throws DataBaseException, DataValidationException {

        List<WorkFlow> workflows = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;

        try (
                Statement statement = connector.getConnection().createStatement();
                ResultSet result = statement.executeQuery(query)
        ) {

            while (result.next()) {
                workflows.add(mapDataToObject(result, true));
            }

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while getting all workflows",
                    e
            );
        }

        return workflows;
    }

    @Override
    public WorkFlow getById(int id, boolean mapping)
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

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while getting workflow by id",
                    e
            );
        }

        return null;
    }

    @Override
    public List<WorkFlow> getsByIds(List<Integer> ids, boolean mapping)
            throws DataBaseException, DataValidationException {
        List<WorkFlow> workflows = new ArrayList<>();

        for (Integer id : ids) {
            WorkFlow workflow = getById(id, mapping);

            if (workflow != null) {
                workflows.add(workflow);
            }
        }

        return workflows;
    }

    @Override
    public boolean insert(WorkFlow workflow)
            throws DataBaseException, DataValidationException {
        workflowTypeDA.checkExist(workflow.getWorkflowType());
        statusDA.checkExist(workflow.getStatus());
        clientSupplierDA.checkExist(workflow.getUs());
        if (workflow.getOtherParty() != null)
            clientSupplierDA.checkExist(workflow.getOtherParty());

        String query = String.format("""
            INSERT INTO %s
            (
                workFlowTypeId,
                statusId,
                usId,
                otherId
            )
            VALUES (?, ?, ?, ?)
        """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(
                                query,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setInt(
                    1,
                    workflow.getWorkflowType().getId()
            );

            statement.setString(
                    2,
                    workflow.getStatus().getName()
            );

            statement.setInt(
                    3,
                    workflow.getUs().getId()
            );

            if (workflow.getOtherParty() != null) {
                statement.setInt(
                        4,
                        workflow.getOtherParty().getId()
                );
            } else {
                statement.setNull(
                        4,
                        java.sql.Types.INTEGER
                );
            }

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    int generatedId = generatedKeys.getInt(1);
                    workflow.setId(generatedId);
                    IDS_MAPPING_OBJECT.put(
                            generatedId,
                            getById(generatedId)
                    );
                }
            }

            return true;

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while inserting workflow",
                    e
            );
        }
    }

    @Override
    public boolean update(WorkFlow workflow, WorkFlow newWorkflow)
            throws DataBaseException, DataValidationException {

        boolean updated = true;

        if (!workflow.getStatus().equals(newWorkflow.getStatus())) {
            updated &= updateFieldStatus(
                    workflow,
                    newWorkflow.getStatus()
            );
        }

        if (!workflow.getWorkflowType().equals(newWorkflow.getWorkflowType())) {
            updated &= updateFieldWorkflowType(
                    workflow,
                    newWorkflow.getWorkflowType()
            );
        }

        if (!workflow.getUs().equals(newWorkflow.getUs())) {
            updated &= updateFieldUs(
                    workflow,
                    newWorkflow.getUs()
            );
        }

        if (
                workflow.getOtherParty() == null
                        && newWorkflow.getOtherParty() != null
        ) {

            updated &= updateFieldOtherParty(
                    workflow,
                    newWorkflow.getOtherParty()
            );

        } else if (
                workflow.getOtherParty() != null
                        && !workflow.getOtherParty().equals(
                        newWorkflow.getOtherParty()
                )
        ) {

            updated &= updateFieldOtherParty(
                    workflow,
                    newWorkflow.getOtherParty()
            );
        }

        return updated;
    }

    @Override
    public boolean delete(WorkFlow workflow)
            throws DataBaseException, DataValidationException {

        String query = "DELETE FROM WorkFlow WHERE id_ = ?";

        for (Document doc: workflow.getDocuments())
            DocumentDA.getInstance().delete(doc);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, workflow.getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                IDS_MAPPING_OBJECT.remove(workflow.getId());
                return true;
            }

            return false;

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while deleting workflow",
                    e
            );
        }
    }

    @Override
    boolean checkExist(WorkFlow workflow)
            throws DataBaseException, DataValidationException {

        return getById(workflow.getId()) != null;
    }

    /**
     * Update workflow status.
     */
    public boolean updateFieldStatus(
            WorkFlow workflow,
            Status status
    ) throws DataBaseException {

        String query = String.format("""
            UPDATE %s
            SET statusId = ?
            WHERE id_ = ?
        """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setString(1, status.getName());
            statement.setInt(2, workflow.getId());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while updating workflow status",
                    e
            );
        }
    }

    /**
     * Update workflow type.
     */
    public boolean updateFieldWorkflowType(
            WorkFlow workflow,
            WorkFlowType workflowType
    ) throws DataBaseException {

        String query = String.format("""
            UPDATE %s
            SET workFlowTypeId = ?
            WHERE id_ = ?
        """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, workflowType.getId());
            statement.setInt(2, workflow.getId());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while updating workflow type",
                    e
            );
        }
    }

    /**
     * Update us field.
     */
    public boolean updateFieldUs(
            WorkFlow workflow,
            ClientSupplier us
    ) throws DataBaseException {

        String query = String.format("""
            UPDATE %s
            SET usId = ?
            WHERE id_ = ?
        """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            statement.setInt(1, us.getId());
            statement.setInt(2, workflow.getId());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while updating workflow us field",
                    e
            );
        }
    }

    /**
     * Update other party field.
     */
    public boolean updateFieldOtherParty(
            WorkFlow workflow,
            ClientSupplier otherParty
    ) throws DataBaseException {

        String query = String.format("""
            UPDATE %s
            SET otherId = ?
            WHERE id_ = ?
        """, TABLE_NAME);

        try (
                PreparedStatement statement =
                        connector.getConnection().prepareStatement(query)
        ) {

            if (otherParty != null) {
                statement.setInt(1, otherParty.getId());
            } else {
                statement.setNull(
                        1,
                        java.sql.Types.INTEGER
                );
            }

            statement.setInt(2, workflow.getId());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            throw new DataBaseException(
                    "Error while updating workflow other party",
                    e
            );
        }
    }

    public void deleteByClientSupplierId(int id) throws DataBaseException, DataValidationException {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE usId = ? OR otherId = ?";
        try (PreparedStatement stmt = connector.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.setInt(2, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WorkFlow wf = mapDataToObject(rs, false);
                    delete(wf);
                }
            }
        } catch (Exception e) {
            throw new DataBaseException("Error while deleting workflows for client supplier", e);
        }
    }
}