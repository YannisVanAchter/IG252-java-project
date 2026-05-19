package test.java.be.henallux.project;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.*;

public class LocationProductTest {
    @Test
    public void basicCreationTest() {
        try {
            String locationProductId = "LP1";
            String shelf = "A";
            String floor = "1";
            boolean isStock = true;
            boolean isFreezer = false;
            LocationProduct locationProduct = new LocationProduct(locationProductId, shelf, floor, isStock, isFreezer);
            assertEquals(locationProductId, locationProduct.getLocationProductId(), "Assertion creation LP1 has failed, IDs are different");
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
            assertEquals(new LocationProduct("LP1", "A", "1", true, false), new LocationProduct("LP1", "A", "1", true, false), "AssertEqual LP1 A 1 true false not OK");
        } catch (DataValidationException e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void comparisonNotEqualTest() {
        try {
            assertNotEquals(new LocationProduct("LP1", "A", "1", true, false), new LocationProduct("LP2", "B", "2", false, true), "AssertEqual LP1 A 1 true false VS LP2 B 2 false true not OK");
        } catch (DataValidationException e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void wrongShelf() {
        assertThrows(DataValidationException.class, () -> { new LocationProduct("LP1", "", "1", true, false); });
    }

    @Test
    public void wrongFloor() {
        assertThrows(DataValidationException.class, () -> { new LocationProduct("LP1", "A", "", true, false); });
    }
}
