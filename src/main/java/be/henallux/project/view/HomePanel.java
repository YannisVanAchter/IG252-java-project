package main.java.be.henallux.project.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * HomePanel represents the main dashboard of the application.
 * <p>It provides a structured home screen composed of a header, multiple functional
 * sections (Management, Search, Business tasks), and a footer. Each section contains
 * interactive cards that navigate to the corresponding part of the application when clicked.
 * <p>Cards display a colored accent bar, a title, and a short description. They also
 * provide a hover effect that highlights the card border with the section's accent color.
 *
 * @see MainWindow
 * @see MainWindow#setPage(String)
 */
public class HomePanel extends JPanel {
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_TITLE_SECTION = new Font("SansSerif", Font.BOLD, 11);
    private static final Font FONT_CARD_TITLE = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_REG = new Font("SansSerif", Font.PLAIN, 11);

    private static final Color COLOR_TEXT_DESC = Color.GRAY;
    private static final Color COLOR_BG = Color.WHITE;

    private final MainWindow mainWindow;

    /**
     * Constructs a HomePanel and builds all UI sections.
     *
     * @param mainWindow the main application window used for page navigation
     */
    public HomePanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    /**
     * Builds the header section of the home panel.
     * <p>The header displays the application name "Le Grand Bazar" and the subtitle
     * "Integrated management system", centered horizontally.
     *
     * @return a {@link JPanel} representing the header
     */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Le Grand Bazar");
        title.setFont(FONT_TITLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Integrated management system");
        subtitle.setFont(FONT_REG);
        subtitle.setForeground(COLOR_TEXT_DESC);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        header.add(titlePanel);
        return header;
    }

    /**
     * Builds the main content area of the home panel.
     * * <p>The content is divided into three sections, each built by
     * {@link #createSection(String, String[][])}:
     * <ul><li><b>Management</b>: Client &amp; Supplier, Document</li>
     *   <li><b>Search</b>: Client, Product, Recipe</li>
     *   <li><b>Business tasks</b>: Sale &amp; receipt, Restock alert</li></ul>
     *
     * @return a {@link JPanel} containing all functional sections
     * @see #createCard(String[])
     */
    private JPanel buildContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        panel.add(createSection("Management", new String[][]{
                {"Client & Supplier", "Manage your clients and suppliers directory.", "#0B5FA5", "CLIENT_SUPPLIER"},
                {"Document", "Track orders, deliveries and preparation documents.", "#1E9D75", "DOCUMENT"}
        }));

        panel.add(Box.createVerticalStrut(12));

        panel.add(createSection("Search", new String[][]{
                {"Client", "Find a client by name, email or fidelity card.", "#534AB7", "CLIENT"},
                {"Product", "Browse the catalogue and check current promotions.", "#BA7517", "PRODUCT"},
                {"Recipe", "Look up recipes and their ingredients.", "#0F6E56", "RECIPE"}
        }));

        panel.add(Box.createVerticalStrut(12));

        panel.add(createSection("Business tasks", new String[][]{
                {"Sale & receipt", "Process a sale at the checkout and manage fidelity points.", "#993C1D", "RECEIPT"},
                {"Restock alert", "Get notified when stock runs low and place supplier orders.", "#993556", "STOCK"}
        }));

        return panel;
    }

    /**
     * Creates a labeled section containing a horizontal grid of navigation cards.
     * * <p>The section title is displayed in uppercase above the card grid.
     * Each card is built by {@link #createCard(String[])}.
     *
     * @param sectionTitle the display title of the section, shown in uppercase
     * @param items        a 2D array where each row represents one card and contains:
     *                     <ul><li>[0] card title</li>
     *                       <li>[1] short description</li>
     *                       <li>[2] accent color as a hex string (e.g. {@code "#0B5FA5"})</li>
     *                       <li>[3] page identifier passed to {@link MainWindow#setPage(String)}</li></ul>
     * @return a {@link JPanel} representing the complete section
     */
    private JPanel createSection(String sectionTitle, String[][] items) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

        JLabel labelPanel = new JLabel(sectionTitle.toUpperCase());
        labelPanel.setFont(FONT_TITLE_SECTION);
        labelPanel.setForeground(COLOR_TEXT_DESC);
        labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 0));
        section.add(labelPanel);

        JPanel grid = new JPanel(new GridLayout(1, items.length, 10, 0));
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        for (String[] data : items) {
            grid.add(createCard(data));
        }
        section.add(grid);
        return section;
    }

    /**
     * Creates an interactive navigation card.
     * <p>The card displays a colored accent, a bold title, and a short description. It reacts to mouse events:
     * <ul><li>Click: navigates to the target page via {@link MainWindow#setPage(String)}</li>
     *   <li>Hover enter: highlights the card border with the accent color</li></ul>
     * <p>A {@link JTextArea} is used instead of a {@link JLabel} to allow automatic line wrapping when the dialog is resized or narrower than the text content.
     *
     * @param data an array of four strings:
     *             <ul><li>[0] card title</li>
     *               <li>[1] short description displayed below the title</li>
     *               <li>[2] accent color as a hex string (e.g. {@code "#0B5FA5"})</li>
     *               <li>[3] page identifier passed to {@link MainWindow#setPage(String)}</li> </ul>
     * @return a {@link JPanel} representing a clickable navigation card
     */
    private JPanel createCard(String[] data) {
        String title = data[0];
        String description = data[1];
        Color color = Color.decode(data[2]);
        String pageName = data[3];

        JPanel card = new JPanel();
        card.setBackground(COLOR_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        MouseListener listener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainWindow.setPage(pageName);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color, 1, true),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1, true),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
        };

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JPanel accent = new JPanel();
        accent.setBackground(color);
        accent.setMaximumSize(new Dimension(28, 4));
        accent.setPreferredSize(new Dimension(28, 4));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_CARD_TITLE);

        JTextArea descArea = new JTextArea(description);
        descArea.setFont(FONT_REG);
        descArea.setForeground(COLOR_TEXT_DESC);
        descArea.setBackground(COLOR_BG);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(accent);
        card.add(Box.createVerticalStrut(6));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(descArea);

        card.addMouseListener(listener);
        accent.addMouseListener(listener);
        titleLbl.addMouseListener(listener);
        descArea.addMouseListener(listener);

        return card;
    }
    /**
     * Builds the footer of the home panel.
     * <p>The footer contains a horizontal separator followed by a centered label
     * listing the institution name, group, and the names of the three students.
     *
     * @return a {@link JPanel} representing the footer
     */
    private JPanel buildFooter() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));

        JLabel info = new JLabel("Henallux - Group B — Clément Nelisse · Yannis Van Achter · Antoine Dieu");
        info.setFont(FONT_REG);
        info.setForeground(COLOR_TEXT_DESC);
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(separator);
        panel.add(Box.createVerticalStrut(10));
        panel.add(info);

        return panel;
    }
}