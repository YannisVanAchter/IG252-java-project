package main.java.be.henallux.project.model;

import main.java.be.henallux.project.model.exception.DataValidationException;

/**
 * This class represents an address, which consists of a street name, street number, and a locality.
 * The class includes validation for the street number to ensure it is a positive integer, \
 * and for the locality to ensure it is not null. 
 * The locality itself is represented by the locality class.
 * The Address class provides two constructors: 
 *  1. that takes a locality object directly
 *  2. that takes the components of a locality (name and postal code) to create a locality object.
 */
public class Address implements Model {
    private int addressId;
    private String streetName;
    private int streetNumber;
    private Locality locality;

    public Address(int addressId, String streetName, int streetNumber, Locality locality) throws DataValidationException {
        setAddressId(addressId);
        setStreetName(streetName);
        setStreetNumber(streetNumber);
        setLocality(locality);
    }

    public Address(int addressId, String streetName, int streetNumber, String localityName, int localityPostalCode) throws DataValidationException {
        setAddressId(addressId);
        setStreetName(streetName);
        setStreetNumber(streetNumber);
        setLocality(new Locality(localityName, localityPostalCode));
    }

    public int getAddressId() { return addressId; }

    public void setAddressId(int addressId) throws DataValidationException {
        if (addressId < 0)
            throw new DataValidationException("Address ID must be positive");
        this.addressId = addressId;
    }

    public String getStreetName() { return streetName; }

    private void setStreetName(String streetName) throws DataValidationException {
        if (streetName == null || streetName.isEmpty()) {
            String message = "Street name setting error, street name is null or empty when it shouldn't (current value: " + streetName + ")";
            throw new DataValidationException(message);
        }
        this.streetName = streetName;
    }

    public int getStreetNumber() { return streetNumber; }

    private void setStreetNumber(int streetNumber) throws DataValidationException {
        if (streetNumber <= 0) {
            String message = "Street number setting error, street number is lower or equal to 0 (zero) when it shouldn't (current value: " + streetNumber + ")";
            throw new DataValidationException(message);
        }
        this.streetNumber = streetNumber;
    }

    public Locality getLocality() { return locality; }

    private void setLocality(Locality locality) throws DataValidationException {
        if (locality == null) {
            String message = "locality setting error, locality is null when it shouldn't";
            throw new DataValidationException(message);
        }
        this.locality = locality;
    }

    public String getLabel() {
        return String.format("%s %d, %s", streetName, streetNumber, locality.getLabel());
    }

    @Override
    public String toString() {
        return "Address{streetName='" + streetName + "', streetNumber=" + streetNumber + ", locality=" + locality.toString() + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Address other = (Address) obj;
        return addressId == other.getAddressId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(addressId);
    }
}
