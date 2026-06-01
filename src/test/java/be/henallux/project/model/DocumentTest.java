package be.henallux.project.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.java.be.henallux.project.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.be.henallux.project.model.exception.DataValidationException;

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
    private Locality locality;
    private String comment;
    private Document document;
    private WorkFlowType workFlowType;
    private Recipe recipeOrder;
    private RecipeComposition recipeComposition;
    private Product product;

    @BeforeEach
    public void setUp() throws DataValidationException {
        try {
            String streetName = "Rue de la Loi";
            int streetNumber = 16;
            locality = new Locality("marlon",7500);
            address = new Address(123, streetName, streetNumber, locality);

            id = 123;
            dateOfCreation = LocalDate.of(2024, 1, 1);
            documentType = new DocumentType("Delivery");
            isChecked = false;
            plannedSendDate = LocalDate.of(2024, 1, 5);
            actualSendDate = LocalDate.of(2024, 1, 6);
            plannedDateOfReceipt = LocalDate.of(2024, 1, 10);
            actualDateOfReceipt = LocalDate.of(2024, 1, 11);
            paymentDelay = 30;
            document = new Document(id, dateOfCreation, documentType, null, isChecked, 10, null, address);
            WorkflowDocuments workflowDocuments = null;
            workFlowType = new WorkFlowType(123, "Purchase", true, false, false);
            ProductCategory fruitsCategory = new ProductCategory(1, "Fruits");
            LocationProduct locationProduct = new LocationProduct("etagere 1", "etage 2", true, false);

            List<QuantityProduct> location = new ArrayList<>();
            product = new Product(
                    1, "Smartphone", new BigDecimal("500"), new BigDecimal("50"),
                    10, true, 5, fruitsCategory, null, null
            );

            recipeOrder = new Recipe(123, "Smartphone", "etape 1", product, null);
            recipeOrder.addProductInComposition(product, 30);
            clientSupplier = new ClientSupplier(
                0, "Dupont", "Jean", "jean.dupont@example.com", "0123456789",
                address, true, true, false, "FR12345678901", LocalDate.of(2020, 1, 15)
            );
            workflow = new WorkFlow(123, new Status("TODO"), workFlowType, clientSupplier, workflowDocuments);
            workflow.addDocument(document);
        } catch (DataValidationException e) {
            fail("Failed to initialize test dependencies");
        }
    }

    private Document buildValid() throws DataValidationException {
        setUp();
        return new Document(
            id, dateOfCreation, documentType, null, isChecked,
                plannedSendDate, plannedDateOfReceipt, actualSendDate, actualDateOfReceipt, paymentDelay, workflow,
                address, "Test comment", null
        );
    }

    private Document buildMinimal() throws DataValidationException {
        setUp();
        return new Document(
                id, dateOfCreation, documentType, null, isChecked,
                null, null, null, null, paymentDelay, workflow,
                address, null, null
        );
    }
}