package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class DocumentManager {
    public List<Document> getAllDocuments() throws DataBaseException {
        DocumentData documentData = new DocumentData();
        return documentData.getAllDocument();
    }

    public Document getDocument(int id) throws DataBaseException {
        DocumentData documentData = new DocumentData();
        return documentData.getDocument(id);
    }

    public List<DocumentType> getDocumentTypes() throws DataBaseException {
        DocumentData documentData = new DocumentData();
        return documentData.getDocumentTypes();
    }

    public void addDocument(Document docType) throws DataBaseException {
        DocumentData documentData = new DocumentData();
        documentData.addDocument(docType);
    }

    public void createDocument(Document document) throws DataBaseException {
        DocumentData documentData = new DocumentData();
        documentData.createDocument(document);
    }

    public void receiveDelivery(int documentId, LocalDate date) throws DataBaseException {
        DocumentData documentData = new DocumentData();
        documentData.receiveDelivery(documentId, date);
    }

    public void sendDelivery(int documentId, LocalDate date) throws DataBaseException {
        DocumentData documentData = new DocumentData();
        documentData.sendDelivery(documentId, date);
    }

    public void deleteDocument(int documentId) throws DataBaseException {
        DocumentData documentData = new DocumentData();
        documentData.deleteDocument(documentId);
    }
}
