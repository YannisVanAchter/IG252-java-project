package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.WorkFlowManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Document;
import main.java.be.henallux.project.model.Status;
import main.java.be.henallux.project.model.WorkFlow;
import main.java.be.henallux.project.model.WorkFlowType;

import java.util.ArrayList;

/**
 * @see WorkFlowManager
 */
public class WorkFlowController {

    private final WorkFlowManager workFlowManager;

    public WorkFlowController() {
        this.workFlowManager = new WorkFlowManager();
    }

    /**
     * Returns all workflows.
     * @return list of all {@link WorkFlow}
     * @see WorkFlowManager#getAllWorkFlows()
     */
    public ArrayList<WorkFlow> getAllWorkFlows() throws BusinessException {
        return new ArrayList<>(workFlowManager.getAllWorkFlows());
    }

    /**
     * Returns all buying workflow types.
     * @return list of buying {@link WorkFlowType}
     * @see WorkFlowManager#getAllBuying()
     */
    public ArrayList<WorkFlowType> getAllBuying() throws BusinessException {
        return new ArrayList<>(workFlowManager.getAllBuying());
    }

    /**
     * Returns all internal workflows.
     * @return list of internal {@link WorkFlow}
     * @see WorkFlowManager#getAllInternal()
     */
    public ArrayList<WorkFlow> getAllInternal() throws BusinessException {
        return new ArrayList<>(workFlowManager.getAllInternal());
    }

    /**
     * Returns all selling workflows.
     * @return list of selling {@link WorkFlow}
     * @see WorkFlowManager#getAllSelling()
     */
    public ArrayList<WorkFlow> getAllSelling() throws BusinessException {
        return new ArrayList<>(workFlowManager.getAllSelling());
    }

    /**
     * Returns all workflow types.
     * @return list of all {@link WorkFlowType}
     * @see WorkFlowManager#getWorkFlowType()
     */
    public ArrayList<WorkFlowType> getWorkFlowTypes() throws BusinessException {
        return new ArrayList<>(workFlowManager.getWorkFlowType());
    }

    /**
     * Returns all status.
     * @return list of {@link Status} extracted from all workflows
     * @see WorkFlow#getStatus()
     */
    public ArrayList<Status> getWorkFlowStatus() throws BusinessException {
        ArrayList<Status> status = new ArrayList<>();
        for (WorkFlow type : getAllWorkFlows()) {
            status.add(type.getStatus());
        }
        return status;
    }

    /**
     * Adds a new workflow type.
     * @param newWorkFlowType the {@link WorkFlowType} to add
     * @see WorkFlowManager#addWorkFlowType(WorkFlowType)
     */
    public void addWorkFlowType(WorkFlowType newWorkFlowType) throws BusinessException {
        workFlowManager.addWorkFlowType(newWorkFlowType);
    }

    /**
     * Adds a new workflow.
     * @param workflow the {@link WorkFlow} to add
     * @return
     * @see WorkFlowManager#addWorkFlow(WorkFlow)
     */
    public WorkFlow addWorkFlow(WorkFlow workflow) throws BusinessException {
        workFlowManager.addWorkFlow(workflow);
        return workflow;
    }

    /**
     * Changes the status of a workflow.
     * @param workflowId the workflow ID
     * @param status     the new {@link Status}
     * @see WorkFlowManager#changeStatus(int, Status)
     */
    public void changeStatus(int workflowId, Status status) throws BusinessException {
        workFlowManager.changeStatus(workflowId, status);
    }

    /**
     * Adds a document to a workflow.
     * @param workflowId the workflow ID
     * @param document   the {@link Document} to add
     * @see WorkFlowManager#addDocument(int, Document)
     */
    public void addDocument(int workflowId, Document document) throws BusinessException {
        workFlowManager.addDocument(workflowId, document);
    }
}