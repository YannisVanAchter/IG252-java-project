package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.ProductSearch;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;

import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.ProductCategory;
import main.java.be.henallux.project.model.Discount;
import java.util.List;

public class ProductSearchManager {
    private final ProductSearch productSearch;

    public ProductSearchManager() {
        this.productSearch = new ProductSearch();
    }

    public List<Product> searchProducts(String name, ProductCategory category, Boolean discount) throws BusinessException {
        try {
            return productSearch.search(name, category, discount);
        } catch (DataBaseException e) {
            throw new BusinessException("Error while searching for products.", e);
        }
    }
}
