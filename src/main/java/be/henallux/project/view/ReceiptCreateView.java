package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.ProductController;
import main.java.be.henallux.project.controller.ProductSearchController;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.ProductCategory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.*;
import java.util.List;

/**
 * ReceiptCreateView allow user to create a receipt my managing product selection
 * and receipt (shopping cart) workflow in the application.
 *
 * <p>This view provides a dual-pane interface with {@code JSplitPane}:
 * <ul><li>Left panel: product browsing and filtering</li>
 *     <li>Right panel: receipt/cart management and checkout actions</li></ul>
 *
 * <p>It interacts with {@link ProductController} to retrieve available products
 * and maintains in local memory receipt structure using a {@link java.util.LinkedHashMap}
 * where {@code Key} is {@link Product}. {@code Value} is {@code Integer} representing purchased quantity.
 *
 * @see ProductController
 * @see Product
 * @see ReceiptProductTableModel
 * @see ReceiptTableModel
 * @see ReceiptClientInfoDialog
 * @see java.util.LinkedHashMap
 */
public class ReceiptCreateView extends JPanel {
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_TOTAL = new Font("SansSerif", Font.BOLD, 18);

    private final MainWindow mainWindow;
    private final ProductController productController;
    private final ProductSearchController productSearchController;
    private ReceiptProductTableModel productModel;
    private ReceiptTableModel receiptModel;
    private ArrayList<Product> allProducts;
    private ArrayList<Product> displayProducts;
    private LinkedHashMap<Product, Integer> receipt = new LinkedHashMap<>();

    private JPanel searchPanel;
    private JSplitPane split;
    private JTextField txtSearch;
    private JButton btnScan, btnClear, btnClearAll, btnNext, btnDelete;
    private JTable productTable, receiptTable;
    private JLabel lblTotal;

    public ReceiptCreateView(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.productController = new ProductController();
        this.productSearchController = new ProductSearchController();

        ArrayList<Product> loaded = new ArrayList<>();
        try {
            loaded = productController.getAllProduct();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            mainWindow.goBack();
        }
        this.allProducts = loaded;
        this.displayProducts = new ArrayList<>(allProducts);

        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildSearchPanel(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    /**
     * Builds the search panel used to filter products by name or scan product codes.
     * <p>Includes:
     * <ul><li>Text field for live filtering</li>
     *     <li>Clear button to reset search input</li>
     *     <li>Scan button to manually enter product ID</li></ul>
     *
     * @return {@code JPanel} configured search panel
     */
    private JPanel buildSearchPanel() {
        searchPanel = new JPanel(new BorderLayout(8, 0));

        JPanel search = ViewUtils.createColumnPanel();

        JLabel label = new JLabel("Search for a product by name or scan the barcode");

        txtSearch = new JTextField();
        ViewUtils.setCursor(txtSearch);
        txtSearch.setPreferredSize(new Dimension(250, 20));
        txtSearch = ViewUtils.addFilterListener(txtSearch, this::onFilterClick);

        search.add(label);
        search.add(txtSearch);

        JPanel btnPanel = new JPanel();

        btnClear = new JButton("Clear");
        ViewUtils.setCursor(btnClear);
        btnClear.setPreferredSize(new Dimension(89, 44));
        btnClear.addActionListener(e -> onClearClick());
        btnPanel.add(btnClear);

        btnScan = new JButton("Scan");
        ViewUtils.setCursor(btnScan);
        btnScan.setPreferredSize(new Dimension(89, 44));
        btnScan.addActionListener(e -> onScanClick());
        btnPanel.add(btnScan);

        searchPanel.add(search, BorderLayout.CENTER);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        return searchPanel;
    }

    /**
     * Builds the main body of the view using a horizontal {@code JSplitPane}.
     * <p>The split pane contains: {@link #buildLeftPanel()} product list
     * and {@link #buildRightPanel()} receipt summary
     *
     * @return {@code JSplitPane} configured
     */
    private JSplitPane buildBody() {
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftPanel(), buildRightPanel());
        split.setResizeWeight(0.5);
        split.setDividerSize(0);
        split.setBorder(null);
        return split;
    }

    /**
     * Builds the left panel displaying available products.
     * Features:
     * <ul><li>Product table with selectable rows</li>
     *     <li>Button column for adding products to receipt</li>
     *     <li>Live filtering via search input</li></ul>
     *
     * @return product browsing {@code JPanel}
     */
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);

