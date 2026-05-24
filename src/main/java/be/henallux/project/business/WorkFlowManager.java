package be.henallux.project.business;
import be.henallux.project.data.WorkFlowDA;
import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.business.exception.BusinessException;

import be.henallux.project.model.WorkFlow;
import be.henallux.project.model.Document;
import be.henallux.project.model.WorkFlowType;
import be.henallux.project.model.Status;
import java.util.List;

public class WorkFlowManager {

    private final WorkFlowDA workFlowDA;
    private final DocumentManager documentManager;

    public WorkFlowManager() {
        this.workFlowDA = WorkFlowDA.getInstance();
        this.documentManager = new DocumentManager();
    }
    
    public List<WorkFlow> getAllWorkFlows() throws BusinessException {
        try {
            return workFlowDA.getAllWorkFlows();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the workflows.", e);
        }
    }

    public List<WorkFlow> getAllBuying() throws BusinessException {
        try {
            return workFlowDA.getAllBuying();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the buying workflows.", e);
        }
    }

    public List<WorkFlow> getAllInternal() throws BusinessException {
        try {
            return workFlowDA.getAllInternal();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the internal workflows.", e);
        }
    }

    public List<WorkFlow> getAllSelling() throws BusinessException {
        try {
            return workFlowDA.getAllSelling();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the selling workflows.", e);
        }
    }

    public List<WorkFlowType> getWorkFlowType() throws BusinessException {
        try {
            return workFlowDA.getWorkFlowType();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the workflow types.", e);
        }
    }

    public void addWorkFlowType(WorkFlowType workFlowType) throws BusinessException {
        if (workFlowType == null) {
            throw new BusinessException("The workflow type cannot be null.");
        }
        
        try {
            workFlowDA.addWorkFlowType(workFlowType);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the workflow type.", e);
        }
    }

    public void addWorkFlow(WorkFlow workFlow) throws BusinessException {
        if (workFlow == null) {
            throw new BusinessException("The workflow cannot be null.");
        }

        try {
            workFlowDA.addWorkFlow(workFlow);
            for (Document doc : workFlow.getDocuments()) {
                documentManager.createDocument(doc);
                workFlowDA.addWorkFlow(workFlow);
            }
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the workflow.", e);
        }
    }

    public void changeStatus(int workFlowId, Status status) throws BusinessException {
        
        if (workFlowId <= 0) {
            throw new BusinessException("The workflow ID must be a positive number.");
        }
        if (status == null) {
            throw new BusinessException("The status cannot be null.");
        }
        try {
            workFlowDA.changeStatus(workFlowId, status);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the status.", e);
        }
    }
    public void addDocument(int workFlowId, Document document) throws BusinessException {
        if (document == null) {
                throw new BusinessException("The document cannot be null.");
        }
        if (workFlowId <= 0) {
            throw new BusinessException("The workflow ID must be a positive number.");
        }

        try {
            documentManager.createDocument(document);
            workFlowDA.addDocument(workFlowId, document);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the document.", e);
        }
    }
}
