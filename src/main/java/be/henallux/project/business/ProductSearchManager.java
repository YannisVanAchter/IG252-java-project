package be.henallux.project.business;

import be.henallux.project.data.ProductSearchDA;
import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.business.exception.BusinessException;

import be.henallux.project.model.Product;
import be.henallux.project.model.ProductCategory;
import be.henallux.project.model.exception.DataValidationException;

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
