package be.henallux.project.controller;

import be.henallux.project.business.ClientSupplierSearchManager;
import be.henallux.project.business.exception.BusinessException;
import be.henallux.project.model.ClientSupplier;
import be.henallux.project.model.exception.DataValidationException;

import java.util.List;

/**
 * Controller for client/supplier search operations.
 *
 * @see be.henallux.project.view.ClientSearchTable
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
     *
     * @param name    the name to search for, or {@code null} to ignore
     * @param email   the email to search for, or {@code null} to ignore
     * @param isValid if the fidelity card is valid
     * @return list of matching {@link ClientSupplier}
     * @see ClientSupplierSearchManager#search(String, String, Boolean)
     */
    public List<ClientSupplier> search(String name, String email, Boolean isValid) throws BusinessException, DataValidationException {
        return clientSupplierSearchManager.search(name, email, isValid);
    }
}