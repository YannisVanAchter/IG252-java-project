package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;
import java.util.List;

public class ClientSupplierSearchManager {
    private final ClientSupplierDA clientSupplierDA;

    public ClientSupplierSearchManager() {
        this.clientSupplierDA = ClientSupplierDA.getInstance();
    }

    public List<ClientSupplier> search(String name, String email, Boolean fidelityValid) throws BusinessException {
        return clientSupplierDA.search(name, email, fidelityValid);
    }
}
