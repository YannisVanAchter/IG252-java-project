package be.henallux.project.view;

import be.henallux.project.controller.StockManagementController;
import be.henallux.project.model.ClientSupplier;
import be.henallux.project.model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.*;
import java.util.List;

/**
 * StockAlertView displays supplier-based stock alerts and allows creation of purchase orders in a Swing interface.
 * <p>This view is split into two main areas using a {@link JSplitPane}:
 * <ul><li>a supplier selection panel (left side)</li>
 *   <li>a product restocking panel for the selected supplier (right side)</li></ul>
 * <p>The view is typically opened from the stock management workflow in {@link MainWindow} and acts as a coordination
 * screen between suppliers and low-stock products.
 * <p>When no stock alerts are available, an empty state message is displayed instead of the split layout.
 * <p>It interacts with:
 * <ul><li>{@link } to retrieve suppliers and their products</li>
 *   <li>{@link StockAlertTableModel} to manage selectable restocking items</li></ul>
 * <p>User interactions include selecting a supplier, selecting products to reorder, and triggering purchase order creation.
 *
 * @see StockAlertTableModel
 * @see ClientSupplier
 * @see Product
 * @see MainWindow
 */
public class StockAlertView extends JPanel {
    private static final Font FONT_REG = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 18);
    private static final Color SELECTED_BG = new Color(0xE6, 0xE6, 0xE6);
    private static final Color HOVER_BG = new Color(245, 245, 245);
    private static final Color BORDER = new Color(230, 230, 230);

    private final MainWindow mainWindow;
    private final StockManagementController stockManagementController;

    private ArrayList<ClientSupplier> suppliers;
    private ClientSupplier selectedSupplier;

    private JPanel supplierListPanel;
    private StockAlertTableModel tableModel;

    private JSplitPane splitPane;
    private JTable productTable;
    private JLabel lblSupplierTitle;
    private JButton btnOrder;
    private JCheckBox headerCheckBox;

    public StockAlertView(MainWindow mainWindow, StockManagementController stockManagementController) {
        this.mainWindow = mainWindow;
        this.stockManagementController = stockManagementController;
        this.suppliers = new ArrayList<>();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        stockManagementController.setOnDataUpdated(this::loadFromThread);
        loadFromThread();
    }

    /**
     * Loads data from the shared resource and updates the UI accordingly.
     * <p>If no alerts are available, an empty state panel is shown.
     * <p>Must be called on the EDT.
     */
    private void loadFromThread() {
        List<Map.Entry<Product, ClientSupplier>> alerts = stockManagementController.getSharedResource();

        LinkedHashMap<ClientSupplier, ArrayList<Product>> bySupplier = new LinkedHashMap<>();
        for (Map.Entry<Product, ClientSupplier> entry : alerts) {
            Product product = entry.getKey();
            ClientSupplier supplier = entry.getValue();

            ArrayList<Product> products = bySupplier.get(supplier);
            if (products == null) {
                products = new ArrayList<>();
                bySupplier.put(supplier, products);
            }
            products.add(product);
        }

        suppliers = new ArrayList<>(bySupplier.keySet());

        removeAll();

        if (suppliers.isEmpty()) {
            add(buildEmptyState(), BorderLayout.CENTER);
        } else {
            add(buildBody(), BorderLayout.CENTER);
            refreshSupplierList();
            selectSupplier(suppliers.getFirst());
        }

        revalidate();
        repaint();
    }

    /**
     * Builds the empty panel shown when no stock alerts are available.
     * <p>Displayed when {@link StockManagementController#getSharedResource()} returns an empty list,
     * because the agent has not run yet or because no products are below their minimum stock level.
     *
     * @return the empty {@code JPanel}
     */
    private JPanel buildEmptyState() {

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("✓");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 48));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("No stock alerts");
        title.setFont(FONT_TITLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("All products are sufficiently stocked.");
        subtitle.setFont(FONT_REG);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(icon);
        panel.add(Box.createVerticalStrut(10));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subtitle);
        panel.add(Box.createVerticalGlue());

        return panel;
    }
    /**
     * Builds the main split-pane layout of the view.
     * <p>The layout is divided into supplier navigation (left) and product selection (right).
     * <p>Only called when {@code suppliers} is non-empty.
     *
     * @return the configured {@link JSplitPane} containing the full view layout
     */
    private JSplitPane buildBody() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftPanel(), buildRightPanel());
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(0);
        splitPane.setBorder(null);
        return splitPane;
    }

    /**
     * Builds the left panel containing the list of suppliers.
     * <p>Each supplier is rendered as an interactive row that allows selection and refresh of the product list.
     *
     * @return the supplier list panel
     * @see #refreshSupplierList()
     */
    private JPanel buildLeftPanel() {
        JPanel suppliersPanel = new JPanel(new BorderLayout());
        suppliersPanel.setBackground(Color.WHITE);
        suppliersPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Suppliers");
        title.setFont(FONT_TITLE);
        title.setBorder(new EmptyBorder(0, 0, 12, 0));
        suppliersPanel.add(title, BorderLayout.NORTH);

        supplierListPanel = new JPanel();
        supplierListPanel.setLayout(new BoxLayout(supplierListPanel, BoxLayout.Y_AXIS));
        supplierListPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(supplierListPanel);
        scroll.setBorder(null);
        suppliersPanel.add(scroll, BorderLayout.CENTER);

        return suppliersPanel;
    }

    /**
     * Refreshes the supplier list displayed in the left panel.
     * <p>Each supplier row is rebuilt based on the current shared resource data.
     * @see StockManagementController#getSharedResource()
     */
    private void refreshSupplierList() {
        List<Map.Entry<Product, ClientSupplier>> alerts = stockManagementController.getSharedResource();

        LinkedHashMap<ClientSupplier, Integer> countBySupplier = new LinkedHashMap<>();
        for (Map.Entry<Product, ClientSupplier> entry : alerts) {
            ClientSupplier supplier = entry.getValue();
            Integer count = countBySupplier.get(supplier);
            if (count == null) {
                countBySupplier.put(supplier, 1);
            } else {
                countBySupplier.put(supplier, count + 1);
            }        }

        supplierListPanel.removeAll();
        for (ClientSupplier supplier : suppliers) {
            int count = countBySupplier.getOrDefault(supplier, 0);
            supplierListPanel.add(buildSupplierRow(supplier, count));
            supplierListPanel.add(Box.createVerticalStrut(8));
        }
        supplierListPanel.revalidate();
        supplierListPanel.repaint();
    }

    /**
     * Creates a clickable row representing a supplier.
     * The {@code mouseListener} listens on click and on hover.
     * <p>Click triggers {@link #selectSupplier(ClientSupplier)}.
     *
     * @param supplier     the supplier to display
     * @param productCount the number of products linked to the supplier
     * @return the supplier row component
     */
    private JPanel buildSupplierRow(ClientSupplier supplier, int productCount) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0),
                BorderFactory.createLineBorder(BORDER)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setPreferredSize(new Dimension(0, 50));

        JLabel lblName = new JLabel(supplier.getName());
        lblName.setFont(FONT_REG);
        lblName.setBorder(new EmptyBorder(0, 16, 0, 0));

        JLabel lblCount = new JLabel("(" + productCount + " product)");
        lblCount.setFont(FONT_REG);
        lblCount.setBorder(new EmptyBorder(0, 0, 0, 16));

        row.add(lblName, BorderLayout.WEST);
        row.add(lblCount, BorderLayout.EAST);

        boolean isSelected = supplier.equals(selectedSupplier);
        row.setBackground(isSelected ? SELECTED_BG : Color.WHITE);
        row.setOpaque(true);

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!supplier.equals(selectedSupplier)) {
                    row.setBackground(HOVER_BG);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(supplier.equals(selectedSupplier) ? SELECTED_BG : Color.WHITE);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                selectSupplier(supplier);
            }
        });
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return row;
    }

    /**
     * Builds the right panel containing the product restocking table for the selected supplier.
     * <p>This panel allows users to select products and define a purchase order based on stock alerts.
     * <p>It also includes selection controls such as a header checkbox for bulk selection.
     *
     * @return the product selection panel
     * @see StockAlertTableModel
     * @see RowColorRenderer
     * @see SpinnerEditor
     */
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);

        lblSupplierTitle = new JLabel("—");
        lblSupplierTitle.setFont(FONT_TITLE);

        JLabel lblSubtitle = new JLabel("Products to reorder");
        lblSubtitle.setFont(FONT_REG);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 4));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(lblSupplierTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);
        headerPanel.setBorder(new EmptyBorder(0, 16, 12, 0));
        panel.add(headerPanel, BorderLayout.NORTH);

        tableModel = new StockAlertTableModel(new ArrayList<>());
        tableModel.addTableModelListener(e -> updateButtonState());
        productTable = new JTable(tableModel);
        productTable.getColumnModel().getColumn(0).setMaxWidth(50);
        productTable.getColumnModel().getColumn(0).setMinWidth(50);
        productTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = productTable.columnAtPoint(e.getPoint());
                if (col == 0) {
                    productTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    productTable.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        headerCheckBox = new JCheckBox();
        ViewUtils.setCursor(headerPanel);
        headerCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        productTable.getColumnModel().getColumn(0).setHeaderRenderer(
                (table, value, isSelected, hasFocus, row, column) -> headerCheckBox
        );
        productTable.getTableHeader().setCursor(new Cursor(Cursor.HAND_CURSOR));
        productTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleCheckBox(e);
            }
        });

        productTable.setDefaultRenderer(Object.class, new RowColorRenderer(tableModel));

        JScrollPane scroll = new JScrollPane(productTable);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(buildButtonFooter(), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Builds the footer containing the purchase order creation button.
     * <p>The button is enabled only when at least one product is selected in the table model.
     *
     * @return the footer panel containing the order button
     */
    private JPanel buildButtonFooter() {
        btnOrder = new JButton("Create purchase order");
        btnOrder.addActionListener(e -> onCreateOrder());
        btnOrder.setPreferredSize(new Dimension(150, 44));
        btnOrder.setOpaque(false);
        btnOrder.setEnabled(false);

        ViewUtils.setCursor(btnOrder);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnBar.setBorder(new EmptyBorder(10, 10, 10, 10));
        btnBar.add(btnOrder);

        return btnBar;
    }

    /**
     * Toggles the state of the header checkbox.
     * <p>All table rows are selected or unselected depending on the current state.
     *
     * @param e the mouse event triggered on the table header
     */
    private void toggleCheckBox(MouseEvent e) {
        int column = productTable.columnAtPoint(e.getPoint());
        if (column == 0) {
            tableModel.toggleAll();
            boolean allSelected = tableModel.getSelectedProducts().size() == tableModel.getRowCount();
            headerCheckBox.setSelected(allSelected);
            productTable.getTableHeader().repaint();
            updateButtonState();
        }
    }

    /**
     * Selects a supplier and refreshes the product table.
     *
     * @param supplier the selected supplier
     */
    private void selectSupplier(ClientSupplier supplier) {
        selectedSupplier = supplier;
        lblSupplierTitle.setText(supplier.getName() + " " + supplier.getFirstname());
        headerCheckBox.setSelected(true);
        ArrayList<Product> products = new ArrayList<>();
        for (Map.Entry<Product, ClientSupplier> entry : stockManagementController.getSharedResource()) {
            if (entry.getValue().equals(supplier)) {
                products.add(entry.getKey());
            }
        }
        tableModel.setProducts(products);
        updateButtonState();
        refreshSupplierList();
    }

    /**
     * Creates a purchase order from selected products.
     * <p>If no product is selected, a warning dialog is displayed.
     *
     * @see MainWindow#openOrderView(ArrayList, ClientSupplier)
     * @see StockOrderCreation
     */
    private void onCreateOrder() {
        ArrayList<Product> selectedProducts = tableModel.getSelectedProducts();
        if (selectedProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products selected.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mainWindow.openOrderView(selectedProducts, selectedSupplier);
    }

    /**
     * Updates the state of the order button based on product selection.
     * <p>The button is enabled only when at least one product is selected in the table model.
     */
    private void updateButtonState() {
        btnOrder.setEnabled(!tableModel.getSelectedProducts().isEmpty());
    }
}
