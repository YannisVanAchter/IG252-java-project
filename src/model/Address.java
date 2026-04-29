package model;

import exception.DataValidationException;

/**
 * This class represents an address, which consists of a street name, street number, and a location.
 * The class includes validation for the street number to ensure it is a positive integer, \
 * and for the location to ensure it is not null. 
 * The location itself is represented by the Location class.
 * The Address class provides two constructors: 
 *  1. that takes a Location object directly
 *  2. that takes the components of a Location (name and postal code) to create a Location object.
 */
public class Address {
    private String streetName;
    private int streetNumber;
    private Location location;

    public Address(String streetName, int streetNumber, Location location) throws DataValidationException {
        setStreetName(streetName);
        setStreetNumber(streetNumber);
        setLocation(location);
    }

    public Address(String streetName, int streetNumber, String locationName, int locationPostalCode) throws DataValidationException {
        setStreetName(streetName);
        setStreetNumber(streetNumber);
        setLocation(new Location(locationName, locationPostalCode));
    }

    public String getStreetName() { return streetName; }

    private void setStreetName(String streetName) {
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

    public Location getLocation() { return location; }

    private void setLocation(Location location) throws DataValidationException {
        if (location == null) {
            String message = "Location setting error, location is null when it shouldn't";
            throw new DataValidationException(message);
        }
        this.location = location;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Address other = (Address) obj;
        return streetName.equals(other.getStreetName()) && streetNumber == other.getStreetNumber() && location.equals(other.getLocation());
    }

    @Override
    public int hashCode() {
        int result = streetName.hashCode();
        result = 31 * result + streetNumber;
        result = 31 * result + location.hashCode();
        return result;
    }
}