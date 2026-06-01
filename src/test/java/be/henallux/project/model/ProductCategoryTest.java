package be.henallux.project.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.ProductCategory;

public class ProductCategoryTest {

    @Test
    public void basicCreationTest() throws DataValidationException {
        int id = 1;
        String name = "Category 1";
        ProductCategory pc = new ProductCategory(id, name);
        assertEquals(id,   pc.getId(),   "id should be 1");
        assertEquals(name, pc.getName(), "name should be Category 1");
    }

    @Test
    public void zeroIdIsValid() throws DataValidationException {
        ProductCategory pc = new ProductCategory(0, "Category Zero");
        assertEquals(0, pc.getId());
    }

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        assertEquals(
            new ProductCategory(1, "Category 1"),
            new ProductCategory(1, "Category 1"),
            "Two identical ProductCategories should be equal"
        );
    }

    @Test
    public void comparisonNotEqualDifferentId() throws DataValidationException {
        assertNotEquals(
            new ProductCategory(1, "Category 1"),
            new ProductCategory(2, "Category 1"),
            "ProductCategories with different ids should not be equal"
        );
    }

    @Test
    public void comparisonNotEqualDifferentName() throws DataValidationException {
        assertNotEquals(
            new ProductCategory(1, "Category 1"),
            new ProductCategory(1, "Category 2"),
            "ProductCategories with different names should not be equal"
        );
    }

    @Test
    public void comparisonNotEqualBothDifferent() throws DataValidationException {
        assertNotEquals(
            new ProductCategory(1, "Category 1"),
            new ProductCategory(2, "Category 2"),
            "Completely different ProductCategories should not be equal"
        );
    }

    @Test
    public void negativeIdThrows() {
        assertThrows(DataValidationException.class, () ->
            new ProductCategory(-1, "Category 1")
        );
    }

    @Test
    public void emptyNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new ProductCategory(1, "")
        );
    }

    @Test
    public void nullNameThrows() {
        assertThrows(DataValidationException.class, () ->
            new ProductCategory(1, null)
        );
    }

    @Test
    public void getLabelTest() throws DataValidationException {
        ProductCategory pc = new ProductCategory(1, "Category 1");
        assertEquals("Category 1", pc.getLabel(), "getLabel should return the name");
    }

    @Test
    public void toStringTest() throws DataValidationException {
        ProductCategory pc = new ProductCategory(1, "Category 1");
        String result = pc.toString();
        assertTrue(result.contains("id=1"),       "toString should contain id=1");
        assertTrue(result.contains("Category 1"), "toString should contain the name");
    }
}