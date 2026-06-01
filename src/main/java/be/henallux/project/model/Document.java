package main.java.be.henallux.project.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import main.java.be.henallux.project.model.exception.DataValidationException;

/**
 * This class represents a document, which can be of various types (e.g., delivery, command, etc.).
 * It contains certains compulsory fields like id, date of creation, document type and is checked. 
 * Regarding the optional fields, they become compulsory depending on the document type.
 */
public class Document implements Model {
    public static final List<DocumentType> TYPES_REQUIRING_PLANNED_SEND_DATE =
            Arrays.asList(new DocumentType(1, "Purchase Order"));
    public static final List<DocumentType> TYPES_REQUIRING_RECEPTION_DATE =
            Arrays.asList(new DocumentType(2, "Delivery Note"));
    public static final List<DocumentType> TYPES_REQUIRING_PAYMENT_DELAY =
            Arrays.asList(new DocumentType(3, "Invoice"));
    public static final List<DocumentType> TYPES_REQUIRING_COMMENTARY =
            Arrays.asList(new DocumentType(5, "Preparation Order"));
    public static final List<DocumentType> TYPES_REQUIRING_ADDRESS =
            Arrays.asList(new DocumentType(1, "Purchase Order"));
    public static final List<DocumentType> TYPES_REQUIRING_RECIPE_ORDER =
            Arrays.asList(new DocumentType(5, "Preparation Order"));

    private int id;
    private LocalDate dateOfCreation;
    private DocumentType documentType;
    private boolean isChecked;
    private DocumentDetails details;
    private LocalDate plannedSendDate;
    private LocalDate actualSendDate;
    private LocalDate plannedDateOfReceipt;
    private LocalDate actualDateOfReceipt;
    private Integer paymentDelay = null;
    private WorkFlow workflow;
    private Address address;
    private String comment;
    private Recipe recipeOrder;

    /**
     * Document constructor
     * @param id 
     * @param dateOfCreation
     * @param documentType
     * @param isChecked inform if a document is 
     * @param plannedSendDate
     * @param plannedDateOfReceipt
     * @param actualSendDate
     * @param actualDateOfReceipt
     * @param paymentDelay
     * @param workflow
     * @param address used in delivery notice
     * @param comment used in preparation orders
     * @param recipeOrder used in preparation Order
     * @throws DataValidationException
     */
    public Document(int id, LocalDate dateOfCreation, DocumentType documentType, DocumentDetails details, boolean isChecked, LocalDate plannedSendDate, LocalDate plannedDateOfReceipt, LocalDate actualSendDate, LocalDate actualDateOfReceipt, Integer paymentDelay, WorkFlow workflow, Address address, String comment, Recipe recipeOrder) throws DataValidationException {
        setId(id);
        setDateOfCreation(dateOfCreation);
        setDocumentType(documentType);
        setIsChecked(isChecked);
        setDetails(details);
        setWorkflow(workflow);

        setPlannedSendDate(plannedSendDate);
        setPlannedDateOfReceipt(plannedDateOfReceipt);
        if (actualSendDate != null)
            setActualSendDate(actualSendDate);
        if (actualDateOfReceipt != null)
            setActualDateOfReceipt(actualDateOfReceipt);
        setPaymentDelay(paymentDelay);
        setAddress(address);
        setComment(comment);
        setRecipeOrder(recipeOrder);
    }
    public Document(LocalDate dateOfCreation, DocumentType documentType, boolean isChecked, LocalDate plannedSendDate, LocalDate plannedDateOfReceipt, LocalDate actualSendDate, LocalDate actualDateOfReceipt, Integer paymentDelay, WorkFlow workflow, Address address, String comment)
            throws DataValidationException {
        this(0, dateOfCreation, documentType, null, isChecked, plannedSendDate, plannedDateOfReceipt, actualSendDate, actualDateOfReceipt, paymentDelay, workflow, address, comment, null);
    }

    /**
     * Delivery document constructor without effective dates
     */
    public Document(int id, LocalDate dateOfCreation, DocumentType documentType, DocumentDetails details, boolean isChecked, LocalDate plannedSendDate, LocalDate plannedDateOfReceipt, Integer paymentDelay, WorkFlow workflow) throws DataValidationException {
        this(
            id,
            dateOfCreation,
            documentType,
            details,
            isChecked,
            plannedSendDate,
            plannedDateOfReceipt,
            (LocalDate) null,
            (LocalDate) null,
            paymentDelay,
            workflow,
            (Address) null,
            (String) null,
            (Recipe) null
        );
    }

