package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.ClientSupplierDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.FidelityCard;
import java.util.List;

public class ClientSupplierSearchManager {
    private final ClientSupplierDA clientSupplierDA;

    public ClientSupplierSearchManager() {
        this.clientSupplierDA = ClientSupplierDA.getInstance();
    }

    public List<ClientSupplier> search(String name, String email, String fidelityCardNb) throws BusinessException {
        try {
            return clientSupplierDA.searchInDataBase(name, email, fidelityCardNb);
        } catch (DataBaseException e) {
            throw new BusinessException("Error while searching for client/supplier", e);
        }
    }

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
}
