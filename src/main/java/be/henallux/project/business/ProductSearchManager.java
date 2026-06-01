package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.ProductSearchDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;

import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.ProductCategory;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.List;

public class ProductSearchManager {
    private final ProductSearchDA productSearchDA;

    public ProductSearchManager() {
        this.productSearchDA = new ProductSearchDA();
    }

    public List<Product> searchProducts(String name, ProductCategory category, boolean discount) throws BusinessException, DataValidationException {
        try {
            return productSearchDA.search(name, category, discount);
        } catch (DataBaseException e) {
            throw new BusinessException("Error while searching for products.", e);
        }
    }
}
