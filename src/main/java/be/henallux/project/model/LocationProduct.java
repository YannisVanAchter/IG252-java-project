package main.java.be.henallux.project.model;

import main.java.be.henallux.project.model.exception.DataValidationException;

public class LocationProduct implements Model {
    private String locationProductId;
    private String shelf;
    private String floor;
    private boolean isStock;
    private boolean isFreezer;

    public LocationProduct(String shelf, String floor, boolean isStock, boolean isFreezer) throws DataValidationException {
        setShelf(shelf);
        setFloor(floor);
        setIsStock(isStock);
        setIsFreezer(isFreezer);
        setLocationProductId(buildLocationProductId());
    }

    public String getLocationProductId() {
        return locationProductId;
    }

    private void setLocationProductId(String locationProductId) throws DataValidationException {
        if (locationProductId == null || locationProductId.isEmpty()) {
            String message = "Location product ID setting error, ID is null or empty when it shouldn't (current value: " + locationProductId + ")";
            throw new DataValidationException(message);
        }
        this.locationProductId = locationProductId;
    }

    private String buildLocationProductId() {
        return shelf + "-" + floor + "-" + isStock;
    }

    public String getShelf() {
        return shelf;
    }

    private void setShelf(String shelf) throws DataValidationException {
        if (shelf == null || shelf.isEmpty()) {
            String message = "Shelf setting error, shelf is null or empty when it shouldn't (current value: " + shelf + ")";
            throw new DataValidationException(message);
        }
        this.shelf = shelf;
    }

    public String getFloor() {
        return floor;
    }

    private void setFloor(String floor) throws DataValidationException {
        if (floor == null || floor.isEmpty()) {
            String message = "Floor setting error, floor is null or empty when it shouldn't (current value: " + floor + ")";
            throw new DataValidationException(message);
        }
        this.floor = floor;
    }

    public boolean getIsStock() {
        return isStock;
    }

    private void setIsStock(boolean isStock) {
        this.isStock = isStock;
    }

    public boolean getIsFreezer() {
        return isFreezer;
    }

    private void setIsFreezer(boolean isFreezer) {
        this.isFreezer = isFreezer;
    }

    public String getLabel() {
        return shelf + " / " + floor + (isStock ? " / stock" : " / non-stock") + (isFreezer ? " / freezer" : "");
    }

    @Override
    public String toString() {
        return "LocationProduct{locationProductId='" + locationProductId + "', shelf='" + shelf + "', floor='" + floor + "', isStock=" + isStock + ", isFreezer=" + isFreezer + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        LocationProduct other = (LocationProduct) obj;
        return  shelf.equals(other.getShelf()) &&
                floor.equals(other.getFloor()) &&
                isStock == other.getIsStock();
    }

    @Override
    public int hashCode() {
        int result = shelf.hashCode();
        result = 31 * result + floor.hashCode();
        result = 31 * result + Boolean.hashCode(isStock);
        return result;
    }
}