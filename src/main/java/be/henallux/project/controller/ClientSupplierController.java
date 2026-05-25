package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.ClientManager;
import main.java.be.henallux.project.business.ClientSupplierManager;
import main.java.be.henallux.project.business.SupplierManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract controller for client/supplier
 * @see ClientManager
 * @see SupplierManager
 */
public abstract class ClientSupplierController {

    private final ClientManager clientManger;
    private final SupplierManager supplierManager;

    public ClientSupplierController() {
        this.clientManger = new ClientManager();
        this.supplierManager = new SupplierManager();
    }

    /**
     * Returns all clients and suppliers.
     * @return list of all {@link ClientSupplier}
     * @see ClientManager#getAllClientSuppliers()
     */
    public ArrayList<ClientSupplier> getAllClientsSuppliers() throws BusinessException {
        return new ArrayList<>(clientManger.getAllClientSuppliers());
    }

    /**
     * Returns a client or supplier by ID.
     * @param id the client/supplier ID
     * @return the matching {@link ClientSupplier}
     * @see ClientManager#getClientSupplier(int)
     */
    public ClientSupplier getClientSupplier(int id) throws BusinessException {
        return clientManger.getClientSupplier(id);
    }

    /**
     * Creates a new client or supplier.
     * @param newClient the {@link ClientSupplier} to create
     * @return the created {@link ClientSupplier}
     * @see ClientManager#createClientSupplier(ClientSupplier)
     */
    public ClientSupplier createClientSupplier(ClientSupplier newClient) throws BusinessException {
        return clientManger.createClientSupplier(newClient);
    }

    /**
     * Changes the address of a client or supplier.
     * @param id         the client/supplier ID
     * @param newAddress the new {@link Address}
     * @see ClientManager#changeAddress(int, Address)
     */
    public void changeAddress(int id, Address newAddress) throws BusinessException {
        clientManger.changeAddress(id, newAddress);
    }

    /**
     * Changes the phone number of a client or supplier.
     * @param id             the client/supplier ID
     * @param newPhoneNumber the new phone number
     * @see ClientManager#changePhoneNumber(int, int)
     */
    public void changePhoneNumber(int id, int newPhoneNumber) throws BusinessException {
        clientManger.changePhoneNumber(id, newPhoneNumber);
    }

    /**
     * Changes the email of a client or supplier.
     * @param id       the client/supplier ID
     * @param newEmail the new email address
     * @see ClientManager#changeEmail(int, String)
     */
    public void changeEmail(int id, String newEmail) throws BusinessException {
        clientManger.changeEmail(id, newEmail);
    }

    /**
     * Deletes a client or supplier by ID.
     * @param id the client/supplier ID
     * @return {@code true} if deleted successfully
     * @see ClientManager#deleteClientSupplier(int)
     */
    public boolean deleteClientSupplier(int id) throws BusinessException {
        return clientManger.deleteClientSupplier(id);
    }

    /**
     * Places an order for the given client or supplier.
     * @param clientSupplierID the client/supplier ID
     * @param products         list of {@link Product} to order
     */
    public abstract void placeOrder(int clientSupplierID, List<Product> products) throws BusinessException;
}