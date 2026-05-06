package view;

import controller.*;
import exception.DataValidationException;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import model.*;

public class ClientSupplierTable extends JPanel {
    private MainWindow mainWindow;
    private ClientSupplierController controller;
    private ClientSupplierTableModel model;
    private ArrayList<ClientSupplier> clientSuppliers;
    private ArrayList<ClientSupplier> displayClientSupplier;

    private JPanel searchPanel, tablePanel;
    private JTextField txtLoyalityCard;
    private JTextField txtLastName;
    private JTextField txtFirstName;
    private JCheckBox chkIsClient;
    private JCheckBox chkIsSupplier;
    private JCheckBox chkIsMember;

    private JTable table;

    public ClientSupplierTable(MainWindow mainWindow) throws DataValidationException {
        this.mainWindow = mainWindow;
        this.controller = new ClientSupplierController();

        setLayout(new BorderLayout(0, 16));

        clientSuppliers = controller.getAllClientSupplier();
        displayClientSupplier = new ArrayList<>(clientSuppliers);

        add(buildHeader(), BorderLayout.NORTH);
        buildSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        buildTablePanel();
        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JLabel title = new JLabel("Client or Supplier Search");
        title.setFont(new Font("Inter", Font.BOLD, 20));

        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.add(title, BorderLayout.NORTH);
        return header;
    }
    /**
     * Builds the search panel containing the filter fields and action buttons.
     * Each field is encapsulated in a smaller JPanel for better display management.
     * @see #labeled(String, JComponent)
     */
    private void buildSearchPanel() {
        searchPanel = new JPanel(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        txtLoyalityCard = new JTextField(10);
        txtLoyalityCard = ViewUtils.addFilterListener(txtLoyalityCard, this::onFilterClick); 
        txtLoyalityCard = ViewUtils.digitsOnly(txtLoyalityCard);
        fieldsPanel.add(ViewUtils.labeled("Client ID", txtLoyalityCard));

        txtLastName = new JTextField(10);
        txtLastName = ViewUtils.addFilterListener(txtLastName, this::onFilterClick); 
        fieldsPanel.add(ViewUtils.labeled("Last name", txtLastName));

        txtFirstName = new JTextField(10);
        txtFirstName = ViewUtils.addFilterListener(txtFirstName, this::onFilterClick); 
        fieldsPanel.add(ViewUtils.labeled("First name", txtFirstName));

        chkIsClient = new JCheckBox("Client");
        chkIsClient = ViewUtils.addFilterListener(chkIsClient, this::onFilterClick);
        chkIsSupplier = new JCheckBox("Supplier");
        chkIsSupplier = ViewUtils.addFilterListener(chkIsSupplier, this::onFilterClick);
        chkIsMember = new JCheckBox("Staff member");
        chkIsMember = ViewUtils.addFilterListener(chkIsMember, this::onFilterClick);

        fieldsPanel.add(ViewUtils.labeled(chkIsClient, chkIsClient));
        fieldsPanel.add(ViewUtils.labeled(chkIsSupplier, chkIsSupplier));
        fieldsPanel.add(ViewUtils.labeled(chkIsMember, chkIsMember));

        searchPanel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> onCreateClick());
        JButton btnCreate = new JButton("Create");
        btnCreate.addActionListener(e -> onCreateClick());

        buttonPanel.add(btnSearch);
        buttonPanel.add(btnCreate);

        searchPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    /**
     * Construct the panel containing the document table.
     * The table uses {@link DocumentTableModel} as its data model.
     */
    private void buildTablePanel() {
        tablePanel = new JPanel(new BorderLayout());

        model = new ClientSupplierTableModel(displayClientSupplier);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 7) {
                    onUpdateClick();
                };
                if (col == 8) {
                    onDeleteClick();
                };
            }
        });
    }

    /**
     * Method called when the search button is clicked.
     *
     * Apply the user to selected filters on the document list:
     * Filter by ID (partial search)
     * Filter by document type
     * Filter by start date (if enabled)
     * Filter by end date (if enabled)
     *
     * Updates the table model with the filtered documents.
     */
    public void onFilterClick() {
        String idText = txtLoyalityCard.getText().trim();
        String lastNameText = txtLastName.getText().trim().toLowerCase();
        String firstNameText = txtFirstName.getText().trim().toLowerCase();

        displayClientSupplier = new ArrayList<>();

        for (ClientSupplier cs : clientSuppliers) {
            boolean match = true;

            if (!idText.isEmpty() && !String.valueOf(cs.getId()).contains(idText)) {
                match = false;
            }

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

    public void onCreateClick(){
        mainWindow.openClientSupplierForm(null);
    }

    /**
     * Called when the user clicks on "Modify".
     * Flow of the selected data:
     * 1. Get the selected row from the table.
     * 2. Retrieve the corresponding ClientSupplier object from displayClientSupplier.
     * 3. Send this object to the MainWindow.
     * @see MainWindow#openClientSupplierForm(ClientSupplier)
     * 4. MainWindow forwards it to the ClientSupplierForm.
     * 5. The form loads the data to allow editing.
     *
     * Important:
     * - If no row is selected → show an error message.
     * - If an object is passed → form is in EDIT mode.
     * - If null was passed → form would be in CREATE mode.
     */
    public void onUpdateClick(){
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client/supplier to modify.");
            return;
        }

        ClientSupplier cs = displayClientSupplier.get(selectedRow);

        mainWindow.openClientSupplierForm(cs);
    }

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

            controller.deleteClientSupplier(csToDelete);

            clientSuppliers.remove(csToDelete);
            displayClientSupplier.remove(selectedRow);

            model.setClientSuppliers(displayClientSupplier);
        }
    }
}