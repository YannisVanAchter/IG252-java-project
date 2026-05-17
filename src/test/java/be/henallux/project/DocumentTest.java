package test.java.be.henallux.project;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.exception.DataValidationException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Document;
import main.java.be.henallux.project.model.DocumentType;
import main.java.be.henallux.project.model.Status;
import main.java.be.henallux.project.model.WorkFlow;
import main.java.be.henallux.project.model.WorkFlowType;

public class DocumentTest {

    private int id;
    private LocalDate dateOfCreation;
    private DocumentType documentType;
    private boolean isChecked;
    private LocalDate plannedSendDate;
    private LocalDate actualSendDate;
    private LocalDate plannedDateOfReceipt;
    private LocalDate actualDateOfReceipt;
    private Integer paymentDelay = null;
    private WorkFlow workflow;
    private ClientSupplier clientSupplier;
    private Address address;
    private String comment;

    @BeforeEach
    public void setUp() throws DataValidationException {
        try {
            id = 0;
            dateOfCreation = LocalDate.of(2024, 1, 1);
            documentType = new DocumentType("Delivery");
            isChecked = false;
            plannedSendDate = LocalDate.of(2024, 1, 5);
            actualSendDate = LocalDate.of(2024, 1, 6);
            plannedDateOfReceipt = LocalDate.of(2024, 1, 10);
            actualDateOfReceipt = LocalDate.of(2024, 1, 11);
            paymentDelay = 30;
            workflow = new WorkFlow(0, new Status("TODO"), new WorkFlowType("Delivery", true, false, false));
            clientSupplier = new ClientSupplier(
                0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                new Address("10 Rue de la Paix", "Paris", "France"), true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
            );
            address = new Address(1, "Rue de la Paix", 10, "Paris", 75000);
        } catch (DataValidationException e) {
            fail("Failed to initialize test dependencies");
        }
    }

    private Document buildValid() throws DataValidationException {
        return new Document(
            id, dateOfCreation, documentType, isChecked,
            plannedSendDate, actualSendDate, plannedDateOfReceipt, actualDateOfReceipt,
            paymentDelay, workflow, clientSupplier, address, "Test comment"
        );
    }

    private Document buildMinimal() throws DataValidationException {
        return new Document(
            0, dateOfCreation, documentType, isChecked,
            null, null, null, null,
            paymentDelay, null, null, null, null
        );
    }

    @Test
    public void basicCreationTest() throws DataValidationException {
        Document doc = buildValid();
        assertEquals(0,              doc.getId());
        assertEquals(dateOfCreation,  doc.getDateOfCreation());
        assertEquals(documentType,   doc.getDocumentType());
        assertFalse(doc.getIsChecked());
        assertEquals(plannedSendDate,   doc.getPlannedSendDate());
        assertEquals(actualSendDate,    doc.getActualSendDate());
        assertEquals(plannedDateOfReceipt, doc.getPlannedDateOfReceipt());
        assertEquals(actualDateOfReceipt, doc.getActualDateOfReceipt());
        assertEquals(paymentDelay,  doc.getPaymentDelay());
        assertEquals(workflow,       doc.getWorkflow());
        assertEquals(clientSupplier, doc.getClientSupplier());
        assertEquals(address,        doc.getAddress());
        assertEquals("Test comment", doc.getComment());
    }

    @Test
    public void isCheckedTrueTest() throws DataValidationException {
        Document doc = new Document(
            1, dateOfCreation, documentType, true,
            null, null, null, null,
            0, null, null, null, null
        );
        assertTrue(doc.getIsChecked());
    }

    @Test
    public void nullDateOfCreationDefaultsToToday() throws DataValidationException {
        Document doc = new Document(
            0, null, documentType, false,
            null, null, null, null,
            0, null, null, null, null
        );
        assertEquals(LocalDate.now(), doc.getDateOfCreation(),
            "A null creation date should default to today");
    }

    @Test
    public void nullDocumentTypeDefaultsToUnknown() throws DataValidationException {
        Document doc = new Document(
            0, dateOfCreation, null, false,
            null, null, null, null,
            0, null, null, null, null
        );
        assertEquals("Unknown", doc.getDocumentType().getName(),
            "A null document type should default to 'Unknown'");
    }

