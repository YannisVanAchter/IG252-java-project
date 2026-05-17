package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.ProductController;
import main.java.be.henallux.project.controller.SupplierController;
import main.java.be.henallux.project.exception.DataValidationException;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * View displaying stock alerts grouped by supplier.
 * <p>This panel allows the user to:
 * <ul><li>browse suppliers</li><li>view products requiring restocking</li><li>select products to order</li><li>create a purchase order</li></ul>
 * <p>The view is divided into two sections using a {@link JSplitPane}:
 * <ul><li>the supplier list</li><li>the product reorder table</li></ul>
 *
 * @see StockAlertTableModel
 * @see JTable
 * @see ClientSupplier
 * @see Product
 */
public class StockAlertView extends JPanel {
    private static final Font FONT_REG = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 18);
    private static final Color SELECTED_BG = new Color(0xE6, 0xE6, 0xE6);
    private static final Color HOVER_BG = new Color(245, 245, 245);
    private static final Color BORDER = new Color(230,230,230);

    private final MainWindow mainWindow;
    private final SupplierController supplierController;
    private final ProductController productController;

    private ArrayList<ClientSupplier> suppliers;
    private ClientSupplier selectedSupplier;

    private JPanel supplierListPanel;
    private StockAlertTableModel tableModel;

    private JSplitPane split;
    private JTable productTable;
    private JLabel lblSupplierTitle;
    private JButton btnOrder;
    private JCheckBox headerCheckBox;

    public StockAlertView(MainWindow mainWindow) throws DataValidationException {
        this.mainWindow = mainWindow;
        this.supplierController = new SupplierController();
        this.productController = new ProductController();
        this.suppliers = supplierController.getAllSuppliers();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildBody(), BorderLayout.CENTER);

        if (!suppliers.isEmpty()) {
            selectSupplier(suppliers.get(0));
        }
    }

    /**
     * Builds the main split layout.
     * @return the main split pane
     */
    private JSplitPane buildBody() {
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(300);
        split.setResizeWeight(0.5);
        split.setDividerSize(0);
        split.setBorder(null);
        return split;
    }

    /**
     * Builds the left panel containing a list of supplier.
     * @return the supplier panel
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

        refreshSupplierList();
        return suppliersPanel;
    }

    /**
     * Refreshes the supplier list displayed on screen.
     * <p>A visual row is created for each supplier.
     */
    private void refreshSupplierList() {
        supplierListPanel.removeAll();
        for (ClientSupplier s : suppliers) {
            int count = supplierController.getAllProduct(s.getId()).size();            supplierListPanel.add(buildSupplierRow(s, count));
            supplierListPanel.add(Box.createVerticalStrut(8));
        }
        supplierListPanel.revalidate();
        supplierListPanel.repaint();
    }

    /**
     * Creates a clickable row representing a supplier.
     * The {@code mouseListener} lister on click and on hover
     * <p>Click trigger {@link #selectSupplier(ClientSupplier)}
     * @param supplier the supplier to display
     * @param productCount the number of products linked to the supplier
     * @return the supplier row component
     */
    private JPanel buildSupplierRow(ClientSupplier supplier, int productCount) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0,0,0,0),
                BorderFactory.createLineBorder(BORDER)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setPreferredSize(new Dimension(0, 50));

        JLabel lblName = new JLabel(supplier.getName() + " " + supplier.getFirstname());
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
                row.setBackground(
                        supplier.equals(selectedSupplier) ? SELECTED_BG : Color.WHITE
                );
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
     * Builds the right panel containing reorderable products for the selected supplier.
     * <p>A {@code CheckBox} handle the selection of product to reorder.
     * <p>Handle color with the status of the product stock.
     * @see SpinnerEditor
     * @see RowColorRenderer
     * @return the product panel
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
        productTable = new JTable(tableModel);
        productTable.getColumnModel().getColumn(0).setMaxWidth(50);
        productTable.getColumnModel().getColumn(0).setMinWidth(50);

        headerCheckBox = new JCheckBox();
        headerCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        productTable.getColumnModel().getColumn(0).setHeaderRenderer(
                (table, value, isSelected, hasFocus, row, column) -> headerCheckBox
        );
        productTable.getTableHeader().setCursor(new Cursor(Cursor.HAND_CURSOR));
        productTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toogleCheckBox(e);
            }
        });

        productTable.setDefaultRenderer(Object.class,
                new RowColorRenderer(tableModel)
        );

        JScrollPane scroll = new JScrollPane(productTable);
        panel.add(scroll, BorderLayout.CENTER);


        btnOrder = new JButton("Create purchase order");
        btnOrder.addActionListener(e -> onCreateOrder());
        btnOrder.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnBar.setOpaque(false);
        btnBar.add(btnOrder);
        panel.add(btnBar, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Toggles the state of the header checkbox.
     * <p>All table rows are selected or unselected depending on the current state.
     * @param e the mouse event triggered on the table header
     */
    private void toogleCheckBox(MouseEvent e) {
        int column = productTable.columnAtPoint(e.getPoint());
        if (column == 0) {
            tableModel.toggleAll();
            boolean allSelected = tableModel.getSelectedProducts().size() == tableModel.getRowCount();
            headerCheckBox.setSelected(allSelected);
            productTable.getTableHeader().repaint();
        }
    }

    /**
     * Selects a supplier and refreshes the product table.
     * @param supplier the selected supplier
     */
    private void selectSupplier(ClientSupplier supplier) {
        selectedSupplier = supplier;
        lblSupplierTitle.setText(supplier.getName() + " " + supplier.getFirstname());
        headerCheckBox.setSelected(true);
        ArrayList<Product> products = supplierController.getAllProduct(supplier.getId());
        tableModel.setProducts(products);
        refreshSupplierList();
    }

    /**
     * Creates a purchase order from selected products.
     * <p>If no product is selected, a warning dialog is displayed.
     * @see MainWindow#openOrderView(ArrayList, ClientSupplier)
     * @see StockOrderCreation
     */
    private void onCreateOrder() {
        ArrayList<Product> selectedProducts = tableModel.getSelectedProducts();
        if (selectedProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products selectedProducts.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mainWindow.openOrderView(selectedProducts, selectedSupplier);
    }

}
