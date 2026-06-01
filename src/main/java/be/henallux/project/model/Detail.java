package main.java.be.henallux.project.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.java.be.henallux.project.model.exception.DataValidationException;

public class Detail implements Model {
    private int id;
    private double priceEVAT;
    private BigDecimal vat;
    private int fidelityPointEarned;
    private int quantity;

    private DocumentDetails doc;
    private Product product;
    private List<Batch> batches;

    public Detail(int id, double priceEVAT, BigDecimal vat, int fidelityPointEarned, int quantity, DocumentDetails doc, Product product, List<Batch> batches) throws DataValidationException {
        setId(id);
        setPriceEVAT(priceEVAT);
        setVat(vat);
        setFidelityPointEarned(fidelityPointEarned);
        setQuantity(quantity);

        setDoc(doc);
        setProduct(product);
        setBatches(batches);
    }

    public Detail(int id, double priceEVAT, BigDecimal vat, int fidelityPointEarned, int quantity, Document doc, Product product, List<Batch> batches) throws DataValidationException {
        this(id, priceEVAT, vat, fidelityPointEarned, quantity, new DocumentDetails(doc), product, batches);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) throws DataValidationException {
        if (id < 0)
            throw new DataValidationException("ID's cannot be negative");
        this.id = id;
    }

    public double getPriceEVAT() {
        return priceEVAT;
    }

    public void setPriceEVAT(double priceEVAT) throws DataValidationException {
        if (priceEVAT < 0)
            throw new DataValidationException("Price cannot be negative");
        this.priceEVAT = priceEVAT;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) throws DataValidationException {
        BigDecimal min = new BigDecimal("0.00");
        BigDecimal max = new BigDecimal("1.00");
        if (vat == null || min.compareTo(vat) > 0 || max.compareTo(vat) < 0)
            throw new DataValidationException("vat cannot be under zero or over 1");
        this.vat = vat;
    }

    public int getFidelityPointEarned() {
        return fidelityPointEarned;
    }

    public void setFidelityPointEarned(int fidelityPointEarned) throws DataValidationException {
        if (fidelityPointEarned < 0)
            throw new DataValidationException("Fidelity point cannot be negative");
        this.fidelityPointEarned = fidelityPointEarned;
    }

    public int getQuantity() {
        return quantity;
    }

    private void setQuantity(int quantity) throws DataValidationException {
        if (quantity <= 0)
            throw new DataValidationException("Quantity cannot be negative");
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    private void setProduct(Product product) throws DataValidationException {
        if (product == null)
            throw new DataValidationException("Product cannot be null");
        this.product = product;
    }

    public Document getDocument() {
        return doc.getDocument();
    }

    private void setDoc(DocumentDetails doc) throws DataValidationException {
        if (doc == null)
            throw new DataValidationException("Document cannot be null");
        this.doc = doc;
    }

    public List<Batch> getBatches() {
        return Collections.unmodifiableList(batches);
    }

    private void setBatches(List<Batch> batches) {
        if (batches == null)
            this.batches = new ArrayList<>();
        else
            this.batches = batches;
    }

    public void addBatch(Batch batch) {
        if ( !batches.contains(batch))
            batches.add(batch);
    }

    @Override
    public String getLabel() {
        return "Product #" + id + " - " + quantity + " pcs - "
                + String.format("%.2f", priceEVAT) + "€ EVAT";
    }

    @Override
    public String toString() {
        return String.format("Detail{id=%d, priceEVAT=%d, vat=%d, fidelityPointEarned=%d, quantity=%d, doc=%s, product=%s, batches=%s",
            id,
            priceEVAT,
            vat,
            fidelityPointEarned,
            quantity,
            doc.getDocument().toString(),
            product.toString(),
            batches.toString()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Detail other = (Detail) obj;
        return  id == other.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
