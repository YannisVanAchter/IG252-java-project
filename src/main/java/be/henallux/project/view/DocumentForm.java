package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.DocumentController;
import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;

//TODO : nettoyer quand controller sera près

/**
 * DocumentForm represents the Form panel for creating and modifying a Document.
 * <p>
 * This form allows the user to enter all required information related to a document
 * (dates, workflow, client/supplier, address, etc.).
 * <p>
 * The form communicates with {@link DocumentController} to perform creation and update operations.
 */
public class DocumentForm extends JPanel {
    private final MainWindow mainWindow;
    private final DocumentController controller;
    private Document currentDocument;

    private final ArrayList<ClientSupplier> allClients;
    private final ArrayList<ComboBoxItem<ClientSupplier>> allClientItems = new ArrayList<>();


    private JPanel leftPanel, rightPanel;

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

    /**
     * Constructs a new instance of the DocumentForm.
     *
     * @param mainWindow the main application window associated with this form.
     */
    public DocumentForm(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = new DocumentController();
        this.allClients = controller.getAllClientSupplier();

        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * Builds the header panel containing the title and the back button.
     *
     * @return the header {@code JPanel}
     */
    private JPanel buildHeader() {
        JButton btnBack = new JButton("←");
        ViewUtils.setCursor(btnBack);
        btnBack.addActionListener(e -> mainWindow.goBack());

        JLabel title = new JLabel("Document Details");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.add(btnBack, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        return header;
    }

    /**
     * Constructs and returns the main form panel containing two subpanels.
     * The panel is organized using a {@code GridLayout} with two columns and a horizontal gap of 20 pixels.
     *
     * @return the constructed {@code JPanel} containing all elements.
     */
    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridLayout(1, 2, 20, 0));
        form.add(buildLeftPanel());
        form.add(buildRightPanel());
        return form;
    }

    /**
     * Builds and returns the left section of the document form.
     * This panel contains general document information such as
     * document type, commentary, planned/effective dates, payment delay, and validation check.
     * Required fields are marked with {@code *} using {@link ViewUtils#labeledRequired(String, JComponent)}.
     * Some inputs include validation constraints such as numeric-only fields using {@link ViewUtils#digitsOnly(JTextField)}.
     *
     * @return the left panel of the document form
     */
    private JPanel buildLeftPanel() {
        leftPanel = ViewUtils.createColumnPanel();
        JPanel leftContent = ViewUtils.createColumnPanel();
        leftContent.setBorder(BorderFactory.createTitledBorder("Document"));

        comboDocumentType = new JComboBox<>();
        ViewUtils.setCursor(comboDocumentType);
        comboDocumentType.setEditable(true);
        comboDocumentType.setToolTipText("Select the type of document");

        setDocumentTypes(controller.getAllDocumentType());

        ArrayList<ComboBoxItem<DocumentType>> allTypeItems = new ArrayList<>();
        for (int i = 0; i < comboDocumentType.getItemCount(); i++) {
            allTypeItems.add(comboDocumentType.getItemAt(i));
        }
        ViewUtils.setupAutoComplete(comboDocumentType, allTypeItems);
        leftContent.add(ViewUtils.labeledRequired("Document Type", comboDocumentType));

        commentary = new JTextArea(4, 20);
        ViewUtils.setCursor(commentary);
        commentary.setToolTipText("Optional comment about the document");
        leftContent.add(ViewUtils.labeled("Commentary", new JScrollPane(commentary)));

        chkPlannedSendDate = new JCheckBox();
        ViewUtils.setCursor(chkPlannedSendDate);
        pickerPlannedSendDate = ViewUtils.createDateSpinner();
        chkPlannedSendDate.setToolTipText("Required for Delivery document type");
        leftContent.add(ViewUtils.labeledToggleDate("Planned Send Date", pickerPlannedSendDate, chkPlannedSendDate));

        chkPlannedReceptionDate = new JCheckBox();
        ViewUtils.setCursor(chkPlannedReceptionDate);
        pickerPlannedReceptionDate = ViewUtils.createDateSpinner();
        chkPlannedReceptionDate.setToolTipText("Check to set a planned reception date");
        leftContent.add(ViewUtils.labeledToggleDate("Planned Reception Date", pickerPlannedReceptionDate, chkPlannedReceptionDate));

        chkEffectiveSendDate = new JCheckBox();
        ViewUtils.setCursor(chkEffectiveSendDate);
        pickerEffectiveSendDate = ViewUtils.createDateSpinner();
        chkEffectiveSendDate.setToolTipText("Check to set the effective send date");
        leftContent.add(ViewUtils.labeledToggleDate("Effective Send Date", pickerEffectiveSendDate, chkEffectiveSendDate));

        chkEffectiveReceptionDate = new JCheckBox();
        ViewUtils.setCursor(chkEffectiveReceptionDate);
        pickerEffectiveReceptionDate = ViewUtils.createDateSpinner();
        chkEffectiveReceptionDate.setToolTipText("Check to set the effective reception date");
        leftContent.add(ViewUtils.labeledToggleDate("Effective Reception Date", pickerEffectiveReceptionDate, chkEffectiveReceptionDate));

        spnPaymentDelay = ViewUtils.createNumberSpinner(0, -1, 3650, 1);
        spnPaymentDelay.setToolTipText("Number of days allowed for payment, minimum 0");
        leftContent.add(ViewUtils.labeled("Payment Delay", spnPaymentDelay));

        checkIsChecked = new JCheckBox("Document is checked");
        ViewUtils.setCursor(checkIsChecked);
        leftContent.add(checkIsChecked);

        leftContent.setMaximumSize(new Dimension(Integer.MAX_VALUE, leftContent.getPreferredSize().height));
        leftPanel.add(leftContent);
        return leftPanel;
    }

