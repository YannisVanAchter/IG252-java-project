package exception;

public class DataValidationException extends Exception {
    private String message;

    public DataValidationException(String messageString) { this.message = messageString; }

    public String getMessage() { return message; }

    @Override
    public String toString() { return "Objet DataConstrainsException, with the following message: " + this.message; }
}
