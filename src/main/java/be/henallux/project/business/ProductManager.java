package main.java.be.henallux.project.business;

import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.data.ProductDA;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.ProductCategory;
import main.java.be.henallux.project.model.Discount;
import main.java.be.henallux.project.model.QuantityProduct;
import main.java.be.henallux.project.model.LocationProduct;
import java.util.List;

public class ProductManager {

    private final ProductDA productDA;

    public ProductManager() {
        this.productDA = ProductDA.getInstance();
    }

    public List<Product> getAllProducts() throws BusinessException {
        try {
            return productDA.getAllProducts();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving all products.", e);
        }
    }

    public Product getProduct(int productID) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("The product ID is invalid.");
        }
        try {
            Product product = productDA.getProduct(productID);
            // Règle métier
            if (product == null) {
                throw new BusinessException("The product does not exist.");
            }
            return product;
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the product.", e);
        }
    }

    public List<ProductCategory> getAllProductCategory() throws BusinessException {
        try {
            return productDA.getAllProductCategory();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the product categories.", e);
        }
    }

    public void createProduct(Product product) throws BusinessException {
        // Validation
        if (product == null) {
            throw new BusinessException("The product cannot be null.");
        }
        if (product.getName() == null || product.getName().isBlank()) {
            throw new BusinessException("The product name is required.");
        }
        if (product.getPrice() < 0) {
            throw new BusinessException("The product price cannot be negative.");
        }
        if (product.getVAT() < 0 || product.getVAT() > 100) {
            throw new BusinessException("The VAT must be between 0 and 100.");
        }
        try {
            productDA.createProduct(product);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when creating the product.", e);
        }
    }

    public void changeProductPrice(int productID, double price) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("The product ID is invalid.");
        }
        if (price < 0) {
            throw new BusinessException("The price cannot be negative.");
        }
        try {
            productDA.changeProductPrice(productID, price);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the price.", e);
        }
    }

    public void changeProductVAT(int productID, double VAT) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("The product ID is invalid.");
        }
        // Règle métier
        if (VAT < 0 || VAT > 100) {
            throw new BusinessException("The VAT must be between 0 and 100.");
        }
        try {
            productDA.changeProductVAT(productID, VAT);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the VAT.", e);
        }
    }

    public void changeFidelityPoint(int productID, int points) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("The product ID is invalid.");
        }
        if (points < 0) {
            throw new BusinessException("The fidelity points cannot be negative.");
        }
        try {
            productDA.changeFidelityPoint(productID, points);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the fidelity points.", e);
        }
    }

    public void changeMinimalQuantity(int productID, int minimalQuantity) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("The product ID is invalid.");
        }
        if (minimalQuantity < 0) {
            throw new BusinessException("The minimal quantity cannot be negative.");
        }
        try {
            productDA.changeMinimalQuantity(productID, minimalQuantity);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the minimal quantity.", e);
        }
    }

    public void deleteProduct(int productID) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("The product ID is invalid.");
        }
        try {
            // Règle métier — vérifier que le produit existe avant de le supprimer
            if (productDA.getProduct(productID) == null) {
                throw new BusinessException("The product does not exist.");
            }
            productDA.deleteProduct(productID);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when deleting the product.", e);
        }
    }

    public void createProductCategory(String name) throws BusinessException {
        // Validation
        if (name == null || name.isBlank()) {
            throw new BusinessException("The category name is required.");
        }
        try {
            productDA.createProductCategory(name);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when creating the category.", e);
        }
    }

    public void addDiscount(Discount discount) throws BusinessException {
        // Validation
        if (discount == null) {
            throw new BusinessException("The discount cannot be null.");
        }
        // Règle métier
        if (discount.getPercentage() <= 0 || discount.getPercentage() > 100) {
            throw new BusinessException("The discount percentage must be between 1 and 100.");
        }
        if (discount.getStartDate().isAfter(discount.getEndDate())) {
            throw new BusinessException("The start date must be before the end date.");
        }
        try {
            productDA.addDiscount(discount);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the discount.", e);
        }
    }

    public void deleteDiscount(int discountID) throws BusinessException {
        // Validation
        if (discountID <= 0) {
            throw new BusinessException("The discount ID is invalid.");
        }
        try {
            productDA.deleteDiscount(discountID);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when deleting the discount.", e);
        }
    }
}