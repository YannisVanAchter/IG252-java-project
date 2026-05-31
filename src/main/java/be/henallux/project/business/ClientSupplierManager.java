package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.ClientSupplierDA;
import main.java.be.henallux.project.data.AddressDA;
import main.java.be.henallux.project.data.FidelityCardDA;
import main.java.be.henallux.project.data.ProductDA;
import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.business.exception.BusinessException;

import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.FidelityCard;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.lang.foreign.AddressLayout;
import java.util.List;

public abstract class ClientSupplierManager {

    private final ClientSupplierDA clientSupplierDA;
    private final FidelityCardDA fidelityCardDA;
    private final ProductDA productDA;

    public ClientSupplierManager() {
        this.clientSupplierDA = ClientSupplierDA.getInstance();
        this.fidelityCardDA = FidelityCardDA.getInstance();
        this.productDA = ProductDA.getInstance();
    }

//===================================
//              READ
//===================================

    public List<ClientSupplier> getAllClientSuppliers() throws BusinessException, DataValidationException {
        try {
            return clientSupplierDA.getAll();
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the client suppliers.", e);
        }
    }

    public List<ClientSupplier> getAllSuppliers() throws BusinessException, DataValidationException {
        try {
            return clientSupplierDA.getSuppliers();
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the client suppliers.", e);
        }
    }


    public List<ClientSupplier> getAllClients() throws BusinessException, DataValidationException {
        try {
            return clientSupplierDA.getClients();
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the clients .", e);
        }
    }

    public ClientSupplier getClientByCardID (int id) throws BusinessException, DataValidationException {
        if (id <= 0) {
            throw new BusinessException("The card ID is invalid.");
        }
        try {
            return clientSupplierDA.getById(id);
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the client by card ID.", e);
        }
    }

//===================================
//              CREATE
//===================================

    public void createClientSupplier(ClientSupplier clientSupplier) throws BusinessException, DataValidationException {
        if (clientSupplier == null) {
            throw new BusinessException("The client supplier cannot be null.");
        }        
        if (clientSupplier.getName() == null || clientSupplier.getName().isBlank()) {
            throw new BusinessException("The client supplier name is required.");
        }
        if (clientSupplier.getIsUs() && (clientSupplier.getIsSupplier() || clientSupplier.getIsClient())) {
            throw new BusinessException("The client supplier cannot be Us and (supplier or client).");
        }
        try {
            clientSupplierDA.insert(clientSupplier);
        } catch (DataBaseException e) {
            throw new BusinessException("Error creating the client supplier.", e);
        }
    }

