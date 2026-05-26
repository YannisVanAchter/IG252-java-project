package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.DocumentController;
import main.java.be.henallux.project.controller.ProductController;
import main.java.be.henallux.project.controller.WorkFlowController;
import main.java.be.henallux.project.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
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
    private final WorkFlowController workFlowController;
    private final DocumentController documentController;
    private final ProductController productController;
    private StockOrderTableModel model;
    private JLabel lblSupplier;
    private JLabel lblTotal;

    private JTable table;

    private ArrayList<Product> selectedProduct;
    private ClientSupplier selectedSupplier;

    private boolean hasChanges = false;

    public StockOrderCreation(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.workFlowController = new WorkFlowController();
        this.documentController = new DocumentController();
        this.productController = new ProductController();
        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    /**
     * Builds the complete page content.
     * <p>The layout contains:
     * <ul><li>the title section</li><li>supplier information</li>
     * <li>the editable product table</li><li>the total quantity panel</li>
     * <li>action buttons</li></ul>
     */
    private void buildContent() {
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
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(buildTitle(), BorderLayout.NORTH);
        add(pane, BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    /**
     * Loads a purchase order into the view.
     * <p>The current content is rebuilt using the selected supplier and products.
     * <p>If no product is provided, a warning dialog is displayed and the previous
     * screen is restored.
     *
     * @param selectedProduct  the selected products
     * @param selectedSupplier the supplier linked to the order
     */
    public void loadOrder(ArrayList<Product> selectedProduct, ClientSupplier selectedSupplier) {
        this.selectedProduct = selectedProduct;
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
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


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
     * <p>Before validation, any active cell editor is stopped to ensure spinner values are committed.
     * <p>The order cannot be confirmed if the total quantity equals {@code 0}.
     * <p>A confirmation dialog is displayed before final validation.
     * <p>On confirmation, the following steps are :
     * <ol><li>Create a buying {@link WorkFlow} with status {@code Pending} linked to the selected supplier</li>
     *     <li>Create a {@link Document} of type "Purchase Order" with delivery details</li>
     *     <li>Send the order via {@link DocumentController#sendDelivery(int, LocalDate, LocationProduct)}</li>
     *     <li>Register the reception via {@link DocumentController#receiveDelivery(int, LocalDate, LocationProduct)}</li>
     *     <li>Add ordered quantities to the back stock ({@link LocationProduct#getIsStock()} = {@code true})
     *         for each product with a quantity greater than 0</li>
     *     <li>Update the workflow status to {@code Delivered}</li>
     *     <li>Schedule a success notification via {@link main.java.be.henallux.project.controller.NotificationController}
     *         displayed 5 seconds after navigation</li></ol>
     * <p>If any step fails, the entire operation is interrupted and an error dialog is displayed.
     * <p>On success, the application navigates back to {@code "STOCK"} page.
     *
     * @see WorkFlowController#addWorkFlow(WorkFlow)
     * @see WorkFlowController#changeStatus(int, Status)
     * @see DocumentController#createDocument
     * @see DocumentController#sendDelivery(int, LocalDate, LocationProduct)
     * @see DocumentController#receiveDelivery(int, LocalDate, LocationProduct)
     * @see ProductController#addToStocks(int, int, LocationProduct)
     */
    private void onConfirmClick() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        if (model.getTotal() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please order at least one item before confirming.",
                    "Empty Order", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm purchase order for " + model.getTotal() + " item(s) from "
                        + selectedSupplier.getName() + "?",
                "Confirm Order", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            ArrayList<WorkFlowType> allBuyType = workFlowController.getAllBuying();
            WorkFlowType buyType = null;
            for (WorkFlowType workFlowType : allBuyType) {
                if (workFlowType.getIsBuy()) {
                    buyType = workFlowType;
                }
            }
            if (buyType == null) {
                throw new Exception("Buying workflow type not found.");
            }
            Status pending = new Status("Pending");
            ClientSupplier us = null; // FIXME: get or create
            WorkFlow workflow = new WorkFlow(pending, buyType, us, selectedSupplier);
            workFlowController.addWorkFlow(workflow);

            Address supplierAddress = selectedSupplier.getAddress();
            DocumentType documentType = new DocumentType("Purchase Order");

            Document newDoc = new Document(
                    LocalDate.now(),
                    documentType,
                    true,
                    LocalDate.now(), null,
                    LocalDate.now(), null,
                    30,
                    workflow,
                    supplierAddress,
                    "Purchase order for " + model.getTotal() + " item(s) from " + selectedSupplier.getName()
            );

            Document createdDoc = documentController.createDocument(newDoc);
            WorkFlow createdWorkflow = workFlowController.addWorkFlow(workflow);
            workFlowController.addDocument(createdWorkflow.getId(), createdDoc);
            if (createdDoc == null) {
                throw new Exception("Failed to create document.");
            }

            LocationProduct stockLocation = productController.getOrCreateStockLocation(selectedProduct);
            documentController.sendDelivery(newDoc.getId(), LocalDate.now(), stockLocation);
            documentController.receiveDelivery(newDoc.getId(), LocalDate.now(), stockLocation);

            for (int i = 0; i < model.getRowCount(); i++) {
                Product product = selectedProduct.get(i);
                int quantity = (int) model.getValueAt(i, StockOrderTableModel.TBL_SPN_INDEX);

                if (quantity > 0) {
                    boolean added = false;
                    for (QuantityProduct quantityProduct : product.getLocation()) {
                        if (quantityProduct.getLocationProduct().getIsStock() && !added) {
                            productController.addToStocks(product.getId(), quantity, quantityProduct.getLocationProduct());
                            added = true;
                        }
                    }
                }
            }

            workFlowController.changeStatus(workflow.getId(), new Status("Delivered"));

            JOptionPane.showMessageDialog(this,
                    "Purchase order sent and delivery registered successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            Timer timer = new Timer(5000, e ->
                    mainWindow.getNotificationController().push(new NotificationItem(
                            "New purchase order received",
                            "Order received from " + selectedSupplier.getName() + " — " + model.getTotal() + " item(s) added to stock.",
                            NotificationItem.Type.INFO
                    ))
            );
            timer.setRepeats(false);
            timer.start();

            mainWindow.setPage("STOCK");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
        }
        return panel;
    }

    /**
     * Creates a key-value display row using a preconfigured JLabel for the label part.
     *
     * @param label the prebuilt label component
     * @param value the value associated with the label
     * @return a horizontal panel showing the label-value pair
     */
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