    /**
     * Delivery document constructor with effective send dates
     */
    public Document(int id, LocalDate dateOfCreation, DocumentType documentType, DocumentDetails details, boolean isChecked, LocalDate plannedSendDate, LocalDate plannedDateOfReceipt, LocalDate effectiveSendDate, Integer paymentDelay, WorkFlow workflow) throws DataValidationException {
        this(
            id,
            dateOfCreation,
            documentType,
            details,
            isChecked,
            plannedSendDate,
            plannedDateOfReceipt,
            effectiveSendDate,
            (LocalDate) null,
            paymentDelay,
            workflow,
            (Address) null,
            (String) null,
            (Recipe) null
        );
    }

    /**
     * Delivery document constructor with effective dates
     */
    public Document(int id, LocalDate dateOfCreation, DocumentType documentType, DocumentDetails details, boolean isChecked, LocalDate plannedSendDate, LocalDate plannedDateOfReceipt, LocalDate effectiveSendDate, LocalDate effectiveDateOfRecipe, Integer paymentDelay, WorkFlow workflow) throws DataValidationException {
        this(
            id,
            dateOfCreation,
            documentType,
            details,
            isChecked,
            plannedSendDate,
            plannedDateOfReceipt,
            effectiveSendDate,
            effectiveDateOfRecipe,
            paymentDelay,
            workflow,
            (Address) null,
            (String) null,
            (Recipe) null
        );
    }

    /**
     * Command document constructor without effective dates
     */
    public Document(int id, LocalDate dateOfCreation, DocumentType documentType, DocumentDetails details, boolean isChecked, Integer paymentDelay, WorkFlow workflow, Address address) throws DataValidationException {
        this(
            id, 
            dateOfCreation, 
            documentType, 
            details,
            isChecked, 
            (LocalDate) null, 
            (LocalDate) null, 
            (LocalDate) null, 
            (LocalDate) null, 
            paymentDelay,
            workflow,
            address,
            (String) null,
            (Recipe) null
        );
    }

    /**
     * Preparation order document constructor without effective dates
     */
    public Document(int id, LocalDate dateOfCreation, DocumentType documentType, DocumentDetails details, boolean isChecked, LocalDate plannedSendDate, WorkFlow workflow, String commentary, Recipe recipeOrder) throws DataValidationException {
        this(
            id,
            dateOfCreation,
            documentType,
            details,
            isChecked,
            plannedSendDate,
            (LocalDate) null,
            (LocalDate) null,
            (LocalDate) null,
            (Integer) null,
            workflow,
            (Address) null,
            commentary,
            recipeOrder
        );
    }

    public int getId() { return id; }

    public void setId(int id) throws DataValidationException {
        if (id < 0) {
            String message = "ID setting error, ID is lower or equal to 0 (zero) when it shouldn't (current value: " + id + ")";
            throw new DataValidationException(message);
        }
        this.id = id;
    }

    public LocalDate getDateOfCreation() { return dateOfCreation; }

    private void setDateOfCreation(LocalDate dateOfCreation) {
        if (dateOfCreation == null)
            this.dateOfCreation = LocalDate.now();
        else 
            this.dateOfCreation = dateOfCreation;
    }

    public DocumentType getDocumentType() { return documentType; }

    private void setDocumentType(DocumentType documentType) {
        if (documentType == null) {
            this.documentType = new DocumentType(-1,"Unknown");
            return;
        }
        this.documentType = documentType;
    }

    public boolean getIsChecked() { return isChecked; }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public List<Detail> getDetails() {
        return details.getDetails();
    }

    private void setDetails(DocumentDetails details) {
        if (details == null)
            this.details = new DocumentDetails(this);
        else
            this.details = details;
    }

    public void addDetail(Detail detail) {
        this.details.addDetail(detail);
    }

    public LocalDate getPlannedSendDate() { return plannedSendDate; }

    private void setPlannedSendDate(LocalDate plannedSendDate) throws DataValidationException {
        if (TYPES_REQUIRING_PLANNED_SEND_DATE.contains(getDocumentType()) && plannedSendDate == null) {
            throw new DataValidationException(
                String.format(  "Planned send date cannot be null for %s document types.",
                                        TYPES_REQUIRING_PLANNED_SEND_DATE.toString())
            );
        }
        this.plannedSendDate = plannedSendDate;
    }

    public LocalDate getActualSendDate() { return actualSendDate; }

