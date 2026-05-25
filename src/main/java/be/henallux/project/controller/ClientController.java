package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.ClientManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.FidelityCard;
import main.java.be.henallux.project.model.Product;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @see ClientManager
 * @see ClientSupplierController
 */
public class ClientController extends ClientSupplierController {

    private final ClientManager clientManager;

    public ClientController() {
        this.clientManager = new ClientManager();
    }

    /**
     * Returns all clients.
     * @return list of all {@link ClientSupplier} of type client
     * @see ClientManager#getAllClients()
     */
    public ArrayList<ClientSupplier> getAllClients() throws BusinessException {
        return new ArrayList<>(clientManager.getAllClients());
    }

    /**
     * Creates a fidelity card for the given client.
     * @param clientID the client's ID
     * @return the created {@link FidelityCard}
     * @see ClientManager#createFidelityCard(int)
     */
    public FidelityCard createFidelityCard(int clientID) throws BusinessException {
        return clientManager.createFidelityCard(clientID);
    }

    /**
     * Returns the client associated with the given fidelity card ID.
     * @param cardID the fidelity card ID
     * @return list of matching {@link ClientSupplier}
     * @see ClientManager#getClientByCardID(int)
     */
    public List<ClientSupplier> getClientByCardID(int cardID) throws BusinessException {
        return clientManager.getClientByCardID(cardID);
    }

    /**
     * Validates that a fidelity card belongs to the given client.
     * @param clientID the client's ID
     * @param cardID   the fidelity card ID
     * @return {@code true} if the card belongs to the client, {@code false} otherwise
     * @see ClientManager#validateFidelityCardOwnership(int, int)
     */
    public boolean validateFidelityCardOwnership(int clientID, int cardID) throws BusinessException {
        return clientManager.validateFidelityCardOwnership(clientID, cardID);
    }

    /**
     * Records a checkout without a client.
     * @param products map of {@link Product} and their quantities
     * @see ClientManager#addCheckout(LinkedHashMap)
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products) throws BusinessException {
        clientManager.addCheckout(products);
    }

    /**
     * Records a checkout for a given client.
     * @param products map of {@link Product} and their quantities
     * @param clientID the client's ID
     * @see ClientManager#addCheckout(LinkedHashMap, int)
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products, int clientID) throws BusinessException {
        clientManager.addCheckout(products, clientID);
    }

    /**
     * Records a checkout for a client with a fidelity card.
     * @param products         map of {@link Product} and their quantities
     * @param clientID         the client's ID
     * @param fidelityCard     the client's {@link FidelityCard}
     * @param useFidelityPoint whether to redeem fidelity points
     * @see ClientManager#addCheckout(LinkedHashMap, int, FidelityCard, boolean)
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products, int clientID, FidelityCard fidelityCard, boolean useFidelityPoint) throws BusinessException {
        clientManager.addCheckout(products, clientID, fidelityCard, useFidelityPoint);
    }

    /**
     * Records a checkout for a client with a fidelity card without redeeming points.
     * @param products     map of {@link Product} and their quantities
     * @param clientID     the client's ID
     * @param fidelityCard the client's {@link FidelityCard}
     * @see ClientManager#addCheckout(LinkedHashMap, int, FidelityCard)
     */
    public void addCheckout(LinkedHashMap<Product, Integer> products, int clientID, FidelityCard fidelityCard) throws BusinessException {
        clientManager.addCheckout(products, clientID, fidelityCard);
    }

    /**
     * Deletes a client account.
     * @param clientID the client's ID
     * @return {@code true} if the account was deleted successfully
     * @see ClientManager#deleteClientAccount(int)
     */
    public Boolean deleteClientAccount(int clientID) throws BusinessException {
        return clientManager.deleteClientAccount(clientID);
    }

    /**
     * Deletes a client account along with its fidelity card.
     * @param clientID the client's ID
     * @param cardID   the fidelity card ID
     * @return {@code true} if the account was deleted successfully
     * @see ClientManager#deleteClientAccount(int, int)
     */
    public Boolean deleteClientAccount(int clientID, int cardID) throws BusinessException {
        return clientManager.deleteClientAccount(clientID, cardID);
    }

    /**
     * //TODO quoi faire
     * @param clientSupplierID the client's ID
     * @param products         list of {@link Product} to order
     */
    @Override
    public void placeOrder(int clientSupplierID, List<Product> products) throws BusinessException {

    }
}