    @Test
    public void optionalFieldsCanBeNull() throws DataValidationException {
        Document doc = buildMinimal();
        assertNull(doc.getPlannedSendDate());
        assertNull(doc.getActualSendDate());
        assertNull(doc.getPlannedDateOfReceipt());
        assertNull(doc.getActualDateOfReceipt());
        assertNull(doc.getWorkflow());
        assertNull(doc.getClientSupplier());
        assertNull(doc.getAddress());
        assertNull(doc.getComment());
    }

    @Test
    public void negativeIdThrows() {
        assertThrows(DataValidationException.class, () ->
            new Document(-1, dateOfCreation, documentType, false,
                null, null, null, null,
                paymentDelay, null, null, null, null)
        );
    }

    @Test
    public void zeroIdIsValid() throws DataValidationException {
        Document doc = new Document(
            0, dateOfCreation, documentType, false,
            null, null, null, null,
            paymentDelay, null, null, null, null
        );
        assertEquals(0, doc.getId());
    }

    @Test
    public void nullPlannedSendDateForDeliveryThrows() {
        assertThrows(DataValidationException.class, () ->
            new Document(0, dateOfCreation, documentType, false,
                null, null, null, null,
                paymentDelay, null, null, null, null)
        );
    }

    @Test
    public void nullPlannedSendDateForCommandThrows() {
        assertThrows(DataValidationException.class, () ->
            new Document(0, dateOfCreation, documentType, false,
                null, null, null, null,
                paymentDelay, null, null, null, null)
        );
    }

    @Test
    public void nullPlannedSendDateForOtherTypeOk() throws DataValidationException {
        Document doc = new Document(
            0, dateOfCreation, documentType, false,
            null, null, null, null,
            paymentDelay, null, null, null, null
        );
        assertNull(doc.getPlannedSendDate());
    }

    @Test
    public void negativePaymentDelayThrows() {
        assertThrows(DataValidationException.class, () ->
            new Document(0, dateOfCreation, documentType, false,
                null, null, null, null,
                -1, null, null, null, null)
        );
    }

    @Test
    public void zeroPaymentDelayIsValid() throws DataValidationException {
        Document doc = new Document(
            0, dateOfCreation, documentType, false,
            null, null, null, null,
            0, null, null, null, null
        );
        assertEquals(0, doc.getPaymentDelay());
    }

    @Test
    public void toStringTest() throws DataValidationException {
        Document doc = buildValid();
        String result = doc.toString();
        assertTrue(result.contains("id=0"),            "toString should contain id=0");
        assertTrue(result.contains("isChecked=false"), "toString should contain isChecked=false");
        assertTrue(result.contains("paymentDelay=30"), "toString should contain paymentDelay=30");
        assertTrue(result.contains("Test comment"),    "toString should contain the comment");
    }

    @Test
    public void comparisonEqualTest() throws DataValidationException {
        Document doc1 = buildValid();
        Document doc2 = buildValid();
        assertEquals(doc1, doc2, "Two identical Documents should be equal");
    }

    @Test
    public void comparisonNotEqualDifferentId() throws DataValidationException {
        Document doc1 = buildValid();
        Document doc2 = new Document(
            1, dateOfCreation, documentType, false,
            plannedSendDate, actualSendDate, plannedDateOfReceipt, actualDateOfReceipt,
            paymentDelay, workflow, clientSupplier, address, "Test comment"
        );
        assertNotEquals(doc1, doc2, "Documents with different ids should not be equal");
    }

    @Test
    public void comparisonNotEqualDifferentType() throws DataValidationException {
        Document doc1 = buildValid();
        Document doc2 = new Document(
            0, dateOfCreation, typeCommand, false,
            plannedSendDate, actualSendDate, plannedDateOfReceipt, actualDateOfReceipt,
            paymentDelay, workflow, clientSupplier, address, "Test comment"
        );
        assertNotEquals(doc1, doc2, "Documents with different types should not be equal");
    }
}