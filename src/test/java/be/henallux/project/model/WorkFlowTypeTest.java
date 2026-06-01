package be.henallux.project.model;

import static org.junit.jupiter.api.Assertions.*; 
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.*;

public class WorkFlowTypeTest {
    @Test
    public void basicCreationTest() {
        try {
            int id = 123;
            String name = "Test";
            boolean isBuy = true;
            boolean isSell = false;
            boolean isInternal = false;
            WorkFlowType wft = new WorkFlowType(id, name, isBuy, isSell, isInternal);
            assertEquals(name, wft.getName(), "Assertion creation Test has failed, names are different");
        } catch (DataValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void wrongCombination() {
        assertThrows(DataValidationException.class, () -> { new WorkFlowType(123, "madame leroy", true, false, false); });
        assertThrows(DataValidationException.class, () -> { new WorkFlowType(456, "madame louise", false, true, false); });
        assertThrows(DataValidationException.class, () -> { new WorkFlowType(789, "madame leboeuf", false, false, true); });
    }
}
