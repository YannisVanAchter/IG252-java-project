package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public abstract class ClientSupplierManager {
    public List<ClientSupplier> getAllClientSuppliers() throws DataBaseException {
        return ClientSupplierData.getAllClientSuppliers();
    }

    public ClientSupplier getClientSupplier(int id) throws DataBaseException {
        return ClientSupplierData.getClientSupplier(id);
    }

    public void createClientSupplier(ClientSupplier clientSupplier) throws DataBaseException {
        ClientSupplierData.createClientSupplier(clientSupplier);
    }

    public void changeAddress(int id, Address address) throws DataBaseException {
        ClientSupplierData.changeAddress(id, address);
    }

    public void changePhoneNumber(int id, int phoneNumber) throws DataBaseException {
        ClientSupplierData.changePhoneNumber(id, phoneNumber);
    }

    public void changeEmail(int id, String email) throws DataBaseException {
        ClientSupplierData.changeEmail(id, email);
    }

    public void deleteClientSupplier(int id) throws DataBaseException {
        ClientSupplierData.deleteClientSupplier(id);
    }

    public abstract void placeOrder(int clientSupplierId, List<Product> products) throws DataBaseException;
}
