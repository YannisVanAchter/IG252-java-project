package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;
import main.java.be.henallux.project.model.Status;
import java.util.List;

public class WorkFlowManager {

    private final WorkFlowData workFlowData;
    private final DocumentManager documentManager;

    public WorkFlowManager(WorkFlowData workFlowData, DocumentManager documentManager) {
        this.workFlowData = workFlowData;
        this.documentManager = documentManager;
    }
    
    public List<WorkFlow> getAllWorkFlows() throws BusinessException {
        try {
            return workFlowData.getAllWorkFlows();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des flux de travail.", e);
        }
    }

    public List<WorkFlow> getAllBuying() throws BusinessException {
        try {
            return workFlowData.getAllBuying();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des flux d'achat.", e);
        }
    }

    public List<WorkFlow> getAllInternal() throws BusinessException {
        try {
            return workFlowData.getAllInternal();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des flux internes.", e);
        }
    }

    public List<WorkFlow> getAllSelling() throws BusinessException {
        try {
            return workFlowData.getAllSelling();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des flux de vente.", e);
        }
    }

    public List<WorkFlowType> getWorkFlowType() throws BusinessException {
        try {
            return workFlowData.getWorkFlowType();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des types de flux.", e);
        }
    }

    public void addWorkFlowType(WorkFlowType workFlowType) throws BusinessException {
        if (workFlowType == null) {
            throw new BusinessException("Le type de flux ne peut pas être nul.");
        }
        
        try {
            workFlowData.addWorkFlowType(workFlowType);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout du type de flux.", e);
        }
    }

    public void addWorkFlow(WorkFlow workFlow) throws BusinessException {
        if (workFlow == null) {
            throw new BusinessException("Le flux de travail ne peut pas être nul.");
        }

        try {
            workFlowData.addWorkFlow(workFlow);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout du flux de travail.", e);
        }
    }

    public void changeStatus(int workFlowId, Status status) throws BusinessException {
        
        if (workFlowId <= 0) {
            throw new BusinessException("L'identifiant du flux doit être positif.");
        }
        if (status == null) {
            throw new BusinessException("Le statut ne peut pas être nul.");
        }
        try {
            workFlowData.changeStatus(workFlowId, status);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement de statut.", e);
        }
    }
    public void addDocument(int workFlowId, Document document) throws BusinessException {
        if (document == null) {
                throw new BusinessException("Le document ne peut pas être nul.");
        }
        if (workFlowId <= 0) {
            throw new BusinessException("L'identifiant du flux de travail doit être un nombre positif.");
        }

        documentManager.getDocument(document.getId());
        try {
            workFlowData.addDocument(workFlowId, document);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout du document.", e);
        }
    }
}
