package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.DataValidationException;
import model.*;

public class InnerDocumentTypeTest {

    public void basicCreationTest() {
        try {
            String name = "Passport";
            DocumentType dt = new DocumentType(name);
            assertEquals(name, dt.getName(), "Assertion creation Passport has failed, names are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonEqualTest() {
        try {
            assertEquals(new DocumentType("Passport"), new DocumentType("Passport"), "AssertEqual Passport not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonNotEqualTest() {
        try {
            assertNotEquals(new DocumentType("Passpoort"), new DocumentType("Passport"), "AssertEqual Passpoort VS Passport not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void wrongName() {
        assertThrows(DataValidationException.class, () -> { new DocumentType(""); });
    }

    @Test
    public void nullName() {
        assertThrows(DataValidationException.class, () -> { new DocumentType(null); });
    }
}