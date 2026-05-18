package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.ProductController;
import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * ReceiptView allow user to create a receipt my managing product selection
 * and receipt (shopping cart) workflow in the application.
 *
 * <p>This view provides a dual-pane interface with {@code JSplitPane}:
 * <ul><li>Left panel: product browsing and filtering</li>
 *     <li>Right panel: receipt/cart management and checkout actions</li></ul>
 *
 * <p>It interacts with {@link controller.ProductController} to retrieve available products
 * and maintains in local memory receipt structure using a {@link java.util.LinkedHashMap}
 * where {@code Key} is {@link Product}. {@code Vlue} is {@code Integer} representing purchased quantity. *
 * @see controller.ProductController
 * @see model.Product
 * @see ReceiptProductTableModel
 * @see ReceiptTableModel
 * @see ReceiptClientInfoDialog
 * @see java.util.LinkedHashMap
 */
public class ReceiptView extends JPanel {
    private static final Font FONT_REG = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_TOTAL = new Font("SansSerif", Font.BOLD, 18);

    private static final int TBL_BTN_INDEX = 2;

    private final MainWindow mainWindow;
    private final ProductController productController;
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

    public ReceiptView(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.productController = new ProductController();
        this.allProducts = productController.getAllProduct();
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
     * @return {@code JPanel} configured search panel
     */
    private JPanel buildSearchPanel() {
        searchPanel = new JPanel(new BorderLayout(8, 0));

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(0, 44));
        txtSearch = ViewUtils.addFilterListener(txtSearch, this::onFilterClick);

        JPanel btnPanel = new JPanel();
        btnClear = new JButton("Clear");
        btnClear.setPreferredSize(new Dimension(89, 44));
        btnClear.addActionListener(e -> onClearClick());
        btnPanel.add(btnClear);

        btnScan = new JButton("Scan");
        btnScan.setPreferredSize(new Dimension(89, 44));
        btnScan.addActionListener(e -> onScanClick());
        btnPanel.add(btnScan);

        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        return searchPanel;
    }

    /**
     * Builds the main body of the view using a horizontal {@code JSplitPane}.
     * <p>The split pane contains: {@link #buildLeftPanel()} product list
     * and {@link #buildRightPanel()} receipt summary
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
                if (productTable.columnAtPoint(e.getPoint()) == TBL_BTN_INDEX) {
                    addToReceipt(displayProducts.get(row));
                }
            }
        });

        productTable.getColumnModel().getColumn(TBL_BTN_INDEX).setCellRenderer(new ButtonRenderer());

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
     * @return footer {@code JPanel} with action controls
     */
    private JPanel buildReceiptFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        lblTotal = new JLabel("Total: 0.00€");
        lblTotal.setFont(FONT_TOTAL);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        footer.add(lblTotal, BorderLayout.NORTH);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnBar.setBorder(new EmptyBorder(16, 0, 0, 0));

        btnClearAll = new JButton("Clear all");
        btnClearAll.setPreferredSize(new Dimension(150, 44));
        btnClearAll.addActionListener(e -> clearAll());

        btnDelete = new JButton("Delete Last Item");
        btnDelete.setPreferredSize(new Dimension(150, 44));
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deleteSelected());

        btnNext = new JButton("Next");
        btnNext.setPreferredSize(new Dimension(88, 44));
        btnNext.setEnabled(false);
        btnNext.addActionListener(e -> {
            try {
                onNext();
            } catch (DataValidationException ex) {
                throw new RuntimeException(ex);
            }
        });

        btnBar.add(btnClearAll);
        btnBar.add(btnDelete);
        btnBar.add(btnNext);
        footer.add(btnBar, BorderLayout.SOUTH);
        return footer;
    }

    /**
     * Handles scanning of a product by ID using a {@code showInputDialog}.
     * <p>Validates an input format and searches for a matching product
     * in {@link #allProducts}. If found, the product is added to the receipt.
     */
    private void onScanClick() {
        String input = JOptionPane.showInputDialog("Enter the code of product");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        int productCode;
        try {
            productCode = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Code format invalide", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean found = false;
        int i = 0;
        while (i < allProducts.size() && !found) {
            Product product = allProducts.get(i);
            if (product.getId() == productCode) {
                addToReceipt(product);
                found = true;
            }
            i++;
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No product found", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Clears the product search input field.
     */
    private void onClearClick() {
        txtSearch.setText("");
    }

    /**
     * Filters displayed products based on user input in {@link #txtSearch}.
     * <p>This method is triggered on keyboard input and updates {@link #displayProducts} and refreshes the product table model.
     */
    private void onFilterClick() {
        String query = txtSearch.getText().trim().toLowerCase();
        displayProducts.clear();
        for (Product product : allProducts) {
            if (query.isBlank() || product.getName().toLowerCase().contains(query)) {
                displayProducts.add(product);
            }
        }
        productModel.setProducts(displayProducts);
    }

    /**
     * Adds a product to the receipt with stock validation.
     * <p>If the product already exists in the receipt, its quantity is increased. If stock limits are reached, the operation is rejected.
     * @param product product to add to receipt
     */
    private void addToReceipt(Product product) {
        int stock = product.getQuantity().getNbProduct();
        int alreadyInCart = receipt.getOrDefault(product, 0);
        if (alreadyInCart >= stock) {
            JOptionPane.showMessageDialog(this, "Insufficient stock for this product", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (receipt.containsKey(product)) {
            int currentQuantity = receipt.get(product);
            receipt.put(product, currentQuantity + 1);
        } else {
            receipt.put(product, 1);
        }
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

    private void onNext() throws DataValidationException {
        if (!receipt.isEmpty()) {
            openDialog();
        }
    }

    /**
     * Opens a modal dialog allowing the user to attach a client to the current receipt.
     */
    public void openDialog() throws DataValidationException {
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