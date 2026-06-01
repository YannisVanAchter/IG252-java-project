package be.henallux.project.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.*;

public class InnerDocumentTypeTest {

    public void basicCreationTest() throws DataValidationException {
        String name = "Passport";
        DocumentType dt = new DocumentType(name);
        assertEquals(name, dt.getName(), "Assertion creation Passport has failed, names are different");
    }

    @Test
    public void comparisonEqualTest() {
        assertEquals(new DocumentType("Passport"), new DocumentType("Passport"), "AssertEqual Passport not OK");
    }

    @Test
    public void comparisonNotEqualTest() {
        assertNotEquals(new DocumentType("Passpoort"), new DocumentType("Passport"), "AssertEqual Passpoort VS Passport not OK");
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