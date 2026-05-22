package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;

public class SupplierManager extends ClientSupplierManager {

    private final ClientSupplierData clientSupplierData;
    private final ProductData productData;

    public SupplierManager(ClientSupplierData clientSupplierData, ProductData productData) {
        super(clientSupplierData);
        this.clientSupplierData = clientSupplierData;
        this.productData = productData;
    }

    public List<ClientSupplier> getAllSuppliers() throws DataBaseException {
        return clientSupplierData.getAllSuppliers();
    }

    public List<Product> getAllProducts(int supplierId) throws DataBaseException {
        return productData.getAllProducts(supplierId);
    }

    public void changeVATNumber(int supplierId, String VATNumber) throws DataBaseException {
        clientSupplierData.changeVATNumber(supplierId, VATNumber);
    }

    @Override
    public void placeOrder(int clientSupplierId, List<Product> products) throws DataBaseException {
        // logique commande fournisseur
    }
}
