package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.ProductDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.Category;
import main.java.be.henallux.project.model.Discount;
import java.util.List;

public class ProductSearchManager {
    private final ProductDA productDA;

    public ProductSearchManager() {
        this.productDA = ProductDA.getInstance();
    }

    public List<Product> searchProducts(String name, String category, Boolean discount) throws BusinessException {
        try {
            return productDA.search(name, category, discount);
        } catch (DataBaseException e) {
            throw new BusinessException("Error while searching for products.", e);
        }
    }
}
