package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;

import java.lang.foreign.AddressLayout;
import java.util.List;

public class SupplierManager extends ClientSupplierManager {

    private final ProductData productData;

    public SupplierManager(ClientSupplierData clientSupplierData, AddressManager addressManager, ProductData productData) {
        super(clientSupplierData, addressManager);
        this.productData = productData;
    }

    public List<ClientSupplier> getAllSuppliers() throws BusinessException {
        return getAllClientSuppliers();
    }

    public List<Product> getAllProducts(int supplierId) throws BusinessException {
        if (supplierId <= 0) {
            throw new BusinessException("L'identifiant du fournisseur doit être un nombre positif.");
        }
        try {
            return productData.getAllProducts(supplierId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des produits.", e);
        }
    }

    public void changeVATNumber(int supplierId, String VATNumber) throws BusinessException {
        if (supplierId <= 0) {
            throw new BusinessException("L'identifiant du fournisseur doit être un nombre positif.");
        }
        if (VATNumber == null || VATNumber.isBlank()) {
            throw new BusinessException("Le numéro de TVA est obligatoire.");
        }
        try {
            clientSupplierData.changeVATNumber(supplierId, VATNumber);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement du numéro de TVA.", e);
        }
    }

    @Override
    public void placeOrder(int clientSupplierId, List<Product> products) throws BusinessException {
        if (clientSupplierId <= 0) {
            throw new BusinessException("L'identifiant du fournisseur doit être un nombre positif.");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessException("La liste des produits est obligatoire.");
        }
        try {
            // logique commande fournisseur
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la passation de commande.", e);
        }
    }
}
