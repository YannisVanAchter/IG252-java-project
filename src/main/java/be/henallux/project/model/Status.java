package main.java.be.henallux.project.model;

import main.java.be.henallux.project.exception.DataValidationException;

/**
 * A status with a name.
 */
public class Status {
    private String name;

    public Status(String name) throws DataValidationException {
        setName(name);
    }

    public String getName() { return name; }

    private void setName(String name) throws DataValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new DataValidationException("Status name cannot be null or empty.");
        }
        this.name = name;
    }

    public String getLabel() { return getName(); }

    @Override
    public String toString() {
        return "Status{name='" + name + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        return obj != null && getClass() == obj.getClass() && name.equals(((Status) obj).getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
