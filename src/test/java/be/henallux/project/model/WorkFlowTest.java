package be.henallux.project.model;

import static org.junit.jupiter.api.Assertions.*;

import main.java.be.henallux.project.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.exception.DataValidationException;

import java.time.LocalDate;

public class WorkFlowTest {

    private Status statusTodo;
    private Status statusInProgress;
    private WorkFlowType typeBuy;
    private WorkFlowType typeSell;
    private WorkFlow workFlow;
    private ClientSupplier clientSupplier;

    @BeforeEach
    public void setUp() throws DataValidationException {
        String streetName = "Rue de la Loi";
        int streetNumber = 16;
        Locality locality = new Locality("marlon",7500);
        Address address = new Address(123, streetName, streetNumber, locality);
        clientSupplier = new ClientSupplier(
                0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
        );

        statusTodo       = new Status("TODO");
        statusInProgress = new Status("IN_PROGRESS");
        typeSell         = new WorkFlowType(456, "Sell", false, true, false);
        typeBuy = new WorkFlowType(123, "Buy", true, false, false);
        workFlow = new WorkFlow(123, statusTodo, typeSell, clientSupplier, (WorkflowDocuments) null);
    }


    @Test
    public void basicCreationTest() throws DataValidationException {
        assertEquals(0,          workFlow.getId(),           "id should be 0");
        assertEquals(statusTodo, workFlow.getStatus(),       "status should be TODO");
        assertEquals(typeBuy,    workFlow.getWorkflowType(), "type should be Buy");
    }


    @Test
    public void comparisonEqualTest() throws DataValidationException {
        setUp();
        WorkFlow wf1 = new WorkFlow(123, statusTodo, typeSell, clientSupplier, (WorkflowDocuments) null);
        WorkFlow wf2 = new WorkFlow(123, statusTodo, typeSell, clientSupplier, (WorkflowDocuments) null);
        assertEquals(wf1, wf2, "Two identical WorkFlows should be equal");
    }

    @Test
    public void comparisonNotEqualDifferentId() throws DataValidationException {
        WorkFlow wf1 = new WorkFlow(123, statusTodo, typeSell, clientSupplier, (WorkflowDocuments) null);
        WorkFlow wf2 = new WorkFlow(123, statusTodo, typeSell, clientSupplier, (WorkflowDocuments) null);
        assertNotEquals(wf1, wf2, "WorkFlows with different ids should not be equal");
    }
}