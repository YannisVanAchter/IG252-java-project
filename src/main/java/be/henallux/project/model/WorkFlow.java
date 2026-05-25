package main.java.be.henallux.project.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.java.be.henallux.project.model.exception.DataValidationException;

/**
 * A workfow as an id, a status and a workflow type.
 */
public class WorkFlow implements Model {
    private int id;
    private Status status;
    private WorkFlowType workflowType;
    private ClientSupplier us;
    private ClientSupplier otherParty;
    private WorkflowDocuments documents;

    public WorkFlow(int id, Status status, WorkFlowType type, ClientSupplier us, ClientSupplier otherParty, WorkflowDocuments documents) throws DataValidationException {
        setId(id);
        setStatus(status);
        setWorkflowType(type);
        setUs(us);
        setOtherParty(otherParty);
        setDocuments(documents);
    }

    public WorkFlow(int id, Status status, WorkFlowType type, ClientSupplier us, WorkflowDocuments documents) throws DataValidationException {
        setId(id);
        setStatus(status);
        setWorkflowType(type);
        setUs(us);
        setDocuments(documents);

        if ( !workflowType.getIsInternal() )
            throw new DataValidationException("Only internal workflow can referee to one and only one client or supplier (it is us)");
    }

    public WorkFlow(int id, Status status, WorkFlowType type, ClientSupplier us, ClientSupplier otherParty) throws DataValidationException {
        this(id, status, type, us, otherParty, null);
    }

    public WorkFlow(int id, Status status, WorkFlowType type, ClientSupplier us) throws DataValidationException {
        this(id, status, type, us, (ClientSupplier) null);
    }

    public WorkFlow(Status status, WorkFlowType type, ClientSupplier us, ClientSupplier otherParty) throws DataValidationException {
        this(0, status, type, us, otherParty);
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

    public void setStatus(Status status) throws DataValidationException {
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

    public ClientSupplier getUs() { return us; }

    private void setUs(ClientSupplier us) throws DataValidationException {
        if (us == null || !us.getIsUs())
            throw new DataValidationException("You need to include the store within a workflow");
        this.us = us;
    }

    public ClientSupplier getOtherParty() { return otherParty; }

    private void setOtherParty(ClientSupplier otherParty) throws DataValidationException {
        if ((otherParty == null || otherParty.getIsUs()) && ! workflowType.getIsInternal())
            throw new DataValidationException("When a workflow involve external relationship, an other party is required");
        this.otherParty = otherParty;
    }

    public List<Document> getDocuments() { return documents.getDocuments(); }

    private void setDocuments(WorkflowDocuments docs) {
        if (docs == null) 
            this.documents = new WorkflowDocuments(this);
        else 
            this.documents = docs;
    }

    public void addDocument(Document doc) {
        documents.addDocument(doc);
        doc.setWorkFlow(this);
    }

    public String getLabel() {
        return String.format("%s #%d", workflowType.getLabel(), id);
    }

    @Override
    public String toString() {
        return  "WorkFlow{id=" + id + 
                        ", status=" + status + 
                        ", workflowType=" + workflowType + 
                        ", us=" + us + 
                        ", otherParty=" + (otherParty != null ? otherParty:"null") + 
                        ", documents=" + documents + 
                        "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        WorkFlow other = (WorkFlow) obj;
        return  id == other.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
