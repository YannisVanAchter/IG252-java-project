package be.henallux.project.controller;

import be.henallux.project.business.ProductManager;
import be.henallux.project.business.StockManager;
import be.henallux.project.business.exception.BusinessException;
import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.model.Product;
import be.henallux.project.model.ProductCategory;
import be.henallux.project.model.LocationProduct;
import be.henallux.project.model.QuantityProduct;
import be.henallux.project.model.Discount;
import be.henallux.project.model.exception.DataValidationException;

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
     * @param product      the chosen product
     * @param newPriceEVAT the new price excluding VAT
     * @param newVAT       the new VAT rate
     * @see ProductManager#changeProductPrice(Product, BigDecimal, BigDecimal)
     */
    public void changeProductPrice(Product product, BigDecimal newPriceEVAT, BigDecimal newVAT) throws BusinessException, DataValidationException {
        productManager.changeProductPrice(product, newPriceEVAT, newVAT);
    }

    /**
     * Changes the fidelity points awarded for a product.
     * @param product          the chosen product
     * @param newFidelityPoint the new fidelity points value
     * @see ProductManager#changeFidelityPoint(Product, int)
     */
    public void changeFidelityPoint(Product product, int newFidelityPoint) throws BusinessException, DataValidationException, DataBaseException {
        productManager.changeFidelityPoint(product, newFidelityPoint);
    }

    /**
     * Changes the minimal stock quantity for a product.
     * @param product     the chosen product
     * @param newQuantity the new minimal quantity
     * @see ProductManager#changeMinimalQuantity(Product, int)
     */
    public void changeMinimalQuantity(Product product, int newQuantity) throws BusinessException, DataValidationException {
        productManager.changeMinimalQuantity(product, newQuantity);
    }

    /**
     * Deletes a product.
     * @param product the {@link Product} to delete
     * @see ProductManager#deleteProduct(Product)
     */
    public void deleteProduct(Product product) throws BusinessException, DataValidationException {
        productManager.deleteProduct(product);
    }

    /**
     * Creates a new product category.
     * @param newProductCategory the {@link ProductCategory} to create
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
    public void addStockLocation(LocationProduct newLocation) throws BusinessException, DataValidationException {
        stockManager.addStockLocation(newLocation);
    }

    /**
     * Adds quantity to a product's stock at a given location.
     * @param productID     the product ID
     * @param quantity      the quantity to add
     * @param storeLocation the target {@link LocationProduct}
     * @see StockManager#addToStocks(int, int, LocationProduct)
     */
    public void addToStocks(int productID, int quantity, LocationProduct storeLocation) throws BusinessException, DataValidationException {
        stockManager.addToStocks(productID, quantity, storeLocation);
    }

    /**
     * Subtracts quantity from a product's stock at a given location.
     * @param productID     the product ID
     * @param quantity      the quantity to subtract
     * @param storeLocation the target {@link LocationProduct}
     * @see StockManager#subtractFromStock(int, int, LocationProduct)
     */
    public void subtractFromStock(int productID, int quantity, LocationProduct storeLocation) throws BusinessException, DataValidationException {
        stockManager.subtractFromStock(productID, quantity, storeLocation);
    }

    /**
     * Deletes a stock location.
     * @param location the {@link LocationProduct} to delete
     * @see StockManager#deleteStockLocation(LocationProduct)
     */
    public void deleteStockLocation(LocationProduct location) throws BusinessException, DataValidationException {
        stockManager.deleteStockLocation(location);
    }

    /**
     * Adds a discount to a product.
     * @param product  the target {@link Product}
     * @param discount the {@link Discount} to add
     * @see ProductManager#addDiscount(Product, Discount)
     */
    public void addDiscount(Product product, Discount discount) throws BusinessException, DataValidationException {
        productManager.addDiscount(product, discount);
    }

    /**
     * Deletes a discount.
     * @param discount the {@link Discount} to delete
     * @see ProductManager#deleteDiscount(Discount)
     */
    public void deleteDiscount(Discount discount) throws BusinessException {
        productManager.deleteDiscount(discount);
    }
}