package model;

import exception.DataValidationException;

public class FidelityCard {
    private int id;
    private int totalPoint;
    private boolean isValid;
    private ClientSupplier client;

    public FidelityCard(int id, int totalPoint, boolean isValid, ClientSupplier client) throws DataValidationException {
        setId(id);
        setTotalPoint(totalPoint);
        setIsValid(isValid);
        setClient(client);
    }

    public int getId() {
        return id;
    }

    private void setId(int id) throws DataValidationException {
        if (id < 0) {
            String message = "ID setting error, ID is lower than 0 when it shouldn't (current value: " + id + ")";
            throw new DataValidationException(message);
        }
        this.id = id;
    }

    public int getTotalPoint() {
        return totalPoint;
    }

    private void setTotalPoint(int totalPoint) throws DataValidationException {
        if (totalPoint < 0) {
            String message = "Total point setting error, total point is lower than 0 when it shouldn't (current value: " + totalPoint + ")";
            throw new DataValidationException(message);
        }
        this.totalPoint = totalPoint;
    }

    public boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public ClientSupplier getClient() {
        return client;
    }

    private void setClient(ClientSupplier client) throws DataValidationException {
        if (client == null) {
            String message = "Client setting error, client is null when it shouldn't";
            throw new DataValidationException(message);
        }
        this.client = client;
    }

    public void addPoint(int point) throws DataValidationException {
        if (point < 0) {
            String message = "Add point error, point to add is lower than 0 when it shouldn't (current value: " + point + ")";
            throw new DataValidationException(message);
        }
        setTotalPoint(getTotalPoint() + point);
    }

    public String getLabel() {
        return "Fidelity card #" + id + " - " + totalPoint + " points";
    }

    @Override
    public String toString() {
        return "FidelityCard{id=" + id + ", totalPoint=" + totalPoint + ", isValid=" + isValid + ", client=" + (client != null ? client.toString() : "null") + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        FidelityCard other = (FidelityCard) obj;
        return  id == other.getId() &&
                totalPoint == other.getTotalPoint() &&
                isValid == other.getIsValid() &&
                client.equals(other.getClient());
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(id);
        result = 31 * result + Integer.hashCode(totalPoint);
        result = 31 * result + Boolean.hashCode(isValid);
        result = 31 * result + client.hashCode();
        return result;
    }
}