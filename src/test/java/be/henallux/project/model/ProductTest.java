package be.henallux.project.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import be.henallux.project.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import be.henallux.project.model.exception.DataValidationException;
import be.henallux.project.data.exception.DataBaseException;
import be.henallux.project.model.Product;
import be.henallux.project.model.ProductCategory;


public class ProductTest {

    private int id;
    private String name;
    private BigDecimal priceEVAT;
    private BigDecimal vat;
    private int fidelityPoint;
    private boolean isEdible;
    private int minStockQuantity;
    private ProductCategory category;
    private List<Discount> discounts;
    private Product product;
    private LocationProduct locationProduct;

    @BeforeEach
    public void setUp() throws DataValidationException {
        try {
            category = new ProductCategory(1, "Electronics");
            locationProduct = new LocationProduct("etagere 1", "etage 2", true, false);

            List<QuantityProduct> location = new ArrayList<>();
            product = new Product(
                    1, "Smartphone", new BigDecimal("500"), new BigDecimal("50"),
                    10, true, 5, category, null, null
            );
            id = 1;
            name = "Laptop";
            priceEVAT = new BigDecimal("1000");
            vat = new BigDecimal("21");
            fidelityPoint = 10;
            isEdible = false;
            minStockQuantity = 5;

            discounts = new ArrayList<>();

            product = new Product(
                    id, name, priceEVAT, vat,
                    fidelityPoint, isEdible, minStockQuantity,
                    category, null, null
            );
        } catch (DataValidationException e) {
            fail("Failed to initialize test dependencies");
        }
    }

    @Test
    public void basicCreationTest() {
        assertEquals(id, product.getId(), "id should be 1");
        assertEquals(name, product.getName(), "name should be Laptop");
        assertEquals(0, priceEVAT.compareTo(product.getPriceEVAT()), "costPrice should be 1000");
        assertEquals(0, vat.compareTo(product.getVat()), "sellingPrice should be 1500");
        assertEquals(0, product.getStockQuantity(), "stockQuantity should be 0 at creation");
        assertEquals(minStockQuantity, product.getMinStockQuantity(), "minStock should be 5");
        assertEquals(category, product.getCategory(), "category should be Electronics");
        assertNotNull(product.getDiscounts(), "discounts should not be null");
    }

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        Product product2 = new Product(
                id, name, priceEVAT, vat,
                fidelityPoint, isEdible, minStockQuantity,
                category, null, discounts
        );
        assertEquals(product, product2, "Two identical Products should be equal");
    }

    @Test
    public void comparisonNotEqualTest() throws DataValidationException {
        Product product2 = new Product(
                2, "Banane", new BigDecimal("500"), new BigDecimal("50"),
                10, true, 5, category, null, null);
        assertNotEquals(product, product2, "Different Products should not be equal");
    }

    @Test
    public void negativeIdThrows() {
        assertThrows(DataValidationException.class, () ->
                new Product(-1, name, priceEVAT, vat,
                        fidelityPoint, isEdible, minStockQuantity,
                        category, null, discounts)
        );
    }
}