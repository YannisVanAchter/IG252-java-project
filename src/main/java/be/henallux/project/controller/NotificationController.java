package be.henallux.project.controller;

import be.henallux.project.model.NotificationItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller managing the notification lifecycle.
 * <p>Responsibilities:
 * <ul><li>storing the notification history;</li>
 *     <li>notifying registered listeners so the UI (toast, badge, dropdown) stays in sync.</li></ul>
 * <p>The toast display is delegated to a listener registered externally via {@link #addListener(Consumer)},
 * keeping this controller decoupled from any Swing dependency.
 */
public class NotificationController {

    private final List<NotificationItem> notifications = new ArrayList<>();
    private final List<Consumer<NotificationItem>> listeners = new ArrayList<>();

    /**
     * Pushes a new notification into the controller.
     * <p>If {@code notif} is not {@code null}, this method:
     * <ol><li>wraps the notification's original action so that clicking the toast
     *         also triggers {@link #notifyListeners(NotificationItem)};</li>
     *     <li>adds the notification to the history;</li>
     *     <li>notifies all registered listeners immediately, including the toast display listener.</li></ol>
     *
     * @param notif the notification to push; ignored if {@code null}
     */
    public void push(NotificationItem notif) {
        if (notif == null) return;
        Runnable originalAction = notif.getAction();
        notif.setAction(() -> {
            if (originalAction != null) originalAction.run();
            notif.setIsRead(true);
            notifyListeners(null);
        });

        notifications.add(notif);
        notifyListeners(notif);
    }

    /**
     * Registers a listener that will be called on every {@link #push(NotificationItem)}
     * and {@link #markAllRead()} event.
     * <p>The {@link NotificationItem} passed to the listener is the newly pushed item,
     * or {@code null} when triggered by {@link #markAllRead()}.
     *
     * @param listener a {@link Consumer} receiving the relevant notification, or {@code null}
     */
    public void addListener(Consumer<NotificationItem> listener) {
        listeners.add(listener);
    }

    /**
     * Returns the full notification history, ordered from oldest to most recent.
     *
     * @return an unmodifiable view of the internal notification list
     */
    public List<NotificationItem> getAll() {
        return notifications;
    }

    /**
     * Counts notifications that have not yet been read.
     *
     * @return the number of {@link NotificationItem} instances where {@code isRead} is {@code false}
     */
    public int unreadCount() {
        int count = 0;
        for (NotificationItem n : notifications) {
            if (!n.getIsRead()) count++;
        }
        return count;
    }

    /**
     * Marks all notifications as read and notifies listeners with a {@code null} item
     * to signal a whole state change rather than a single new notification.
     */
    public void markAllRead() {
        for (NotificationItem n : notifications) {
            n.setIsRead(true);
        }
        notifyListeners(null);
    }

    /**
     * Dispatches the given notification to all registered listeners.
     *
     * @param notif the notification to broadcast, or {@code null} for whole events such as {@link #markAllRead()}
     */
    private void notifyListeners(NotificationItem notif) {
        for (Consumer<NotificationItem> listener : listeners) {
            listener.accept(notif);
        }
    }
}