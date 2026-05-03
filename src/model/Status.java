package model;

public class Status {
    private String name;

    public Status(String name) {
        this.name = name;
    }

    public String getName() { return name; }

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
