package model;

import exception.DataValidationException;

/**
 * A workfow as an id, a status and a workflow type.
 */
public class WorkFlow {
    private int id;
    private Status status;
    private WorkFlowType workflowType;

    public WorkFlow(int id, Status status, WorkFlowType type) throws DataValidationException {
        setId(id);
        setStatus(status);
        setWorkflowType(type);
    }

    public int getId() {
        return id;
    }

    private void setId(int id) throws DataValidationException {
        if (id < 0) {
            throw new DataValidationException("ID cannot be negative.");
        }
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    private void setStatus(Status status) throws DataValidationException {
        if (status == null) {
            throw new DataValidationException("Status cannot be null.");
        }
        this.status = status;
    }

    public WorkFlowType getWorkflowType() {
        return workflowType;
    }

    private void setWorkflowType(WorkFlowType workflowType) throws DataValidationException {
        if (workflowType == null) {
            throw new DataValidationException("Workflow type cannot be null.");
        }
        this.workflowType = workflowType;
    }

    @Override
    public String toString() {
        return "WorkFlow{id=" + id + ", status=" + status + ", workflowType=" + workflowType + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        WorkFlow other = (WorkFlow) obj;
        return id == other.getId() && status.equals(other.getStatus()) && workflowType.equals(other.getWorkflowType());
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(id);
        result = 31 * result + status.hashCode();
        result = 31 * result + workflowType.hashCode();
        return result;
    }
}
