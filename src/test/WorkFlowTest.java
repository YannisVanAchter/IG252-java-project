package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.DataValidationException;
import model.WorkFlow;
import model.WorkFlowType;
import model.Status;

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

    @Test
    public void basicCreationTest() throws DataValidationException {
        assertEquals(0,           workFlow.getId(),             "id doit être 0");
        assertEquals(statusTodo,  workFlow.getStatus(),         "status doit être TODO");
        assertEquals(typeBuy,     workFlow.getWorkflowType(),   "type doit être Buy");
    }

    @Test
    public void basicCreationTestWithDifferentType() throws DataValidationException {
        WorkFlow wf = new WorkFlow(1, statusTodo, typeSell);
        assertEquals(1,        wf.getId());
        assertEquals(statusTodo, wf.getStatus());
        assertEquals(typeSell, wf.getWorkflowType());
    }

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        WorkFlow wf1 = new WorkFlow(0, statusTodo, typeBuy);
        WorkFlow wf2 = new WorkFlow(0, statusTodo, typeBuy);
        assertEquals(wf1, wf2, "Deux WorkFlow identiques devraient être égaux");
    }

    @Test
    public void comparisonNotEqualDifferentId() throws DataValidationException {
        WorkFlow wf1 = new WorkFlow(0, statusTodo, typeBuy);
        WorkFlow wf2 = new WorkFlow(1, statusTodo, typeBuy);
        assertNotEquals(wf1, wf2, "Des WorkFlow avec des ids différents ne devraient pas être égaux");
    }

    @Test
    public void comparisonNotEqualDifferentStatus() throws DataValidationException {
        WorkFlow wf1 = new WorkFlow(0, statusTodo,       typeBuy);
        WorkFlow wf2 = new WorkFlow(0, statusInProgress, typeBuy);
        assertNotEquals(wf1, wf2, "Des WorkFlow avec des status différents ne devraient pas être égaux");
    }

    @Test
    public void comparisonNotEqualDifferentType() throws DataValidationException {
        WorkFlow wf1 = new WorkFlow(0, statusTodo, typeBuy);
        WorkFlow wf2 = new WorkFlow(0, statusTodo, typeSell);
        assertNotEquals(wf1, wf2, "Des WorkFlow avec des types différents ne devraient pas être égaux");
    }

    @Test
    public void toStringTest() {
        String result = workFlow.toString();
        assertTrue(result.contains("id=0"),       "toString doit contenir id=0");
        assertTrue(result.contains("TODO"),        "toString doit contenir le status");
        assertTrue(result.contains("Buy"),         "toString doit contenir le type");
    }

    @Test
    public void negativeIdThrows() {
        assertThrows(DataValidationException.class, () ->
            new WorkFlow(-1, statusTodo, typeBuy)
        );
    }

    @Test
    public void nullStatusThrows() {
        assertThrows(DataValidationException.class, () ->
            new WorkFlow(0, null, typeBuy)
        );
    }

    @Test
    public void nullTypeThrows() {
        assertThrows(DataValidationException.class, () ->
            new WorkFlow(0, statusTodo, null)
        );
    }
}