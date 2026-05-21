package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

/**
 * View used to create and confirm a purchase order.
 * <p>This panel displays:
 * <ul><li>supplier information</li><li>products to order</li>
 * <li>editable ordered quantities</li><li>the total quantity ordered</li></ul>
 * <p>Ordered quantities can be modified directly inside the table using a spinner editor.
 * <p>The view allows the user to:
 * <ul><li>review supplier details</li><li>adjust ordered quantities</li>
 * <li>cancel the order creation</li><li>confirm the purchase order</li></ul>
 *
 * @see StockOrderTableModel
 * @see SpinnerEditor
 * @see JTable
 * @see ClientSupplier
 */
public class StockOrderCreation extends JPanel {
    private static final String LABEL_NO_DATA = "N/A";

    private static final Font FONT_COMMENT = new Font("SansSerif", Font.ITALIC, 12);
    private static final Font FONT_REG = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 18);

    private final MainWindow mainWindow;
    private StockOrderTableModel model;
    private JLabel lblSupplier;
    private JLabel lblTotal;

    private JTable table;

    private ArrayList selectedProduct;
    private ClientSupplier selectedSupplier;

    private boolean hasChanges = false;

    public StockOrderCreation(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    /**
     * Builds the complete page content.
     * <p>The layout contains:
     * <ul><li>the title section</li><li>supplier information</li>
     * <li>the editable product table</li><li>the total quantity panel</li>
     * <li>action buttons</li></ul>
     *
     * @return the assembled content panel
     */
    private JPanel buildContent() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(0, 0, 8, 0));
        center.add(buildSupplier());
        center.add(Box.createVerticalStrut(16));
        center.add(buildTable());
        center.add(Box.createVerticalStrut(16));
        center.add(buildTotal());

        JScrollPane pane = new JScrollPane(center);
        pane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(pane, BorderLayout.NORTH);

        add(buildTitle(), BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads a purchase order into the view.
     * <p>The current content is rebuilt using the selected supplier and products.
     * <p>If no product is provided, a warning dialog is displayed and the previous
     * screen is restored.
     *
     * @param seletedProduct   the selected products
     * @param selectedSupplier the supplier linked to the order
     */
    public void loadOrder(ArrayList seletedProduct, ClientSupplier selectedSupplier) {
        this.selectedProduct = seletedProduct;
        this.selectedSupplier = selectedSupplier;
        hasChanges = false;

        if (this.selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Please select product.");
            mainWindow.goBack();
            return;
        }
        removeAll();
        buildContent();
        revalidate();
        repaint();
    }

    /**
     * Builds the page title section.
     *
     * @return the title panel
     */
    private JPanel buildTitle() {
        JLabel title = new JLabel("Create a Purchase Order");
        title.setFont(FONT_TITLE);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(title, BorderLayout.WEST);
        return panel;
    }


    /**
     * Builds the supplier information card.
     * <p>The card displays:
     * <ul><li>supplier name</li><li>email</li><li>phone number</li>
     * <li>VAT number</li><li>postal address</li></ul>
     *
     * @return the supplier information panel
     * @see Address
     */
    private JPanel buildSupplier() {
        JPanel card = createCard("Supplier information");

        lblSupplier = new JLabel(selectedSupplier.getName());
        lblSupplier.setFont(FONT_BOLD);
        card.add(labelValue(lblSupplier, ""));
        card.add(Box.createVerticalStrut(10));
        card.add(labelValue("Email", selectedSupplier.getEmail()));
        card.add(Box.createVerticalStrut(10));
        card.add(labelValue("Phone", selectedSupplier.getPhoneNumber()));
        card.add(Box.createVerticalStrut(10));
        card.add(labelValue("VAT", selectedSupplier.getVATNumber()));
        Address address = selectedSupplier.getAddress();
        String adresseTxt = address != null ? address.getLabel() : LABEL_NO_DATA;
        card.add(Box.createVerticalStrut(10));
        card.add(labelValue("Adresse", adresseTxt));

        return card;
    }

    /**
     * Builds the editable product table.
     * <p>The quantity column uses a {@link SpinnerEditor} editor to allow quantity
     * adjustments directly inside the table.
     * <p>The total quantity label is automatically refreshed whenever table data changes.
     *
     * @return the table container panel
     * @see StockOrderTableModel
     * @see SpinnerEditor
     */
    private JPanel buildTable() {
        JPanel card = createCard("Items ordered");
        model = new StockOrderTableModel(selectedProduct);
        table = new JTable(model);
        table.setRowHeight(28);
        table.getColumnModel().getColumn(StockOrderTableModel.TBL_SPN_INDEX).setCellEditor(new SpinnerEditor());
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (col == StockOrderTableModel.TBL_SPN_INDEX) {
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                } else {
                    table.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        JLabel hint = new JLabel("✏ Click on 'Quantity Ordered' to edit");
        hint.setFont(FONT_COMMENT);
        hint.setForeground(Color.GRAY);
        JPanel hintPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hintPanel.setOpaque(false);
        hintPanel.add(hint);
        card.add(hintPanel);

        model.addTableModelListener(e -> {
            updateTotal();
            hasChanges = true;
        });
        int rowCount = Math.max(3, model.getRowCount());
        int tableHeight = Math.min(rowCount * table.getRowHeight() + table.getTableHeader().getPreferredSize().height + 4, 200);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, tableHeight));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, tableHeight));

        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    /**
     * Builds the total quantity panel.
     * <p>The displayed value is synchronized with {@link StockOrderTableModel#getTotal()}.
     *
     * @return the total panel
     */
    private JPanel buildTotal() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245, 245, 245));
        p.setBorder(new EmptyBorder(12, 16, 12, 16));

        lblTotal = new JLabel();
        lblTotal.setFont(FONT_REG);

        updateTotal();

        p.add(lblTotal, BorderLayout.WEST);
        return p;
    }

    /**
     * Updates the displayed total quantity.
     * <p>The total is computed using {@link StockOrderTableModel#getTotal()}.
     */
    private void updateTotal() {
        lblTotal.setText("Total Items: " + model.getTotal());
    }

    /**
     * Builds the action button bar.
     * <p>Available actions:
     * <ul><li>cancel the order creation</li><li>confirm the purchase order</li></ul>
     *
     * @return the button panel
     */
    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        p.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        ViewUtils.setCursor(btnCancel);
        btnCancel.addActionListener(e -> onCancelClick());
        JButton btnConfirm = new JButton("Confirm");
        ViewUtils.setCursor(btnConfirm);
        btnConfirm.addActionListener(e -> onConfirmClick());

        p.add(btnCancel);
        p.add(btnConfirm);

        return p;
    }

    /**
     * Handles the cancel action.
     * <p> A confirmation dialog box is displayed before leaving the page if any changes have been made.
     * <p>If confirmed, the application navigates back to the previous view.
     *
     * @see MainWindow#goBack()
     */
    private void onCancelClick() {
        if (!hasChanges) {
            mainWindow.goBack();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel? All changes will be lost.",
                "Cancel Order",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            mainWindow.goBack();
        }
    }

    /**
     * Handles the order confirmation action.
     * <p>Current table editing is stopped before validation.
     * <p>The order cannot be confirmed if the total quantity equals {@code 0}.
     * <p>A confirmation dialog is displayed before final validation.
     */
    private void onConfirmClick() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        if (model.getTotal() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please order at least one item before confirming.",
                    "Empty Order",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirm purchase order for " + model.getTotal() + " item(s) from "
                        + selectedSupplier.getName() + "?",
                "Confirm Order",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                    this,
                    "",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            mainWindow.setPage("STOCK");
        }
    }


    /**
     * Creates a bordered card container with a title.
     *
     * @param title the title of the section
     * @return a styled panel
     */
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createTitledBorder(title));
        return card;
    }

    /**
     * Creates a key-value label row.
     *
     * @param label the field name
     * @param value the field value
     * @return a horizontal panel displaying the label and value
     */
    private JPanel labelValue(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(new JLabel(label + " : "));
        if (value != null) {
            panel.add(new JLabel(value));
        } else {
            panel.add(new JLabel(LABEL_NO_DATA));
        }        return panel;
    }

    private JPanel labelValue(JLabel label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(label);
        if (value != null) {
            panel.add(new JLabel(value));
        } else {
            panel.add(new JLabel(LABEL_NO_DATA));
        }
        return panel;
    }
}