package main.java.be.henallux.project.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * HelpPanel displays the application's help and documentation page.
 * <p>It is composed of four sections stacked vertically inside a scrollable panel:
 * <ul><li>A header with the page title and subtitle</li>
 *   <li>A <b>keyboard shortcut</b> reference table listing all available accelerators</li>
 *   <li>A <b>section guide</b> describing what each part of the application does</li>
 *   <li><b>Project information</b>: institution, group, students, and academic year</li></ul>
 * <p>This panel is intended to be displayed inside a non-modal {@link JDialog} so the user can consult it while continuing to use the application.
 *
 * @see MenuWindow
 */
public class HelpPanel extends JPanel {

    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FONT_SECTION = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_H3 = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_REG = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);

    private static final Color COLOR_TEXT_DESC = Color.GRAY;
    private final JScrollPane scrollPane;

    public HelpPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#EEEEEE"));

        scrollPane = new JScrollPane(buildContent());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setValue(0);

        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Builds the full scrollable content panel containing all sections.
     *
     * @return a {@link JPanel} with all help sections stacked vertically
     */
    private JPanel buildContent() {
        JPanel panel = ViewUtils.createColumnPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        panel.add(buildHeader());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildShortcuts());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildSectionDescriptions());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildProjectInfo());
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Builds the header section with a title, subtitle, and a horizontal separator.
     *
     * @return a {@link JPanel} representing the page header
     */
    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("Help & Documentation");
        title.setFont(FONT_TITLE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Reference guide for Le Grand Bazard management system");
        subtitle.setFont(FONT_REG);
        subtitle.setForeground(COLOR_TEXT_DESC);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));

        panel.add(title);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(16));
        panel.add(sep);

        return panel;
    }

    /**
     * Builds the keyboard shortcuts section.
     * <p>Displays a non-editable {@link JTable} listing all menu accelerators grouped by menu name.
     *
     * @return a {@link JPanel} containing the shortcut table
     */
    private JPanel buildShortcuts() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(makeSectionTitle("Keyboard shortcuts"));
        panel.add(Box.createVerticalStrut(10));

        String[] columns = {" Menu", "Action", "Shortcut"};
        String[][] rows = {
                {" File", "Home", "Ctrl / Cmd + H"},
                {" File", "Quit", "Ctrl / Cmd + Q"},
                {" Management", "Client & Supplier", "Ctrl / Cmd + C"},
                {" Management", "Document", "Ctrl / Cmd + D"},
                {" Search", "Client", "Ctrl / Cmd + L"},
                {" Search", "Product", "Ctrl / Cmd + P"},
                {" Search", "Recipe", "Ctrl / Cmd + R"},
                {" Business tasks", "Sale & receipt", "Ctrl / Cmd + T"},
                {" Business tasks", "Restock alert", "Ctrl / Cmd + K"},
                {" Help", "Help", "F1"},
        };

        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.getTableHeader().setBackground(new Color(0xF5F5F5));
        table.setFocusable(false);
        table.setRowSelectionAllowed(false);

        int rowHeight = table.getRowHeight();
        int headerHeight = table.getTableHeader().getPreferredSize().height;
        int height = headerHeight + (rowHeight * rows.length) + 4;

        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        scroll.setPreferredSize(new Dimension(100, height));

        panel.add(scroll);
        return panel;
    }

    /**
     * Builds the section guide containing a description for each functional area of the application.
     * <p>Each entry is built by {@link #makeDescription(String, String)} and describes what the user can do in the corresponding section.
     *
     * @return a {@link JPanel} containing all section descriptions
     */
    private JPanel buildSectionDescriptions() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(makeSectionTitle("Section guide"));
        panel.add(Box.createVerticalStrut(10));

        String[][] sections = {
                {"Management > Client & Supplier", "Create, update and delete clients and suppliers. Each entry stores contact details, address, VAT number and fidelity card information."},
                {"Management > Document", "Manage all business documents: purchase orders sent to suppliers, delivery notes and preparation orders for the kitchen. Track planned and effective dates."},
                {"Search > Client", "Search across clients using their name, email address or fidelity card number. Results include address and fidelity point balance."},
                {"Search > Product", "Browse the product catalogue by name or category. Filter by active promotions to see discounts, required quantities and validity dates."},
                {"Search > Recipe", "Look up kitchen recipes by name or by one of their ingredient products. Results show the full composition and linked preparation orders."},
                {"Business tasks > Sale & receipt", "Process a checkout: scan products, identify the client, apply fidelity points and generate a receipt. Creates or updates the client's fidelity card."},
                {"Business tasks > Restock alert", "Displays products whose stock has fallen below the minimum threshold. Guides the stock manager through creating a supplier order and encoding the received batches into the stock."},
        };

        for (String[] entry : sections) {
            panel.add(makeDescription(entry[0], entry[1]));
            panel.add(Box.createVerticalStrut(8));
        }

        return panel;
    }

    /**
     * Builds the project information section.
     * <p>Displays static metadata about the project: institution, course, academic year, group, and student names.
     * Each row is built by {@link #labeled(String, String)}.
     *
     * @return a {@link JPanel} containing all project info rows
     */
    private JPanel buildProjectInfo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xDDDDDD));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(14));

        panel.add(makeSectionTitle("Project information"));
        panel.add(Box.createVerticalStrut(10));

        String[][] info = {
                {"Project", "Le Grand Bazard"},
                {"Students", "Clément Nelisse · Yannis Van Achter · Antoine Dieu"},
                {"Group", "DA · Block 2 · Group B"},
                {"Institution", "Henallux — Haute École de Namur-Liège-Luxembourg"},
                {"Unit", "IG252 UE Projet informatique intégré"},
                {"Course", "Programmation orientée objet avancée"},
                {"Academic year", "2024 — 2025"},
        };

        for (String[] row : info) {
            panel.add(labeled(row[0], row[1]));
            panel.add(Box.createVerticalStrut(4));
        }

        return panel;
    }


    /**
     * Creates a bold section title label aligned to the left.
     *
     * @param text the title text to display
     * @return a {@link JLabel} styled as a section heading
     */
    private JLabel makeSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SECTION);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * Creates a description entry composed of a bold section label and a wrapping description text area below it.
     * <p>A {@link JTextArea} is used instead of a {@link JLabel} to allow automatic line wrapping when the dialog is resized or narrower than the text content.
     *
     * @param label       the section name displayed as a bold heading
     * @param description the descriptive text displayed below the heading
     * @return a {@link JPanel} containing the label and description
     */
    private JPanel makeDescription(String label, String description) {
        JPanel entry = new JPanel();
        entry.setLayout(new BoxLayout(entry, BoxLayout.Y_AXIS));
        entry.setOpaque(false);
        entry.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(label);
        title.setFont(FONT_H3);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea desc = new JTextArea(description);
        desc.setFont(FONT_SMALL);
        desc.setForeground(COLOR_TEXT_DESC);
        desc.setOpaque(false);
        desc.setEditable(false);
        desc.setFocusable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        entry.add(title);
        entry.add(Box.createVerticalStrut(2));
        entry.add(desc);
        return entry;
    }

    /**
     * Creates a single key-value row for the project information section.
     * <p>The key is displayed in bold and the value in regular gray text, both on the same line using a left-aligned {@link FlowLayout}.
     *
     * @param key   the field name (e.g. {@code "Institution"})
     * @param value the field value (e.g. {@code "Henallux"})
     * @return a {@link JPanel} containing the key-value pair
     */
    private JPanel labeled(String key, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel keyLabel = new JLabel(key + ": ");
        keyLabel.setFont(FONT_H3);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(FONT_REG);
        valueLabel.setForeground(COLOR_TEXT_DESC);

        row.add(keyLabel);
        row.add(valueLabel);
        return row;
    }

    /**
     * Scrolls to the top of the documentation.
     * <p>This operation directly resets the vertical scroll bar position of the internal
     * {@link JScrollPane} without modifying the content or layout.
     * <p>Fix a bug of the central ScrollPane at opening.
     *
     * @see JScrollPane#getVerticalScrollBar()
     */
    public void scrollToTop() {
        scrollPane.getVerticalScrollBar().setValue(0);
    }
}