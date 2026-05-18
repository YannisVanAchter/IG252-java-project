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

    private static final int        VALID_QUANTITY   = 2;
    private static final BigDecimal VALID_PERCENTAGE = new BigDecimal("10");
    private static final LocalDate  VALID_START      = LocalDate.of(2024, 1, 1);
    private static final LocalDate  VALID_END        = LocalDate.of(2024, 1, 31);
    private static final String     VALID_NAME       = "New Year Discount";

    private ProductCategory categoryFood;
    private ProductCategory categoryElectronics;
    private Product         validProduct;

    @BeforeEach
    public void setUp() throws DataValidationException {
        categoryFood        = new ProductCategory(1, "Food");
        categoryElectronics = new ProductCategory(2, "Electronics");
        validProduct        = new Product(
            1, "Test Product", new BigDecimal("100"), new BigDecimal("20"),
            10, true, 5, categoryFood, new ArrayList<>()
        );
    }

    private Discount buildValid() throws DataValidationException {
        return new Discount(VALID_QUANTITY, VALID_PERCENTAGE, VALID_START, VALID_END, VALID_NAME, validProduct);
    }

    @Test
    public void basicCreationTest() throws DataValidationException {
        Discount discount = buildValid();
        assertEquals(VALID_QUANTITY, discount.getRequiredQuantity());
        assertEquals(0,              VALID_PERCENTAGE.compareTo(discount.getDiscountPercentage()));
        assertEquals(VALID_START,    discount.getStartDate());
        assertEquals(VALID_END,      discount.getEndDate());
        assertEquals(VALID_NAME,     discount.getName());
        assertEquals(validProduct,   discount.getProduct());
    }

    @Test
    public void invalidRequiredQuantityZeroThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(0, VALID_PERCENTAGE, VALID_START, VALID_END, VALID_NAME, validProduct)
        );
    }

    @Test
    public void invalidRequiredQuantityNegativeThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(-1, VALID_PERCENTAGE, VALID_START, VALID_END, VALID_NAME, validProduct)
        );
    }

    @Test
    public void requiredQuantityOneIsValid() throws DataValidationException {
        Discount d = new Discount(1, VALID_PERCENTAGE, VALID_START, VALID_END, VALID_NAME, validProduct);
        assertEquals(1, d.getRequiredQuantity());
    }

    @Test
    public void nullDiscountPercentageThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(VALID_QUANTITY, null, VALID_START, VALID_END, VALID_NAME, validProduct)
        );
    }

    @Test
    public void negativeDiscountPercentageThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(VALID_QUANTITY, new BigDecimal("-5"), VALID_START, VALID_END, VALID_NAME, validProduct)
        );
    }

    @Test
    public void discountPercentageOver100Throws() {
        assertThrows(DataValidationException.class, () ->
            new Discount(VALID_QUANTITY, new BigDecimal("150"), VALID_START, VALID_END, VALID_NAME, validProduct)
        );
    }

    @Test
    public void discountPercentageZeroIsValid() throws DataValidationException {
        Discount d = new Discount(VALID_QUANTITY, BigDecimal.ZERO, VALID_START, VALID_END, VALID_NAME, validProduct);
        assertEquals(0, BigDecimal.ZERO.compareTo(d.getDiscountPercentage()));
    }

    @Test
    public void discountPercentage100IsValid() throws DataValidationException {
        Discount d = new Discount(VALID_QUANTITY, new BigDecimal("100"), VALID_START, VALID_END, VALID_NAME, validProduct);
        assertEquals(0, new BigDecimal("100").compareTo(d.getDiscountPercentage()));
    }

    @Test
    public void nullStartDateThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(VALID_QUANTITY, VALID_PERCENTAGE, null, VALID_END, VALID_NAME, validProduct)
        );
    }

    @Test
    public void nullEndDateThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(VALID_QUANTITY, VALID_PERCENTAGE, VALID_START, null, VALID_NAME, validProduct)
        );
    }

    @Test
    public void endDateBeforeStartDateThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(VALID_QUANTITY, VALID_PERCENTAGE,
                LocalDate.of(2024, 2, 1), LocalDate.of(2024, 1, 1),
                VALID_NAME, validProduct)
        );
    }

    @Test
    public void startDateEqualsEndDateIsValid() throws DataValidationException {
        LocalDate sameDay = LocalDate.of(2024, 1, 15);
        Discount d = new Discount(VALID_QUANTITY, VALID_PERCENTAGE, sameDay, sameDay, VALID_NAME, validProduct);
        assertEquals(sameDay, d.getStartDate());
        assertEquals(sameDay, d.getEndDate());
    }

    @Test
    public void nullNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(VALID_QUANTITY, VALID_PERCENTAGE, VALID_START, VALID_END, null, validProduct)
        );
    }

    @Test
    public void emptyNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(VALID_QUANTITY, VALID_PERCENTAGE, VALID_START, VALID_END, "", validProduct)
        );
    }

    @Test
    public void nullProductThrows() {
        assertThrows(DataValidationException.class, () ->
            new Discount(VALID_QUANTITY, VALID_PERCENTAGE, VALID_START, VALID_END, VALID_NAME, null)
        );
    }

    @Test
    public void getLabelTest() throws DataValidationException {
        Discount d = buildValid();
        String label = d.getLabel();
        assertTrue(label.contains(VALID_NAME),                     "getLabel should contain the name");
        assertTrue(label.contains("10"),                           "getLabel should contain the percentage");
        assertTrue(label.contains(String.valueOf(VALID_QUANTITY)), "getLabel should contain the minimum quantity");
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
            new Discount(3, VALID_PERCENTAGE, VALID_START, VALID_END, VALID_NAME, validProduct)
        );
    }

    @Test
    public void comparisonNotEqualDifferentPercentage() throws DataValidationException {
        assertNotEquals(
            buildValid(),
            new Discount(VALID_QUANTITY, new BigDecimal("15"), VALID_START, VALID_END, VALID_NAME, validProduct)
        );
    }

    @Test
    public void comparisonNotEqualDifferentName() throws DataValidationException {
        assertNotEquals(
            buildValid(),
            new Discount(VALID_QUANTITY, VALID_PERCENTAGE, VALID_START, VALID_END, "Other Discount", validProduct)
        );
    }

    @Test
    public void comparisonNotEqualDifferentProduct() throws DataValidationException {
        Product otherProduct = new Product(
            2, "Another Product", new BigDecimal("200"), new BigDecimal("30"),
            20, true, 10, categoryElectronics, new ArrayList<>()
        );
        assertNotEquals(
            buildValid(),
            new Discount(VALID_QUANTITY, VALID_PERCENTAGE, VALID_START, VALID_END, VALID_NAME, otherProduct)
        );
    }
}