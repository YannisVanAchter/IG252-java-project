package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.ClientSupplierManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.FidelityCard;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
     * Returns a client or supplier by fidelity card ID.
     *
     * @param id the fidelity card ID
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
     * @see ClientSupplierManager#createClientSupplier(ClientSupplier)
     */
    public void createClientSupplier(ClientSupplier newClient) throws BusinessException, DataValidationException {
        clientSupplierManager.createClientSupplier(newClient);
    }

    /**
     * Changes the address of a client or supplier.
     *
     * @param oldModel          the existing {@link ClientSupplier}
     * @param newClientSupplier the updated {@link ClientSupplier}
     * @see ClientSupplierManager#updateClientSupplier(ClientSupplier, ClientSupplier)
     */
    public void updateClientSupplier(ClientSupplier oldModel, ClientSupplier newClientSupplier) throws BusinessException, DataValidationException {
        clientSupplierManager.updateClientSupplier(oldModel, newClientSupplier);
    }

    /**
     * Deletes a client or supplier.
     *
     * @param clientSupplier the {@link ClientSupplier} to delete
     * @return {@code true} if deleted successfully
     * @see ClientSupplierManager#deleteClientSupplier(ClientSupplier)
     */
    public boolean deleteClientSupplier(ClientSupplier clientSupplier) throws BusinessException, DataValidationException {
        return clientSupplierManager.deleteClientSupplier(clientSupplier);
    }

    /**
     * Returns the client/supplier entity representing the store itself.
     *
     * @return the {@link ClientSupplier} entity where {@code isUs = true}
     * @see ClientSupplierManager#getAllClientSuppliers()
     */
    public ClientSupplier getUs() throws BusinessException, DataValidationException {
        return clientSupplierManager.getUs();
    }

    // ===================================
    // CLIENTS
    // ===================================

    /**
     * Returns all clients.
     *
     * @return list of all {@link ClientSupplier} of type client
     * @see ClientSupplierManager#getAllClients()
     */
    public ArrayList<ClientSupplier> getAllClients() throws BusinessException, DataValidationException {
        return new ArrayList<>(clientSupplierManager.getAllClients());
    }

    /**
     * Creates a fidelity card for a client.
     *
     * @param fidelityCard the {@link FidelityCard} to create
     * @see ClientSupplierManager#createFidelityCard(FidelityCard)
     */
    public void createFidelityCard(FidelityCard fidelityCard) throws BusinessException, DataValidationException {
        clientSupplierManager.createFidelityCard(fidelityCard);
    }

    /**
     * Validates that a fidelity card belongs to the given client.
     *
     * @param clientID the client's ID
     * @param cardID   the fidelity card ID
     * @return {@code true} if the card belongs to the client, {@code false} otherwise
     * @see ClientSupplierManager#validateFidelityCardOwnership(int, int)
     */
    public boolean validateFidelityCardOwnership(int clientID, int cardID) throws BusinessException, DataValidationException {
        return clientSupplierManager.validateFidelityCardOwnership(clientID, cardID);
    }

    /**
     * Records a checkout without a client (anonymous purchase).
     * Decrements stock for each product by the given quantity.
     *
     * @param products map of {@link Product} to their purchased quantity
     * @see ClientSupplierManager#addCheckout(HashMap)
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products) throws BusinessException, DataValidationException {
        clientSupplierManager.addCheckout(new HashMap<>(products));
    }

    /**
     * Records a checkout for a given client.
     * Decrements stock for each product by the given quantity.
     *
     * @param products map of {@link Product} to their purchased quantity
     * @param clientID the client's ID
     * @see ClientSupplierManager#addCheckout(HashMap, int)
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products, int clientID) throws BusinessException, DataValidationException {
        clientSupplierManager.addCheckout(new HashMap<>(products), clientID);
    }

    /**
     * Records a checkout for a client with a fidelity card, without redeeming points.
     * Decrements stock and adds earned fidelity points to the card.
     *
     * @param products     map of {@link Product} to their purchased quantity
     * @param clientID     the client's ID
     * @param fidelityCard the client's {@link FidelityCard}
     * @see ClientSupplierManager#addCheckout(HashMap, int, FidelityCard)
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products, int clientID, FidelityCard fidelityCard) throws BusinessException, DataValidationException {
        clientSupplierManager.addCheckout(new HashMap<>(products), clientID, fidelityCard);
    }

    /**
     * Records a checkout for a client with a fidelity card.
     * Decrements stock, optionally redeems all current fidelity points, then adds earned points.
     *
     * @param products         map of {@link Product} to their purchased quantity
     * @param clientID         the client's ID
     * @param fidelityCard     the client's {@link FidelityCard}
     * @param useFidelityPoint if {@code true}, all current points are redeemed before adding earned points
     * @see ClientSupplierManager#addCheckout(HashMap, int, FidelityCard, boolean)
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products, int clientID, FidelityCard fidelityCard, boolean useFidelityPoint) throws BusinessException, DataValidationException {
        clientSupplierManager.addCheckout(new HashMap<>(products), clientID, fidelityCard, useFidelityPoint);
    }

    /**
     * Deletes a client account.
     *
     * @param clientSupplier the {@link ClientSupplier} to delete
     * @see ClientSupplierManager#deleteClientSupplier(ClientSupplier)
     */
    public void deleteClientAccount(ClientSupplier clientSupplier) throws BusinessException, DataValidationException {
        clientSupplierManager.deleteClientSupplier(clientSupplier);
    }

    /**
     * Deletes a client account after verifying fidelity card ownership.
     *
     * @param clientID the client's ID
     * @param cardID   the fidelity card ID
     * @see ClientSupplierManager#deleteClientAccount(int, int)
     */
    public void deleteClientAccount(int clientID, int cardID) throws BusinessException, DataValidationException {
        clientSupplierManager.deleteClientAccount(clientID, cardID);
    }

    // ===================================
    // SUPPLIERS
    // ===================================

    /**
     * Returns all suppliers.
     *
     * @return list of all {@link ClientSupplier} of type supplier
     * @see ClientSupplierManager#getAllSuppliers()
     */
    public ArrayList<ClientSupplier> getAllSuppliers() throws BusinessException, DataValidationException {
        return new ArrayList<>(clientSupplierManager.getAllSuppliers());
    }

    /**
     * Places an order to a supplier.
     *
     * @param supplierID the supplier's ID
     * @param products   map of {@link Product} to their ordered quantity
     * @see ClientSupplierManager#placeSupplierOrder(int, HashMap)
     */
    public void placeSupplierOrder(int supplierID, LinkedHashMap<Product, Integer> products) throws BusinessException, DataValidationException {
        clientSupplierManager.placeSupplierOrder(supplierID, new HashMap<>(products));
    }
}