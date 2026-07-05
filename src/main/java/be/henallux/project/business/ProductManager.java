package be.henallux.project.business;

import be.henallux.project.business.exception.BusinessException;
import be.henallux.project.data.DiscountDA;
import be.henallux.project.data.ProductCategoryDA;
import be.henallux.project.data.ProductDA;
import be.henallux.project.data.exception.DataBaseException;

import be.henallux.project.model.Product;
import be.henallux.project.model.ProductCategory;
import be.henallux.project.model.Discount;
import be.henallux.project.model.QuantityProduct;
import be.henallux.project.model.LocationProduct;
import be.henallux.project.model.exception.DataValidationException;

import java.math.BigDecimal;
import java.util.List;

public class ProductManager {

    private final ProductDA productDA;
    private final ProductCategoryDA productCategoryDA;
    private final DiscountDA discountDA;

    public ProductManager() {
        this.productDA = ProductDA.getInstance();
        this.productCategoryDA = ProductCategoryDA.getInstance();
        this.discountDA = DiscountDA.getInstance();
    }

    public List<Product> getAllProducts() throws BusinessException, DataValidationException {
        try {
            return productDA.getAll();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving all products.", e);
        }
    }

    public Product getProduct(int productID) throws BusinessException, DataValidationException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("The product ID is invalid.");
        }
        try {
            Product product = productDA.getById(productID);
            // Business rule — check that the product exists before returning it
            if (product == null) {
                throw new BusinessException("The product does not exist.");
            }
            return product;
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the product.", e);
        }
    }

    public List<ProductCategory> getAllProductCategory() throws BusinessException, DataValidationException {
        try {
            return productCategoryDA.getAll();
        } catch (DataBaseException e) {
            throw new BusinessException("Error when retrieving the product categories.", e);
        }
    }

    public void createProduct(Product product) throws BusinessException, DataValidationException {
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
        if ((product.getVat().compareTo(java.math.BigDecimal.ZERO) < 0) || (product.getVat().compareTo(new java.math.BigDecimal("100")) > 0)) {
            throw new BusinessException("The VAT must be between 0 and 100.");
        }
        try {
            boolean insert = productDA.insert(product);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when creating the product.", e);
        }
    }

    public boolean changeProductPrice(Product product, BigDecimal newPriceEVAT, BigDecimal newVAT) throws BusinessException, DataValidationException {
        // Validation
        if (newPriceEVAT == null) {
            throw new BusinessException("The product ID is invalid.");
        }
        if (newVAT == null) {
            throw new BusinessException("The price cannot be negative.");
        }
        try {
            productDA.updatePrice(product, newPriceEVAT, newVAT);
            return true;
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the price.", e);
        }
    }
/* Superseded by change product price
    public double changeProductVAT(int productID, double VAT) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("The product ID is invalid.");
        }
        // Business rule — check that the VAT is within the valid range
        if (VAT < 0 || VAT > 100) {
            throw new BusinessException("The VAT must be between 0 and 100.");
        }
        try {
            productDA.changeProductVAT(productID, VAT);
            return VAT;
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the VAT.", e);
        }
    }
*/
    public void changeFidelityPoint(Product product, int newFidelityPoint) throws BusinessException, DataValidationException, DataBaseException {
        // Validation
        if (productDA.checkExist(product)) {
            throw new BusinessException("The product ID is invalid.");
        }
        if (newFidelityPoint < 0) {
            throw new BusinessException("The fidelity points cannot be negative.");
        }
        try {
            boolean b = productDA.updateFidelityPoint(product, newFidelityPoint);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the fidelity points.", e);
        }
    }

    public void changeMinimalQuantity(Product product, int newQuantity) throws BusinessException, DataValidationException {
        // Validation
        if (product == null) {
            throw new BusinessException("The product ID is invalid.");
        }
        if (newQuantity < 0) {
            throw new BusinessException("The minimal quantity cannot be negative.");
        }
        try {
            boolean b = productDA.updateQuantity(product, newQuantity);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when changing the minimal quantity.", e);
        }
    }

    public void deleteProduct(Product product) throws BusinessException, DataValidationException {
        // Validation
        if (product == null) {
            throw new BusinessException("The product is empty.");
        }
        try {
            // Business rule — check that the product exists before deleting it
            if (productDA.checkExist(product)) {
                productDA.delete(product);
            }
        } catch (DataBaseException e) {
            throw new BusinessException("Error when deleting the product.", e);
        }
    }

    public void createProductCategory(ProductCategory newProductCategory) throws BusinessException, DataValidationException, DataBaseException {
        // Validation
        if (newProductCategory == null) {
            throw new BusinessException("The product category is null.");
        }
        try {
           boolean b = productCategoryDA.insert(newProductCategory);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when creating the category.", e);
        }
    }

    public void addDiscount(Product product, Discount newDiscount) throws BusinessException, DataValidationException {
        // Validation
        if (newDiscount == null) {
            throw new BusinessException("The discount cannot be null.");
        }
        // Business rule — check that the discount percentage is within the valid range
        if ((newDiscount.getDiscountPercentage().compareTo(java.math.BigDecimal.ZERO) == -1) || (newDiscount.getDiscountPercentage().compareTo(new java.math.BigDecimal("100")) == 1)) {
            throw new BusinessException("The discount percentage must be between 1 and 100.");
        }
        if (newDiscount.getStartDate().isAfter(newDiscount.getEndDate())) {
            throw new BusinessException("The start date must be before the end date.");
        }
        try {
            boolean insert = discountDA.insert(newDiscount);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when adding the discount.", e);
        }
    }

    public void deleteDiscount(Discount discount) throws BusinessException {
        // Validation
        if (discount == null) {
            throw new BusinessException("The discount cannot be null.");
        }
        try {
            boolean delete = discountDA.delete(discount);
        } catch (DataBaseException e) {
            throw new BusinessException("Error when deleting the discount.", e);
        }
    }
}