package test.java.be.henallux.project;

import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.exception.DataValidationException;

import main.java.be.henallux.project.model.*;

public class WorkFlowTypeTest {
    @Test
    public void basicCreationTest() {
        try {
            String name = "Test";
            boolean isBuy = true;
            boolean isSell = false;
            boolean isInternal = false;
            WorkFlowType wft = new WorkFlowType(name, isBuy, isSell, isInternal);
            assertEquals(name, wft.getName(), "Assertion creation Test has failed, names are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonEqualTest() {
        try {
            assertEquals(new WorkFlowType("Test", true, false, false), new WorkFlowType("Test", true, false, false), "AssertEqual Test not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void comparisonNotEqualTest() {
        try {
            assertNotEquals(new WorkFlowType("Test", true, false, false), new WorkFlowType("Test", false, true, false), "AssertEqual Test buy VS sell not OK");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void wrongCombination() {
        assertThrows(DataValidationException.class, () -> { new WorkFlowType("Test", true, true, false); });
        assertThrows(DataValidationException.class, () -> { new WorkFlowType("Test", true, false, true); });
        assertThrows(DataValidationException.class, () -> { new WorkFlowType("Test", false, true, true); });
    }
}
