package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.NotificationController;
import main.java.be.henallux.project.model.NotificationItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * A dropdown panel displaying the list of notifications, embedded in the
 * parent {@link JFrame}'s {@link JLayeredPane} by {@link NotifBellButton}.
 *
 * <p>The panel is split into two areas:
 * <ul><li>a fixed header showing the unread count and a "Mark all read" button;</li>
 *   <li>a scrollable list of notification rows, ordered from most to least recent.</li></ul>
 * <p>Call {@link #refresh()} to resync both areas with the current state of the
 * {@link NotificationController} without rebuilding the whole component.
 */
public class NotificationDropdown extends JPanel {

    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_ROW_TITLE = new Font("SansSerif", Font.BOLD, 12);
    private static final Font FONT_TIME = new Font("SansSerif", Font.PLAIN, 10);
    private static final Font FONT_MESSAGE = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font FONT_BUTTON = new Font("SansSerif", Font.PLAIN, 11);

    private final NotificationController controller;

    private JPanel listPanel;
    private JLabel title;

    private final Runnable onClose;

    /**
     * Constructs a {@code NotificationDropdown}.
     * <p>Calls {@link #build()} to create the static structure, then {@link #refresh()} to populate it with live data immediately.
     *
     * @param controller the {@link NotificationController} managing the notification list
     * @param onClose    a {@link Runnable} called when the notification is clicked;
     *                   {@code ex: MainWindow.setPage(..) or sout(...)}
     */
    public NotificationDropdown(NotificationController controller, Runnable onClose) {
        this.controller = controller;
        this.onClose = onClose;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(340, 0));

        build();
        refresh();
    }

    public void refresh() {
        rebuildHeader();
        buildList();
        revalidate();
        repaint();
    }

    /**
     * Builds the static structure of the dropdown: border, header panel, list panel,
     * and scroll pane. Called once at construction time.
     */
    private void build() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JPanel headerPanel = buildHeader();
        add(headerPanel, BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Builds a single notification row.
     * <p>The row contains:
     * <ul><li>a colored dot indicating the notification {@link NotificationItem.Type};</li>
     *   <li>the notification title and formatted timestamp on the first line;</li>
     *   <li>the message body on the second line.</li></ul>
     * <p>Clicking the row marks the notif as read, refreshes the list, runs the notif's
     * action if present, and invokes {@link #onClose} for the dropdown.
     *
     * @param notif the {@link NotificationItem} to render
     * @return a configured {@link JPanel} representing the row
     */
    private JPanel buildRow(NotificationItem notif) {

        JPanel row = new JPanel(new BorderLayout(8, 2));
        row.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.setBackground(baseColor(notif));

        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(10, 10));
        dot.setMaximumSize(new Dimension(10, 10));
        dot.setBackground(dotColor(notif.getType()));

        JLabel lblTitle = new JLabel(notif.getTitle());
        lblTitle.setFont(FONT_ROW_TITLE);

        JLabel lblTime = new JLabel(notif.getFormattedTime());
        lblTime.setFont(FONT_TIME);
        lblTime.setForeground(Color.GRAY);

        JLabel lblMsg = new JLabel(notif.getMessage());
        lblMsg.setFont(FONT_MESSAGE);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblTime, BorderLayout.EAST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(top);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(lblMsg);

        row.add(dot, BorderLayout.WEST);
        row.add(textPanel, BorderLayout.CENTER);

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                notif.setIsRead(true);
                refresh();
                if (notif.getAction() != null) {
                    notif.getAction().run();
                }
                if (onClose != null) onClose.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(220, 232, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(baseColor(notif));
            }
        });

        return row;
    }

    /**
     * Builds the header panel containing the unread-count title and the
     * "Mark all read" button. Called once during {@link #build()}.
     * <p>Clicking button call {@link NotificationController#markAllRead()} and immediately calls {@link #refresh()}.
     *
     * @return the constructed header {@link JPanel}
     */
    private JPanel buildHeader() {

        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        title = new JLabel();
        title.setFont(FONT_TITLE);

        JButton markAll = new JButton("Mark all read");
        markAll.setFont(FONT_BUTTON);
        markAll.setBorderPainted(false);
        markAll.setContentAreaFilled(false);
        markAll.setFocusPainted(false);
        markAll.setForeground(Color.BLUE);
        markAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        markAll.addActionListener(e -> {
            controller.markAllRead();
            refresh();
        });

        header.add(title, BorderLayout.WEST);
        header.add(markAll, BorderLayout.EAST);

        return header;
    }

    /**
     * Updates the header title label to reflect the current unread count. Called on every {@link #refresh()}.
     */
    private void rebuildHeader() {
        title.setText("Notifications (" + controller.unreadCount() + " unread)");
    }

    /**
     * Clears and repopulates {@link #listPanel} with one row per notification,
     * ordered from most recent to oldest. Inserts a {@link JSeparator} between rows.
     * Shows a "No notification" placeholder when the list is empty.
     */
    private void buildList() {
        listPanel.removeAll();

        List<NotificationItem> items = controller.getAll();

        if (items.isEmpty()) {
            JLabel empty = new JLabel("No notification");
            empty.setForeground(Color.GRAY);
            empty.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
            listPanel.add(empty);
        } else {
            for (int i = items.size() - 1; i >= 0; i--) {
                listPanel.add(buildRow(items.get(i)));
                if (i > 0) {
                    JSeparator sep = new JSeparator();
                    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    listPanel.add(sep);
                }
            }
        }
    }

    /**
     * Returns the dot color associated with the given notification type.
     *
     * @param type the {@link NotificationItem.Type} of the notification
     * @return green for {@code SUCCESS}, orange for {@code WARNING},red for {@code ERROR}, blue for {@code INFO}
     */
    private Color dotColor(NotificationItem.Type type) {
        return switch (type) {
            case SUCCESS -> new Color(34, 139, 80);
            case WARNING -> new Color(200, 130, 20);
            case ERROR -> new Color(180, 40, 40);
            default -> new Color(50, 100, 180);
        };
    }

    /**
     * Returns the background color for a notification row based on its read state.
     * White for a read notification and light blue for an unread notification
     *
     * @param item the {@link NotificationItem} whose read state is checked
     * @return {@link Color#WHITE} if the notification has been read,
     *         a light blue ({@code #EBF3FF}) otherwise
     */
    private Color baseColor(NotificationItem item) {
        return item.getIsRead() ? Color.WHITE : new Color(235, 243, 255);
    }
}