package main.java.be.henallux.project.data.exception;

public class DataBaseException extends Exception {
    public DataBaseException(String message) {
        super(message);
    }

    public DataBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
