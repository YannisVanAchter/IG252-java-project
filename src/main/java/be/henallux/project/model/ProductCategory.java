package be.henallux.project.model;

import be.henallux.project.model.exception.DataValidationException;

public class ProductCategory implements Model {
    private int id;
    private String name;

    public ProductCategory(int id, String name) throws DataValidationException {
        setId(id);
        setName(name);
    }

    public int getId() { return id; }

    public final void setId(int id) throws DataValidationException {
        if (id < 0)
            throw new DataValidationException("Product category must posses a positive id");
        this.id = id;
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
        return String.format("ProductCategory{id=%d, name=%s}", id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ProductCategory other = (ProductCategory) obj;
        return id == other.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
