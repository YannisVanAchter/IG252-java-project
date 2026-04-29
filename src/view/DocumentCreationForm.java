package view;

import controler.DocumentController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * DocumentCreationForm represents the Form panel for creating a new document.
 *
 * This form allows the user to enter all required information related to a document
 * (dates, workflow, client/supplier, address, etc.).
 *
 * The form communicates with {@link DocumentController}
 */
public class DocumentCreationForm extends JPanel {
    private MainWindow mainWindow;
    private DocumentController controller;

    private ArrayList<ClientSupplier> allClients;

    private JPanel appPanel, panelContent, leftPanel, rightPanel;

    private JTextField id;
    private JTextArea commentary;
    private JCheckBox checkIsChecked;

    private JSpinner pickerPlannedSendDate;
    private JSpinner pickerPlannedReceptionDate;
    private JSpinner pickerEffectiveSendDate;
    private JSpinner pickerEffectiveReceptionDate;

    private JTextField txtPaymentDelay;

    private JComboBox<String> comboWorkflow;
    private JComboBox<String> comboClientSupplier;

    private JSpinner spnStreetNumber;
    private JSpinner spnPostalCode;

    private JTextField txtStreet;
    private JTextField txtCity;
    private JTextField txtCountry;

    private JButton btnSave;
    private JButton btnClear;
    private JButton btnNewClient;

    public DocumentCreationForm(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = new DocumentController();

        this.allClients = controller.getAllClientSupplier();

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Document Details");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        appPanel = new JPanel(new BorderLayout(20, 20));
        panelContent = new JPanel(new GridLayout(1, 2, 20, 0));

        buildLeftPanel();
        buildRightPanel();

        panelContent.add(leftPanel);
        panelContent.add(rightPanel);

        appPanel.add(panelContent, BorderLayout.CENTER);
        add(appPanel, BorderLayout.CENTER);
    }

    private void buildLeftPanel() {
        leftPanel = createColumnPanel();

        id = new JTextField(1);
        leftPanel.add(labeled("ID du document", id));

        commentary = new JTextArea(4, 20);
        leftPanel.add(labeled("Commentaire", new JScrollPane(commentary))); // FIX important

        pickerPlannedSendDate = createDateSpinner();
        leftPanel.add(labeled("Planned Send Date", pickerPlannedSendDate));

        pickerPlannedReceptionDate = createDateSpinner();
        leftPanel.add(labeled("Planned Reception Date", pickerPlannedReceptionDate));

        pickerEffectiveSendDate = createDateSpinner();
        leftPanel.add(labeled("Effective Send Date", pickerEffectiveSendDate));

        pickerEffectiveReceptionDate = createDateSpinner();
        leftPanel.add(labeled("Effective Reception Date", pickerEffectiveReceptionDate)); // FIX bug

        txtPaymentDelay = new JTextField();
        leftPanel.add(labeled("Payment delay", txtPaymentDelay));

        checkIsChecked = new JCheckBox("Document is checked");
        leftPanel.add(checkIsChecked);
    }

