package test;

import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.Test;

import exception.DataValidationException;

import model.*;

public class LocationTest {
    @Test
    public void basicCreationTest() {
        try {
            String name = "Namur";
            int postalCode = 5000;
            Location l = new Location(name, postalCode);
            assertEquals(name, l.getName(), "Assertion creation Namur has failed, names are different");
            assertEquals(postalCode, l.getPostalCode(), "Assertion creation 5000 has failed, postal codes are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonEqualTest() {
        try {
            assertEquals(new Location("Namur", 5000), new Location("Namur", 5000), "AssertEqual Namur 5000 not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonNotEqualTest() {
        try {
            assertNotEquals(new Location("Namuur", 5000), new Location("Namur", 5000), "AssertEqual Namuur VS Namur, 5000 not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void wrongPostalCode() {
        assertThrows(DataValidationException.class, () -> { new Location("Namur", -32); });
    }
}
