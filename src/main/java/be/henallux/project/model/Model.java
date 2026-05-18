package main.java.be.henallux.project.model;

public interface Model {
    /**
     * Returns a human-readable label for the model, 
     * which can be used in UI components like combo boxes.
     * @return a string label representing the model
     */
    public String getLabel();

    /**
     * Returns a string representation of the model, which can be used for debugging or logging purposes.
     * @return a string representation of the model
     */
    @Override
    public String toString();

    /**
     * Determines whether this model is equal to another object.
     * @param obj the object to compare with this model
     * @return true if the specified object is equal to this model, false otherwise
     */
    @Override
    public boolean equals(Object obj);

    /**
    * Returns a hash code value for the model, which is used in hashing-based collections like HashMap.
    * @return a hash code value for this model
    */
    @Override
    public int hashCode();
}
