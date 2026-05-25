package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.ProductSearchManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.model.Product;

import java.util.ArrayList;

/**
 * @see main.java.be.henallux.project.view.ProductSearchTable
 * @see ProductSearchManager
 */
public class ProductSearchController {

    private final ProductSearchManager productSearchManager;

    public ProductSearchController() {
        this.productSearchManager = new ProductSearchManager();
    }

    /**
     * Searches for products by name, category, and promotion status.
     * @param name      the product name to search for, or {@code null} to ignore
     * @param category  the category name to filter by, or {@code null} to ignore
     * @param promotion {@code true} to filter products with a discount, {@code null} to ignore
     * @return list of matching {@link Product}
     * @see ProductSearchManager#searchProducts(String, String, Boolean)
     */
    public ArrayList<Product> searchProducts(String name, String category, Boolean promotion) throws BusinessException {
        return new ArrayList<>(productSearchManager.searchProducts(name, category, promotion));
    }
}