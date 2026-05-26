package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.FidelityCardDA;
import main.java.be.henallux.project.data.CheckoutDA;
import main.java.be.henallux.project.data.DocumentDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.FidelityCard;
import main.java.be.henallux.project.model.ClientSupplier;
import java.util.List;
public class ClientManager extends ClientSupplierManager {

    private final FidelityCardDA fidelityCardDA;
    private final CheckoutDA checkoutDA;
    private final DocumentDA documentDA;
    
    public ClientManager() {
        super();
        this.fidelityCardDA = FidelityCardDA.getInstance();
        this.checkoutDA = CheckoutDA.getInstance();
        this.documentDA = DocumentDA.getInstance();
    }

    public List<ClientSupplier> getAllClients() throws BusinessException {
        return getAllClientSuppliers();
    }

    public List<ClientSupplier> getClientByCardID (int cardId) throws BusinessException {
        if (cardId <= 0) {
            throw new BusinessException("The card ID is invalid.");
        }
        try {
            return clientSupplierDA.getClientByCardID(cardId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the client by card ID.", e);
        }
    }

    public void createFidelityCard(int clientId) throws BusinessException {
        // Validation
        if (clientId <= 0) {
            throw new BusinessException("Error: Client ID is invalid.");
        }
        try {
            // Business rule — a client can only have one card
            
            if (fidelityCardDA.hasFidelityCard(clientId)) {
                throw new BusinessException("Error: This client already has a loyalty card.");
            }
            // TODO - confirm return object of create method
            fidelityCardDA.createFidelityCard(clientId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error creating loyalty card.", e);
        }
    }

    public boolean validateFidelityCardOwnership(int clientId, int cardId) throws BusinessException {
        // Validation
        if (clientId <= 0 || cardId <= 0) {
            throw new BusinessException("Error: The provided IDs are invalid.");
        }
        try {
            return fidelityCardDA.validateFidelityCardOwnership(clientId, cardId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error validating loyalty card ownership.", e);
        }
    }

    public void addCheckout(List<Product> products) throws BusinessException {
        // Validation
        if (products == null || products.isEmpty()) {
            throw new BusinessException("Error: The product list cannot be empty.");
        }
        try {
            checkoutDA.addCheckout(products);
        } catch (DataBaseException e) {
            throw new BusinessException("Error recording checkout.", e);
        }
    }

    public void addCheckout(List<Product> products, int clientId) throws BusinessException {
        // Validation
        if (products == null || products.isEmpty()) {
            throw new BusinessException("Error: The product list cannot be empty.");
        }
        if (clientId <= 0) {
            throw new BusinessException("Error: The client ID is invalid.");
        }
        try {
            // Business rule — verify that the client exists
            super.getClientSupplier(clientId);
            checkoutDA.addCheckout(products, clientId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error recording checkout.", e);
        }
    }

    public void addCheckout(List<Product> products, int clientId,
                            FidelityCard fidelityCard, boolean useFidelityPoint) throws BusinessException {
        if (products == null || products.isEmpty()) {
            throw new BusinessException("Error: The product list cannot be empty.");
        }
        if (clientId <= 0) {
            throw new BusinessException("Error: The client ID is invalid.");
        }
        if (fidelityCard == null) {
            throw new BusinessException("Error: The loyalty card is invalid.");
        }
        try {
            // Business rule — verify that the card belongs to the client
            if (!fidelityCardDA.validateFidelityCardOwnership(clientId, fidelityCard.getId())) {
                throw new BusinessException("Error: This loyalty card does not belong to the specified client.");
            }
            checkoutDA.addCheckout(products, clientId, fidelityCard, useFidelityPoint);
        } catch (DataBaseException e) {
            throw new BusinessException("Error recording checkout.", e);
        }
    }

    public void addCheckout(List<Product> products, int clientId,
                            FidelityCard fidelityCard) throws BusinessException {
        // Delegate to the complete version without using points
        addCheckout(products, clientId, fidelityCard, false);
    }

    public void deleteClientAccount(int clientId) throws BusinessException {
        if (clientId <= 0) {
            throw new BusinessException("Error: The client ID is invalid.");
        }
        try {
            // Orchestration — remove the loyalty card before the account deletion to respect the business rule that a card cannot exist without an associated client
            if (fidelityCardDA.hasFidelityCard(clientId)) {
                fidelityCardDA.deleteFidelityCard(clientId);
            }
            super.deleteClientSupplier(clientId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error: Failed to delete client account.", e);
        }
    }

    public void deleteClientAccount(int clientId, int cardId) throws BusinessException {
        if (clientId <= 0 || cardId <= 0) {
            throw new BusinessException("Error: The provided IDs are invalid.");
        }
        try {
            // Business rule — verify that the card belongs to the client before deletion
            if (!fidelityCardDA.validateFidelityCardOwnership(clientId, cardId)) {
                throw new BusinessException("Error: This loyalty card does not belong to the specified client.");
            }
            fidelityCardDA.deleteFidelityCard(clientId);
            super.deleteClientSupplier(clientId, cardId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error: Failed to delete client account.", e);
        }
    }

    @Override
    public void placeOrder(int clientSupplierId, List<Product> products) throws BusinessException {
        if (clientSupplierId <= 0) {
            throw new BusinessException("Error: The client ID is invalid.");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessException("Error: The product list cannot be empty.");
        }
        try {
            // Business rule — verify that the client exists before placing an order
            super.getClientSupplier(clientSupplierId);
            checkoutDA.addCheckout(products, clientSupplierId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error: Failed to place order.", e);
        }
    }
}