package be.henallux.project.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.*;

public class StatusTest {
    @Test
    public void basicCreationTest() {
        try {
            String name = "Test";
            Status status = new Status(name);
            assertEquals(name, status.getName(), "Assertion creation Test has failed, names are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonEqualTest() {
        try {
            assertEquals(new Status("Test"), new Status("Test"), "AssertEqual Test has failed, names are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonNotEqualTest() {
        try {
            assertNotEquals(new Status("Test"), new Status("Test2"), "AssertEqual Test has failed, names are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void emptyNameTest() {
        assertThrows(DataValidationException.class, () -> { new Status(""); });
    }
}
