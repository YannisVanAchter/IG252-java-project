package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.LocationProductDA;
import main.java.be.henallux.project.model.LocationProduct;
import main.java.be.henallux.project.data.DocumentDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;

import main.java.be.henallux.project.model.*;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.time.LocalDate;
import java.util.List;

public class DocumentManager {

    private final DocumentDA documentDA;
    protected final ProductManager productManager;
    protected final StockManager stockManager;
    private final AddressManager addressManager;
    private final LocationProductDA locationProductDA;

    public DocumentManager() {
        this.documentDA = DocumentDA.getInstance();
        this.productManager = new ProductManager();
        this.stockManager = new StockManager();
        this.addressManager = new AddressManager();
        this.locationProductDA = LocationProductDA.getInstance();
    }

    public List<Document> getAllDocuments() throws BusinessException, DataValidationException {
        try {
            return documentDA.getAll();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving documents.", e);
        }
    }

    public Document getDocument(int id) throws BusinessException, DataValidationException {
        if (id <= 0) {
            throw new BusinessException("The document ID must be a positive number.");
        }
        try {
            return documentDA.getById(id);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the document.", e);
        }
    }

    public List<DocumentType> getDocumentTypes() {
            return DocumentTypeRepository.getInstance().getDocumentTypes();
    }

    public Address getAddress(int id) throws BusinessException, DataValidationException, DataBaseException {
        return addressManager.getAddressById(id);
    }

    public LocationProduct getLocationProduct(int id) throws BusinessException, DataValidationException {
        try {
            return locationProductDA.getById(id);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving location product.", e);
        }
    }

    public DocumentType addDocumentType(DocumentType docType) throws BusinessException {
        if (docType == null) {
            throw new BusinessException("The document type cannot be null.");
        }
        try {
            documentDA.addDocumentType(docType);
            return docType;
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the document type.", e);
        }
    }

    public boolean createDocument(Document document) throws BusinessException, DataValidationException {
        if (document == null) {
            throw new BusinessException("The document cannot be null.");
        }
        try {
            boolean retCode = documentDA.insert(document);
            for (Detail detail : document.getDetails().getDetails()) {
                DetailDA.insert(detail);
            }
            return retCode;
        } catch (DataBaseException e) {
            throw new BusinessException("Error when creating the document.", e);
        }
    }

    public void updateDocument(Document oldDocument, Document newDocument) throws BusinessException, DataValidationException {
        if (oldDocument == null) {
            throw new BusinessException("The old document cannot be null.");
        }
        if (newDocument == null) {
            throw new BusinessException("The new document cannot be null.");
        }
        try {
            if (!documentDA.checkExist(oldDocument)) {
                throw new BusinessException("The document with the specified ID does not exist.");
            }
            documentDA.update(oldDocument, newDocument);
            for ()
        } catch (DataBaseException e) {
            throw new BusinessException("Error when updating the document.", e);
        }
    }

        public boolean deleteDocument(Document document) throws BusinessException, DataValidationException {
        if (document == null) {
            throw new BusinessException("The document cannot be null.");
        }
        try {
            if (!documentDA.checkExist(document)) {
                throw new BusinessException("The document doesn't exist.");
            }
            documentDA.delete(document);
            return true;
        } catch (DataBaseException e) {
            throw new BusinessException("Error when deleting the document.", e);
        }
    }

    public void receiveDelivery(Document document, LocalDate date) throws BusinessException, DataValidationException {
        if (document == null) {
            throw new BusinessException("The document cannot be null.");
        }
        if (date == null) {
            throw new BusinessException("The delivery date cannot be null.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new BusinessException("The delivery date cannot be in the future.");
        }
        try {
            if (!documentDA.checkExist(document)) {
                throw new BusinessException("The document doesn't exist.");
            }
            documentDA.updateActualReceiveDate(document, date);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when receiving the delivery.", e);
        }
    }

    public void sendDelivery(Document document, LocalDate date) throws BusinessException, DataValidationException {
        if (document == null) {
            throw new BusinessException("The document cannot be null.");
        }
        if (date == null) {
            throw new BusinessException("The delivery date cannot be null.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new BusinessException("The delivery date cannot be in the future.");
        }
        try {
            // Business rule — check that the document exists before sending the delivery
            if (!documentDA.checkExist(document)) {
                throw new BusinessException("The document with the specified ID does not exist.");
            }
            documentDA.updateActualSendDate(document, date);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when sending the delivery.", e);
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
