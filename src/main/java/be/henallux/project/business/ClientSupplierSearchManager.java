package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.ClientSupplierDA;
import main.java.be.henallux.project.data.ClientSupplierSearchDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.FidelityCard;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.List;

public class ClientSupplierSearchManager {
    private final ClientSupplierSearchDA clientSupplierSearchDA;

    public ClientSupplierSearchManager() {
        this.clientSupplierSearchDA = new ClientSupplierSearchDA();
    }

    public List<ClientSupplier> search(String name, String email, Boolean isFidelityCarValid) throws BusinessException, DataValidationException {
        try {
            return clientSupplierSearchDA.search(name, email, isFidelityCarValid);
        } catch (DataBaseException e) {
            throw new BusinessException("Error while searching for client/supplier", e);
        }
    }

    // Not used
    /*
    public List<ClientSupplier> searchByCard(int cardId) throws BusinessException {
        if (cardId <= 0) {
            throw new BusinessException("The card ID must be a positive number.");
        }
        try {
            return clientSupplierDA.searchByCard(cardId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error while searching for client/supplier by card.", e);
        }
    }

    public List<ClientSupplier> searchByProduct(int productId) throws BusinessException {
        if (productId <= 0) {
            throw new BusinessException("The product ID must be a positive number.");
        }
        try {
            return clientSupplierDA.searchByProduct(productId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error while searching for client/supplier by product.", e);
        }
    }
     */
}
