package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import main.java.be.henallux.project.model.*;

/**
 * This view allows users to manage and search through a list of clients and suppliers.
 * <p>This class extends {@link JPanel} to provide a table view and search functionality for
 * filtering client/supplier data based on various parameters such as ID, first name, last name,
 * and type (client, supplier, or staff member).
 * <p>The table is populated via a custom model, {@link ClientSupplierTableModel}, and provides
 * ease of navigation with interactive search fields and filter checkboxes.
 * <p>Clicking on a row opens a detailed Form Client/Supplier view through the main application window.
 *
 * @see ClientSupplierTableModel
 * @see ClientSupplierController
 * @see MainWindow
 */
public class ClientSupplierTable extends JPanel {

    private final MainWindow mainWindow;
    private final ClientSupplierController clientSupplierController;
    private final ArrayList<ClientSupplier> clientSuppliers;
    private ClientSupplierTableModel model;
    private ArrayList<ClientSupplier> displayClientSupplier;

    private JTextField txtLoyaltyCard;
    private JTextField txtLastName;
    private JTextField txtFirstName;
    private JCheckBox chkIsClient;
    private JCheckBox chkIsSupplier;
    private JCheckBox chkIsMember;

    private JTable table;

    public ClientSupplierTable(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.clientSupplierController = new ClientSupplierController();

        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        ArrayList<ClientSupplier> loaded = new ArrayList<>();
        try {
            loaded = clientSupplierController.getAllClientsSuppliers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            mainWindow.goBack();
        }
        clientSuppliers = loaded;

        displayClientSupplier = new ArrayList<>(clientSuppliers);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(buildHeader(), BorderLayout.NORTH);
        top.add(buildSearchPanel(), BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    /**
     * Builds the header section containing the title.
     *
     * @return a {@link JPanel} representing the header
     */
    private JPanel buildHeader() {
        JLabel title = new JLabel("Client or Supplier Search");
        title.setFont(new Font("Inter", Font.BOLD, 20));

        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.add(title, BorderLayout.NORTH);
        return header;
    }

    /**
     * Builds the search and filter panel.
     * <p>The panel is divided into two sections, each wrapped in Border.
     * <p>This panel contains input fields for filtering clients/suppliers:
     * ID, last name, first name, and type selection (client, supplier, staff member),
     * as well as action buttons create.
     *
     * @return the configured search panel
     */
    private JPanel buildSearchPanel() {
        txtLoyaltyCard = new JTextField(10);
        ViewUtils.setCursor(txtLoyaltyCard);
        ViewUtils.addFilterListener(txtLoyaltyCard, this::onFilterClick);
        ViewUtils.digitsOnly(txtLoyaltyCard);
        JPanel idFields = new JPanel(new BorderLayout(0, 4));
        idFields.add(new JLabel("Client ID"), BorderLayout.NORTH);
        idFields.add(txtLoyaltyCard, BorderLayout.CENTER);

        txtLastName = new JTextField(10);
        ViewUtils.setCursor(txtLastName);
        ViewUtils.addFilterListener(txtLastName, this::onFilterClick);
        JPanel lastNameFields = new JPanel(new BorderLayout(0, 4));
        lastNameFields.add(new JLabel("Last name"), BorderLayout.NORTH);
        lastNameFields.add(txtLastName, BorderLayout.CENTER);

        txtFirstName = new JTextField(10);
        ViewUtils.setCursor(txtFirstName);
        ViewUtils.addFilterListener(txtFirstName, this::onFilterClick);
        JPanel firstNameFields = new JPanel(new BorderLayout(0, 4));
        firstNameFields.add(new JLabel("First name"), BorderLayout.NORTH);
        firstNameFields.add(txtFirstName, BorderLayout.CENTER);

        chkIsClient = new JCheckBox("Client");
        ViewUtils.addFilterListener(chkIsClient, this::onFilterClick);
        chkIsSupplier = new JCheckBox("Supplier");
        ViewUtils.addFilterListener(chkIsSupplier, this::onFilterClick);
        chkIsMember = new JCheckBox("Staff member");
        ViewUtils.addFilterListener(chkIsMember, this::onFilterClick);

        JButton btnSearch = new JButton("Search");
        ViewUtils.setCursor(btnSearch);
        btnSearch.addActionListener(e -> onFilterClick());
        JButton btnCreate = new JButton("Create");
        ViewUtils.setCursor(btnCreate);
        btnCreate.addActionListener(e -> onCreateClick());

        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.add(ViewUtils.makeRow(idFields));
        leftColumn.add(Box.createVerticalStrut(6));
        leftColumn.add(ViewUtils.makeRow(lastNameFields));
        leftColumn.add(Box.createVerticalStrut(6));
        leftColumn.add(ViewUtils.makeRow(firstNameFields));
        leftColumn.add(Box.createVerticalStrut(8));
        leftColumn.add(ViewUtils.makeRow(btnCreate));

        JPanel rightColumn = ViewUtils.createColumnPanel();
        rightColumn.setBorder(BorderFactory.createTitledBorder("Type"));
        rightColumn.add(ViewUtils.makeRow(chkIsClient));
        rightColumn.add(Box.createVerticalStrut(2));
        rightColumn.add(ViewUtils.makeRow(chkIsSupplier));
        rightColumn.add(Box.createVerticalStrut(2));
        rightColumn.add(ViewUtils.makeRow(chkIsMember));

        JPanel fieldsRow = new JPanel(new BorderLayout(12, 0));
        fieldsRow.add(leftColumn, BorderLayout.CENTER);
        fieldsRow.add(rightColumn, BorderLayout.EAST);

        JPanel fieldsColumn = new JPanel();
        fieldsColumn.setLayout(new BoxLayout(fieldsColumn, BoxLayout.Y_AXIS));
        fieldsColumn.setBorder(BorderFactory.createTitledBorder("Filters"));
        fieldsColumn.add(ViewUtils.makeRow(fieldsRow));

        return fieldsColumn;
    }

    /**
     * Builds the panel containing the client/supplier table.
     * <p>The table uses {@link ClientSupplierTableModel} as its data model
     * and is configured to allow single row selection only.
     * <p>A mouse listener is added to detect clicks on specific columns:
     * <ul><li>Column 8: triggers the delete action via {@code onDeleteClick()}.</li>
     *  <li>Other Column: triggers the update action via {@code onUpdateClick()}.</li></ul>
     *
     * @return a {@link JScrollPane} containing the configured table
     */
    private JScrollPane buildTablePanel() {
        model = new ClientSupplierTableModel(displayClientSupplier);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = table.convertColumnIndexToModel(
                        table.columnAtPoint(e.getPoint()));
                if (col == ClientSupplierTableModel.TBL_BTN_DEL) onDeleteClick();
                if (col == ClientSupplierTableModel.TBL_BTN_UPDATE) onUpdateClick();
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());

                if (col == ClientSupplierTableModel.TBL_BTN_UPDATE || col == ClientSupplierTableModel.TBL_BTN_DEL) {
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    table.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        table.getColumnModel().getColumn(ClientSupplierTableModel.TBL_BTN_DEL).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(ClientSupplierTableModel.TBL_BTN_UPDATE).setCellRenderer(new ButtonRenderer());

        ViewUtils.resizeColumnWidth(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 250));
        return scroll;
    }

    /**
     * Applies filters to the client/supplier list and refreshes live table.
     * <p>Filtering is performed on:
     * <ul><li>Client ID (partial match)</li>
     *   <li>Last name (case-insensitive partial match)</li>
     *   <li>First name (case-insensitive partial match)</li>
     *   <li>Type (client, supplier, staff member)</li></ul>
     * <p>If no type filter is selected, all types are included.
     * All Client/Supplier in {@code clientSupplier}s are filter and add in {@code displayClientSupplier}
     * The table is reload in {@link ClientSupplierTableModel#setClientSuppliers(ArrayList)}
     */
    public void onFilterClick() {
        String idText = txtLoyaltyCard.getText().trim();
        String lastNameText = txtLastName.getText().trim().toLowerCase();
        String firstNameText = txtFirstName.getText().trim().toLowerCase();

        displayClientSupplier = new ArrayList<>();

        for (ClientSupplier cs : clientSuppliers) {
            boolean match = idText.isEmpty() || String.valueOf(cs.getId()).contains(idText);

            if (!lastNameText.isEmpty()
                    && !cs.getName().toLowerCase().contains(lastNameText)) {
                match = false;
            }

            if (!firstNameText.isEmpty()
                    && !cs.getFirstname().toLowerCase().contains(firstNameText)) {
                match = false;
            }

            boolean typeMatch = false;

            if (!chkIsClient.isSelected() &&
                    !chkIsSupplier.isSelected() &&
                    !chkIsMember.isSelected()) {
                typeMatch = true;
            } else {
                if (chkIsClient.isSelected() && cs.getIsClient()) typeMatch = true;
                if (chkIsSupplier.isSelected() && cs.getIsSupplier()) typeMatch = true;
                if (chkIsMember.isSelected() && cs.getIsUs()) typeMatch = true;
            }

            if (!typeMatch) {
                match = false;
            }

            if (match) {
                displayClientSupplier.add(cs);
            }
        }

        model.setClientSuppliers(displayClientSupplier);
    }

    public void onCreateClick() {
        mainWindow.openClientSupplierForm(null);
    }

    /**
     * Opens the selected client/supplier in edit mode.
     * <p>The selected row from the table is converted into a
     * {@link ClientSupplier} and passed to
     * {@link MainWindow#openClientSupplierForm(ClientSupplier)}.
     * <p>Depend on the selection state:
     * <ul><li>If no row is selected, an error message is displayed and the operation is aborted</li>
     *     <li>If a {@link ClientSupplier} is provided, the form is opened in EDIT mode</li>
     *     <li>If {@code null} is provided, the form would be opened in CREATE mode</li></ul>
     */
    public void onUpdateClick() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client/supplier to modify.");
            return;
        }

