package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.ClientSupplierManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.FidelityCard;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ClientSupplierController {

    private final ClientSupplierManager clientSupplierManager;

    public ClientSupplierController() {
        this.clientSupplierManager = new ClientSupplierManager();
    }

    /**
     * Returns all clients and suppliers.
     *
     * @return list of all {@link ClientSupplier}
     * @see ClientSupplierManager#getAllClientSuppliers()
     */
    public ArrayList<ClientSupplier> getAllClientsSuppliers() throws BusinessException, DataValidationException {
        return new ArrayList<>(clientSupplierManager.getAllClientSuppliers());
    }

    /**
     * Returns a client or supplier by ID.
     *
     * @param id the client/supplier ID
     * @return the matching {@link ClientSupplier}
     * @see ClientSupplierManager#getClientByCardID(int)
     */
    public ClientSupplier getClientByCardID(int id) throws BusinessException, DataValidationException {
        return clientSupplierManager.getClientByCardID(id);
    }

    /**
     * Creates a new client or supplier.
     *
     * @param newClient the {@link ClientSupplier} to create
     * @return the created {@link ClientSupplier}
     * @see ClientSupplierManager#createClientSupplier(ClientSupplier)
     */
    public void createClientSupplier(ClientSupplier newClient) throws BusinessException, DataValidationException {
        clientSupplierManager.createClientSupplier(newClient);
    }

    /**
     * Changes the address of a client or supplier.
     *
     * @param oldModel   the client/supplier ID
     * @param newClientSupplier the new {@link ClientSupplier}
     * @see ClientSupplierManager#changeAddress(ClientSupplier, Address)
     */
    public void updateClientSupplier(ClientSupplier oldModel, ClientSupplier newClientSupplier) throws BusinessException, DataValidationException {
        clientSupplierManager.changeAddress(oldModel, newClientSupplier);
    }

    /**
     * Deletes a client or supplier by ID.
     * @param clientSupplier the client/supplier ID
     * @return {@code true} if deleted successfully
     * @see ClientSupplierManager#deleteClientSupplier(ClientSupplier)
     */
    public boolean deleteClientSupplier(ClientSupplier clientSupplier) throws BusinessException, DataValidationException {
        return clientSupplierManager.deleteClientSupplier(clientSupplier);
    }

    // ===================================
    // CLIENTS
    // ===================================
    /**
     * Creates a fidelity card for the given client.
     * @param fidelityCard the fidelityCard
     * @return the created {@link FidelityCard}
     * @see ClientSupplierManager#createFidelityCard(FidelityCard)
     */
    public void createFidelityCard(FidelityCard fidelityCard) throws BusinessException, DataValidationException {
        clientSupplierManager.createFidelityCard(fidelityCard);
    }

    /**
     * Validates that a fidelity card belongs to the given client.
     * @param clientID the client's ID
     * @param cardID   the fidelity card ID
     * @return {@code true} if the card belongs to the client, {@code false} otherwise
     * @see ClientSupplierManager#validateFidelityCardOwnership(int, int)
     */
    public boolean validateFidelityCardOwnership(int clientID, int cardID) throws BusinessException, DataValidationException {
        return clientSupplierManager.validateFidelityCardOwnership(clientID, cardID);
    }
    
    /**
     * Records a checkout without a client.
     * @param products map of {@link Product} and their quantities
     * @see ClientSupplierManager#addCheckout
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products) throws BusinessException {
        clientSupplierManager.addCheckout(new HashMap<>(products));
    }

    /**
     * Records a checkout for a given client.
     * @param products map of {@link Product} and their quantities
     * @param clientID the client's ID
     * @see ClientSupplierManager#addCheckout(List, int)
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products, int clientID) throws BusinessException {
        clientSupplierManager.addCheckout(new HashMap<>(products), clientID);
    }

    /**
     * Records a checkout for a client with a fidelity card.
     * @param products         map of {@link Product} and their quantities
     * @param clientID         the client's ID
     * @param fidelityCard     the client's {@link FidelityCard}
     * @param useFidelityPoint whether to redeem fidelity points
     * @see ClientSupplierManager#addCheckout(List, int, FidelityCard, boolean)
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products, int clientID, FidelityCard fidelityCard, boolean useFidelityPoint) throws BusinessException {
        clientSupplierManager.addCheckout(new HashMap<>(products), clientID, fidelityCard, useFidelityPoint);
    }

    /**
     * Records a checkout for a client with a fidelity card without redeeming points.
     * @param products     map of {@link Product} and their quantities
     * @param clientID     the client's ID
     * @param fidelityCard the client's {@link FidelityCard}
     * @see ClientSupplierManager#addCheckout(List, int, FidelityCard, boolean)
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products, int clientID, FidelityCard fidelityCard) throws BusinessException {
        clientSupplierManager.addCheckout(new HashMap<>(products), clientID, fidelityCard);
    }


    /**
     * Deletes a client account.
     * @param clientSupplier the client's
     * @return {@code true} if the account was deleted successfully
     * @see ClientSupplierManager#deleteClientAccount(int)
     */
    public void deleteClientAccount(ClientSupplier clientSupplier) throws BusinessException, DataValidationException {
        deleteClientSupplier(clientSupplier);
    }

    /**
     * Deletes a client account along with its fidelity card.
     * @param clientID the client's ID
     * @param cardID   the fidelity card ID
     * @return {@code true} if the account was deleted successfully
     * @see ClientSupplierManager#deleteClientAccount(int, int)
     */
    public void deleteClientAccount(int clientID, int cardID) throws BusinessException, DataValidationException {
        clientSupplierManager.deleteClientAccount(clientID, cardID);
    }

    /**
     * @param clientSupplierID the client's ID
     * @param products         list of {@link Product} to order
     */
    public void placeClientOrder(int clientSupplierID, LinkedHashMap<Product, Integer> products) throws BusinessException {
        addCheckout(products, clientSupplierID);
    }

    // ===================================
    // SUPPLIERS
    // ===================================

    /**
     * Returns all suppliers.
     * @return list of all {@link ClientSupplier} of type supplier
     * @see ClientSupplierManager#getAllSuppliers()
     */
    public ArrayList<ClientSupplier> getAllSuppliers() throws BusinessException, DataValidationException {
        return new ArrayList<>(clientSupplierManager.getAllSuppliers());
    }

    /**
     * Returns all products offered by a supplier.
     * @param supplierID the supplier ID
     * @return list of {@link Product} from the given supplier
     * @see ClientSupplierManager#getAllProducts(int)
     */
    public ArrayList<Product> getAllProducts(int supplierID) throws BusinessException {
        return new ArrayList<>(clientSupplierManager.get(supplierID));
    }

    /**
     * Returns the supplier associated with a given product.
     * @param productID the product ID
     * @return the matching {@link ClientSupplier}
     * @see ClientSupplierManager#getSupplierByProduct(int)
     */
    public ClientSupplier getSupplierByProduct(int productID) throws BusinessException {
        return clientSupplierManager.getSupplierByProduct(productID);
    }

    /**
     * Changes the VAT number of a supplier.
     * @param supplierID    the supplier ID
     * @param newVATNumber  the new VAT number
     * @see ClientSupplierManager#changeVATNumber(int, String)
     */
    public void changeVATNumber(int supplierID, String newVATNumber) throws BusinessException {
        clientSupplierManager.changeVATNumber(supplierID, newVATNumber);
    }

    /**
     * @param supplierID the supplier ID
     * @param products         list of {@link Product} to order
     * @throws BusinessException not thrown
     */
    public void placeSupplierOrder(int supplierID, LinkedHashMap<Product, Integer> products) throws BusinessException {
        clientSupplierManager.placeOrder(supplierID, new HashMap<>(products));
    }

    /**
     * Get the client/supplier entity representing with store.
     *
     * @return the {@link ClientSupplier} entity representing the store
     */
    public ClientSupplier getUs() throws BusinessException, DataValidationException {
        List<ClientSupplier> all = getAllClientsSuppliers();
        return all.stream()
                .filter(ClientSupplier::getIsUs)
                .findFirst()
                .orElseThrow(() -> new BusinessException("No 'us' client supplier found."));
    }
}