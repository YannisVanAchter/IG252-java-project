package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.DocumentManager;
import main.java.be.henallux.project.business.WorkFlowManager;
import main.java.be.henallux.project.business.ClientSupplierManager;
import main.java.be.henallux.project.business.AddressManager;
import main.java.be.henallux.project.controller.ClientSupplierController;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * @see DocumentManager
 * @see WorkFlowManager
 */
public class DocumentController {

    private final DocumentManager documentManager;
    private final ClientSupplierManager clientSupplierManager;
    private final ClientSupplierController clientSupplierController;
    private final AddressManager addressManager;
    private final WorkFlowManager workFlowManager;

    public DocumentController() {
        this.documentManager = new DocumentManager();
        this.clientSupplierManager = new ClientSupplierManager();
        this.clientSupplierController = new ClientSupplierController();
        this.addressManager = new AddressManager();
        this.workFlowManager = new WorkFlowManager();
    }

    /**
     * Returns all documents.
     * @return list of all {@link Document}
     * @see DocumentManager#getAllDocuments()
     */
    public ArrayList<Document> getAllDocuments() throws BusinessException, DataValidationException {
        return new ArrayList<>(documentManager.getAllDocuments());
    }

    /**
     * Returns a document by ID.
     * @param id the document ID
     * @return the matching {@link Document}
     * @see DocumentManager#getDocument(int)
     */
    public Document getDocument(int id) throws BusinessException, DataValidationException {
        return documentManager.getDocument(id);
    }

    /**
     * Returns all document types.
     * @return list of all {@link DocumentType}
     * @see DocumentManager#getDocumentTypes()
     */
    public ArrayList<DocumentType> getAllDocumentTypes() throws BusinessException, DataValidationException {
        return new ArrayList<>(documentManager.getDocumentTypes());
    }

    /**
     * Creates and registers a new document type.
     * @param name the name of the document type
     * @return the created {@link DocumentType}
     * @see DocumentManager#addDocumentType(DocumentType)
     */
    public DocumentType addDocumentType(String name) throws BusinessException, DataValidationException {
        DocumentType newType = new DocumentType(name);
        documentManager.addDocumentType(newType);
        return newType;
    }

    /**
     * Returns all clients and suppliers.
     * @return list of all {@link ClientSupplier}
     * @see ClientSupplierController#getAllClientsSuppliers()
     */
    public ArrayList<ClientSupplier> getAllClientSupplier() throws BusinessException, DataValidationException {
        return new ArrayList<>(clientSupplierManager.getAllClientSuppliers());
    }

    /**
     * Records a delivery reception for a document.
     * @param document      the chosen document
     * @param date            the reception date
     * @see DocumentManager#receiveDelivery(Document, LocalDate)
     */
    public void receiveDelivery(Document document, LocalDate date) throws BusinessException, DataValidationException {
        documentManager.receiveDelivery(document, date);
    }

    /**
     * Records a delivery send for a document.
     * @param document      the chosen document
     * @param date            the send date
     * @see DocumentManager#sendDelivery(Document, LocalDate)
     */
    public void sendDelivery(Document document, LocalDate date) throws BusinessException, DataValidationException {
        documentManager.sendDelivery(document, date);
    }

    /**
     * Deletes a document.
     * @param document the {@link Document} to delete
     * @return {@code true} if deleted successfully, {@code false} otherwise
     * @see DocumentManager#deleteDocument(Document)
     */
    public boolean deleteDocument(Document document) throws BusinessException, DataValidationException {
        documentManager.deleteDocument(document);
        return true;
    }

    /**
     * Creates a document with address, workflow, and document type.
     * @param document the {@link Document} to create
     * @return the created {@link Document}
     * @see DocumentManager#createDocument(Document)
     */
    public void createDocument(Document document) throws BusinessException, DataValidationException {
        addressManager.createAddress(document.getAddress(), document.getAddress().getLocality());
        workFlowManager.createWorkFlow(document.getWorkflow());
        documentManager.addDocumentType(document.getDocumentType());
    }

    /**
     * Updates the document.
     *
     * @param oldDoc the {@link Document} to update
     * @param newDoc  the new {@link Address}
     * @see DocumentManager#updateDocument(Document, Document)
     */
    public void updateDocument(Document oldDoc, Document newDoc) throws BusinessException, DataValidationException {
        documentManager.updateDocument(oldDoc, newDoc);
    }

    public ClientSupplier getUs() throws BusinessException, DataValidationException {
        return clientSupplierController.getUs();
    }
}