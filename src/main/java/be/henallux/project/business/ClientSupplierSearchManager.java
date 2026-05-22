package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ClientSupplierSearchManager {
    private String name;
    private String email;
    private Boolean fidelityValid;

    public ClientSupplierSearchManager(String name, String email, Boolean fidelityValid) {
        this.name = name;
        this.email = email;
        this.fidelityValid = fidelityValid;
    }

    public List<ClientSupplier> search() throws DataBaseException {
        ClientSupplierData data = new ClientSupplierData();
        return data.search(name, email, fidelityValid);
    }
}
