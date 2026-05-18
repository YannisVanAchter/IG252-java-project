package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.NotificationController;

import javax.swing.*;
import java.awt.*;


/**
 * A {@link JButton} displayed in the {@link JMenuBar} that acts as a notification bell.
 * <p>It shows a badge with the number of unread notifications and toggles
 * a {@link NotificationDropdown} panel inside the frame's {@link JLayeredPane} when clicked.
 * <p>The badge and foreground color update automatically whenever a new
 * notification is pushed via the {@link NotificationController}.
 */
public class NotifBellButton extends JButton {

    private final NotificationController controller;
    private NotificationDropdown dropdown;
    private boolean opened = false;


    /**
     * Constructs a {@code NotifBellButton} bound to the given controller.
     * <p>On construction, the button:
     * <ul><li>renders its initial label via {@link #updateLabel()};</li>
     *   <li>registers a listener on the controller to refresh the badge on every new notification;</li>
     *   <li>attaches an action listener that calls {@link #toggleDropdown()} on click.</li></ul>
     *
     * @param controller the {@link NotificationController} that manages the notification list
     */
    public NotifBellButton(NotificationController controller) {
        this.controller = controller;

        updateLabel();
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(getFont().deriveFont(13f));

        controller.addListener(n -> SwingUtilities.invokeLater(this::updateLabel));

        addActionListener(e -> toggleDropdown());
    }

    /**
     * Refreshes the button text and foreground color based on the current unread count.
     * <p>If there are unread notifications, the label shows {@code "🔔 N"} in anred color. 
     * Otherwise, it shows {@code "🔔"} in the default label color.
     */
    private void updateLabel() {
        int unread = controller.unreadCount();
        setText(unread > 0 ? "🔔 " + unread : "🔔");
        setForeground(unread > 0 ? new Color(200, 80, 20) : Color.black);
    }

    /**
     * Toggles the visibility of the {@link NotificationDropdown} panel.
     * <p>Open the dropdown, a listener is registered on the controller to call 
     * {@link NotificationDropdown#refresh()} whenever a new notification arrives.
     * <p>The dropdown is added to / removed from the parent {@link JFrame}'s
     * {@link JLayeredPane} at {@link JLayeredPane#POPUP_LAYER}, positioned just below
     * and to the left of this button.
     */
    private void toggleDropdown() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (!(window instanceof JFrame frame)) return;

        JLayeredPane layeredPane = frame.getLayeredPane();

        if (!opened) {
            if (dropdown == null) {
                dropdown = new NotificationDropdown(controller, this::toggleDropdown);
                controller.addListener(n -> SwingUtilities.invokeLater(dropdown::refresh));
            }
            dropdown.setSize(340, 400);
            dropdown.refresh();

            int x = getX() - 300;
            int y = getY() + getHeight() + 5;
            dropdown.setLocation(x, y);

            layeredPane.add(dropdown, JLayeredPane.POPUP_LAYER);
            dropdown.setVisible(true);
            layeredPane.repaint();
            opened = true;
        } else {
            layeredPane.remove(dropdown);
            layeredPane.repaint();
            opened = false;
        }
    }
}