package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ProductSearchManager {
    private String name;
    private String category;
    private Boolean discount;

    public ProductSearchManager(String name, String category, Boolean discount) {
        this.name = name;
        this.category = category;
        this.discount = discount;
    }

    public List<Product> searchProducts(String name, String category, Boolean discount) throws DataBaseException {
        ProductData productData = new ProductData();
        return productData.search(name, category, discount);
    }
}
