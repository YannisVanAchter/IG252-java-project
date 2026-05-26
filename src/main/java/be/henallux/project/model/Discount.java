package main.java.be.henallux.project.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import main.java.be.henallux.project.model.exception.DataValidationException;

public class Discount implements Model {
    private int requiredQuantity;
    private BigDecimal discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
    private Product product;

    public Discount(int requiredQuantity, BigDecimal discountPercentage, LocalDate startDate, LocalDate endDate, String name, Product product) throws DataValidationException {
        setRequiredQuantity(requiredQuantity);
        setDiscountPercentage(discountPercentage);
        setStartDate(startDate);
        setEndDate(endDate);
        setName(name);
        setProduct(product);
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    private void setRequiredQuantity(int requiredQuantity) throws DataValidationException {
        if (requiredQuantity < 1) {
            String message = "Required quantity setting error, required quantity is lower than 1 when it shouldn't (current value: " + requiredQuantity + ")";
            throw new DataValidationException(message);
        }
        this.requiredQuantity = requiredQuantity;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    private void setDiscountPercentage(BigDecimal discountPercentage) throws DataValidationException {
        if (discountPercentage == null) {
            String message = "Discount percentage setting error, discount percentage is null when it shouldn't";
            throw new DataValidationException(message);
        }
        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || discountPercentage.compareTo(new BigDecimal("100")) > 0) {
            String message = "Discount percentage setting error, discount percentage is outside [0, 100] when it shouldn't (current value: " + discountPercentage + ")";
            throw new DataValidationException(message);
        }
        this.discountPercentage = discountPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    private void setStartDate(LocalDate startDate) throws DataValidationException {
        if (startDate == null) {
            String message = "Start date setting error, start date is null when it shouldn't";
            throw new DataValidationException(message);
        }
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    private void setEndDate(LocalDate endDate) throws DataValidationException {
        if (endDate == null) {
            String message = "End date setting error, end date is null when it shouldn't";
            throw new DataValidationException(message);
        }
        if (getStartDate() != null && endDate.isBefore(getStartDate())) {
            String message = "End date setting error, end date is before start date when it shouldn't (current values: startDate=" + getStartDate() + ", endDate=" + endDate + ")";
            throw new DataValidationException(message);
        }
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) throws DataValidationException {
        if (name == null || name.isEmpty()) {
            String message = "Name setting error, name is null or empty when it shouldn't (current value: " + name + ")";
            throw new DataValidationException(message);
        }
        this.name = name;
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

    public String getLabel() {
        return name + " (" + discountPercentage + "%, min " + requiredQuantity + ")";
    }

    @Override
    public String toString() {
        return "Discount{requiredQuantity=" + requiredQuantity +
                ", discountPercentage=" + discountPercentage +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", name='" + name + '\'' +
                ", product=" + (product != null ? product.toString() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Discount other = (Discount) obj;
        return requiredQuantity == other.getRequiredQuantity() &&
                discountPercentage.compareTo(other.getDiscountPercentage()) == 0 &&
                startDate.equals(other.getStartDate()) &&
                endDate.equals(other.getEndDate());
    }

    public static int hashCode(int quantity, BigDecimal percent, LocalDate start, LocalDate end) {
        int result = Integer.hashCode(quantity);
        result = 31 * result + percent.stripTrailingZeros().hashCode();
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }

    @Override
    public int hashCode() {
        return hashCode(this.requiredQuantity, this.discountPercentage, this.startDate, this.endDate);
    }
}