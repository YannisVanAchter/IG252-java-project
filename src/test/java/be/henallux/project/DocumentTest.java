package test.java.be.henallux.project;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Document;
import main.java.be.henallux.project.model.DocumentType;
import main.java.be.henallux.project.model.Status;
import main.java.be.henallux.project.model.WorkFlow;
import main.java.be.henallux.project.model.WorkFlowType;

public class DocumentTest {

    private static final LocalDate DATE_CREATION  = LocalDate.of(2024, 1, 1);
    private static final LocalDate DATE_PLANNED   = LocalDate.of(2024, 2, 1);
    private static final LocalDate DATE_ACTUAL    = LocalDate.of(2024, 2, 5);
    private static final LocalDate DATE_RECEIPT_P = LocalDate.of(2024, 2, 10);
    private static final LocalDate DATE_RECEIPT_A = LocalDate.of(2024, 2, 12);
    private static final int PAYMENT_DELAY        = 30;

    private DocumentType   typeDelivery;
    private DocumentType   typeCommand;
    private DocumentType   typeOther;
    private WorkFlow       workflow;
    private ClientSupplier clientSupplier;
    private Address        address;

    @BeforeEach
    public void setUp() throws DataValidationException {
        typeDelivery   = new DocumentType("Delivery");
        typeCommand    = new DocumentType("Command");
        typeOther      = new DocumentType("Invoice");

        Status status    = new Status("TODO");
        WorkFlowType wft = new WorkFlowType("Buy", true, false, false);
        workflow         = new WorkFlow(0, status, wft);

        address        = new Address("10 Rue de la Paix", "Paris", "France");

        clientSupplier = new ClientSupplier(
            0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
            address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 1)
        );
    }

    private Document buildValid() throws DataValidationException {
        return new Document(
            0, DATE_CREATION, typeDelivery, false,
            DATE_PLANNED, DATE_ACTUAL, DATE_RECEIPT_P, DATE_RECEIPT_A,
            PAYMENT_DELAY, workflow, clientSupplier, address, "Test comment"
        );
    }

    private Document buildMinimal() throws DataValidationException {
        return new Document(
            0, DATE_CREATION, typeOther, false,
            null, null, null, null,
            0, null, null, null, null
        );
    }

    @Test
    public void basicCreationTest() throws DataValidationException {
        Document doc = buildValid();
        assertEquals(0,              doc.getId());
        assertEquals(DATE_CREATION,  doc.getDateOfCreation());
        assertEquals(typeDelivery,   doc.getDocumentType());
        assertFalse(doc.getIsChecked());
        assertEquals(DATE_PLANNED,   doc.getPlannedSendDate());
        assertEquals(DATE_ACTUAL,    doc.getActualSendDate());
        assertEquals(DATE_RECEIPT_P, doc.getPlannedDateOfReceipt());
        assertEquals(DATE_RECEIPT_A, doc.getActualDateOfReceipt());
        assertEquals(PAYMENT_DELAY,  doc.getPaymentDelay());
        assertEquals(workflow,       doc.getWorkflow());
        assertEquals(clientSupplier, doc.getClientSupplier());
        assertEquals(address,        doc.getAddress());
        assertEquals("Test comment", doc.getComment());
    }

    @Test
    public void isCheckedTrueTest() throws DataValidationException {
        Document doc = new Document(
            1, DATE_CREATION, typeOther, true,
            null, null, null, null,
            0, null, null, null, null
        );
        assertTrue(doc.getIsChecked());
    }

    @Test
    public void nullDateOfCreationDefaultsToToday() throws DataValidationException {
        Document doc = new Document(
            0, null, typeOther, false,
            null, null, null, null,
            0, null, null, null, null
        );
        assertEquals(LocalDate.now(), doc.getDateOfCreation(),
            "A null creation date should default to today");
    }

    @Test
    public void nullDocumentTypeDefaultsToUnknown() throws DataValidationException {
        Document doc = new Document(
            0, DATE_CREATION, null, false,
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
            new Document(-1, DATE_CREATION, typeOther, false,
                null, null, null, null,
                0, null, null, null, null)
        );
    }

    @Test
    public void zeroIdIsValid() throws DataValidationException {
        Document doc = new Document(
            0, DATE_CREATION, typeOther, false,
            null, null, null, null,
            0, null, null, null, null
        );
        assertEquals(0, doc.getId());
    }

    @Test
    public void nullPlannedSendDateForDeliveryThrows() {
        assertThrows(DataValidationException.class, () ->
            new Document(0, DATE_CREATION, typeDelivery, false,
                null, null, null, null,
                0, null, null, null, null)
        );
    }

    @Test
    public void nullPlannedSendDateForCommandThrows() {
        assertThrows(DataValidationException.class, () ->
            new Document(0, DATE_CREATION, typeCommand, false,
                null, null, null, null,
                0, null, null, null, null)
        );
    }

    @Test
    public void nullPlannedSendDateForOtherTypeOk() throws DataValidationException {
        Document doc = new Document(
            0, DATE_CREATION, typeOther, false,
            null, null, null, null,
            0, null, null, null, null
        );
        assertNull(doc.getPlannedSendDate());
    }

    @Test
    public void negativePaymentDelayThrows() {
        assertThrows(DataValidationException.class, () ->
            new Document(0, DATE_CREATION, typeOther, false,
                null, null, null, null,
                -1, null, null, null, null)
        );
    }

    @Test
    public void zeroPaymentDelayIsValid() throws DataValidationException {
        Document doc = new Document(
            0, DATE_CREATION, typeOther, false,
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
            99, DATE_CREATION, typeDelivery, false,
            DATE_PLANNED, DATE_ACTUAL, DATE_RECEIPT_P, DATE_RECEIPT_A,
            PAYMENT_DELAY, workflow, clientSupplier, address, "Test comment"
        );
        assertNotEquals(doc1, doc2, "Documents with different ids should not be equal");
    }

    @Test
    public void comparisonNotEqualDifferentType() throws DataValidationException {
        Document doc1 = buildValid();
        Document doc2 = new Document(
            0, DATE_CREATION, typeCommand, false,
            DATE_PLANNED, DATE_ACTUAL, DATE_RECEIPT_P, DATE_RECEIPT_A,
            PAYMENT_DELAY, workflow, clientSupplier, address, "Test comment"
        );
        assertNotEquals(doc1, doc2, "Documents with different types should not be equal");
    }
}