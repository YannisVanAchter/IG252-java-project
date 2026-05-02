package view;

import controller.DocumentController;
import exception.DataValidationException;
import model.ClientSupplier;
import model.Document;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * DocumentCreationForm represents the Form panel for creating a new document.
 * <p>
 * This form allows the user to enter all required information related to a document
 * (dates, workflow, client/supplier, address, etc.).
 * <p>
 * The form communicates with {@link DocumentController}
 */
public class DocumentForm extends JPanel {
    private MainWindow mainWindow;
    private DocumentController controller;
    private Document currentDocument;

    private ArrayList<ClientSupplier> allClients;

    private JPanel appPanel, panelContent, leftPanel, rightPanel;

    private JSpinner id;
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

    public DocumentForm(MainWindow mainWindow) throws DataValidationException {
        this.mainWindow = mainWindow;
        this.controller = new DocumentController();

        this.allClients = controller.getAllClientSupplier();

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Document Details");
        title.setFont(new Font("Inter", Font.BOLD, 20));

        JPanel topBar = new JPanel(new BorderLayout());

        JButton btnBack = new JButton("←");
        btnBack.addActionListener(e -> mainWindow.goBack());

        topBar.add(btnBack, BorderLayout.WEST);
        topBar.add(title, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);


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

        id = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        id.setEditor(new JSpinner.NumberEditor(id, "#"));
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

    private void buildRightPanel() throws DataValidationException {
        rightPanel = createColumnPanel();


        comboWorkflow = new JComboBox<>(controller.getAllWorkFlow());
        comboWorkflow.setEditable(true);
        rightPanel.add(labeled("Workflow", comboWorkflow));

        comboClientSupplier = new JComboBox<>(controller.getAllClientNames());
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
        spnStreetNumber.setEditor(new JSpinner.NumberEditor(spnStreetNumber, "#"));
        rightPanel.add(labeled("Street Number", spnStreetNumber));
        spnPostalCode = new JSpinner(new SpinnerNumberModel(1000, 0, 99999, 1));
        spnPostalCode.setEditor(new JSpinner.NumberEditor(spnPostalCode, "#"));
        rightPanel.add(labeled("Postal Code", spnPostalCode));

        txtStreet = new JTextField();
        rightPanel.add(labeled("Street", txtStreet));

        txtCity = new JTextField();
        rightPanel.add(labeled("City", txtCity));

        txtCountry = new JTextField();
        txtCountry.setEditable(true);
        rightPanel.add(labeled("Country", txtCountry));

        btnSave = new JButton("Save");
        if (currentDocument == null) {
            btnSave = new JButton("Save");

        } else {
            btnSave = new JButton("Edit");
        }
        btnSave.addActionListener(e -> saveEditForm());
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
     * <p>
     * If required fields are missing, a dialog is displayed and the process
     * is stopped.
     */
    private void saveEditForm() {

        if (id.getValue() == null) {
            JOptionPane.showMessageDialog(this, "ID required");
            return;
        }

        if (comboWorkflow.getSelectedItem() == null || comboWorkflow.getSelectedItem().toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Workflow required");
            return;
        }

        if (comboClientSupplier.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Client/Supplier required");
            return;
        }

        String documentId = String.valueOf(id.getValue());
        String commentaryText = commentary.getText();

        LocalDate plannedSend = getDate(pickerPlannedSendDate);
        LocalDate plannedReception = getDate(pickerPlannedReceptionDate);
        LocalDate effectiveSend = getDate(pickerEffectiveSendDate);
        LocalDate effectiveReception = getDate(pickerEffectiveReceptionDate);

        int paymentDelay;
        if (txtPaymentDelay.getText().trim().isEmpty()) {
            paymentDelay = -1;
        } else {
            try {
                paymentDelay = Integer.parseInt(txtPaymentDelay.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Payment delay must be a number");
                return;
            }
        }

        String workflow = comboWorkflow.getSelectedItem().toString().trim();
        String clientSupplier = comboClientSupplier.getSelectedItem().toString();

        int streetNumber = (int) spnStreetNumber.getValue();
        int postalCode = (int) spnPostalCode.getValue();

        String street = txtStreet.getText();
        String city = txtCity.getText();
        String country = txtCountry.getText();

        boolean isChecked = checkIsChecked.isSelected();
        try {
            if (currentDocument == null) {
                controller.createDocument(documentId, commentaryText, plannedSend, plannedReception,
                        effectiveSend, effectiveReception, paymentDelay, workflow, clientSupplier,
                        streetNumber, postalCode, street, city, country, isChecked
                );
            } else {
                controller.updateDocument(documentId, commentaryText, plannedSend, plannedReception,
                        effectiveSend, effectiveReception, paymentDelay, workflow, clientSupplier,
                        streetNumber, postalCode, street, city, country, isChecked
                );
            }
            JOptionPane.showMessageDialog(this, "Document saved !");
            clearForm();
            mainWindow.goBack();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Resets all input fields in the form to their default values.
     */
    public void clearForm() {
        id.setValue(0);
        commentary.setText("");

        pickerPlannedSendDate.setValue(new Date());
        pickerPlannedReceptionDate.setValue(new Date());
        pickerEffectiveSendDate.setValue(new Date());
        pickerEffectiveReceptionDate.setValue(new Date());

        txtPaymentDelay.setText("");

        checkIsChecked.setSelected(false);

        comboWorkflow.setSelectedIndex(-1);
        comboClientSupplier.setSelectedIndex(-1);

        spnStreetNumber.setValue(0);
        spnPostalCode.setValue(1000);

        txtStreet.setText("");
        txtCity.setText("");
        txtCountry.setText("");

        currentDocument = null;
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
     * Converts a JSpinner containing a Date to a LocalDate.
     * @param spinner component containing a Date
     * @return LocalDate corresponding to the spinner's value
     */
    private LocalDate getDate(JSpinner spinner) {
        return ((Date) spinner.getValue())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Converts a LocalDate to Date.
     * @param localDate date to convert
     * @return Date usable by JSpinners
     */
    private Date toDate(LocalDate localDate) {
        if (localDate == null) return null;

        return Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
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

    /**
     * Loads data from a Document into the form.
     * @param doc document to display
     * If doc is null:
     * - the form is in create mode
     */
    public void loadDocument(Document doc) {
        if (doc == null) {
            currentDocument = null;
            clearForm();
            return;
        }

        currentDocument = doc;

        id.setValue(doc.getId());
        //commentary.setText(doc.getCommentary());

        if (doc.getPlannedSenDate() != null)
            pickerPlannedSendDate.setValue(toDate(doc.getPlannedSenDate()));

        if (doc.getPlannedDateOfReceipt() != null)
            pickerPlannedReceptionDate.setValue(toDate(doc.getPlannedDateOfReceipt()));

        if (doc.getActualSendDate() != null)
            pickerEffectiveSendDate.setValue(toDate(doc.getActualSendDate()));

        if (doc.getActualDateOfReceipt() != null)
            pickerEffectiveReceptionDate.setValue(toDate(doc.getActualDateOfReceipt()));

        txtPaymentDelay.setText(
                doc.getPaymentDelay() == -1 ? "" : String.valueOf(doc.getPaymentDelay())
        );

        comboWorkflow.setSelectedItem(doc.getWorkflow().getWorkflowType());

        comboClientSupplier.setSelectedItem(doc.getClientSupplier());

        spnStreetNumber.setValue(doc.getAddress().getStreetNumber());
        spnPostalCode.setValue(doc.getAddress().getLocality().getPostalCode());

        txtStreet.setText(doc.getAddress().getStreetName());
        txtCity.setText(doc.getAddress().getLocality().getName());
        //txtCountry.setText(doc.getAddress().getLocality().getCountry();

        //checkIsChecked.setSelected(doc.getIsChecked());
    }
}