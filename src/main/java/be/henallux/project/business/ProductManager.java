package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ProductManager {
    public List<Product> getAllProducts() throws DataBaseException {
        ProductData data = new ProductData();
        return data.getAllProducts();
    }

    public Product getProduct(int productID) throws DataBaseException {
        ProductData data = new ProductData();
        return data.getProduct(productID);
    }

    public List<Category> getAllProductCategory() throws DataBaseException {
        ProductData data = new ProductData();
        return data.getAllProductCategory();
    }

    public void createProduct(Product Product) throws DataBaseException {
        ProductData data = new ProductData();
        data.createProduct(Product);
    }

    public void changeProductPrice(int productID, double price) throws DataBaseException {
        ProductData data = new ProductData();
        data.changeProductPrice(productID, price);
    }

    public void changeProductVAT(int productID, double VAT) throws DataBaseException {
        ProductData data = new ProductData();
        data.changeProductVAT(productID, VAT);
    }

    public void changeFidelityPoint(int productID, int points) throws DataBaseException {
        ProductData data = new ProductData();
        data.changeFidelityPoint(productID, points);
    }

    public void changeMinimalQuantity(int productID, int minimalQuantity) throws DataBaseException {
        ProductData data = new ProductData();
        data.changeMinimalQuantity(productID, minimalQuantity);
    }

    public void deleteProduct(int productID) throws DataBaseException {
        ProductData data = new ProductData();
        data.deleteProduct(productID);
    }

    public void createProductCategory(String name) throws DataBaseException {
        ProductData data = new ProductData();
        data.createProductCategory(name);
    }

    public void addDiscount(Discount discount) throws DataBaseException {
        ProductData data = new ProductData();
        data.addDiscount(discount);
    }

    public void deleteDiscount(int discountID) throws DataBaseException {
        ProductData data = new ProductData();
        data.deleteDiscount(discountID);
    }
}
