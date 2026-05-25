package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.ClientController;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.FidelityCard;
import main.java.be.henallux.project.model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * ReceiptClientInfoDialog is a Swing dialog panel used during the receipt workflow to associate a client with the current transaction.
 * <p>This view allows the user to:
 * <ul><li>Select an existing client from a searchable combo box</li>
 *     <li>Create and register a new client</li>
 *     <li>Scan a loyalty or membership card</li>
 *     <li>View client information before payment</li>
 *     <li>Continue checkout with or without a linked client</li></ul>
 * <p>The panel interacts with {@link ClientController} to retrieve available clients
 * and maintains the currently selected {@link ClientSupplier}.
 * <p>When the workflow is validated, the view transitions to {@link ReceiptPayment} to finalize the transaction.
 *
 * @see ClientController
 * @see ClientSupplier
 * @see Product
 * @see ReceiptCreateView
 * @see ReceiptPayment
 */
public class ReceiptClientInfoDialog extends JPanel {
    private final MainWindow mainWindow;
    private final ReceiptCreateView receiptCreateView;
    private final ClientController controller;
    private final ArrayList<ClientSupplier> allClients;
    private final ArrayList<ComboBoxItem<ClientSupplier>> allClientItems = new ArrayList<>();
    private LinkedHashMap<Product, Integer> receipt;
    private ClientSupplier selectedClient;
    private JComboBox<ComboBoxItem<ClientSupplier>> comboClientSupplier;
    private JButton btnNew, btnScan, btnCancel, btnNext;
    private JPanel infoPanel;

    public ReceiptClientInfoDialog(MainWindow mainWindow, ReceiptCreateView receiptCreateView, LinkedHashMap<Product, Integer> receipt) {
        this.mainWindow = mainWindow;
        this.receiptCreateView = receiptCreateView;
        this.receipt = receipt;
        this.controller = new ClientController();
        ArrayList<ClientSupplier> loaded = new ArrayList<>();
        try {
            loaded = controller.getAllClientsSuppliers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.allClients = loaded;
        this.selectedClient = null;

        setPreferredSize(new Dimension(400, 300));
        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(build(), BorderLayout.NORTH);
        add(buildInfoPanel(), BorderLayout.CENTER);
        add(buildBtnPanel(), BorderLayout.SOUTH);
    }

    /**
     * Builds the main selection panel containing the client/supplier {@code JComboBox} and associated action buttons.
     * A Scan button for search a client by card number.
     * A modal for create a new client.
     * <p>The method also registers listeners to update {@code selectedClient}
     * and refresh the information panel when selection changes.
     *
     * @return the constructed selection panel
     */
    private JPanel build() {
        JPanel panel = ViewUtils.createColumnPanel();
        JPanel comboPanel = ViewUtils.createColumnPanel();

        JLabel lblClient = new JLabel("Client:");
        comboPanel.add(lblClient);

        comboClientSupplier = new JComboBox<>();
        ViewUtils.setCursor(comboClientSupplier);
        comboClientSupplier.setEditable(true);
        setComboClients(allClients);

        for (int i = 0; i < comboClientSupplier.getItemCount(); i++) {
            allClientItems.add(comboClientSupplier.getItemAt(i));
        }
        ViewUtils.setupAutoComplete(comboClientSupplier, allClientItems);

        comboClientSupplier.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboClientSupplier.getPreferredSize().height));
        comboClientSupplier.addActionListener(e -> {
            if (comboClientSupplier.getSelectedIndex() <= 0) {
                selectedClient = null;
                refreshInfoPanel();
                return;
            }
            Object selected = comboClientSupplier.getSelectedItem();
            if (selected instanceof ComboBoxItem<?> item) {
                selectedClient = ((ComboBoxItem<ClientSupplier>) item).getObject();
                refreshInfoPanel();
            } else {
                selectedClient = null;
                refreshInfoPanel();
            }
        });
        comboPanel.add(comboClientSupplier);
        panel.add(comboPanel);

        JPanel comboBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnNew = new JButton("New Client");
        ViewUtils.setCursor(btnNew);
        btnNew.addActionListener(e -> onNewClick());
        btnScan = new JButton("Scan card");
        ViewUtils.setCursor(btnScan);
        btnScan.addActionListener(e -> onScanClick());
        comboBtnPanel.add(btnNew);
        comboBtnPanel.add(btnScan);

        comboPanel.add(comboBtnPanel);

        panel.add(comboPanel, BorderLayout.NORTH);

