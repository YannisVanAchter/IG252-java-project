package model;

import exception.DataValidationException;

public class ProductCategory {
    private String name;

    public ProductCategory(String name) throws DataValidationException {
        setName(name);
    }

    public String getName() { return name; }

    private void setName(String name) throws DataValidationException {
        if (name == null || name.isEmpty()) {
            throw new DataValidationException("A product category must posses a name");
        }
        this.name = name;
    }

    public String getLabel() { return getName(); }

    @Override
    public String toString() {
        return String.format("ProductCategory{name=%s", name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ProductCategory o = (ProductCategory) obj;
        return o.getName().equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
