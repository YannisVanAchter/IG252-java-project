package model;

/**
 * A document type with a name.
 */
public class DocumentType {
    private String name;

    public DocumentType(String name) {
        setName(name);
    }

    public String getName() { return name; }

    private void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.name = "Unknown";
            return;
        }
        this.name = name;
    }

    public String getLabel() { return getName(); }

    @Override
    public String toString() {
        return "DocumentType{name='" + name + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DocumentType that = (DocumentType) obj;
        return name.equals(that.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
