package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import exception.DataValidationException;
import model.ProductCategory;

public class ProductCategoryTest {

    @Test
    public void basicCreationTest() throws DataValidationException {
        int id = 1;
        String name = "Category 1";
        ProductCategory pc = new ProductCategory(id, name);
        assertEquals(id,   pc.getId(),   "Assertion creation id 1 has failed");
        assertEquals(name, pc.getName(), "Assertion creation name Category 1 has failed");
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
            "Deux ProductCategory identiques devraient être égaux"
        );
    }

    @Test
    public void comparisonNotEqualDifferentId() throws DataValidationException {
        assertNotEquals(
            new ProductCategory(1, "Category 1"),
            new ProductCategory(2, "Category 1"),
            "Des ProductCategory avec des ids différents ne devraient pas être égaux"
        );
    }

    @Test
    public void comparisonNotEqualDifferentName() throws DataValidationException {
        assertNotEquals(
            new ProductCategory(1, "Category 1"),
            new ProductCategory(1, "Category 2"),
            "Des ProductCategory avec des noms différents ne devraient pas être égaux"
        );
    }

    @Test
    public void comparisonNotEqualBothDifferent() throws DataValidationException {
        assertNotEquals(
            new ProductCategory(1, "Category 1"),
            new ProductCategory(2, "Category 2"),
            "Des ProductCategory entièrement différents ne devraient pas être égaux"
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
        assertEquals("Category 1", pc.getLabel(), "getLabel doit retourner le nom");
    }

    @Test
    public void toStringTest() throws DataValidationException {
        ProductCategory pc = new ProductCategory(1, "Category 1");
        String result = pc.toString();
        assertTrue(result.contains("id=1"),          "toString doit contenir id=1");
        assertTrue(result.contains("Category 1"),    "toString doit contenir le nom");
    }
}