package be.henallux.project.business;

import be.henallux.project.data.ProductDA;
import be.henallux.project.data.QuantityProductDA;
import be.henallux.project.data.LocationProductDA;
import be.henallux.project.data.exception.DataBaseException;

import be.henallux.project.business.exception.BusinessException;

import be.henallux.project.model.ClientSupplier;
import be.henallux.project.model.LocationProduct;
import be.henallux.project.model.Product;
import be.henallux.project.model.QuantityProduct;
import be.henallux.project.model.exception.DataValidationException;

import java.util.List;
import java.util.Map;

public class StockManager {

    private final ProductDA productDA;
    private final QuantityProductDA quantityProductDA;
    private final LocationProductDA locationProductDA;

    public StockManager() {
        this.productDA = ProductDA.getInstance();
        this.quantityProductDA = QuantityProductDA.getInstance();
        this.locationProductDA = LocationProductDA.getInstance();
    }

    public void createStockLocation(LocationProduct location) throws BusinessException, DataValidationException {
        if (location == null) {
            throw new BusinessException("The location cannot be null.");
        }
        try {
            locationProductDA.insert(location);
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while adding stock location.", e);
        }
    }
    public void addStockLocation(LocationProduct location) throws BusinessException, DataValidationException {
        try {
            locationProductDA.insert(location);
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while creating product location.", e);
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
            QuantityProduct quantityProduct = new QuantityProduct(storeLocation, product, quantity);
            if (quantityProductDA.checkExist(quantityProduct)) {
                quantityProductDA.update(quantityProduct, quantity);
            }
            else {
                quantityProductDA.insert(quantityProduct);
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
            QuantityProduct quantityProduct = new QuantityProduct(storeLocation, product, quantity);
            if (quantityProductDA.checkExist(quantityProduct)) {
                if ((quantityProduct.getQuantity() - quantity) < 0) {
                    throw new DataValidationException("The final stock has a negative quantity.");
                }
                quantityProductDA.update(quantityProduct, (quantityProduct.getQuantity() - quantity));
            }
            else {
                throw new  DataValidationException("The stock location with the product does not exist.");
            }
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while subtracting from stock.", e);
        }
    }

    public void deleteStockLocation(LocationProduct location) throws BusinessException, DataValidationException {
        if (location == null) {
            throw new BusinessException("The product location must be not null.");
        }
        try {
            List<QuantityProduct> quantityProductList = quantityProductDA.getAll();
            boolean isInUse = quantityProductList.stream()
                    .anyMatch(qp -> qp.getLocationProduct().equals(location));
            if (isInUse) {
                throw new DataValidationException("The location product cannot be deleted because still in use.");
            }
            boolean b = locationProductDA.delete(location);
        } catch (DataBaseException e) {
            throw new BusinessException("Error occurred while adding to stock.", e);
        }
    }

    public Map<ClientSupplier, List<Product>> getAllShortSuppliedProduct() throws BusinessException, DataValidationException {
        try {
            return productDA.getLowQuantityProduct();
        } catch (DataBaseException e) {
            e.printStackTrace();
            throw new BusinessException("Error occurred while retrieving products.", e);
        }
    }
}