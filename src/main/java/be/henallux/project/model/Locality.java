package be.henallux.project.model;

import be.henallux.project.model.exception.DataValidationException;
import be.henallux.project.model.Model;

/**
 * This class represents a Locality, which is a part of an address. 
 * It contains the city of the Locality and its postal code. 
 * The class also includes validation for the postal code \
 * to ensure it is a positive integer and does not exceed a specified maximum value.
 */
public class Locality implements Model {
    private String city;
    private int postalCode;
    public static final Integer MAX_POSTAL_CODE_VALUE = Integer.MAX_VALUE;

    public Locality(String city, int postalCode) throws DataValidationException {
        setCity(city);
        setPostalCode(postalCode);
    }

    public String getCity() { return city; }

    private void setCity(String city) throws DataValidationException {
        if (city == null || city.isEmpty()) {
            String message = "city setting error, city is null or empty when it shouldn't (current value: " + city + ")";
            throw new DataValidationException(message);
        }
        this.city = city;
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

    public String getLabel() {
        return postalCode + " " + city;
    }

    @Override
    public String toString() {
        return "Locality{city='" + city + "', postalCode=" + postalCode + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Locality other = (Locality) obj;
        return city.equals(other.getCity()) && postalCode == other.getPostalCode();
    }

    @Override
    public int hashCode() {
        int result = city.hashCode();
        result = 31 * result + postalCode;
        return result;
    }
}