package be.henallux.project.controller;

import be.henallux.project.business.WorkFlowManager;
import be.henallux.project.business.exception.BusinessException;
import be.henallux.project.model.Document;
import be.henallux.project.model.Status;
import be.henallux.project.model.WorkFlow;
import be.henallux.project.model.WorkFlowType;
import be.henallux.project.model.exception.DataValidationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    public ArrayList<WorkFlow> getAllWorkFlows() throws BusinessException, DataValidationException {
        return new ArrayList<>(workFlowManager.getAllWorkFlows());
    }

    /**
     * Returns all buying workflows.
     * @return list of buying {@link WorkFlow}
     * @see WorkFlowManager#getAllBuying()
     */
    public List<WorkFlow> getAllBuying() throws BusinessException, DataValidationException {
        return workFlowManager.getAllBuying();
    }

    /**
     * Returns all internal workflows.
     * @return list of internal {@link WorkFlow}
     * @see WorkFlowManager#getAllInternal()
     */
    public List<WorkFlow> getAllInternal() throws BusinessException, DataValidationException {
        return workFlowManager.getAllInternal();
    }

    /**
     * Returns all selling workflows.
     * @return list of selling {@link WorkFlow}
     * @see WorkFlowManager#getAllSelling()
     */
    public List<WorkFlow> getAllSelling() throws BusinessException, DataValidationException {
        return workFlowManager.getAllSelling();
    }

    /**
     * Returns all workflow types.
     * @return list of all {@link WorkFlowType}
     */
    public ArrayList<WorkFlowType> getWorkFlowTypes() throws BusinessException, DataValidationException {
        return new ArrayList<>(workFlowManager.getAllWorkFlows().stream()
                .map(WorkFlow::getWorkflowType)
                .distinct()
                .toList());
    }

    /**
     * Returns all statuses extracted from all workflows.
     * @return list of {@link Status}
     * @see WorkFlow#getStatus()
     */
    public List<Status> getWorkFlowStatus() throws BusinessException, DataValidationException {
        List<Status> status = new ArrayList<>();
        for (WorkFlow workFlow : getAllWorkFlows()) {
            status.add(workFlow.getStatus());
        }
        return status;
    }

    /**
     * Changes the status of a workflow.
     * @param workflowId the workflow ID
     * @param status     the new {@link Status}
     * @see WorkFlowManager#changeStatus(int, Status)
     */
    public void changeStatus(int workflowId, Status status) throws BusinessException, DataValidationException {
        workFlowManager.changeStatus(workflowId, status);
    }

    /**
     * Creates a new workflow.
     * @param workFlow the {@link WorkFlow} to create
     * @see WorkFlowManager#createWorkFlow(WorkFlow)
     */
    public void createWorkFlow(WorkFlow workFlow) throws BusinessException, DataValidationException {
        workFlowManager.createWorkFlow(workFlow);
    }

    /**
     * Adds a document to a workflow.
     * @param workflowId the workflow ID
     * @param document   the {@link Document} to add
     * @see WorkFlowManager#addDocument(int, Document)
     */
    public void addDocument(int workflowId, Document document) throws BusinessException, DataValidationException {
        workFlowManager.addDocument(workflowId, document);
    }
}