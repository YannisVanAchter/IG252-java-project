package test.java.be.henallux.project;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.ProductCategory;

public class ProductTest {

    private static final int        VALID_ID        = 1;
    private static final String     VALID_NAME      = "Laptop";
    private static final BigDecimal VALID_COST      = new BigDecimal("1000");
    private static final BigDecimal VALID_SELLING   = new BigDecimal("1500");
    private static final int        VALID_STOCK     = 100;
    private static final boolean    VALID_AVAILABLE = false;
    private static final int        VALID_MIN_STOCK = 5;

    private ProductCategory validCategory;
    private Product         product;

    @BeforeEach
    public void setUp() throws DataValidationException {
        validCategory = new ProductCategory(1, "Electronics");
        product = new Product(
            VALID_ID, VALID_NAME, VALID_COST, VALID_SELLING,
            VALID_STOCK, VALID_AVAILABLE, VALID_MIN_STOCK,
            validCategory, new ArrayList<>()
        );
    }

    @Test
    public void basicCreationTest() {
        assertEquals(VALID_ID,   product.getId(),   "id should be 1");
        assertEquals(VALID_NAME, product.getName(), "name should be Laptop");
        assertEquals(0, VALID_COST.compareTo(product.getCostPrice()),       "costPrice should be 1000");
        assertEquals(0, VALID_SELLING.compareTo(product.getSellingPrice()), "sellingPrice should be 1500");
        assertEquals(VALID_STOCK,     product.getStockQuantity(), "stockQuantity should be 100");
        assertFalse(product.getIsAvailable(),                     "isAvailable should be false");
        assertEquals(VALID_MIN_STOCK, product.getMinStock(),      "minStock should be 5");
        assertEquals(validCategory,   product.getCategory(),      "category should be Electronics");
        assertNotNull(product.getDiscounts(),                      "discounts should not be null");
    }

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        Product product2 = new Product(
            VALID_ID, VALID_NAME, VALID_COST, VALID_SELLING,
            VALID_STOCK, VALID_AVAILABLE, VALID_MIN_STOCK,
            validCategory, new ArrayList<>()
        );
        assertEquals(product, product2, "Two identical Products should be equal");
    }

    @Test
    public void comparisonNotEqualTest() throws DataValidationException {
        Product product2 = new Product(
            2, "Smartphone", new BigDecimal("500"), new BigDecimal("800"),
            50, false, 3, validCategory, new ArrayList<>()
        );
        assertNotEquals(product, product2, "Different Products should not be equal");
    }

    @Test
    public void negativeIdThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(-1, VALID_NAME, VALID_COST, VALID_SELLING,
                VALID_STOCK, VALID_AVAILABLE, VALID_MIN_STOCK,
                validCategory, new ArrayList<>())
        );
    }

    @Test
    public void zeroIdIsValid() throws DataValidationException {
        Product p = new Product(
            0, VALID_NAME, VALID_COST, VALID_SELLING,
            VALID_STOCK, VALID_AVAILABLE, VALID_MIN_STOCK,
            validCategory, new ArrayList<>()
        );
        assertEquals(0, p.getId());
    }

    @Test
    public void emptyNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(VALID_ID, "", VALID_COST, VALID_SELLING,
                VALID_STOCK, VALID_AVAILABLE, VALID_MIN_STOCK,
                validCategory, new ArrayList<>())
        );
    }

    @Test
    public void nullNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(VALID_ID, null, VALID_COST, VALID_SELLING,
                VALID_STOCK, VALID_AVAILABLE, VALID_MIN_STOCK,
                validCategory, new ArrayList<>())
        );
    }

    @Test
    public void negativeCostPriceThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(VALID_ID, VALID_NAME, new BigDecimal("-1"), VALID_SELLING,
                VALID_STOCK, VALID_AVAILABLE, VALID_MIN_STOCK,
                validCategory, new ArrayList<>())
        );
    }

    @Test
    public void nullCostPriceThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(VALID_ID, VALID_NAME, null, VALID_SELLING,
                VALID_STOCK, VALID_AVAILABLE, VALID_MIN_STOCK,
                validCategory, new ArrayList<>())
        );
    }

    @Test
    public void negativeSellingPriceThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(VALID_ID, VALID_NAME, VALID_COST, new BigDecimal("-1"),
                VALID_STOCK, VALID_AVAILABLE, VALID_MIN_STOCK,
                validCategory, new ArrayList<>())
        );
    }

    @Test
    public void sellingPriceLessThanCostPriceThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(VALID_ID, VALID_NAME, VALID_COST, new BigDecimal("900"),
                VALID_STOCK, VALID_AVAILABLE, VALID_MIN_STOCK,
                validCategory, new ArrayList<>())
        );
    }

    @Test
    public void sellingPriceEqualToCostPriceIsValid() throws DataValidationException {
        Product p = new Product(
            VALID_ID, VALID_NAME, VALID_COST, VALID_COST,
            VALID_STOCK, VALID_AVAILABLE, VALID_MIN_STOCK,
            validCategory, new ArrayList<>()
        );
        assertEquals(0, VALID_COST.compareTo(p.getSellingPrice()));
    }

    @Test
    public void negativeStockQuantityThrows() {
        assertThrows(DataValidationException.class, () ->
            new Product(VALID_ID, VALID_NAME, VALID_COST, VALID_SELLING,
                -1, VALID_AVAILABLE, VALID_MIN_STOCK,
                validCategory, new ArrayList<>())
        );
    }

    @Test
    public void zeroStockIsValid() throws DataValidationException {
        Product p = new Product(
            VALID_ID, VALID_NAME, VALID_COST, VALID_SELLING,
            0, VALID_AVAILABLE, VALID_MIN_STOCK,
            validCategory, new ArrayList<>()
        );
        assertEquals(0, p.getStockQuantity());
    }
}