package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.ClientSupplierSearchManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.ClientSupplier;

import java.util.List;

/**
 * Controller for client/supplier search operations.
 * @see main.java.be.henallux.project.view.ClientSearchTable
 * @see ClientSupplierSearchManager
 */
public class ClientSupplierSearchController {

    private final ClientSupplierSearchManager clientSupplierSearchManager;

    /**
     * Creates a new {@code ClientSupplierSearchController} with its {@link ClientSupplierSearchManager}.
     */
    public ClientSupplierSearchController() {
        this.clientSupplierSearchManager = new ClientSupplierSearchManager();
    }

    /**
     * Searches for clients or suppliers by name, email, or fidelity card number.
     * @param name               the name to search for, or {@code null} to ignore
     * @param email              the email to search for, or {@code null} to ignore
     * @param fidelityCardNumber the fidelity card number, or {@code null} to ignore
     * @return list of matching {@link ClientSupplier}
     * @see ClientSupplierSearchManager#search(String, String, String)
     */
    public List<ClientSupplier> search(String name, String email, String fidelityCardNumber) throws BusinessException {
        return clientSupplierSearchManager.search(name, email, fidelityCardNumber);
    }
}