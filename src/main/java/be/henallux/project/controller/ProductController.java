package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.ProductManager;
import main.java.be.henallux.project.business.StockManager;
import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.ProductCategory;
import main.java.be.henallux.project.model.LocationProduct;
import main.java.be.henallux.project.model.QuantityProduct;
import main.java.be.henallux.project.model.Discount;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @see ProductManager
 * @see StockManager
 */
public class ProductController {

    private final ProductManager productManager;
    private final StockManager stockManager;

    public ProductController() {
        this.productManager = new ProductManager();
        this.stockManager = new StockManager();
    }

    /**
     * Returns all products.
     * @return list of all {@link Product}
     * @see ProductManager#getAllProducts()
     */
    public ArrayList<Product> getAllProduct() throws BusinessException, DataValidationException {
        return new ArrayList<>(productManager.getAllProducts());
    }

    /**
     * Returns a product by ID.
     * @param productID the product ID
     * @return the matching {@link Product}
     * @see ProductManager#getProduct(int)
     */
    public Product getProduct(int productID) throws BusinessException, DataValidationException {
        return productManager.getProduct(productID);
    }

    /**
     * Returns all product categories.
     * @return list of all {@link ProductCategory}
     * @see ProductManager#getAllProductCategory()
     */
    public ArrayList<ProductCategory> getAllProductCategory() throws BusinessException, DataValidationException {
        return new ArrayList<>(productManager.getAllProductCategory());
    }

    /**
     * Creates a new product.
     * @param newProduct the {@link Product} to create
     * @see ProductManager#createProduct(Product)
     */
    public void createNewProduct(Product newProduct) throws BusinessException, DataValidationException {
        productManager.createProduct(newProduct);
    }

    /**
     * Changes the price of a product.
     * @param product the chosen product
     * @param newPriceEVAT  the new priceEVAT
     * @param newVAT the new priceVAT
     * @see ProductManager#changeProductPrice(Product, BigDecimal, BigDecimal)
     */
    public void changeProductPrice(Product product, BigDecimal newPriceEVAT, BigDecimal newVAT) throws BusinessException, DataValidationException {
        productManager.changeProductPrice(product, newPriceEVAT, newVAT);
    }

    /**
     * Changes the VAT rate of a product.
     * @param productID the product ID
     * @param newVAT    the new VAT rate (0–100)
     * @see ProductManager#changeProductVAT(int, double)
     */

//    public void changeProductVAT(int productID, double newVAT) throws BusinessException {
//        productManager.changeProductVAT(productID, newVAT);
//    }

    /**
     * Changes the fidelity points awarded for a product.
     * @param product the chosen product
     * @param newFidelitypoint the new fidelity points value
     * @see ProductManager#changeFidelityPoint(Product, int)
     */
    public void changeFidelityPoint(Product product, int newFidelitypoint) throws BusinessException, DataValidationException, DataBaseException {
        productManager.changeFidelityPoint(product, newFidelitypoint);
    }

    /**
     * Changes the minimal stock quantity for a product.
     * @param productID   the product ID
     * @param newQuantity the new minimal quantity
     * @see ProductManager#changeMinimalQuantity(int, int)
     */
    public void changeMinimalQuantity(int productID, int newQuantity) throws BusinessException {
        productManager.changeMinimalQuantity(productID, newQuantity);
    }

    /**
     * Deletes a product by ID.
     * @param productID the product ID
     * @see ProductManager#deleteProduct(Product, int)
     */
    public void deleteProduct(Product product, int productID) throws BusinessException, DataValidationException, DataBaseException {
        productManager.deleteProduct(product, productID);
    }

    /**
     * Creates a new product category.
     * @param newProductCategory the chosen category
     * @see ProductManager#createProductCategory(ProductCategory)
     */
    public void createProductCategory(ProductCategory newProductCategory) throws BusinessException, DataValidationException, DataBaseException {
        productManager.createProductCategory(newProductCategory);
    }

    /**
     * Adds a new stock location.
     * @param newLocation the {@link LocationProduct} to add
     * @see StockManager#addStockLocation(LocationProduct)
     */
    public void addStockLocation(LocationProduct newLocation) throws BusinessException {
        stockManager.addStockLocation(newLocation);
    }

    /**
     * Returns the first stock location found in the given products, or creates a default one.
     * @param products list of {@link Product} to search through
     * @return an existing or newly created {@link LocationProduct}
     * @see StockManager#addStockLocation(LocationProduct)
     */
    public LocationProduct getOrCreateStockLocation(ArrayList<Product> products) throws BusinessException {
        LocationProduct stockLocation = null;

        for (Product product : products) {
            if (stockLocation == null) {
                for (QuantityProduct quantityProduct : product.getLocation()) {
                    if (stockLocation == null
                            && quantityProduct.getLocationProduct().getIsStock()) {
                        stockLocation = quantityProduct.getLocationProduct();
                    }
                }
            }
        }

        if (stockLocation == null) {
            try {
                stockLocation = new LocationProduct("A", "1", true, false);
                addStockLocation(stockLocation);
            } catch (Exception e) {
                throw new BusinessException("Failed to create default stock location: " + e.getMessage());
            }
        }
        return stockLocation;
    }

    /**
     * Adds quantity to a product's stock at a given location.
     * @param productID     the product ID
     * @param quantity      the quantity to add
     * @param storeLocation the target {@link LocationProduct}
     * @see StockManager#addToStocks(int, int, LocationProduct)
     */
    public void addToStocks(int productID, int quantity, LocationProduct storeLocation) throws BusinessException {
        stockManager.addToStocks(productID, quantity, storeLocation);
    }

    /**
     * Subtracts quantity from a product's stock at a given location.
     * @param productID     the product ID
     * @param quantity      the quantity to subtract
     * @param storeLocation the target {@link LocationProduct}
     * @see StockManager#subtractFromStock(int, int, LocationProduct)
     */
    public void subtractFromStock(int productID, int quantity, LocationProduct storeLocation) throws BusinessException {
        stockManager.subtractFromStock(productID, quantity, storeLocation);
    }

    /**
     * Deletes a stock location.
     * @param location the {@link LocationProduct} to delete
     * @see StockManager#deleteStockLocation(LocationProduct)
     */
    public void deleteStockLocation(LocationProduct location) throws BusinessException {
        stockManager.deleteStockLocation(location);
    }

    /**
     * Adds a discount to a product.
     * @param discount the {@link Discount} to add
     * @see ProductManager#addDiscount(Discount)
     */
    public void addDiscount(Discount discount) throws BusinessException, DataValidationException {
        productManager.addDiscount(discount);
    }

    /**
     * Deletes a discount by its ID.
     * @param discount the {@link Discount} to delete
     * @see ProductManager#deleteDiscount(Discount)
     */
    public void deleteDiscount(Discount discount) throws BusinessException {
        productManager.deleteDiscount(discount);
    }
}