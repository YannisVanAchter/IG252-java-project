package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.ClientSupplierDA;
import main.java.be.henallux.project.data.AddressDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Address;
import java.util.List;

public abstract class ClientSupplierManager {

    protected final ClientSupplierDA clientSupplierDA;
    private final AddressManager addressManager;

    public ClientSupplierManager(ClientSupplierDA clientSupplierDA, AddressManager addressManager) {
        this.clientSupplierDA = clientSupplierDA;
        this.addressManager = addressManager;
    }
    
    public List<ClientSupplier> getAllClientSuppliers() throws BusinessException {
        try {
            return clientSupplierDA.getAllClientSuppliers();
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the client suppliers.", e);
        }
    }

    public ClientSupplier getClientSupplier(int id) throws BusinessException {        
        if (id <= 0) {
            throw new BusinessException("The client supplier ID must be a positive number.");
        }
        
        try {
            ClientSupplier result = clientSupplierDA.getClientSupplier(id);
            if (result == null) {
                throw new BusinessException("The client supplier does not exist.");
            }
            return result;
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the client supplier.", e);
        }
    }

    public void createClientSupplier(ClientSupplier clientSupplier) throws BusinessException {
        if (clientSupplier == null) {
            throw new BusinessException("The client supplier cannot be null.");
        }        
        if (clientSupplier.getName() == null || clientSupplier.getName().isBlank()) {
            throw new BusinessException("The client supplier name is required.");
        }
        try {
            clientSupplierDA.createClientSupplier(clientSupplier);
        } catch (DataBaseException e) {
            throw new BusinessException("Error creating the client supplier.", e);
        }
    }

    public void changeAddress(int id, Address address) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("The client supplier ID must be a positive number.");
        }
        if (address == null) {
            throw new BusinessException("The address cannot be null.");
        }
        try {
            addressManager.changeAddress(id, address);
        } catch (DataBaseException e) {
            throw new BusinessException("Error changing the address.", e);
        }
    }

    public void changePhoneNumber(int id, int phoneNumber) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("The client supplier ID must be a positive number.");
        }
        if (phoneNumber <= 0) {
            throw new BusinessException("The phone number must be a positive number.");
        }
        if (String.valueOf(phoneNumber).length() < 7 || String.valueOf(phoneNumber).length() > 15) {
            throw new BusinessException("The phone number must contain between 7 and 15 digits.");
        }
        try {
            clientSupplierDA.changePhoneNumber(id, phoneNumber);
        } catch (DataBaseException e) {
            throw new BusinessException("Error changing the phone number.", e);
        }
    }

    public void changeEmail(int id, String email) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("The client supplier ID must be a positive number.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("The email cannot be null or empty.");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BusinessException("The email is not in a valid format.");
        }
        try {
            clientSupplierDA.changeEmail(id, email);
        } catch (DataBaseException e) {
            throw new BusinessException("Error changing the email.", e);
        }
    }

    public void deleteClientSupplier(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("The client supplier ID must be a positive number.");
        }
        try {
            clientSupplierDA.deleteClientSupplier(id);
        } catch (DataBaseException e) {
            throw new BusinessException("Error deleting the client supplier .", e);
        }
    }

    public abstract void placeOrder(int clientSupplierId, List<Product> products) throws BusinessException;
}
