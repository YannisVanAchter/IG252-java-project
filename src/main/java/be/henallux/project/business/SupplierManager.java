package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
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

    public List<ClientSupplier> getAllSuppliers() throws BusinessException {
        try {
            return clientSupplierData.getAllSuppliers();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des fournisseurs.", e);
        }
    }

    public List<Product> getAllProducts(int supplierId) throws BusinessException {
        try {
            return productData.getAllProducts(supplierId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des produits.", e);
        }
    }

    public void changeVATNumber(int supplierId, String VATNumber) throws BusinessException {
        try {
            clientSupplierData.changeVATNumber(supplierId, VATNumber);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement du numéro de TVA.", e);
        }
    }

    @Override
    public void placeOrder(int clientSupplierId, List<Product> products) throws BusinessException {
        try {
            // logique commande fournisseur
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la passation de commande.", e);
        }
    }
}
