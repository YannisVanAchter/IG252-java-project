package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;

import java.time.LocalDate;
import java.util.List;

public class DocumentManager {

    private final DocumentData documentData;

    public DocumentManager(DocumentData documentData) {
        this.documentData = documentData;
    }

    public List<Document> getAllDocuments() throws BusinessException {
        try {
            return documentData.getAllDocuments();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des documents.", e);
        }
    }

    public Document getDocument(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("L'identifiant du document doit être un nombre positif.");
        }
        try {
            return documentData.getDocument(id);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération du document.", e);
        }
    }

    public List<DocumentType> getDocumentTypes() throws BusinessException {
        try {
            return documentData.getDocumentTypes();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des types de documents.", e);
        }
    }

    public void addDocumentType(DocumentType docType) throws BusinessException {
        if (docType == null) {
            throw new BusinessException("Le type de document ne peut pas être nul.");
        }
        try {
            documentData.addDocumentType(docType);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout du type de document.", e);
        }
    }

    public void createDocument(Document document) throws BusinessException {
        if (document == null) {
            throw new BusinessException("Le document ne peut pas être nul.");
        }
        try {
            documentData.createDocument(document);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la création du document.", e);
        }
    }

    public void receiveDelivery(int documentId, LocalDate date) throws BusinessException {
        if (documentId <= 0) {
            throw new BusinessException("L'identifiant du document doit être un nombre positif.");
        }
        if (date == null) {
            throw new BusinessException("La date de livraison ne peut pas être nulle.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new BusinessException("La date de livraison ne peut pas être dans le futur.");
        }
        try {
            documentData.receiveDelivery(documentId, date);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la réception de la livraison.", e);
        }
    }

    public void sendDelivery(int documentId, LocalDate date) throws BusinessException {
        if (documentId <= 0) {
            throw new BusinessException("L'identifiant du document doit être un nombre positif.");
        }
        if (date == null) {
            throw new BusinessException("La date de livraison ne peut pas être nulle.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new BusinessException("La date de livraison ne peut pas être dans le futur.");
        }
        try {
            // Règle métier — vérifier que le document existe avant d'envoyer la livraison
            if (!documentData.documentExists(documentId)) {
                throw new BusinessException("Le document avec l'identifiant spécifié n'existe pas.");
            }
            documentData.sendDelivery(documentId, date);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'envoi de la livraison.", e);
        }
    }

    public void deleteDocument(int documentId) throws BusinessException {
        if (documentId <= 0) {
            throw new BusinessException("L'identifiant du document doit être un nombre positif.");
        }
        try {
            if (!documentData.documentExists(documentId)) {
                throw new BusinessException("Le document n'existe pas.");
            }
            documentData.deleteDocument(documentId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression.", e);
        }
    }

    public List<Document> getDeliveryOrders() throws BusinessException {
        try {
            return documentData.getDeliveryOrders();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des commandes de livraison.", e);
        }
    }

    public List<Document> getDeliveryOrderByClient(int clientId) throws BusinessException {
        if (clientId <= 0) {
            throw new BusinessException("L'identifiant du client doit être un nombre positif.");
        }
        try {
            return documentData.getDeliveryOrdersByClient(clientId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des commandes de livraison par client.", e);
        }
    }
}
