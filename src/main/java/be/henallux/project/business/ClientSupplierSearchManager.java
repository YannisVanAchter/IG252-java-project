package be.henallux.project.business;

import be.henallux.project.data.ClientSupplierDA;
import be.henallux.project.data.ClientSupplierSearchDA;
import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.business.exception.BusinessException;
import be.henallux.project.model.ClientSupplier;
import be.henallux.project.model.FidelityCard;
import be.henallux.project.model.exception.DataValidationException;

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
}
