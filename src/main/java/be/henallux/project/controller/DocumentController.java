package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.AddressManager;
import main.java.be.henallux.project.business.ClientManager;
import main.java.be.henallux.project.business.SupplierManager;
import main.java.be.henallux.project.business.DocumentManager;
import main.java.be.henallux.project.business.WorkFlowManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Document;
import main.java.be.henallux.project.model.DocumentType;
import main.java.be.henallux.project.model.LocationProduct;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * @see DocumentManager
 * @see WorkFlowManager
 */
public class DocumentController {

    private final DocumentManager documentManager;
    private final WorkFlowManager workFlowManager;
    private final SupplierManager supplierManager;
    private final ClientManager clientManager;
    private final AddressManager addressManager;

    public DocumentController() {
        this.documentManager = new DocumentManager();
        this.workFlowManager = new WorkFlowManager();
        this.supplierManager = new SupplierManager();
        this.clientManager = new ClientManager();
        this.addressManager = new AddressManager();
    }

    /**
     * Returns all documents.
     * @return list of all {@link Document}
     * @see DocumentManager#getAllDocuments()
     */
    public ArrayList<Document> getAllDocuments() throws BusinessException {
        return new ArrayList<>(documentManager.getAllDocuments());
    }

    /**
     * Returns a document by ID.
     * @param id the document ID
     * @return the matching {@link Document}
     * @see DocumentManager#getDocument(int)
     */
    public Document getDocument(int id) throws BusinessException {
        return documentManager.getDocument(id);
    }

    /**
     * Returns all document types.
     * @return list of all {@link DocumentType}
     * @see DocumentManager#getDocumentTypes()
     */
    public ArrayList<DocumentType> getAllDocumentTypes() throws BusinessException {
        return new ArrayList<>(documentManager.getDocumentTypes());
    }

    /**
     * Creates and registers a new document type.
     * @param name the name of the document type
     * @return the created {@link DocumentType}
     * @see DocumentManager#addDocumentType(DocumentType)
     */
    public DocumentType addDocumentType(String name) throws BusinessException {
        DocumentType newType = new DocumentType(name);
        documentManager.addDocumentType(newType);
        return newType;
    }

    /**
     * Returns all clients and suppliers.
     * @return list of all {@link ClientSupplier}
     * @see ClientManager#getAllClientSuppliers()
     */
    public ArrayList<ClientSupplier> getAllClientSupplier() throws BusinessException {
        return new ArrayList<>(clientManager.getAllClientSuppliers());
    }

    /**
     * Records a delivery reception for a document.
     * @param documentId      the document ID
     * @param date            the reception date
     * @param locationProduct the stock location
     * @see DocumentManager#receiveDelivery(int, LocalDate, LocationProduct)
     */
    public void receiveDelivery(int documentId, LocalDate date, LocationProduct locationProduct) throws BusinessException {
        documentManager.receiveDelivery(documentId, date, locationProduct);
    }

    /**
     * Records a delivery send for a document.
     * @param documentId      the document ID
     * @param date            the send date
     * @param locationProduct the stock location
     * @see DocumentManager#sendDelivery(int, LocalDate, LocationProduct)
     */
    public void sendDelivery(int documentId, LocalDate date, LocationProduct locationProduct) throws BusinessException {
        documentManager.sendDelivery(documentId, date, locationProduct);
    }

    /**
     * Deletes a document.
     * @param doc the {@link Document} to delete
     * @return {@code true} if deleted successfully, {@code false} otherwise
     * @see DocumentManager#deleteDocument(int)
     */
    public boolean deleteDocument(Document doc) {
        try {
            documentManager.deleteDocument(doc.getId());
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * Creates a document with address, workflow, and document type.
     * @param document the {@link Document} to create
     * @return the created {@link Document}
     * @see DocumentManager#createDocument(Document)
     */
    public Document createDocument(Document document) throws BusinessException {
        addressManager.createAddress(document.getAddress());
        workFlowManager.addWorkFlow(document.getWorkflow());
        documentManager.addDocumentType(document.getDocumentType());
        return documentManager.createDocument(document);
    }

    /**
     * Updates a document with an address and workflow.
     * @param document the {@link Document} to update
     * @see DocumentManager#updateDocument(Document)
     */
    public void updateDocument(Document document) throws BusinessException {
        addressManager.createAddress(document.getAddress());
        workFlowManager.addWorkFlow(document.getWorkflow());
        documentManager.updateDocument(document);
    }
}