    /**
     * Builds and returns the right section of the form.
     * This panel contains two grouped sections:
     * <ul><li>Workflox information: Workflow status, Workflow type.</li>
     *   <li>Address information: street, street number, postal code, city, and country.</li></ul>
     * Some fields include predefined values, such as the non-editable country field
     * or restrictions such as numeric spinners for address and loyalty data.
     *
     * @return the right form panel
     */
    private JPanel buildRightPanel() {
        rightPanel = ViewUtils.createColumnPanel();
        JPanel workflowPanel = ViewUtils.createColumnPanel();
        workflowPanel.setBorder(BorderFactory.createTitledBorder("Workflow"));

        comboWorkflowStatus = new JComboBox<>();
        ViewUtils.setCursor(comboWorkflowStatus);
        comboWorkflowStatus.setEditable(true);
        comboWorkflowStatus.setToolTipText("Current status of the workflow");
        setWorkflowStatus(controller.getAllWorkflowStatus());

        ArrayList<ComboBoxItem<Status>> allStatusItems = new ArrayList<>();
        for (int i = 0; i < comboWorkflowStatus.getItemCount(); i++) {
            allStatusItems.add(comboWorkflowStatus.getItemAt(i));
        }
        ViewUtils.setupAutoComplete(comboWorkflowStatus, allStatusItems);
        workflowPanel.add(ViewUtils.labeledRequired("Workflow Status", comboWorkflowStatus));

        isBuy = new JRadioButton("Buy");
        ViewUtils.setCursor(isBuy);
        isSell = new JRadioButton("Sell");
        ViewUtils.setCursor(isSell);
        isInternal = new JRadioButton("Internal");
        ViewUtils.setCursor(isInternal);

        workflowGroup = new ButtonGroup();
        workflowGroup.add(isBuy);
        workflowGroup.add(isSell);
        workflowGroup.add(isInternal);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        radioPanel.add(isBuy);
        radioPanel.add(Box.createHorizontalStrut(5));
        radioPanel.add(isSell);
        radioPanel.add(Box.createHorizontalStrut(5));
        radioPanel.add(isInternal);
        workflowPanel.add(ViewUtils.labeled("Workflow Type *", radioPanel));

        JPanel clientPanel = ViewUtils.createColumnPanel();
        clientPanel.setBorder(BorderFactory.createTitledBorder("Client / Supplier"));

        comboClientSupplier = new JComboBox<>();
        ViewUtils.setCursor(comboClientSupplier);
        comboClientSupplier.setEditable(true);
        setClientSuppliers(allClients);

        for (int i = 0; i < comboClientSupplier.getItemCount(); i++) {
            allClientItems.add(comboClientSupplier.getItemAt(i));
        }
        ViewUtils.setupAutoComplete(comboClientSupplier, allClientItems);

        JButton btnNewClient = new JButton("New");
        ViewUtils.setCursor(btnNewClient);
        btnNewClient.addActionListener(e -> onNewClientClicked());

        JPanel clientRow = new JPanel();
        clientRow.setLayout(new BoxLayout(clientRow, BoxLayout.X_AXIS));
        clientRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        clientRow.add(comboClientSupplier);
        clientRow.add(Box.createHorizontalStrut(10));
        clientRow.add(btnNewClient);
        clientPanel.add(clientRow);

        JPanel addressPanel = ViewUtils.createColumnPanel();
        addressPanel.setBorder(BorderFactory.createTitledBorder("Address"));

        txtStreet = new JTextField(10);
        ViewUtils.setCursor(txtStreet);
        spnStreetNumber = ViewUtils.createNumberSpinner(1, 1, 10000, 1);
        addressPanel.add(ViewUtils.horizontalRowGroup(
                ViewUtils.labeledRequired("Street", txtStreet),
                ViewUtils.labeledRequired("Street Number", spnStreetNumber)
        ));

        addressPanel.add(Box.createVerticalStrut(8));

        spnPostalCode = ViewUtils.createNumberSpinner(1000, 1, 9999, 1000);
        txtCity = new JTextField(10);
        ViewUtils.setCursor(txtCity);
        addressPanel.add(ViewUtils.horizontalRowGroup(
                ViewUtils.labeledRequired("Postal Code", spnPostalCode),
                ViewUtils.labeledRequired("City", txtCity)
        ));

        addressPanel.add(Box.createVerticalStrut(8));

        txtCountry = new JTextField("Belgium");
        ViewUtils.setCursor(txtCountry);
        txtCountry.setEditable(false);
        txtCountry.setFocusable(false);
        txtCountry.setBackground(Color.LIGHT_GRAY);
        txtCountry.setForeground(Color.DARK_GRAY);
        addressPanel.add(ViewUtils.horizontalRowGroup(ViewUtils.labeled("Country", txtCountry), new JPanel()));

        workflowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        clientPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        workflowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, workflowPanel.getPreferredSize().height));
        clientPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, clientPanel.getPreferredSize().height));
        addressPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, addressPanel.getPreferredSize().height));

        rightPanel.add(workflowPanel);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(clientPanel);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(addressPanel);

        return rightPanel;
    }

    private JPanel buildButtonPanel() {
        btnSave = new JButton("Save");
        ViewUtils.setCursor(btnSave);
        btnSave.addActionListener(e -> saveEditForm());

        JButton btnClear = new JButton("Clear");
        ViewUtils.setCursor(btnClear);
        btnClear.addActionListener(e -> clearForm());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.add(btnClear);
        panel.add(btnSave);
        return panel;
    }

    /**
     * Validates the form fields before submission.
     * Checks that all required fields are filled and follow the rules imposed to correctly fill out the database.
     * The check requirement use {@link #typeRequirement(List, String)}
     * If a validation rule fails, a warning dialog is displayed and the method returns {@code false}.
     *
     * @return {@code true} if all validation rules pass; {@code false} otherwise
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
            JOptionPane.showMessageDialog(this, "A client/supplier is required. Select or create a new one.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String typeName = getSelectedTypeName();

        if (typeRequirement(Document.TYPES_REQUIRING_PLANNED_SEND_DATE, typeName)) {
            if (!chkPlannedSendDate.isSelected()) {
                JOptionPane.showMessageDialog(this, "Planned send date is required for Delivery type.", "Validation", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        if (typeRequirement(Document.TYPES_REQUIRING_RECEPTION_DATE, typeName)) {
            if (!chkPlannedReceptionDate.isSelected()) {
                JOptionPane.showMessageDialog(this, "Planned reception date is required for Delivery type.", "Validation", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        if (typeRequirement(Document.TYPES_REQUIRING_ADDRESS, typeName)) {
            if (txtStreet.getText().trim().isEmpty() || txtCity.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Address (street, city, country) is required for Delivery type.", "Validation", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        if (typeRequirement(Document.TYPES_REQUIRING_PAYMENT_DELAY, typeName)) {
            int paymentDelay = (int) spnPaymentDelay.getValue();
            if (paymentDelay <= 0) {
                JOptionPane.showMessageDialog(this, "Payment delay is required and must be >= 0 for Command type.", "Validation", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        if (typeRequirement(Document.TYPES_REQUIRING_COMMENTARY, typeName)) {
            if (commentary.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Commentary is required for Preparation Order type.", "Validation", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    /**
     * Checks the list of document types contains an element whose name matches the specified type name.
     * The comparison is case-insensitive.
     *
     * @param list     the list of {@link DocumentType} to search through
     * @param typeName the name of the document type to look for
     * @return {@code true} if a matching document type is found, {@code false} otherwise
     */
    private boolean typeRequirement(List<DocumentType> list, String typeName) {
        int i = 0;
        boolean found = false;

        while (i < list.size() && !found) {
            DocumentType dt = list.get(i);
            if (dt.getName().equalsIgnoreCase(typeName)) {
                found = true;
            }
            i++;
        }
        return found;
    }

    /**
     * Helper pour extraire le nom du type de document sélectionné.
     */
    private String getSelectedTypeName() {
        Object selected = comboDocumentType.getSelectedItem();
        if (selected instanceof ComboBoxItem<?> item && item.getObject() instanceof DocumentType documentType) {
            return documentType.getName();
        } else if (selected instanceof String text) {
            return text.trim();
        }
        return "";
    }

    /**
     * Validates the form and saves a document.
     * This method first calls {@link #validateForm()} to ensure all required fields are correctly filled.
     * If the form is valid, all values are collected and sent to the controller
     * to either create a new document or update an existing one depending on
     * whether {@code currentDocument} is {@code null}.
     * After a successful save, a confirmation message is displayed and the view is closed via {@link MainWindow#goBack()}.
     * In case of errors, a {@code JDialog} is displayed with the error message.
     */
    private void saveEditForm() {
        if (!validateForm()) return;

        String commentaryText = commentary.getText().trim();

        LocalDate plannedSend = chkPlannedSendDate.isSelected() ? ViewUtils.getDate(pickerPlannedSendDate) : null;
        LocalDate plannedReception = chkPlannedReceptionDate.isSelected() ? ViewUtils.getDate(pickerPlannedReceptionDate) : null;
        LocalDate effectiveSend = chkEffectiveSendDate.isSelected() ? ViewUtils.getDate(pickerEffectiveSendDate) : null;
        LocalDate effectiveReception = chkEffectiveReceptionDate.isSelected() ? ViewUtils.getDate(pickerEffectiveReceptionDate) : null;

        int paymentDelay = (int) spnPaymentDelay.getValue();

        Object workflowSelected = comboWorkflowStatus.getSelectedItem();
        Status workflowStatus = null;

        if (workflowSelected instanceof ComboBoxItem<?> item) {
            workflowStatus = (Status) item.getObject();
        } else if (workflowSelected instanceof String text) {
            String value = text.trim();
            if (!value.isEmpty()) {
                try {
                    workflowStatus = controller.createStatus(value);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Unable to create the status : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        Object selectedDocumentType = comboDocumentType.getSelectedItem();
        DocumentType documentType = null;

        if (selectedDocumentType instanceof ComboBoxItem<?> item) {
            documentType = (DocumentType) item.getObject();
        } else if (selectedDocumentType instanceof String text) {
            String value = text.trim();
            if (!value.isEmpty()) {
                try {
                    documentType = controller.createDocumentType(value);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Unable to create document type : " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

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
        txtCountry.setText("Belgium");

        currentDocument = null;

        if (btnSave != null) {
            btnSave.setText("Save");
        }
    }

    /**
     * Loads a document into the form.
     * If {@code doc} is {@code null}, the form is reset and switched to create mode.
     * Otherwise, all document data is displayed in the form and the interface is updated to edit mode.
     *
     * @param doc the document to display;
     *            {@code null} to initialize the form in creation mode
     */
    public void loadDocument(Document doc) {
        if (doc == null) {
            currentDocument = null;
            clearForm();
            return;
        }

        currentDocument = doc;

        commentary.setText(doc.getComment() != null ? doc.getComment() : "");

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

        if (doc.getPaymentDelay() != null) {
            spnPaymentDelay.setValue(doc.getPaymentDelay());
        }

        isBuy.setSelected(doc.getWorkflow().getWorkflowType().getIsBuy());
        isSell.setSelected(doc.getWorkflow().getWorkflowType().getIsSell());
        isInternal.setSelected(doc.getWorkflow().getWorkflowType().getIsInternal());

        ComboBoxItem.selectComboItem(comboWorkflowStatus, doc.getWorkflow().getStatus());
        ComboBoxItem.selectComboItem(comboDocumentType, doc.getDocumentType());
        // ComboBoxItem.selectComboItem(comboClientSupplier, doc.getDetails().getClientSupplier()); // TODO : doc.getDetails().getClientSupplier() — client/supplier not yet available

        if (doc.getAddress() != null) {
            spnStreetNumber.setValue(doc.getAddress().getStreetNumber());
            txtStreet.setText(doc.getAddress().getStreetName() != null ? doc.getAddress().getStreetName() : "");

            if (doc.getAddress().getLocality() != null) {
                spnPostalCode.setValue(doc.getAddress().getLocality().getPostalCode());
                txtCity.setText(doc.getAddress().getLocality().getCity() != null
                        ? doc.getAddress().getLocality().getCity() : "");
            }
        }

        txtCountry.setText("Belgium");

        checkIsChecked.setSelected(doc.getIsChecked());

        btnSave.setText("Edit");
    }

    /**
     * Updates the client/supplier JComboBox list with available entries.
     *
     * @param clientSupplier list of available clients or suppliers
     */
    public void setClientSuppliers(ArrayList<ClientSupplier> clientSupplier) {
        comboClientSupplier.removeAllItems();
        for (ClientSupplier cs : clientSupplier) {
            String label = cs.getName() + " " + (cs.getFirstname() != null ? cs.getFirstname() : "");
            comboClientSupplier.addItem(new ComboBoxItem<>(cs, label.trim()));
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
     * Opens a modal dialog for creating a new client/supplier and adds it to the combo.
     */
    public void onNewClientClicked() {
        ClientSupplier newClient = openDialog();
        if (newClient != null) {
            allClients.add(newClient);
            setClientSuppliers(allClients);

            allClientItems.clear();
            for (int i = 0; i < comboClientSupplier.getItemCount(); i++) {
                allClientItems.add(comboClientSupplier.getItemAt(i));
            }

            ComboBoxItem<ClientSupplier> newItem = comboClientSupplier.getItemAt(comboClientSupplier.getItemCount() - 1);
            comboClientSupplier.setSelectedItem(newItem);
        }
    }

    /**
     * Opens a modal dialog for creating a new client/supplier.
     *
     * @return The newly created {@code ClientSupplier}, or {@code null} if the dialog was closed without saving.
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