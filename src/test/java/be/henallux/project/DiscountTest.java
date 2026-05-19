package test.java.be.henallux.project;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Discount;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.ProductCategory;

public class DiscountTest {

    private int requiredQuantity;
    private BigDecimal discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
    private Product product;

    @BeforeEach
    public void setUp() throws DataValidationException {
        try {
        requiredQuantity   = 2;
        discountPercentage = new BigDecimal("10");
        startDate          = LocalDate.of(2024, 1, 1);
        endDate            = LocalDate.of(2024, 1, 31);
        name               = "New Year Discount";
        ProductCategory fruitsCategory = new ProductCategory(1, "Fruits", new ArrayList<>());
        product = new Product(
            1, "Smartphone", new BigDecimal("500"), new BigDecimal("50"),
            10, true, 5, fruitsCategory, new ArrayList<>()
        );
        } catch (DataValidationException e) {
            fail("Failed to initialize test dependencies");
        }
    }

    private Discount buildValid() throws DataValidationException {
        return new Discount(requiredQuantity, discountPercentage, startDate, endDate, name, product);
    }

    @Test
    public void basicCreationTest() throws DataValidationException {
        Discount discount = buildValid();
        assertEquals(requiredQuantity, discount.getRequiredQuantity());
        assertEquals(0,              discountPercentage.compareTo(discount.getDiscountPercentage()));
        assertEquals(startDate,    discount.getStartDate());
        assertEquals(endDate,      discount.getEndDate());
        assertEquals(name,     discount.getName());
        assertEquals(product,   discount.getProduct());
    }

    @Test
    public void invalidRequiredQuantityZeroThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(0, discountPercentage, startDate, endDate, name, product)
        );
    }

    @Test
    public void invalidRequiredQuantityNegativeThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(-1, discountPercentage, startDate, endDate, name, product)
        );
    }

    @Test
    public void requiredQuantityOneIsValid() throws DataValidationException {
        Discount d = new Discount(1, discountPercentage, startDate, endDate, name, product);
        assertEquals(1, d.getRequiredQuantity());
    }

    @Test
    public void nullDiscountPercentageThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(requiredQuantity, null, startDate, endDate, name, product)
        );
    }

    @Test
    public void negativeDiscountPercentageThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(requiredQuantity, new BigDecimal("-5"), startDate, endDate, name, product)
        );
    }

    @Test
    public void discountPercentageOver100Throws() {
        assertThrows(DataValidationException.class, () ->
            new Discount(requiredQuantity, new BigDecimal("150"), startDate, endDate, name, product)
        );
    }

    @Test
    public void discountPercentageZeroIsValid() throws DataValidationException {
        Discount d = new Discount(requiredQuantity, BigDecimal.ZERO, startDate, endDate, name, product);
        assertEquals(0, BigDecimal.ZERO.compareTo(d.getDiscountPercentage()));
    }

    @Test
    public void discountPercentage100IsValid() throws DataValidationException {
        Discount d = new Discount(requiredQuantity, new BigDecimal("100"), startDate, endDate, name, product);
        assertEquals(0, new BigDecimal("100").compareTo(d.getDiscountPercentage()));
    }

    @Test
    public void nullStartDateThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(requiredQuantity, discountPercentage, null, endDate, name, product)
        );
    }

    @Test
    public void nullEndDateThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(requiredQuantity, discountPercentage, startDate, null, name, product)
        );
    }

    @Test
    public void endDateBeforeStartDateThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(requiredQuantity, discountPercentage, LocalDate.of(2024, 1, 31), LocalDate.of(2024, 1, 1), name, product)
        );
    }

    @Test
    public void startDateEqualsEndDateIsValid() throws DataValidationException {
        LocalDate sameDay = LocalDate.of(2024, 1, 15);
        Discount d = new Discount(requiredQuantity, discountPercentage, sameDay, sameDay, name, product);
        assertEquals(sameDay, d.getStartDate());
        assertEquals(sameDay, d.getEndDate());
    }

    @Test
    public void nullNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(requiredQuantity, discountPercentage, startDate, endDate, null, product)
        );
    }

    @Test
    public void emptyNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(requiredQuantity, discountPercentage, startDate, endDate, "", product)
        );
    }

    @Test
    public void nullProductThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(requiredQuantity, discountPercentage, startDate, endDate, name, null)
        );
    }

    @Test
    public void getLabelTest() throws DataValidationException {
        Discount d = buildValid();
        String label = d.getLabel();
        assertTrue(label.contains(name),                     "getLabel should contain the name");
        assertTrue(label.contains("10"),                           "getLabel should contain the percentage");
        assertTrue(label.contains(String.valueOf(requiredQuantity)), "getLabel should contain the minimum quantity");
    }

    @Test
    public void toStringTest() throws DataValidationException {
        Discount d = buildValid();
        String result = d.toString();
        assertTrue(result.contains("requiredQuantity=2"),       "toString should contain requiredQuantity=2");
        assertTrue(result.contains("discountPercentage=10"),    "toString should contain discountPercentage=10");
        assertTrue(result.contains("name='New Year Discount'"), "toString should contain the name");
    }

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        Discount d1 = buildValid();
        Discount d2 = buildValid();
        assertEquals(d1, d2, "Two identical Discounts should be equal");
    }

    @Test
    public void comparisonNotEqualDifferentQuantity() throws DataValidationException {
        assertNotEquals(
            buildValid(),
            new Discount(3, discountPercentage, startDate, endDate, name, product)
        );
    }

    @Test
    public void comparisonNotEqualDifferentPercentage() throws DataValidationException {
        assertNotEquals(
            buildValid(),
            new Discount(requiredQuantity, new BigDecimal("15"), startDate, endDate, name, product)
        );
    }

    @Test
    public void comparisonNotEqualDifferentName() throws DataValidationException {
        assertNotEquals(
            buildValid(),
            new Discount(requiredQuantity, discountPercentage, startDate, endDate, "Other Discount", product)
        );
    }

    @Test
    public void comparisonNotEqualDifferentProduct() throws DataValidationException {
        Product otherProduct = new Product(2, "Laptop", new BigDecimal("1000"), new BigDecimal("100"), 5,
        true, 10, new ProductCategory(2, "fruitsCategory", new ArrayList<>()), new ArrayList<>()
        );
        assertNotEquals(
            buildValid(),
            new Discount(requiredQuantity, discountPercentage, startDate, endDate, name, otherProduct)
        );
    }
}