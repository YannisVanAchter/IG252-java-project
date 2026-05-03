package view;

import controller.DocumentController;
import exception.DataValidationException;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.*;
//Todo : uncomment country in loadDocument when available

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
    private ArrayList<DocumentType> allDocumentTypes;
    private ArrayList<Status> allWorkflowStatuses;

    private JPanel appPanel, panelContent, leftPanel, rightPanel;

    private JTextArea commentary;
    private JCheckBox checkIsChecked;

    private JSpinner pickerPlannedSendDate;
    private JSpinner pickerPlannedReceptionDate;
    private JSpinner pickerEffectiveSendDate;
    private JSpinner pickerEffectiveReceptionDate;
    private JSpinner spnPaymentDelay;

    private JCheckBox chkPlannedSendDate;
    private JCheckBox chkPlannedReceptionDate;
    private JCheckBox chkEffectiveSendDate;
    private JCheckBox chkEffectiveReceptionDate;

    private JComboBox<ComboBoxItem<DocumentType>> comboDocumentType;
    private JComboBox<ComboBoxItem<ClientSupplier>> comboClientSupplier;
    private JComboBox<ComboBoxItem<Status>> comboWorkflowStatus;

    private JRadioButton isBuy;
    private JRadioButton isSell;
    private JRadioButton isInternal;
    private ButtonGroup workflowGroup;

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

    private void buildLeftPanel() throws DataValidationException {
        leftPanel = ViewUtils.createColumnPanel();

        comboDocumentType = new JComboBox<>();
        setDocumentTypes(controller.getAllDocumentType());
        leftPanel.add(ViewUtils.labeledRequired("Document Type", comboDocumentType));

        commentary = new JTextArea(4, 20);
        leftPanel.add(ViewUtils.labeled("Commentaire", new JScrollPane(commentary))); // FIX important

        chkPlannedSendDate = new JCheckBox();
        pickerPlannedSendDate = ViewUtils.createDateSpinner();
        leftPanel.add(ViewUtils.labeledToggleDate("Planned Send Date", pickerPlannedSendDate, chkPlannedSendDate));


        chkPlannedReceptionDate = new JCheckBox();
        pickerPlannedReceptionDate = ViewUtils.createDateSpinner();
        leftPanel.add(ViewUtils.labeledToggleDate("Planned Reception Date", pickerPlannedReceptionDate, chkPlannedReceptionDate));

        chkEffectiveSendDate = new JCheckBox();
        pickerEffectiveSendDate = ViewUtils.createDateSpinner();
        leftPanel.add(ViewUtils.labeledToggleDate("Effective Send Date", pickerEffectiveSendDate, chkEffectiveSendDate));

        chkEffectiveReceptionDate = new JCheckBox();
        pickerEffectiveReceptionDate = ViewUtils.createDateSpinner();
        leftPanel.add(ViewUtils.labeledToggleDate("Effective Reception Date", pickerEffectiveReceptionDate, chkEffectiveReceptionDate));

        spnPaymentDelay = ViewUtils.createNumberSpinner(0, -1, 3650, 1);
        leftPanel.add(ViewUtils.labeled("Payment delay", spnPaymentDelay));

        checkIsChecked = new JCheckBox("Document is checked");
        leftPanel.add(checkIsChecked);
    }

    private void buildRightPanel() throws DataValidationException {
        rightPanel = ViewUtils.createColumnPanel();

        comboWorkflowStatus = new JComboBox<>();
        setWorkflowStatus(controller.getAllWorkflowStatus());
        rightPanel.add(ViewUtils.labeledRequired("Workflow Status", comboWorkflowStatus));

        isBuy = new JRadioButton("Buy");
        isSell = new JRadioButton("Sell");
        isInternal = new JRadioButton("Internal");

        workflowGroup = new ButtonGroup();
        workflowGroup.add(isBuy);
        workflowGroup.add(isSell);
        workflowGroup.add(isInternal);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
        radioPanel.add(isBuy);
        radioPanel.add(isSell);
        radioPanel.add(isInternal);

        rightPanel.add(ViewUtils.labeledRequired("WorkflowType", radioPanel));

        comboClientSupplier = new JComboBox<>();
        setClientSuppliers(allClients);
        comboClientSupplier.setEditable(true);

        btnNewClient = new JButton("New");
        btnNewClient.addActionListener(e -> onNewClientcliked());
        JPanel clientPanel = new JPanel();
        clientPanel.setLayout(new BoxLayout(clientPanel, BoxLayout.X_AXIS));
        clientPanel.add(comboClientSupplier);
        clientPanel.add(Box.createHorizontalStrut(10));
        clientPanel.add(btnNewClient);

        rightPanel.add(ViewUtils.labeled("Client / Supplier", clientPanel));

        spnStreetNumber = ViewUtils.createNumberSpinner(1, 1, 10000, 1);
        rightPanel.add(ViewUtils.labeled("Street Number", spnStreetNumber));
        spnPostalCode = ViewUtils.createNumberSpinner(1000, 1, 99999, 1);
        rightPanel.add(ViewUtils.labeled("Postal Code", spnPostalCode));

        txtStreet = new JTextField();
        rightPanel.add(ViewUtils.labeled("Street", txtStreet));

        txtCity = new JTextField();
        rightPanel.add(ViewUtils.labeled("City", txtCity));

        txtCountry = new JTextField();
        txtCountry.setEditable(true);
        rightPanel.add(ViewUtils.labeled("Country", txtCountry));

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
     * Validates all required fields in the document form.
     *
     * @return true if all required fields are valid, false otherwise
     */
    private boolean validateForm() {
        if (comboDocumentType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Document type is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (comboWorkflowStatus.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Workflow status is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!isBuy.isSelected() && !isSell.isSelected() && !isInternal.isSelected()) {
            JOptionPane.showMessageDialog(this, "Workflow type (Buy/Sell/Internal) is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!(comboClientSupplier.getSelectedItem() instanceof ComboBoxItem)) {
            JOptionPane.showMessageDialog(this, "Client/Supplier is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        ComboBoxItem<DocumentType> typeItem = (ComboBoxItem<DocumentType>) comboDocumentType.getSelectedItem();
        String typeName = typeItem.getObject().getName();
        if ((typeName.equals("Delivery") || typeName.equals("Command")) && !chkPlannedSendDate.isSelected()) {
            JOptionPane.showMessageDialog(this, "Planned send date is required for Delivery and Command types.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Validates user input and sends the document data to {@link DocumentController}
     * for creation or update.
     */
    private void saveEditForm() {
        if (!validateForm()) return;

        String commentaryText = commentary.getText().trim();

        LocalDate plannedSend = chkPlannedSendDate.isSelected() ? ViewUtils.getDate(pickerPlannedSendDate) : null;
        LocalDate plannedReception = chkPlannedReceptionDate.isSelected() ? ViewUtils.getDate(pickerPlannedReceptionDate) : null;
        LocalDate effectiveSend = chkEffectiveSendDate.isSelected() ? ViewUtils.getDate(pickerEffectiveSendDate) : null;
        LocalDate effectiveReception = chkEffectiveReceptionDate.isSelected() ? ViewUtils.getDate(pickerEffectiveReceptionDate) : null;

        int paymentDelay = (int) spnPaymentDelay.getValue();

        ComboBoxItem<Status> workflowStatusItem = (ComboBoxItem<Status>) comboWorkflowStatus.getSelectedItem();
        Status workflowStatus = workflowStatusItem.getObject();

        ComboBoxItem<DocumentType> documentTypeItem = (ComboBoxItem<DocumentType>) comboDocumentType.getSelectedItem();
        DocumentType documentType = documentTypeItem.getObject();

        ComboBoxItem<ClientSupplier> clientItem = (ComboBoxItem<ClientSupplier>) comboClientSupplier.getSelectedItem();
        ClientSupplier clientSupplier = clientItem.getObject();

        boolean isBuySelected = isBuy.isSelected();
        boolean isSellSelected = isSell.isSelected();
        boolean isInternalSelected = isInternal.isSelected();

        int streetNumber = (int) spnStreetNumber.getValue();
        int postalCode = (int) spnPostalCode.getValue();

        String street = txtStreet.getText().trim();
        String city = txtCity.getText().trim();
        String country = txtCountry.getText().trim();

        boolean isChecked = checkIsChecked.isSelected();

        try {
            if (currentDocument == null) {
                controller.createDocument(
                        documentType, commentaryText,
                        plannedSend, plannedReception,
                        effectiveSend, effectiveReception,
                        paymentDelay, workflowStatus,
                        isBuySelected, isSellSelected, isInternalSelected,
                        clientSupplier,
                        streetNumber, postalCode,
                        street, city, country,
                        isChecked
                );
            } else {
                controller.updateDocument(
                        currentDocument.getId(), documentType, commentaryText,
                        plannedSend, plannedReception,
                        effectiveSend, effectiveReception,
                        paymentDelay, workflowStatus,
                        isBuySelected, isSellSelected, isInternalSelected,
                        clientSupplier,
                        streetNumber, postalCode,
                        street, city, country,
                        isChecked
                );
            }

            JOptionPane.showMessageDialog(this, "Document saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            mainWindow.goBack();

        } catch (DataValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resets all input fields in the form to their default values.
     */
    public void clearForm() {
        commentary.setText("");

        Date now = new Date();

        chkPlannedSendDate.setSelected(false);
        chkPlannedReceptionDate.setSelected(false);
        chkEffectiveSendDate.setSelected(false);
        chkEffectiveReceptionDate.setSelected(false);

        pickerPlannedSendDate.setValue(now);
        pickerPlannedReceptionDate.setValue(now);
        pickerEffectiveSendDate.setValue(now);
        pickerEffectiveReceptionDate.setValue(now);

        spnPaymentDelay.setValue(0);

        checkIsChecked.setSelected(false);

        comboDocumentType.setSelectedIndex(-1);
        comboWorkflowStatus.setSelectedIndex(-1);
        comboClientSupplier.setSelectedIndex(-1);

        workflowGroup.clearSelection();

        spnStreetNumber.setValue(0);
        spnPostalCode.setValue(1000);

        txtStreet.setText("");
        txtCity.setText("");
        txtCountry.setText("");

        currentDocument = null;

        if (btnSave != null) {
            btnSave.setText("Save");
        }
    }

    /**
     * Loads data from a Document into the form.a
     *
     * @param doc document to display
     *            If doc is null, the form is in create mode
     */
    public void loadDocument(Document doc) {
        if (doc == null) {
            currentDocument = null;
            clearForm();
            return;
        }

        currentDocument = doc;

        commentary.setText(doc.getComment());

        if (doc.getPlannedSendDate() != null) {
            chkPlannedSendDate.setSelected(true);
            pickerPlannedSendDate.setEnabled(true);
            pickerPlannedSendDate.setValue(ViewUtils.toDate(doc.getPlannedSendDate()));
        }
        if (doc.getPlannedDateOfReceipt() != null) {
            chkPlannedReceptionDate.setSelected(true);
            pickerPlannedReceptionDate.setEnabled(true);
            pickerPlannedReceptionDate.setValue(ViewUtils.toDate(doc.getPlannedDateOfReceipt()));
        }
        if (doc.getActualSendDate() != null) {
            chkEffectiveSendDate.setSelected(true);
            pickerEffectiveSendDate.setEnabled(true);
            pickerEffectiveSendDate.setValue(ViewUtils.toDate(doc.getActualSendDate()));
        }
        if (doc.getActualDateOfReceipt() != null) {
            chkEffectiveReceptionDate.setSelected(true);
            pickerEffectiveReceptionDate.setEnabled(true);
            pickerEffectiveReceptionDate.setValue(ViewUtils.toDate(doc.getActualDateOfReceipt()));
        }


        spnPaymentDelay.setValue(doc.getPaymentDelay());

        isBuy.setSelected(doc.getWorkflow().getWorkflowType().getIsBuy());
        isSell.setSelected(doc.getWorkflow().getWorkflowType().getIsSell());
        isInternal.setSelected(doc.getWorkflow().getWorkflowType().getIsInternal());

        ComboBoxItem.selectComboItem(comboWorkflowStatus, doc.getWorkflow().getStatus());
        ComboBoxItem.selectComboItem(comboDocumentType, doc.getDocumentType());
        ComboBoxItem.selectComboItem(comboClientSupplier, doc.getClientSupplier());

        spnStreetNumber.setValue(doc.getAddress().getStreetNumber());
        spnPostalCode.setValue(doc.getAddress().getLocality().getPostalCode());

        txtStreet.setText(doc.getAddress().getStreetName());
        txtCity.setText(doc.getAddress().getLocality().getName());
        //txtCountry.setText(doc.getAddress().getLocality().getCountry();

        checkIsChecked.setSelected(doc.getIsChecked());
    }

    /**
     * Updates the client/supplier JComboBox list with available entries.
     *
     * @param clientSupplier list of available clients or suppliers
     */
    public void setClientSuppliers(ArrayList<ClientSupplier> clientSupplier) {
        comboClientSupplier.removeAllItems();

        for (ClientSupplier cs : clientSupplier) {
            String string = cs.getName() + " " + (cs.getFirstname() != null ? cs.getFirstname() : "");
            comboClientSupplier.addItem(new ComboBoxItem<>(cs, string));
        }
    }

    /**
     * Updates the document type JComboBox list with available entries.
     *
     * @param types list of available document types
     */
    public void setDocumentTypes(ArrayList<DocumentType> types) {
        comboDocumentType.removeAllItems();
        for (DocumentType dt : types) {
            comboDocumentType.addItem(new ComboBoxItem<>(dt, dt.getName()));
        }
    }

    /**
     * Updates the workflow status JComboBox list with available entries.
     *
     * @param statuses list of available workflow statuses
     */
    public void setWorkflowStatus(ArrayList<Status> statuses) {
        comboWorkflowStatus.removeAllItems();
        for (Status s : statuses) {
            comboWorkflowStatus.addItem(new ComboBoxItem<>(s, s.getName()));
        }
    }

    /**
     * This method opens a modal to create a new client/supplier.
     * If a new client or supplier is successfully created, it is added to the
     * ClientSuplier Comboboxlist.
     *
     * @see ClientSupplierForm
     */
    public void onNewClientcliked() {
        ClientSupplier newClient = openDialog();

        if (newClient != null) {
            allClients.add(newClient);
            setClientSuppliers(allClients);
            ComboBoxItem<ClientSupplier> newItem = comboClientSupplier.getItemAt(comboClientSupplier.getItemCount() - 1);
            comboClientSupplier.setSelectedItem(newItem);
        }
    }

    /**
     * Opens a modal dialog for creating or selecting a client/supplier.
     *
     * @return The new created {@code ClientSupplier} from the dialog,
     * or {@code null} if the dialog is closed before saving.
     */
    public ClientSupplier openDialog() {
        JDialog dialog = new JDialog(mainWindow, "Add new Client", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ClientSupplierForm form = new ClientSupplierForm(mainWindow, true);
        dialog.setContentPane(form);

        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);

        return form.getCurrentClientSupplier();
    }
}