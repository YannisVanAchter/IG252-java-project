package be.henallux.project.view;

import be.henallux.project.model.NotificationItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * A floating toast notification window displayed above the {@link MainWindow}.
 * <p>Each instance wraps a {@link JWindow} that appears in the bottom-right corner of the parent window,
 * stacked above any other active toasts. The window auto-dismisses after 4 seconds or immediately when clicked.
 * <p>Background color, hover effect, and text colors are determined by the {@link NotificationItem.Type} of the notification.
 */
public class ToastWindow {
    private final MainWindow mainWindow;
    private final JWindow window;
    private static int activeCount = 0;

    private static final Color COLOR_SUCCESS = new Color(34, 139, 80);
    private static final Color COLOR_WARNING = new Color(200, 130, 20);
    private static final Color COLOR_ERROR = new Color(180, 40, 40);
    private static final Color COLOR_INFO = new Color(50, 100, 180);

    private static final Color TEXT_TITLE = Color.WHITE;
    private static final Color TEXT_MSG = new Color(220, 220, 220);
    private static final Color TEXT_TIME = new Color(180, 180, 180);

    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_MSG = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_TIME = new Font("SansSerif", Font.PLAIN, 10);

    /**
     * Constructs and immediately shows a toast for notification.
     * <p>Delegates to {@link #ToastWindow(MainWindow, NotificationItem, boolean)}
     * with {@code visible = true}.
     *
     * @param mainWindow the parent window used as the positioning anchor
     * @param notif      the notification to display
     */
    public ToastWindow(MainWindow mainWindow, NotificationItem notif) {
        this(mainWindow, notif, true);
    }

    /**
     * Constructs a toast for notification, optionally showing it immediately.
     * <p>Builds the panel layout (title, message, timestamp), attaches mouse listeners
     * for click and hover, positions the window via {@link #positionWindow()},
     * applies rounded corners, and calls {@link #show()} if {@code visible} is {@code true}.
     *
     * @param mainWindow the parent window used as the positioning anchor
     * @param notif      the notification to display
     * @param visible    if {@code true}, the toast is shown immediately after construction
     */
    public ToastWindow(MainWindow mainWindow, NotificationItem notif, boolean visible) {
        this.mainWindow = mainWindow;

        window = new JWindow();
        window.setAlwaysOnTop(true);

        JPanel panel = new JPanel(new BorderLayout(8, 4));
        panel.setBackground(toastColor(notif.getType()));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblTitle = new JLabel(notif.getTitle());
        lblTitle.setForeground(TEXT_TITLE);
        lblTitle.setFont(FONT_TITLE);

        JLabel lblMsg = new JLabel(notif.getMessage());
        lblMsg.setForeground(TEXT_MSG);
        lblMsg.setFont(FONT_MSG);

        JLabel lblTime = new JLabel(notif.getFormattedTime());
        lblTime.setForeground(TEXT_TIME);
        lblTime.setFont(FONT_TIME);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblTime, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(lblMsg, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick(notif);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(toastColor(notif.getType()).darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(toastColor(notif.getType()));
            }
        });

        window.add(panel);
        window.setPreferredSize(new Dimension(320, 72));
        window.pack();

        activeCount++;
        positionWindow();

        window.setShape(new RoundRectangle2D.Double(
                0, 0, window.getWidth(), window.getHeight(), 14, 14));

        if (visible) {
            show();
        }
    }

    /**
     * Returns the background color matching the given notification type.
     *
     * @param type the {@link NotificationItem.Type} of the notification
     * @return {@code Color} green for {@code SUCCESS}, orange for {@code WARNING},
     *         red for {@code ERROR}, blue for {@code INFO}
     */
    private Color toastColor(NotificationItem.Type type) {
        return switch (type) {
            case SUCCESS -> COLOR_SUCCESS;
            case WARNING -> COLOR_WARNING;
            case ERROR -> COLOR_ERROR;
            default -> COLOR_INFO;
        };
    }

    /**
     * Positions the toast window in the bottom-right corner of {@link #mainWindow},
     * offset upward based on {@link #activeCount} to stack multiple toasts.
     * <p>Uses {@link MainWindow#getLocationOnScreen()} as the origin, so the position
     * is absolute on screen regardless of the window's location on the desktop.
     */
    private void positionWindow() {
        Point origin = mainWindow.getLocationOnScreen();
        int x = origin.x + mainWindow.getWidth() - window.getWidth() - 20;
        int y = origin.y + mainWindow.getHeight() - window.getHeight() - 20 - ((activeCount - 1) * 80);
        window.setLocation(x, y);
    }
    
    /**
     * Handles a click on the toast panel.
     * <p>Marks the notification as read, dismisses the toast via {@link #dismiss()},
     * and runs the notification's associated action if one is defined.
     * @param notif the notification that was clicked
     */
    private void onClick(NotificationItem notif) {
        notif.setIsRead(true);
        dismiss();
        if (notif.getAction() != null) {
            notif.getAction().run();
        }
    }

    /**
     * Makes the toast visible and starts a 4-second auto-dismiss timer.
     * <p>The timer it calls {@link #dismiss()} once after the delay.
     */
    public void show() {
        window.setVisible(true);
        Timer timer = new Timer(4000, e -> dismiss());
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Hides and disposes the toast window, then decrements {@link #activeCount}
     * so the next toast can reclaim the freed vertical slot.
     */
    private void dismiss() {
        window.setVisible(false);
        window.dispose();
        if (activeCount > 0) {
            activeCount--;
        }
    }
}