        return panel;
    }

    /**
     * Builds the information panel displaying details of the currently selected client.
     * <p>If no client is selected, a placeholder message is shown.
     * Otherwise, the panel displays: Full name, Email address, and Loyalty points
     * (retrieved from the client's {@link FidelityCard}, or "No fidelity card" if absent).
     * <p>This panel is dynamically rebuilt whenever {@code selectedClient} changes.
     *
     * @return the constructed client information {@code JPanel}
     */
    private JPanel buildInfoPanel() {
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Client information"));

        if (selectedClient == null) {
            JPanel noClientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            JLabel lblName = new JLabel("Select a customer or continue without");
            noClientPanel.add(lblName);
            infoPanel.add(noClientPanel);
        } else {
            Dimension labelSize = new Dimension(100, 25);
            infoPanel.removeAll();

            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            JLabel lblName = new JLabel("Name:");
            lblName.setPreferredSize(labelSize);
            JLabel lblNameValue = new JLabel(selectedClient.getFirstname() + " " + selectedClient.getName());
            namePanel.add(lblName);
            namePanel.add(lblNameValue);
            infoPanel.add(namePanel);

            JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            JLabel lblEmail = new JLabel("Email:");
            lblEmail.setPreferredSize(labelSize);
            JLabel lblEmailValue = new JLabel(selectedClient.getEmail());
            emailPanel.add(lblEmail);
            emailPanel.add(lblEmailValue);
            infoPanel.add(emailPanel);

            JPanel loyaltyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            JLabel lblLoyalty = new JLabel("Loyalty points:");
            lblLoyalty.setPreferredSize(labelSize);

            FidelityCard card = selectedClient.getFidelityCard();
            String loyaltyValue = (card != null)
                    ? String.valueOf(card.getTotalPoint())
                    : "No fidelity card";
            JLabel lblLoyaltyValue = new JLabel(loyaltyValue);

            loyaltyPanel.add(lblLoyalty);
            loyaltyPanel.add(lblLoyaltyValue);
            infoPanel.add(loyaltyPanel);
        }

        infoPanel.revalidate();
        infoPanel.repaint();
        return infoPanel;
    }

    /**
     * Builds the bottom action panel containing navigation buttons.
     * Cancel: closes the dialog without proceeding
     * Next: validates selection and proceeds to the payment step
     *
     * @return the constructed button {@code JPanel}
     */
    private JPanel buildBtnPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnCancel = new JButton("Cancel");
        ViewUtils.setCursor(btnCancel);
        btnCancel.addActionListener(e -> onCancelClick());
        btnNext = new JButton("Next");
        ViewUtils.setCursor(btnNext);
        btnNext.addActionListener(e -> onNextClick());
        panel.add(btnCancel);
        panel.add(btnNext);
        return panel;
    }

    /**
     * Handles scanning of a client card number.
     * <p>Prompts the user for a numeric input, validates it, and searches for a matching client
     * by comparing the input against each client's {@link FidelityCard} ID.
     * <p>If a match is found, the client is selected and the UI is updated.
     * Otherwise, an informational message is displayed.
     */
    private void onScanClick() {
        String input = JOptionPane.showInputDialog("Enter the card number:");
        if (input == null || input.trim().isEmpty()) return;

        int cardNumber;
        try {
            cardNumber = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "The number is invalid", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<ClientSupplier> clients = controller.getClientByCardID(cardNumber);
            if (clients != null && !clients.isEmpty()) {
                ClientSupplier selectedClient = clients.getFirst();
                setComboClient(selectedClient);
                refreshInfoPanel();
            } else {
                JOptionPane.showMessageDialog(this, "No customers found", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Opens a dialog to create a new client.
     * <p>After successful creation:
     * <ol><li>The new client is added to the internal list</li>
     *     <li>The combo box is updated</li>
     *     <li>The newly created client is selected automatically</li></ol>
     */
    private void onNewClick() {
        JDialog dialog = new JDialog(mainWindow, "Add new Client", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ClientSupplierForm form = new ClientSupplierForm(mainWindow, true);
        dialog.setContentPane(form);

        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
        ClientSupplier newClient = form.getCurrentClientSupplier();
        if (newClient == null) return;
        allClients.add(newClient);
        String label = newClient.getName() + " " + newClient.getFirstname();
        ComboBoxItem<ClientSupplier> newItem = new ComboBoxItem<>(newClient, label);
        comboClientSupplier.addItem(newItem);
        allClientItems.add(newItem);
        setComboClient(newClient);
    }

    /**
     * Validates the current selection and proceeds to the payment step.
     * <p>Closes the current window and navigates to {@link ReceiptPayment},passing the selected client and receipt data.
     */
    private void onNextClick() {
        SwingUtilities.getWindowAncestor(this).dispose();
        mainWindow.addPage(new ReceiptPayment(mainWindow, receiptCreateView, selectedClient, receipt), "RECEIPT_PAYEMENT");
        mainWindow.setPage("RECEIPT_PAYEMENT");
    }

    /**
     * Cancels the current operation and closes the dialog window.
     * <p>No data is persisted or modified when this method is invoked.
     */
    private void onCancelClick() {
        SwingUtilities.getWindowAncestor(this).dispose();
    }

    /**
     * Rebuilds and refreshes the client information panel.
     * <p>This method removes the existing {@code infoPanel} component, recreates it using {@link #buildInfoPanel()}
     */
    private void refreshInfoPanel() {
        remove(infoPanel);
        infoPanel = buildInfoPanel();
        add(infoPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Add client/supplier {@code JComboBox} with available entries.
     * <p>A default "-- No client --" option is always added at index 0.
     *
     * @param clientSupplier list of available {@link ClientSupplier} entries
     */
    public void setComboClients(ArrayList<ClientSupplier> clientSupplier) {
        comboClientSupplier.removeAllItems();
        comboClientSupplier.addItem(
                new ComboBoxItem<>(null, "-- No client --")
        );
        for (ClientSupplier cs : clientSupplier) {
            String label = cs.getName() + " " + (cs.getFirstname() != null ? cs.getFirstname() : "");
            comboClientSupplier.addItem(new ComboBoxItem<>(cs, label.trim()));
        }
    }

    /**
     * Selects a specific client in the {@code JComboBox} if present.
     * <p>Iterates through all combo box items and selects the one matching the provided client instance.
     *
     * @param client the client to select in the combo box
     */
    public void setComboClient(ClientSupplier client) {
        boolean found = false;
        for (int i = 0; i < comboClientSupplier.getItemCount() && !found; i++) {
            ClientSupplier current = comboClientSupplier.getItemAt(i).getObject();
            if (current != null && current.getId() == client.getId()) {
                comboClientSupplier.setSelectedIndex(i);
                found = true;
            }
        }
    }
}