package be.henallux.project.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import be.henallux.project.model.exception.DataValidationException;
import be.henallux.project.model.*;

public class LocationProductTest {
    @Test
    public void basicCreationTest() {
        try {
            String shelf = "A";
            String floor = "1";
            boolean isStock = true;
            boolean isFreezer = false;
            LocationProduct locationProduct = new LocationProduct(shelf, floor, isStock, isFreezer);
            assertEquals("A-1-true", locationProduct.getLocationProductId(), "Assertion creation LP1 has failed, IDs are different");
            assertEquals(shelf, locationProduct.getShelf(), "Assertion creation A has failed, shelves are different");
            assertEquals(floor, locationProduct.getFloor(), "Assertion creation 1 has failed, floors are different");
            assertEquals(isStock, locationProduct.getIsStock(), "Assertion creation true has failed, stock status are different");
            assertEquals(isFreezer, locationProduct.getIsFreezer(), "Assertion creation false has failed, freezer status are different");
        } catch (DataValidationException e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void comparisonEqualTest() {
        try {
            assertEquals(new LocationProduct("LP1", "A", true, true),
                    new LocationProduct("LP1", "A", true, true),
                    "AssertEqual LP1 A true true not OK");
        } catch (DataValidationException e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void comparisonNotEqualTest() {
        try {
            assertNotEquals(new LocationProduct("LP1", "A", true, true),
                            new LocationProduct("LP2", "B", true, false),
                            "AssertEqual LP1 A 1 true false VS LP2 B 2 false true not OK");
        } catch (DataValidationException e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void wrongShelf() {
        assertThrows(DataValidationException.class, () -> { new LocationProduct("", "1", true, true); });
    }

    @Test
    public void wrongFloor() {
        assertThrows(DataValidationException.class, () -> { new LocationProduct("LP1", "", true, true); });
    }
}
