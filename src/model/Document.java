package model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import exception.DataValidationException;

/**
 * This class represents a document, which can be of various types (e.g., delivery, command, etc.).
 * It contains certains compulsory fields like id, date of creation, document type and is checked. 
 * Regarding the optional fields, they become compulsory depending on the document type.
 */
public class Document {
    // TODO: discus how do we plan to name the differents document types, and if we want to use an enum for that
    private static final List<DocumentType> TYPES_REQUIRING_PLANNED_SEND_DATE =
        Arrays.asList(new DocumentType("Delivery"), new DocumentType("Command"));

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

    public Document(int id, LocalDate dateOfCreation, DocumentType documentType, boolean isChecked, LocalDate plannedSendDate, LocalDate actualSendDate, LocalDate plannedDateOfReceipt, LocalDate actualDateOfReceipt, Integer paymentDelay, WorkFlow workflow, ClientSupplier clientSupplier, Address address, String comment) throws DataValidationException {
        setId(id);
        setDateOfCreation(dateOfCreation);
        setDocumentType(documentType);
        setIsChecked(isChecked);
        setPlannedSendDate(plannedSendDate);
        setActualSendDate(actualSendDate);
        setPlannedDateOfReceipt(plannedDateOfReceipt);
        setActualDateOfReceipt(actualDateOfReceipt);
        setPaymentDelay(paymentDelay);
        setWorkflow(workflow);
        setClientSupplier(clientSupplier);
        setAddress(address);
        setComment(comment);
    }

    public int getId() { return id; }

    private void setId(int id) throws DataValidationException {
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
            this.documentType = new DocumentType("Unknown");
            return;
        }
        this.documentType = documentType;
    }

    public boolean getIsChecked() { return isChecked; }

    private void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public LocalDate getPlannedSendDate() { return plannedSendDate; }

    private void setPlannedSendDate(LocalDate plannedSendDate) throws DataValidationException {
        if (TYPES_REQUIRING_PLANNED_SEND_DATE.contains(getDocumentType()) && plannedSendDate == null) {
            throw new DataValidationException("Planned send date cannot be null for Delivery and Command document types.");
        }
        this.plannedSendDate = plannedSendDate;
    }

    public LocalDate getActualSendDate() { return actualSendDate; }

    private void setActualSendDate(LocalDate actualSendDate) {
        this.actualSendDate = actualSendDate;
    }

    public LocalDate getPlannedDateOfReceipt() { return plannedDateOfReceipt; }

    private void setPlannedDateOfReceipt(LocalDate plannedDateOfReceipt) {
        this.plannedDateOfReceipt = plannedDateOfReceipt;
    }

    public LocalDate getActualDateOfReceipt() { return actualDateOfReceipt; }

    private void setActualDateOfReceipt(LocalDate actualDateOfReceipt) {
        this.actualDateOfReceipt = actualDateOfReceipt;
    }

    public Integer getPaymentDelay() { return paymentDelay; }

    private void setPaymentDelay(Integer paymentDelay) throws DataValidationException {
        if (paymentDelay != null && paymentDelay < 0) {
            String message = "Payment delay setting error, payment delay is lower than 0 (zero) when it shouldn't (current value: " + paymentDelay + ")";
            throw new DataValidationException(message);
        }
        this.paymentDelay = paymentDelay;
    }

    public WorkFlow getWorkflow() { return workflow; }

    private void setWorkflow(WorkFlow workflow) {
        this.workflow = workflow;
    }

    public ClientSupplier getClientSupplier() { return clientSupplier; }

    private void setClientSupplier(ClientSupplier clientSupplier) {
        this.clientSupplier = clientSupplier;
    }

    public Address getAddress() { return address; }

    private void setAddress(Address address) {
        this.address = address;
    }

    public String getComment() { return comment; }

    private void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Document{id=" + id + ", dateOfCreation=" + dateOfCreation + ", documentType=" + documentType + ", isChecked=" + isChecked + 
                ", plannedSendDate=" + plannedSendDate + ", actualSendDate=" + actualSendDate + ", plannedDateOfReceipt=" + plannedDateOfReceipt + 
                ", actualDateOfReceipt=" + actualDateOfReceipt + ", paymentDelay=" + paymentDelay + ", workflow=" + workflow + 
                ", clientSupplier=" + clientSupplier + ", address=" + address + ", comment='" + comment + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Document other = (Document) obj;
        return  id == other.getId() && dateOfCreation.equals(other.getDateOfCreation()) && 
                documentType.equals(other.getDocumentType()) && isChecked == other.getIsChecked() && plannedSendDate.equals(other.getPlannedSendDate()) && 
                actualSendDate.equals(other.getActualSendDate()) && plannedDateOfReceipt.equals(other.getPlannedDateOfReceipt()) && 
                actualDateOfReceipt.equals(other.getActualDateOfReceipt()) && paymentDelay.equals(other.getPaymentDelay()) && 
                workflow.equals(other.getWorkflow()) && clientSupplier.equals(other.getClientSupplier()) && 
                address.equals(other.getAddress()) && comment.equals(other.getComment());
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(id);
        result = 31 * result + dateOfCreation.hashCode();
        result = 31 * result + documentType.hashCode();
        result = 31 * result + Boolean.hashCode(isChecked);
        result = 31 * result + plannedSendDate.hashCode();
        result = 31 * result + actualSendDate.hashCode();
        result = 31 * result + plannedDateOfReceipt.hashCode();
        result = 31 * result + actualDateOfReceipt.hashCode();
        result = 31 * result + paymentDelay.hashCode();
        result = 31 * result + workflow.hashCode();
        result = 31 * result + clientSupplier.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + comment.hashCode();
        return result;
    }
}
