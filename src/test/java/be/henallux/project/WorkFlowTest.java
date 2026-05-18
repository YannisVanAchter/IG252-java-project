package test.java.be.henallux.project;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.WorkFlow;
import main.java.be.henallux.project.model.WorkFlowType;
import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Status;

public class WorkFlowTest {

    private Status statusTodo;
    private Status statusInProgress;
    private WorkFlowType typeBuy;
    private WorkFlowType typeSell;
    private WorkFlow workFlow;

    @BeforeEach
    public void setUp() throws DataValidationException {
        statusTodo       = new Status("TODO");
        statusInProgress = new Status("IN_PROGRESS");
        typeBuy          = new WorkFlowType("Buy",  true,  false, false);
        typeSell         = new WorkFlowType("Sell", false, true,  false);
        workFlow         = new WorkFlow(0, statusTodo, typeBuy);
    }

    // =========================================================
    // 1. Basic creation
    // =========================================================

    @Test
    public void basicCreationTest() throws DataValidationException {
        assertEquals(0,          workFlow.getId(),           "id should be 0");
        assertEquals(statusTodo, workFlow.getStatus(),       "status should be TODO");
        assertEquals(typeBuy,    workFlow.getWorkflowType(), "type should be Buy");
    }

    @Test
    public void basicCreationTestWithDifferentType() throws DataValidationException {
        WorkFlow wf = new WorkFlow(1, statusTodo, typeSell);
        assertEquals(1,          wf.getId());
        assertEquals(statusTodo, wf.getStatus());
        assertEquals(typeSell,   wf.getWorkflowType());
    }

    // =========================================================
    // 2. equals / hashCode
    // =========================================================

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        WorkFlow wf1 = new WorkFlow(0, statusTodo, typeBuy);
        WorkFlow wf2 = new WorkFlow(0, statusTodo, typeBuy);
        assertEquals(wf1, wf2, "Two identical WorkFlows should be equal");
    }

    @Test
    public void comparisonNotEqualDifferentId() throws DataValidationException {
        WorkFlow wf1 = new WorkFlow(0, statusTodo, typeBuy);
        WorkFlow wf2 = new WorkFlow(1, statusTodo, typeBuy);
        assertNotEquals(wf1, wf2, "WorkFlows with different ids should not be equal");
    }

    @Test
    public void comparisonNotEqualDifferentStatus() throws DataValidationException {
        WorkFlow wf1 = new WorkFlow(0, statusTodo,       typeBuy);
        WorkFlow wf2 = new WorkFlow(0, statusInProgress, typeBuy);
        assertNotEquals(wf1, wf2, "WorkFlows with different statuses should not be equal");
    }

    @Test
    public void comparisonNotEqualDifferentType() throws DataValidationException {
        WorkFlow wf1 = new WorkFlow(0, statusTodo, typeBuy);
        WorkFlow wf2 = new WorkFlow(0, statusTodo, typeSell);
        assertNotEquals(wf1, wf2, "WorkFlows with different types should not be equal");
    }

    // =========================================================
    // 3. toString
    // =========================================================

    @Test
    public void toStringTest() {
        String result = workFlow.toString();
        assertTrue(result.contains("id=0"), "toString should contain id=0");
        assertTrue(result.contains("TODO"), "toString should contain the status");
        assertTrue(result.contains("Buy"),  "toString should contain the type");
    }

    // =========================================================
    // 4. Validations — id
    // =========================================================

    @Test
    public void negativeIdThrows() {
        assertThrows(DataValidationException.class, () ->
            new WorkFlow(-1, statusTodo, typeBuy)
        );
    }

    // =========================================================
    // 5. Validations — status
    // =========================================================

    @Test
    public void nullStatusThrows() {
        assertThrows(DataValidationException.class, () ->
            new WorkFlow(0, null, typeBuy)
        );
    }

    // =========================================================
    // 6. Validations — workflowType
    // =========================================================

    @Test
    public void nullTypeThrows() {
        assertThrows(DataValidationException.class, () ->
            new WorkFlow(0, statusTodo, null)
        );
    }
}