    private void buildRightPanel() {
        rightPanel = createColumnPanel();

        comboWorkflow = new JComboBox<>(new String[]{
                "Invoice", "Contract", "Purchase order", "Delivery note", "Quote"
        });
        comboWorkflow.setEditable(true);
        rightPanel.add(labeled("Workflow", comboWorkflow));

        comboClientSupplier = new JComboBox<>();
        setClientSuppliers(allClients);
        comboClientSupplier.setEditable(true);

        btnNewClient = new JButton("New");

        JPanel clientPanel = new JPanel();
        clientPanel.setLayout(new BoxLayout(clientPanel, BoxLayout.X_AXIS));
        clientPanel.add(comboClientSupplier);
        clientPanel.add(Box.createHorizontalStrut(10));
        clientPanel.add(btnNewClient);

        rightPanel.add(labeled("Client / Supplier", clientPanel));

        spnStreetNumber = new JSpinner(new SpinnerNumberModel(1, 0, 10000, 1));
        rightPanel.add(labeled("Street Number", spnStreetNumber));

        spnPostalCode = new JSpinner(new SpinnerNumberModel(1000, 0, 99999, 1));
        rightPanel.add(labeled("Postal Code", spnPostalCode));

        txtStreet = new JTextField();
        txtStreet.setEditable(true);
        rightPanel.add(labeled("Street", txtStreet));

        txtCity = new JTextField();
        txtCity.setEditable(true);
        rightPanel.add(labeled("City", txtCity));

        txtCountry = new JTextField();
        txtCountry.setEditable(true);
        rightPanel.add(labeled("Country", txtCountry));

        btnSave = new JButton("Save");
        btnSave.addActionListener(e -> saveForm());
        btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearForm());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClear);

        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(buttonPanel);
    }

    /**
     * Validates user input and sends the document data to the controller
     * for creation.
     *
     * If required fields are missing, a dialog is displayed and the process
     * is stopped.
     */
    private void saveForm() {

        if (id.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID required");
            return;
        }

        if (comboWorkflow.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Workflow required");
            return;
        }

        if (comboClientSupplier.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Client/Supplier required");
            return;
        }

        String documentId = id.getText().trim();
        String commentaryText = commentary.getText();

        java.util.Date plannedSend = (java.util.Date) pickerPlannedSendDate.getValue();
        java.util.Date plannedReception = (java.util.Date) pickerPlannedReceptionDate.getValue();
        java.util.Date effectiveSend = (java.util.Date) pickerEffectiveSendDate.getValue();
        java.util.Date effectiveReception = (java.util.Date) pickerEffectiveReceptionDate.getValue();

        String paymentDelay = txtPaymentDelay.getText();

        String workflow = comboWorkflow.getSelectedItem().toString();
        String clientSupplier = comboClientSupplier.getSelectedItem().toString();

        int streetNumber = (int) spnStreetNumber.getValue();
        int postalCode = (int) spnPostalCode.getValue();

        String street = txtStreet.getText();
        String city = txtCity.getText();
        String country = txtCountry.getText();

        boolean isChecked = checkIsChecked.isSelected();

        try {
            controller.createDocument(documentId, commentaryText, plannedSend, plannedReception,
                    effectiveSend, effectiveReception, paymentDelay, workflow, clientSupplier,
                    streetNumber, postalCode, street, city, country, isChecked
            );

            JOptionPane.showMessageDialog(this, "Document saved !");
            clearForm();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Resets all input fields in the form to their default values.
     */
    private void clearForm() {
        id.setText("");
        commentary.setText("");

        pickerPlannedSendDate.setValue(new java.util.Date());
        pickerPlannedReceptionDate.setValue(new java.util.Date());
        pickerEffectiveSendDate.setValue(new java.util.Date());
        pickerEffectiveReceptionDate.setValue(new java.util.Date());

        txtPaymentDelay.setText("");

        checkIsChecked.setSelected(false);

        comboWorkflow.setSelectedIndex(0);
        comboClientSupplier.setSelectedIndex(-1);

        spnStreetNumber.setValue(0);
        spnPostalCode.setValue(1000);

        txtStreet.setText("");
        txtCity.setText("");
        txtCountry.setText("");
    }


    private JPanel createColumnPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JSpinner createDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        return spinner;
    }

    private JPanel labeled(String text, JComponent comp) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(new JLabel(text));
        p.add(Box.createVerticalStrut(4));
        p.add(comp);
        return p;
    }

    /**
     * Updates the client/supplier dropdown list with available entries.
     *
     * @param clientSupplier list of available clients or suppliers
     */
    public void setClientSuppliers(ArrayList<ClientSupplier> clientSupplier) {
        comboClientSupplier.removeAllItems();

        for (ClientSupplier client : clientSupplier) {
            comboClientSupplier.addItem(client.getName());
        }
    }
}