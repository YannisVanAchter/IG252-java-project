package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.ProductDA;
import main.java.be.henallux.project.data.QuantityProductDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.business.exception.BusinessException;

import main.java.be.henallux.project.model.LocationProduct;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.QuantityProduct;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.util.List;
public class StockManager {

    private final ProductDA productDA;
    private final QuantityProductDA quantityProductDA;

    public StockManager() {
        this.productDA = ProductDA.getInstance();
        this.quantityProductDA = QuantityProductDA.getInstance();
    }

    public LocationProduct addStockLocation(LocationProduct location) throws BusinessException {
        if (location == null) {
            throw new BusinessException("The location cannot be null.");
        }
        try {
            stockDA.addStockLocation(location);
            return location;
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while adding stock location.", e);
        }
    }

    public void addToStocks(int productID, int quantity, LocationProduct storeLocation) throws BusinessException, DataValidationException {
        if (productID <= 0) {
            throw new BusinessException("The product ID is invalid.");
        }
        if (quantity <= 0) {
            throw new BusinessException("The quantity must be positive.");
        }
        try {
            Product product = productDA.getById(productID);
            QuantityProduct quantityProduct = quantityProductDA.getById(QuantityProduct.hashCode(storeLocation, product));
            if (quantity < product.getStockQuantity()) {
                quantityProductDA.update(quantityProduct, quantity);
            }
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while adding to stock.", e);
        }
    }

    public void subtractFromStock(int productID, int quantity, LocationProduct storeLocation) throws BusinessException, DataValidationException {
        if (productID <= 0) {
            throw new BusinessException("The product ID is invalid.");
        }
        if (quantity <= 0) {
            throw new BusinessException("The quantity must be positive.");
        }
        if (storeLocation == null) {
            throw new BusinessException("The stock location is invalid.");
        }
        try {
            // Business rule — check if there is enough stock before subtracting
            Product product = productDA.getById(productID);
            QuantityProduct quantityProduct = quantityProductDA.getById(QuantityProduct.hashCode(storeLocation, product));
            if (quantity < product.getStockQuantity()) {
                quantityProductDA.update(quantityProduct, quantity);
            }
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while subtracting from stock.", e);
        }
    }

    public boolean deleteStockLocation(LocationProduct location) throws BusinessException {
        if (location == null) {
            throw new BusinessException("The location cannot be null.");
        }
        try {
            // Business rule — check if there are still products in this location before deleting
            if (stockDA.hasProducts(location)) {
                throw new BusinessException("Impossible to delete location because it still contains products.");
            }
            stockDA.deleteStockLocation(location);
            return true;
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while deleting stock location.", e);
        }
    }

    public List<Product> getAllShortSuppliedProduct() throws BusinessException {
        try {
            return stockDA.getAllShortSuppliedProduct();
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while retrieving out-of-stock products.", e);
        }
    }
}