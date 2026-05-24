package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.ProductDA;
import main.java.be.henallux.project.data.ClientSupplierDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;

import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.LocationProduct;
import main.java.be.henallux.project.model.Discount;
import main.java.be.henallux.project.model.QuantityProduct;
import main.java.be.henallux.project.model.ProductCategory;
import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.RecipeComposition;

import java.util.List;

public class SupplierManager extends ClientSupplierManager {

    private final ProductDA productDA;

    public SupplierManager() {
        super();
        this.productDA = ProductDA.getInstance();
    }

    public List<ClientSupplier> getAllSuppliers() throws BusinessException {
        return getAllClientSuppliers();
    }

    public List<Product> getAllProducts(int supplierId) throws BusinessException {
        if (supplierId <= 0) {
            throw new BusinessException("The supplier ID is invalid.");
        }
        try {
            return productDA.getAllProducts(supplierId);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the products.", e);
        }
    }

    public void changeVATNumber(int supplierId, String VATNumber) throws BusinessException {
        if (supplierId <= 0) {
            throw new BusinessException("The supplier ID is invalid.");
        }
        if (VATNumber == null || VATNumber.isBlank()) {
            throw new BusinessException("The VAT number is required.");
        }
        try {
            clientSupplierDA.changeVATNumber(supplierId, VATNumber);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the VAT number.", e);
        }
    }

    @Override
    public void placeOrder(int clientSupplierId, List<Product> products) throws BusinessException {
        if (clientSupplierId <= 0) {
            throw new BusinessException("The supplier ID is invalid.");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessException("The list of products is required.");
        }
        try {
            // ? What to do ?
        } catch (DataBaseException e) {
            throw new BusinessException("Error when placing the order.", e);
        }
    }
}
