package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ClientManager {
    
    public List<ClientSupplier> getAllClients() throws DataBaseException {
        ClientSupplierData data = new ClientSupplierData();
        return data.getAllClients();
    }

    public void createFidelityCard(int clientId) throws DataBaseException {
        FidelityCardData data = new FidelityCardData();
        data.createFidelityCard(clientId);
    }

    public boolean validateFidelityCardOwnership(int clientId, int cardId) throws DataBaseException {
        FidelityCardData data = new FidelityCardData();
        return data.validateFidelityCardOwnership(clientId, cardId);
    }

    public void addCheckout(List<Product> products) throws DataBaseException {
        CheckoutData data = new CheckoutData();
        data.addCheckout(products);
    }

    public void addCheckout(List<Product> products, int clientId) throws DataBaseException {
        CheckoutData data = new CheckoutData();
        data.addCheckout(products, clientId);
    }

    public void addCheckout(List<Product> products, int clientId, FidelityCard fidelityCardId, boolean useFidelityPoint) throws DataBaseException {
        CheckoutData data = new CheckoutData();
        data.addCheckout(products, clientId, fidelityCardId, useFidelityPoint);
    }

    public void addCheckout(List<Product> products, int clientId, FidelityCard fidelityCardId) throws DataBaseException {
        CheckoutData data = new CheckoutData();
        data.addCheckout(products, clientId, fidelityCardId);
    }

    public int deleteClientAccount(int clientId) throws DataBaseException {
        ClientSupplierData data = new ClientSupplierData();
        return data.deleteClientAccount(clientId);
    }

    public int deleteClientAccount(int clientId, int cardId) throws DataBaseException {
        ClientSupplierData data = new ClientSupplierData();
        return data.deleteClientAccount(clientId, cardId);
    }

    public List<Document> getDeliveryOrders() throws DataBaseException {
        DocumentData data = new DocumentData();
        return data.getDeliveryOrders();
    }
}
