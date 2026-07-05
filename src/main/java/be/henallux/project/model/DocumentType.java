package be.henallux.project.model;

/**
 * A document type with a name.
 */
public class DocumentType implements Model {
    private int id;
    private String name;

    public DocumentType(int id, String name) {
        setId(id);
        setName(name);
    }
    public DocumentType(String name) {
        this(0, name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "DocumentType{id" + id + "name='" + name + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DocumentType that = (DocumentType) obj;
        return id == that.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
