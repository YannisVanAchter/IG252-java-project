package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;
import main.java.be.henallux.project.model.Status;
import java.util.List;

public class WorkFlowManager {

    private final WorkFlowDA workFlowDA;
    private final DocumentManager documentManager;

    public WorkFlowManager(WorkFlowDA workFlowDA, DocumentManager documentManager) {
        this.workFlowDA = workFlowDA;
        this.documentManager = documentManager;
    }
    
    public List<WorkFlow> getAllWorkFlows() throws BusinessException {
        try {
            return workFlowDA.getAllWorkFlows();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des flux de travail.", e);
        }
    }

    public List<WorkFlow> getAllBuying() throws BusinessException {
        try {
            return workFlowDA.getAllBuying();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des flux d'achat.", e);
        }
    }

    public List<WorkFlow> getAllInternal() throws BusinessException {
        try {
            return workFlowDA.getAllInternal();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des flux internes.", e);
        }
    }

    public List<WorkFlow> getAllSelling() throws BusinessException {
        try {
            return workFlowDA.getAllSelling();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des flux de vente.", e);
        }
    }

    public List<WorkFlowType> getWorkFlowType() throws BusinessException {
        try {
            return workFlowDA.getWorkFlowType();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des types de flux.", e);
        }
    }

    public void addWorkFlowType(WorkFlowType workFlowType) throws BusinessException {
        if (workFlowType == null) {
            throw new BusinessException("Le type de flux ne peut pas être nul.");
        }
        
        try {
            workFlowDA.addWorkFlowType(workFlowType);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout du type de flux.", e);
        }
    }

    public void addWorkFlow(WorkFlow workFlow) throws BusinessException {
        if (workFlow == null) {
            throw new BusinessException("Le flux de travail ne peut pas être nul.");
        }

        try {
            workFlowDA.addWorkFlow(workFlow);
            for (Document doc : workFlow.getDocuments()) {
                documentManager.createDocument(doc);
                workFlowDA.addWorkFlow(workFlow);
    // changez cela en fonction de la manière dont vous gérez les IDs des flux de travail et des documents
            }
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
            workFlowDA.changeStatus(workFlowId, status);
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

        try {
            documentManager.createDocument(document);
            workFlowDA.addDocument(workFlowId, document);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout du document.", e);
        }
    }
}