        ClientSupplier cs = displayClientSupplier.get(selectedRow);

        mainWindow.openClientSupplierForm(cs);
    }

    /**
     * Handles the delete action triggered from the UI.
     * <p>Retrieves the currently selected row in the table, asks for user confirmation,
     * and delegates the deletion to the overloaded {@link #onDeleteClick(ClientSupplier)} method.
     * <p>If no row is selected, a warning dialog is shown and the operation is canceled.
     *
     * @see JOptionPane#showMessageDialog(Component, Object)
     */
    public void onDeleteClick() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client/supplier to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this entry?",
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            ClientSupplier csToDelete = displayClientSupplier.get(selectedRow);
            onDeleteClick(csToDelete);
        }
    }

    /**
     * Deletes the specified ClientSupplier and updates the UI and model accordingly.
     * <p>If the deletion is successful, the item is removed from both the internal lists
     * and the table model, and a success notification is displayed.
     * <p>If the deletion fails, an error notification is shown with an option to retry the operation.
     *
     * @param csToDelete the ClientSupplier to delete
     */
    public void onDeleteClick(ClientSupplier csToDelete) {
        try {
            if (csToDelete.getIsClient() && csToDelete.getFidelityCard() != null) {
                clientSupplierController.deleteClientAccount(csToDelete.getId(), csToDelete.getFidelityCard().getId());
            } else {
                clientSupplierController.deleteClientSupplier(csToDelete);
            }
        } catch (Exception e) {
            mainWindow.getNotificationController().push(new NotificationItem(
                    "Delete",
                    "Failed to delete. Click to retry.",
                    NotificationItem.Type.ERROR,
                    () -> onDeleteClick(csToDelete)
            ));
        }
        clientSuppliers.remove(csToDelete);
        displayClientSupplier.remove(csToDelete);
        model.setClientSuppliers(displayClientSupplier);

        mainWindow.getNotificationController().push(new NotificationItem(
                "Delete",
                csToDelete.getLabel() + " has been deleted.",
                NotificationItem.Type.SUCCESS,
                null
        ));
    }
}