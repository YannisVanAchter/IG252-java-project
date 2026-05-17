package main.java.be.henallux.project.model;

import main.java.be.henallux.project.exception.DataValidationException;

public class QuantityProduct {
    private LocationProduct locationProduct;
    private Product product;
    private int quantity;

    public QuantityProduct(LocationProduct locationProduct, Product product, int quantity) throws DataValidationException {
        setLocationProduct(locationProduct);
        setProduct(product);
        setQuantity(quantity);
    }

    public LocationProduct getLocationProduct() {
        return locationProduct;
    }

    private void setLocationProduct(LocationProduct locationProduct) throws DataValidationException {
        if (locationProduct == null) {
            String message = "LocationProduct setting error, locationProduct is null when it shouldn't";
            throw new DataValidationException(message);
        }
        this.locationProduct = locationProduct;
    }

    public Product getProduct() {
        return product;
    }

    private void setProduct(Product product) throws DataValidationException {
        if (product == null) {
            String message = "Product setting error, product is null when it shouldn't";
            throw new DataValidationException(message);
        }
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    private void setQuantity(int quantity) throws DataValidationException {
        if (quantity < 0) {
            String message = "Quantity setting error, quantity is lower than 0 when it shouldn't (current value: " + quantity + ")";
            throw new DataValidationException(message);
        }
        this.quantity = quantity;
    }

    public String getLabel() {
        return product.getLabel() + " at " + locationProduct.getLabel() + " : " + quantity;
    }

    @Override
    public String toString() {
        return "QuantityProduct{locationProduct=" + locationProduct + ", product=" + product + ", quantity=" + quantity + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        QuantityProduct other = (QuantityProduct) obj;
        return  locationProduct.equals(other.getLocationProduct()) &&
                product.equals(other.getProduct());
    }

    @Override
    public int hashCode() {
        int result = locationProduct.hashCode();
        result = 31 * result + product.hashCode();
        return result;
    }
}