    public final void setActualSendDate(LocalDate actualSendDate) throws DataValidationException {
        if (TYPES_REQUIRING_PLANNED_SEND_DATE.contains(getDocumentType()) && actualSendDate == null) {
            throw new DataValidationException(
                String.format(  "Send date cannot be null for %s document types.",
                                        TYPES_REQUIRING_PLANNED_SEND_DATE.toString())
            );
        }
        this.actualSendDate = actualSendDate;
    }

    public LocalDate getPlannedDateOfReceipt() { return plannedDateOfReceipt; }

    private void setPlannedDateOfReceipt(LocalDate plannedDateOfReceipt) throws DataValidationException {
        if (TYPES_REQUIRING_RECEPTION_DATE.contains(getDocumentType()) && plannedDateOfReceipt == null) {
            throw new DataValidationException(
                String.format(  "Planned reception date cannot be null for %s document types.",
                                        TYPES_REQUIRING_RECEPTION_DATE.toString())
            );
        }
        this.plannedDateOfReceipt = plannedDateOfReceipt;
    }

    public LocalDate getActualDateOfReceipt() { return actualDateOfReceipt; }

    public final void setActualDateOfReceipt(LocalDate actualDateOfReceipt) throws DataValidationException {
        if (TYPES_REQUIRING_RECEPTION_DATE.contains(getDocumentType()) && plannedDateOfReceipt == null) {
            throw new DataValidationException(
                String.format(  "Planned reception date cannot be null for %s document types.",
                                        TYPES_REQUIRING_RECEPTION_DATE.toString())
            );
        }
        this.actualDateOfReceipt = actualDateOfReceipt;
    }

    public Integer getPaymentDelay() { return paymentDelay; }

    private void setPaymentDelay(Integer paymentDelay) throws DataValidationException {

        if (TYPES_REQUIRING_PAYMENT_DELAY.contains(getDocumentType())
                && paymentDelay == null) {

            throw new DataValidationException(
                    String.format("Payment delay cannot be null for %s document types.",
                            TYPES_REQUIRING_PAYMENT_DELAY.toString())
            );
        }

        if (paymentDelay != null && paymentDelay < 0) {
            throw new DataValidationException(
                    "Payment delay setting error, payment delay is lower than 0"
            );
        }

        this.paymentDelay = paymentDelay;
    }

    public WorkFlow getWorkflow() { return workflow; }

    public void setWorkflow(WorkFlow workflow) {
        this.workflow = workflow;
    }

    public Address getAddress() { return address; }

    private void setAddress(Address address) throws DataValidationException {
        if (TYPES_REQUIRING_ADDRESS.contains(getDocumentType()) && address == null) {
            throw new DataValidationException(
                    String.format("Address cannot be null for %s document types.",
                            TYPES_REQUIRING_ADDRESS.toString())
            );
        }
        this.address = address;
    }

    public String getComment() { return comment; }

    private void setComment(String comment) throws DataValidationException {

        if (TYPES_REQUIRING_COMMENTARY.contains(getDocumentType())
                && (comment == null || comment.isBlank())) {

            throw new DataValidationException(
                    String.format("Commentary cannot be null for %s document types.",
                            TYPES_REQUIRING_COMMENTARY.toString())
            );
        }

        this.comment = comment;
    }

    public Recipe getRecipeOrder() {
        return recipeOrder;
    }

    public void setRecipeOrder(Recipe recipeOrder) throws DataValidationException {
        if (TYPES_REQUIRING_RECIPE_ORDER.contains(getDocumentType())) {
            throw new DataValidationException(
                String.format(  "Recipe cannot be null for %s document types.",
                                        TYPES_REQUIRING_RECIPE_ORDER.toString())
            );
        }
        this.recipeOrder = recipeOrder;
    }

    public String getLabel() {
        return String.format("%s - %s #%d", workflow.getLabel(), documentType.getLabel(), id);
    }

    @Override
    public String toString() {
        return "Document{id=" + id + ", dateOfCreation=" + dateOfCreation + ", documentType=" + documentType + ", isChecked=" + isChecked + 
                ", plannedSendDate=" + plannedSendDate + ", actualSendDate=" + actualSendDate + ", plannedDateOfReceipt=" + plannedDateOfReceipt + 
                ", actualDateOfReceipt=" + actualDateOfReceipt + ", paymentDelay=" + paymentDelay + ", workflow=" + workflow + 
                ", address=" + address + ", comment='" + comment + ", recipeOrder=" + recipeOrder + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Document other = (Document) obj;
        return  id == other.getId() ;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
