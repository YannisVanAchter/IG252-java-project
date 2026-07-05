package be.henallux.project.model;

import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.Test;

import be.henallux.project.model.exception.DataValidationException;
import be.henallux.project.model.*;

public class LocalityTest {
    @Test
    public void basicCreationTest() {
        try {
            String name = "Namur";
            int postalCode = 5000;
            Locality l = new Locality(name, postalCode);
            assertEquals(name, l.getCity(), "Assertion creation Namur has failed, names are different");
            assertEquals(postalCode, l.getPostalCode(), "Assertion creation 5000 has failed, postal codes are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonEqualTest() {
        try {
            assertEquals(new Locality("Namur", 5000), new Locality("Namur", 5000), "AssertEqual Namur 5000 not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonNotEqualTest() {
        try {
            assertNotEquals(new Locality("Namuur", 5000), new Locality("Namur", 5000), "AssertEqual Namuur VS Namur, 5000 not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void wrongPostalCode() {
        assertThrows(DataValidationException.class, () -> { new Locality("Namur", -32); });
    }
}
