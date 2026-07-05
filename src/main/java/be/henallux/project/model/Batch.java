package be.henallux.project.model;

import java.time.LocalDate;

import be.henallux.project.model.exception.DataValidationException;

public class Batch implements Model {
    private int id;
    private LocalDate expirationDate;
    private String originCountry;

    private Product product;

    public Batch(int id, LocalDate expirationDate, String originCountry, Product product) throws DataValidationException {
        setId(id);
        setProduct(product);
        setOriginCountry(originCountry);
        setExpirationDate(expirationDate);
    }

    public int getId() {
        return id;
    }

    private void setId(int id) throws DataValidationException {
        if (id < 0)
            throw new DataValidationException("ID's cannot be negative");
        this.id = id;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    private void setExpirationDate(LocalDate expirationDate) throws DataValidationException {
        if (expirationDate == null && product.getIsEdible())
            throw new DataValidationException("Expiration date cannot be null if the product is edible");
        this.expirationDate = expirationDate;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    private void setOriginCountry(String originCountry) throws DataValidationException {
        if (originCountry == null || originCountry.isEmpty())
            throw new DataValidationException("Origin country cannot be null or empty");
        this.originCountry = originCountry;
    }

    public Product getProduct() {
        return product;
    }

    private void setProduct(Product product) throws DataValidationException {
        if (product == null)
            throw new DataValidationException("Product cannot be null");
        this.product = product;
    }

    public String getLabel() {
        return String.format("%s", product.toString());
    }

    @Override
    public String toString() {
        return String.format("Batch{id=%d, expirationDate=%s, originCountry=%s, product=%s",
                    id,
                    expirationDate.toString(),
                    originCountry,
                    product.toString()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Batch other = (Batch) obj;
        return  id == other.getId() && 
                product.equals(other.getProduct());
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(id);
        return 31 * result + product.hashCode();
    }
}
