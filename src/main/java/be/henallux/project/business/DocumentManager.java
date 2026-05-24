package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.DocumentDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;

import main.java.be.henallux.project.model.Document;
import main.java.be.henallux.project.model.DocumentType;
import main.java.be.henallux.project.model.LocationProduct;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.DocumentDetails;
import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.WorkFlow;
import main.java.be.henallux.project.model.Address;

import java.time.LocalDate;
import java.util.List;

public class DocumentManager {

    private final DocumentDA documentDA;
    protected final ProductManager productManager;
    protected final StockManager stockManager;

    public DocumentManager() {
        this.documentDA = DocumentDA.getInstance();
        this.productManager = new ProductManager();
        this.stockManager = new StockManager();
    }

    public List<Document> getAllDocuments() throws BusinessException {
        try {
            return documentDA.getAllDocuments();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving documents.", e);
        }
    }

    public Document getDocument(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("The document ID must be a positive number.");
        }
        try {
            return documentDA.getDocument(id);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the document.", e);
        }
    }

    public List<DocumentType> getDocumentTypes() throws BusinessException {
        try {
            return documentDA.getDocumentTypes();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving document types.", e);
        }
    }

    public void addDocumentType(DocumentType docType) throws BusinessException {
        if (docType == null) {
            throw new BusinessException("The document type cannot be null.");
        }
        try {
            documentDA.addDocumentType(docType);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the document type.", e);
        }
    }

    public void createDocument(Document document) throws BusinessException {
        if (document == null) {
            throw new BusinessException("The document cannot be null.");
        }
        try {
            documentDA.createDocument(document);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when creating the document.", e);
        }
    }

    public void receiveDelivery(int documentId, LocalDate date, LocationProduct locationProduct) throws BusinessException {
        if (documentId <= 0) {
            throw new BusinessException("The document ID must be a positive number.");
        }
        if (date == null) {
            throw new BusinessException("The delivery date cannot be null.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new BusinessException("The delivery date cannot be in the future.");
        }
        try {
            documentDA.receiveDelivery(documentId, date, locationProduct);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when receiving the delivery.", e);
        }
    }

    public void sendDelivery(int documentId, LocalDate date, LocationProduct locationProduct) throws BusinessException {
        if (documentId <= 0) {
            throw new BusinessException("The document ID must be a positive number.");
        }
        if (date == null) {
            throw new BusinessException("The delivery date cannot be null.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new BusinessException("The delivery date cannot be in the future.");
        }
        try {
            // Business rule — check that the document exists before sending the delivery
            if (!documentDA.documentExists(documentId)) {
                throw new BusinessException("The document with the specified ID does not exist.");
            }
            documentDA.sendDelivery(documentId, date, locationProduct);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when sending the delivery.", e);
        }
    }

    public void deleteDocument(int documentId) throws BusinessException {
        if (documentId <= 0) {
            throw new BusinessException("The document ID must be a positive number.");
        }
        try {
            if (!documentDA.documentExists(documentId)) {
                throw new BusinessException("The document does not exist.");
            }
            documentDA.deleteDocument(documentId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when deleting the document.", e);
        }
    }

    public List<Document> getDeliveryOrders() throws BusinessException {
        try {
            return documentDA.getDeliveryOrders();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving delivery orders.", e);
        }
    }

    public List<Document> getDeliveryOrderByClient(int clientId) throws BusinessException {
        if (clientId <= 0) {
            throw new BusinessException("The client ID must be a positive number.");
        }
        try {
            return documentDA.getDeliveryOrdersByClient(clientId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving delivery orders by client.", e);
        }
    }
}
