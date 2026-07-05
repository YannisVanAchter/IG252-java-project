package be.henallux.project.controller;

import be.henallux.project.business.ProductSearchManager;
import be.henallux.project.business.exception.BusinessException;
import be.henallux.project.model.Product;
import be.henallux.project.model.ProductCategory;
import be.henallux.project.model.exception.DataValidationException;

import java.util.ArrayList;

/**
 * @see be.henallux.project.view.ProductSearchTable
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
     * @see ProductSearchManager#searchProducts(String, ProductCategory, boolean)
     */
    public ArrayList<Product> searchProducts(String name, ProductCategory category, boolean promotion) throws BusinessException, DataValidationException {
        return new ArrayList<>(productSearchManager.searchProducts(name, category, promotion));
    }
}