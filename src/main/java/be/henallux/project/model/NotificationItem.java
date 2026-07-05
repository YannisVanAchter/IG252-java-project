package be.henallux.project.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model representing a single notification in the application.
 * <p>A notification carries a title, a message, a severity {@link Type},
 * an optional click action, and the timestamp at which it was created.
 * The read/unread state is mutable and tracked via {@link #getIsRead()} /{@link #setIsRead(boolean)}.
 *
 * <p>Three convenience constructors are provided, from the most complete
 * to the most minimal:
 * <ul><li>{@link #NotificationItem(String, String, Type, Runnable)} — full;</li>
 *   <li>{@link #NotificationItem(String, String, Type)} — no action;</li>
 *   <li>{@link #NotificationItem(String, String)} — defaults to {@link Type#INFO}, no action.</li></ul>
 */
public class NotificationItem {

    public enum Type {INFO, SUCCESS, WARNING, ERROR}

    private String title;
    private String message;
    private Type type;
    private Runnable action;
    private final LocalDateTime time;
    private boolean read;

    /**
     * Full constructor.
     *
     * @param title   display title; replaced by {@code "Notification"} if blank or null
     * @param message body text; replaced by an empty string if blank or null
     * @param type    severity level; defaults to {@link Type#INFO} if null
     * @param action  runnable executed on click; defaults to a no-op if null
     */
    public NotificationItem(String title, String message, Type type, Runnable action) {
        setTitle(title);
        setMessage(message);
        setType(type);
        setAction(action);
        this.time = LocalDateTime.now();
        this.read = false;
    }

    /**
     * Constructs a notification without a click action.
     *
     * @param title   display title
     * @param message body text
     * @param type    severity level
     */
    public NotificationItem(String title, String message, Type type) {
        this(title, message, type, null);
    }

    /**
     * Constructs a notification with default type ({@link Type#INFO}) and no click action.
     *
     * @param title   display title
     * @param message body text
     */
    public NotificationItem(String title, String message) {
        this(title, message, null, null);
    }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = (title == null || title.isBlank()) ? "Notification" : title;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) {
        this.message = (message == null || message.isBlank()) ? "" : message;
    }

    public NotificationItem.Type getType() { return type; }
    public void setType(Type type) {
        this.type = (type != null) ? type : Type.INFO;
    }

    public Runnable getAction() { return action; }
    public void setAction(Runnable action) {
        this.action = (action == null) ? () -> {} : action;
    }

    public boolean getIsRead() { return read; }
    public void setIsRead(boolean read) {
        this.read = read;
    }

    /**
     * Returns the creation time formatted as {@code HH:mm:ss}.
     *
     * @return a string such as {@code "14:03:57"}
     */
    public String getFormattedTime() {
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}