    public void createFidelityCard(FidelityCard fidelityCard) throws BusinessException, DataValidationException {
        // Validation
        if (fidelityCard == null) {
            throw new BusinessException("Error : fidelity card cannot be null.");
        }
        try {
            // Business rule — a client can only have one card
            List<FidelityCard> fidelityCardList = fidelityCardDA.getAll();
            boolean cardFound = fidelityCardList.stream().anyMatch(fidelityCard1 ->  fidelityCard1.getClient() == fidelityCard.getClient());
            if (cardFound) {
                throw new BusinessException("Error: This client already has a loyalty card.");
            }
            boolean insert = fidelityCardDA.insert(fidelityCard);
        } catch (DataBaseException e) {
            throw new BusinessException("Error creating loyalty card.", e);
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
                            FidelityCard fidelityCard, boolean useFidelityPoint) throws BusinessException, DataValidationException {
        if (products == null || products.isEmpty()) {
            throw new BusinessException("Error: The product list cannot be empty.");
        }
        if (clientId <= 0) {
            throw new BusinessException("Error: The client ID is invalid.");
        }
        if (fidelityCard == null) {
            addCheckout(products, clientId);
        } else {
            try {
                // Business rule — verify that the card belongs to the client
                if (!validateFidelityCardOwnership(clientId, fidelityCard.getId())) {
                    throw new BusinessException("Error: This loyalty card does not belong to the specified client.");
                }
                checkoutDA.addCheckout(products, clientId, fidelityCard, useFidelityPoint);
            } catch (DataBaseException e) {
                throw new BusinessException("Error recording checkout.", e);
            }
        }
    }

    public void addCheckout(List<Product> products, int clientId,
                            FidelityCard fidelityCard) throws BusinessException {
        // Delegate to the complete version without using points
        addCheckout(products, clientId, fidelityCard, false);
    }

//===================================
//              UPDATE
//===================================

    public Address changeAddress(ClientSupplier oldModel, Address newAddress) throws BusinessException, DataValidationException {
        if (oldModel == null) {
            throw new BusinessException("The client supplier cannot be null.");
        }
        if (newAddress == null) {
            throw new BusinessException("The address cannot be null.");
        }
        try {
            clientSupplierDA.updateAddress(oldModel, newAddress);
        } catch (DataBaseException e) {
            throw new BusinessException("Error changing the address.", e);
        }
        return oldModel.getAddress();
    }

    public int changePhoneNumber(ClientSupplier oldModel, String newPhoneNumber) throws BusinessException, DataValidationException {
        if (oldModel  == null) {
            throw new BusinessException("The client supplier cannot be null.");
        }
        if (newPhoneNumber == null || newPhoneNumber.isBlank()) {
            throw new BusinessException("The phone number cannot be null or blank.");
        }
        if (newPhoneNumber.length() != 11) {
            throw new BusinessException("The phone number must be 11 digits.");
        }
        try {
            clientSupplierDA.updatePhoneNumber(oldModel, newPhoneNumber);
            return newPhoneNumber.length();
        } catch (DataBaseException e) {
            throw new BusinessException("Error changing the phone number.", e);
        }
    }

    public String changeEmail(ClientSupplier oldModel, String newEmail) throws BusinessException, DataValidationException {
        if (oldModel == null) {
            throw new BusinessException("The client supplier cannot be null.");
        }
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new BusinessException("The email cannot be null or empty.");
        }
        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BusinessException("The email is not in a valid format.");
        }
        try {
            clientSupplierDA.updateEmail(oldModel, newEmail);
            return newEmail;
        } catch (DataBaseException e) {
            throw new BusinessException("Error changing the email.", e);
        }
    }


//===================================
//              DELETE
//===================================

    public boolean deleteClientSupplier(ClientSupplier clientSupplier) throws BusinessException, DataValidationException {
        if (clientSupplier == null) {
            throw new BusinessException("The client supplier cannot be null.");
        }
        try {
            clientSupplierDA.delete(clientSupplier);
            return true;
        } catch (DataBaseException e) {
            throw new BusinessException("Error deleting the client supplier.", e);
        }
    }

    public void deleteClientAccount(int clientId) throws BusinessException, DataValidationException {
        if (clientId <= 0) {
            throw new BusinessException("Error: The client ID is invalid.");
        }
        try {
            ClientSupplier clientSupplier = clientSupplierDA.getById(clientId);
            clientSupplierDA.delete(clientSupplier);
        } catch (DataBaseException e) {
            throw new BusinessException("Error: Failed to delete client account.", e);
        }
    }

    public void deleteClientAccount(int clientId, int cardId) throws BusinessException, DataValidationException {
        deleteClientAccount(clientId);
    }


//===================================
//              OTHERS
//===================================
// validate client with clientId is associated with fidelity card with cardId
    public boolean validateFidelityCardOwnership(int clientId, int cardId) throws BusinessException, DataValidationException {
        // Validation
        if (clientId <= 0 || cardId <= 0) {
            throw new BusinessException("Error: The provided IDs are invalid.");
        }
        try {
           ClientSupplier clientSupplier = clientSupplierDA.getById(clientId);
           if (clientSupplier == null) {
               return false;
           }
           if (clientSupplier.getFidelityCard().getId() != cardId) {
               return false;
           }
           return true;
        } catch (DataBaseException e) {
            throw new BusinessException("Error validating loyalty card ownership.", e);
        }
    }

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

    public void placeOrder(int clientSupplierId, List<Product> products) throws BusinessException {
        if (clientSupplierId <= 0) {
            throw new BusinessException("The supplier ID is invalid.");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessException("The list of products is required.");
        }
        try {
            // ? What to do ?
        } catch (DataBaseException e) {
            throw new BusinessException("Error when placing the order.", e);
        }
    }

    public abstract void placeOrder(int clientSupplierId, List<Product> products) throws BusinessException;
}
