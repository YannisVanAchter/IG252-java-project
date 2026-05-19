package test.java.be.henallux.project;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.ProductCategory;
import exception.DataValidationException;
import model.Discount;
import model.Product;
import model.ProductCategory;


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

    @BeforeEach
    public void setUp() throws DataValidationException {
        try {
            id = 1;
            name = "Laptop";
            priceEVAT = new BigDecimal("1000");
            vat = new BigDecimal("500");
            fidelityPoint = 10;
            isEdible = false;
            minStockQuantity = 5;
            category = new ProductCategory(1, "Electronics", new ArrayList<>());
            discounts = new ArrayList<>();
        } catch (DataValidationException e) {
            fail("Failed to initialize test dependencies");
        }
    }

    @Test
    public void basicCreationTest() {
        assertEquals(id,   product.getId(),   "id should be 1");
        assertEquals(name, product.getName(), "name should be Laptop");
        assertEquals(0, priceEVAT.compareTo(product.getCostPrice()),       "costPrice should be 1000");
        assertEquals(0, vat.compareTo(product.getSellingPrice()), "sellingPrice should be 1500");
        assertEquals(minStockQuantity,     product.getStockQuantity(), "stockQuantity should be 100");
        assertFalse(product.getIsAvailable(),                     "isAvailable should be false");
        assertEquals(minStockQuantity, product.getMinStock(),      "minStock should be 5");
        assertEquals(category,   product.getCategory(),      "category should be Electronics");
        assertNotNull(product.getDiscounts(),                      "discounts should not be null");
    }

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        Product product2 = new Product(
            id, name, priceEVAT, vat,
            fidelityPoint, isEdible, minStockQuantity,
            category, discounts
        );
        assertEquals(product, product2, "Two identical Products should be equal");
    }

    @Test
    public void comparisonNotEqualTest() throws DataValidationException {
        Product product2 = new Product(
            2, "Smartphone", new BigDecimal("500"), new BigDecimal("800"),
            50, false, 3, category, new ArrayList<>()
        );
        assertNotEquals(product, product2, "Different Products should not be equal");
    }

    @Test
    public void negativeIdThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(-1, name, priceEVAT, vat,
                fidelityPoint, isEdible, minStockQuantity,
                category, discounts)
        );
    }

    @Test
    public void zeroIdIsValid() throws DataValidationException {
        Product p = new Product(
            0, name, priceEVAT, vat,
            fidelityPoint, isEdible, minStockQuantity,
            category, discounts
        );
        assertEquals(0, p.getId());
    }

    @Test
    public void emptyNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(id, "", priceEVAT, vat,
                fidelityPoint, isEdible, minStockQuantity,
                category, discounts)
        );
    }

    @Test
    public void nullNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(id, null, priceEVAT, vat,
                fidelityPoint, isEdible, minStockQuantity,
                category, discounts)
        );
    }

    @Test
    public void negativeCostPriceThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(id, name, new BigDecimal("-1"), vat,
                fidelityPoint, isEdible, minStockQuantity,
                category, new ArrayList<>())
        );
    }

    @Test
    public void nullCostPriceThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(id, name, null, vat,
                fidelityPoint, isEdible, minStockQuantity,
                category, new ArrayList<>())
        );
    }

    @Test
    public void negativeSellingPriceThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(id, name, priceEVAT, new BigDecimal("-1"),
                stockQuantity, isAvailable, minStockQuantity,
                category, new ArrayList<>())
        );
    }

    @Test
    public void sellingPriceLessThanCostPriceThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(id, name, priceEVAT, new BigDecimal("500"),
                stockQuantity, isAvailable, minStockQuantity,
                category, new ArrayList<>())
        );
    }

    @Test
    public void sellingPriceEqualToCostPriceIsValid() throws DataValidationException {
        Product p = new Product(
            id, name, priceEVAT, priceEVAT,
            stockQuantity, isAvailable, minStockQuantity,
            category, new ArrayList<>()
        );
        assertEquals(0, priceEVAT.compareTo(p.getSellingPrice()));
    }

    @Test
    public void negativeStockQuantityThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(id, name, priceEVAT, vat,
                fidelityPoint, isEdible, -1,
                category, new ArrayList<>())
        );
    }

    @Test
    public void zeroStockIsValid() throws DataValidationException {
        Product p = new Product(
            id, name, priceEVAT, vat,
            fidelityPoint, isEdible, 0,
            category, new ArrayList<>()
        );
        assertEquals(0, p.getStockQuantity());
    }
}