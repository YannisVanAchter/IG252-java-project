package main.java.be.henallux.project.exception;

public class DataValidationException extends Exception {

    public DataValidationException(String messageString) { super(messageString); }

    public DataValidationException(String messageString, Throwable cause) { super(messageString, cause); }

    @Override
    public String toString() { return "Objet DataConstrainsException, with the following message: " + this.getMessage(); }
}
