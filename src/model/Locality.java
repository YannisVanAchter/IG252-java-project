package model;

import exception.DataValidationException;

/**
 * This class represents a Locality, which is a part of an address. 
 * It contains the name of the Locality and its postal code. 
 * The class also includes validation for the postal code \
 * to ensure it is a positive integer and does not exceed a specified maximum value.
 */
public class Locality {
    private String name;
    private int postalCode;
    private final Integer MAX_POSTAL_CODE_VALUE = Integer.MAX_VALUE;

    public Locality(String name, int postalCode) throws DataValidationException {
        setName(name);
        setPostalCode(postalCode);
    }

    public String getName() { return name; }

    private void setName(String name) {
        this.name = name;
    }

    public int getPostalCode() { return postalCode; }

    private void setPostalCode(int postalCode) throws DataValidationException {
        if (postalCode <= 0) {
            String message = "Postal code setting error, postal code is lower or equal to 0 (zero) when it shouldn't (current value: " + postalCode + ")";
            throw new DataValidationException(message);
        }
        if (null != MAX_POSTAL_CODE_VALUE && postalCode > MAX_POSTAL_CODE_VALUE) {
            String message = "Postal code setting error, postal code is higher than " + MAX_POSTAL_CODE_VALUE + " when it shouldn't (current value: " + postalCode + ")";
            throw new DataValidationException(message);
        }
        this.postalCode = postalCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Locality other = (Locality) obj;
        return name.equals(other.getName()) && postalCode == other.getPostalCode();
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + postalCode;
        return result;
    }
}