        JLabel title = new JLabel("Product");
        title.setFont(FONT_TITLE);
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(8, 16, 2, 16));
        header.add(title);
        header.setBorder(new EmptyBorder(8, 16, 2, 16));
        panel.add(header, BorderLayout.NORTH);

        productModel = new ReceiptProductTableModel(displayProducts);
        productTable = new JTable(productModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = productTable.rowAtPoint(e.getPoint());
                if (productTable.columnAtPoint(e.getPoint()) == ReceiptProductTableModel.TBL_BTN_ADD) {
                    addToReceipt(displayProducts.get(row));
                }
            }
        });
        productTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = productTable.columnAtPoint(e.getPoint());

                if (col == ReceiptProductTableModel.TBL_BTN_ADD) {
                    productTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    productTable.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        productTable.getColumnModel().getColumn(ReceiptProductTableModel.TBL_BTN_ADD).setCellRenderer(new ButtonRenderer());

        JScrollPane scroll = new JScrollPane(productTable);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Builds the right panel displaying the receipt contents.
     * Responsibilities:
     * <ul><li>Display selected products and quantities</li>
     *     <li>Allow item removal</li>
     *     <li>Show computed total price</li></ul>
     *
     * @return receipt {@code JPanel}
     */
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);

        JLabel title = new JLabel("Receipt");
        title.setFont(FONT_TITLE);
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(8, 16, 2, 16));
        header.add(title);
        panel.add(header, BorderLayout.NORTH);

        receiptModel = new ReceiptTableModel(receipt);
        receiptTable = new JTable(receiptModel);
        receiptTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        receiptTable.getSelectionModel().addListSelectionListener(e -> updateDeleteButtonText());
        ViewUtils.resizeColumnWidth(receiptTable);
        receiptTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                receiptTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });

        JScrollPane scroll = new JScrollPane(receiptTable);
        panel.add(scroll, BorderLayout.CENTER);

        panel.add(buildReceiptFooter(), BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Builds the footer section of the receipt panel.
     * <p>Contains action buttons:
     * <ul><li>Clear all items</li>
     *     <li>Delete the selected or last item</li>
     *     <li>Proceed to the next checkout step</li></ul>
     *
     * @return footer {@code JPanel} with action controls
     */
    private JPanel buildReceiptFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Total: 0.00€ ");
        lblTotal.setFont(FONT_TOTAL);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        footer.add(lblTotal, BorderLayout.NORTH);

        JPanel btnBar = new JPanel(new BorderLayout());
        btnBar.setBorder(new EmptyBorder(16, 0, 0, 0));

        btnClearAll = new JButton("Clear all");
        ViewUtils.setCursor(btnClearAll);
        btnClearAll.setPreferredSize(new Dimension(150, 44));
        btnClearAll.setEnabled(false);
        btnClearAll.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to clear all items?",
                    "Clear Receipt",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                clearAll();
            }
        });

        btnDelete = new JButton("Delete Last Item");
        ViewUtils.setCursor(btnDelete);
        btnDelete.setPreferredSize(new Dimension(150, 44));
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deleteSelected());

        btnNext = new JButton("Next");
        ViewUtils.setCursor(btnDelete);
        btnNext.setPreferredSize(new Dimension(88, 44));
        btnNext.setEnabled(false);
        btnNext.addActionListener(e -> {
                onNext();
        });

        JPanel leftBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftBtnPanel.add(btnClearAll);

        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBtnPanel.add(btnDelete);
        rightBtnPanel.add(btnNext);

        btnBar.add(leftBtnPanel, BorderLayout.WEST);
        btnBar.add(rightBtnPanel, BorderLayout.EAST);
        footer.add(btnBar, BorderLayout.SOUTH);
        return footer;
    }

    /**
     * Handles scanning of a product by ID using a {@code showInputDialog}.
     * <p>Validates the input format and delegates the product lookup to {@link ProductController#getProduct(int)}.
     * <p>If a matching product is found, it is added to the receipt via {@link #addToReceipt(Product)}.
     * Otherwise, an informational message is displayed.
     *
     * @see ProductController#getProduct(int)
     * @see #addToReceipt(Product)
     */
    private void onScanClick() {
        String input = JOptionPane.showInputDialog("Enter the code of product");
        if (input == null || input.trim().isEmpty()) return;

        int productCode;
        try {
            productCode = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Code format invalide", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Product product = productController.getProduct(productCode);
            if (product != null) {
                addToReceipt(product);
            } else {
                JOptionPane.showMessageDialog(this, "No product found", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }    /**
     * Clears the product search input field.
     */
    private void onClearClick() {
        txtSearch.setText("");
    }

    /**
     * Filters displayed products based on user input in {@link #txtSearch}.
     * <p>This method is triggered on keyboard input and delegates the search to {@link ProductSearchController#searchProducts(String, ProductCategory, Boolean)}.
     * <p>Results are applied to {@link #displayProducts} and the product table model is refreshed.
     *
     * @see ProductSearchController#searchProducts(String, ProductCategory, Boolean)
     */
    private void onFilterClick() {
        String txtQuery = txtSearch.getText().trim();
        try {
            displayProducts = productSearchController.searchProducts(txtQuery, null, false);
            productModel.setProducts(displayProducts);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adds a product to the receipt with stock validation.
     * <p>If the product already exists in the receipt, its quantity is increased.
     * If the quantity already in the cart reaches the total available stock, the operation is rejected.
     *
     * @param product product to add to receipt
     */
    private void addToReceipt(Product product) {
        int stock = product.getNonStockQuantity();
        int alreadyInCart = receipt.getOrDefault(product, 0);
        if (alreadyInCart >= stock) {
            JOptionPane.showMessageDialog(this, "Insufficient stock for this product", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        receipt.put(product, alreadyInCart + 1);
        receiptModel.setProducts(receipt);
        updateButtonStates();
        refreshTotal();
    }

    /**
     * Updates the {@code enabled}/{@code disabled} state of action buttons depending on whether the receipt contains items.
     */
    private void updateButtonStates() {
        boolean hasItems = !receipt.isEmpty();
        btnDelete.setEnabled(hasItems);
        btnNext.setEnabled(hasItems);
        btnClearAll.setEnabled(hasItems);
    }

    /**
     * Updates the delete button label depending on the selection state.
     * <p>If a row is selected, deletion applies to that item. Otherwise, the last inserted item is targeted.
     */
    private void updateDeleteButtonText() {
        if (receiptTable.getSelectedRow() >= 0) {
            btnDelete.setText("Delete Selected Item");
        } else {
            btnDelete.setText("Delete Last Item");
        }
    }

    /**
     * Recalculates and updates the total price displayed in {@link #lblTotal}.
     */
    private void refreshTotal() {
        double total = 0.0;
        for (Product product : receipt.keySet()) {
            total += product.getPrice() * receipt.get(product);
        }
        lblTotal.setText(String.format("Total: %.2f €", total));
    }

    /**
     * Removes an item from the receipt.
     * <ul>
     *     <li>If a row is selected: remove the selected product</li>
     *     <li>If no selection: remove the last inserted product</li>
     * </ul>
     */
    private void deleteSelected() {
        int row = receiptTable.getSelectedRow();
        List<Product> keys = new ArrayList<>(receipt.keySet());
        if (row < 0) {
            if (!keys.isEmpty()) {
                receipt.remove(keys.getLast());
            }
        } else {
            receipt.remove(keys.get(row));
        }
        receiptModel.setProducts(receipt);
        updateButtonStates();
        refreshTotal();
    }

    private void onNext() {
        if (!receipt.isEmpty()) {
            openDialog();
        }
    }

    /**
     * Opens a modal dialog allowing the user to attach a client to the current receipt.
     */
    public void openDialog() {
        JDialog dialog = new JDialog(mainWindow, "Add new Client", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ReceiptClientInfoDialog form = new ReceiptClientInfoDialog(mainWindow, this, receipt);
        dialog.setContentPane(form);

        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }

    /**
     * Clears the entire receipt and resets the UI state.
     */
    public void clearAll() {
        receipt.clear();
        receiptModel.setProducts(receipt);
        refreshTotal();
        updateButtonStates();
        receiptTable.clearSelection();
        updateDeleteButtonText();
    }
}