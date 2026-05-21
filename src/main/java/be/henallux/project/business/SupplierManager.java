package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;

import com.mysql.cj.xdevapi.Client;

import java.util.ArrayList;

public class SupplierManager {
    public List<ClientSupplier> getAllSuppliers() throws DataBaseException {
        ClientSupplierData data = new ClientSupplierData();
        return data.getAllSuppliers();
    }

    public List<Product> getAllProducts(int supplierId) throws DataBaseException {
        ProductData data = new ProductData();
        return data.getAllProducts(supplierId);
    }

    public void changeVATNumber(int supplierId, String VATNumber) throws DataBaseException {
        ClientSupplierData data = new ClientSupplierData();
        data.changeVATNumber(supplierId, VATNumber);
    }
}
