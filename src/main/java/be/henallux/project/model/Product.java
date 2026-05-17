package main.java.be.henallux.project.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.java.be.henallux.project.exception.DataValidationException;

public class Product {
    private int id;
    private String name;
    private BigDecimal priceEVAT;
    private BigDecimal vat;
    private int fidelityPoint;
    private boolean isEdible;
    private int minStockQuantity;
    private ProductCategory category;
    private List<QuantityProduct> location;
    private List<Discount> discounts;

    public Product(int id, String name, BigDecimal priceEVAT, BigDecimal vat, int fidelityPoint, boolean isEdible, int minStockQuantity, ProductCategory category, List<QuantityProduct> location, List<Discount> discounts) throws DataValidationException {
        setId(id);
        setName(name);
        setPriceEVAT(priceEVAT);
        setVat(vat);
        setFidelityPoint(fidelityPoint);
        setIsEdible(isEdible);
        setMinStockQuantity(minStockQuantity);
        setCategory(category);
        setLocation(location);
        setDiscounts(discounts);
    }

    public int getId() {
        return id;
    }

    private void setId(int id) throws DataValidationException {
        if (id < 0) {
            String message = "ID setting error, ID is lower than 0 when it shouldn't (current value: " + id + ")";
            throw new DataValidationException(message);
        }
        this.id = id;
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

    public BigDecimal getPriceEVAT() {
        return priceEVAT;
    }

    public void setPriceEVAT(BigDecimal priceEVAT) throws DataValidationException {
        if (priceEVAT == null) {
            String message = "Price EVAT setting error, price EVAT is null when it shouldn't";
            throw new DataValidationException(message);
        }
        if (priceEVAT.compareTo(BigDecimal.ZERO) < 0) {
            String message = "Price EVAT setting error, price EVAT is lower than 0 when it shouldn't (current value: " + priceEVAT + ")";
            throw new DataValidationException(message);
        }
        this.priceEVAT = priceEVAT.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) throws DataValidationException {
        if (vat == null) {
            String message = "VAT setting error, VAT is null when it shouldn't";
            throw new DataValidationException(message);
        }
        if (vat.compareTo(BigDecimal.ZERO) < 0 || vat.compareTo(new BigDecimal("100")) > 0) {
            String message = "VAT setting error, VAT is outside [0, 100] when it shouldn't (current value: " + vat + ")";
            throw new DataValidationException(message);
        }
        this.vat = vat.setScale(2, RoundingMode.HALF_UP);
    }

    public float getPrice() {
        if (priceEVAT == null || vat == null) {
            return 0f;
        }
        BigDecimal taxMultiplier = BigDecimal.ONE.add(vat.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
        BigDecimal price = priceEVAT.multiply(taxMultiplier);
        return price.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    public int getFidelityPoint() {
        return fidelityPoint;
    }

    public void setFidelityPoint(int fidelityPoint) throws DataValidationException {
        if (fidelityPoint < 0) {
            String message = "Fidelity point setting error, fidelity points are lower than 0 when they shouldn't (current value: " + fidelityPoint + ")";
            throw new DataValidationException(message);
        }
        this.fidelityPoint = fidelityPoint;
    }

    public boolean getIsEdible() {
        return isEdible;
    }

    private void setIsEdible(boolean isEdible) {
        this.isEdible = isEdible;
    }

    public int getMinStockQuantity() {
        return minStockQuantity;
    }

    public void setMinStockQuantity(int minStockQuantity) throws DataValidationException {
        if (minStockQuantity < 0) {
            String message = "Min stock quantity setting error, min stock quantity is lower than 0 when it shouldn't (current value: " + minStockQuantity + ")";
            throw new DataValidationException(message);
        }
        this.minStockQuantity = minStockQuantity;
    }

    public ProductCategory getCategory() {
        return category;
    }

    private void setCategory(ProductCategory category) throws DataValidationException {
        if (category == null) {
            String message = "Category setting error, category is null when it shouldn't";
            throw new DataValidationException(message);
        }
        this.category = category;
    }

    public List<Discount> getDiscounts() {
        return Collections.unmodifiableList(discounts);
    }

    private void setDiscounts(List<Discount> discounts) {
        if (discounts == null) {
            this.discounts = new ArrayList<>();
        } else {
            this.discounts = new ArrayList<>(discounts);
        }
    }

    public boolean getIsDiscounted() {
        return getCurrentDiscount() != null;
    }

    public Discount getCurrentDiscount() {
        if (discounts == null || discounts.isEmpty()) {
            return null;
        }

        LocalDate today = LocalDate.now();
        for (Discount discount : discounts) {
            if (discount != null &&
                !today.isBefore(discount.getStartDate()) &&
                !today.isAfter(discount.getEndDate())) {
                return discount;
            }
        }
        return null;
    }

    public void addDiscount(Discount discount) throws DataValidationException {
        if (discount == null) {
            String message = "Add discount error, discount is null when it shouldn't";
            throw new DataValidationException(message);
        }
        if (discount.getProduct() != this) {
            String message = "Add discount error, discount does not belong to this product";
            throw new DataValidationException(message);
        }
        if (!discounts.contains(discount))
            discounts.add(discount);
    }

    public List<QuantityProduct> getLocation() {
        return location;
    }

    private void setLocation(List<QuantityProduct> location) {
        if (location == null)
            this.location = new ArrayList<>();
        else
            this.location = location;
    }

    public int getStockQuantity() {
        return location.stream()
                .filter(e -> e.getLocationProduct().getIsStock())
                .mapToInt(QuantityProduct::getQuantity)
                .sum();
    }

    public int getNonStockQuantity() {
        return location.stream()
                .filter(e -> !e.getLocationProduct().getIsStock())
                .mapToInt(QuantityProduct::getQuantity)
                .sum();
    }

    public int getTotalQuantity() {
        return location.stream()
                .mapToInt(QuantityProduct::getQuantity)
                .sum();
    }

    public String getLabel() {
        return name + " - " + getPrice();
    }

    @Override
    public String toString() {
        return "Product{id=" + id +
                ", name='" + name + '\'' +
                ", priceEVAT=" + priceEVAT +
                ", vat=" + vat +
                ", fidelityPoint=" + fidelityPoint +
                ", isEdible=" + isEdible +
                ", minStockQuantity=" + minStockQuantity +
                ", category=" + (category != null ? category.toString() : "null") +
                ", discounts=" + discounts +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Product other = (Product) obj;
        return id == other.getId() &&
                name.equals(other.getName()) &&
                priceEVAT.compareTo(other.getPriceEVAT()) == 0 &&
                vat.compareTo(other.getVat()) == 0 &&
                fidelityPoint == other.getFidelityPoint() &&
                isEdible == other.getIsEdible() &&
                minStockQuantity == other.getMinStockQuantity() &&
                category.equals(other.getCategory()) &&
                discounts.equals(other.getDiscounts());
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(id);
        result = 31 * result + name.hashCode();
        result = 31 * result + priceEVAT.stripTrailingZeros().hashCode();
        result = 31 * result + vat.stripTrailingZeros().hashCode();
        result = 31 * result + Integer.hashCode(fidelityPoint);
        result = 31 * result + Boolean.hashCode(isEdible);
        result = 31 * result + Integer.hashCode(minStockQuantity);
        result = 31 * result + category.hashCode();
        result = 31 * result + discounts.hashCode();
        return result;
    }
}
