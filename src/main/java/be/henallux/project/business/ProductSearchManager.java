package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.*;
import java.util.List;

public class ProductSearchManager {
    private final String name;
    private final String category;
    private final Boolean discount;
    private final ProductData productData;

    public ProductSearchManager(String name, String category, Boolean discount) {
        this.name = name;
        this.category = category;
        this.discount = discount;
        this.productData = new ProductData();
    }

    public List<Product> searchProducts() throws BusinessException {
        try {
            return productData.search(this.name, this.category, this.discount);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la recherche de produits.", e);
        }
    }
}
