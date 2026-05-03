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

    private JPanel appPanel, panelContent, leftPanel, rightPanel;

    private JTextArea commentary;
    private JCheckBox checkIsChecked;

    private JSpinner pickerPlannedSendDate;
    private JSpinner pickerPlannedReceptionDate;
    private JSpinner pickerEffectiveSendDate;
    private JSpinner pickerEffectiveReceptionDate;
    private JSpinner spnPaymentDelay;


    private JComboBox<String> comboDocumentType;
    private JComboBox<String> comboClientSupplier;
    private JComboBox<String> comboWorkflowStatus;

    private JRadioButton rbBuy;
    private JRadioButton rbSell;
    private JRadioButton rbInternal;
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

        comboDocumentType = new JComboBox<>(controller.getAllDocumentType());
        comboDocumentType.setEditable(true);
        leftPanel.add(ViewUtils.labeled("Document Type", comboDocumentType));

        commentary = new JTextArea(4, 20);
        leftPanel.add(ViewUtils.labeled("Commentaire", new JScrollPane(commentary))); // FIX important

        pickerPlannedSendDate = ViewUtils.createDateSpinner();
        leftPanel.add(ViewUtils.labeled("Planned Send Date", pickerPlannedSendDate));

        pickerPlannedReceptionDate = ViewUtils.createDateSpinner();
        leftPanel.add(ViewUtils.labeled("Planned Reception Date", pickerPlannedReceptionDate));

        pickerEffectiveSendDate = ViewUtils.createDateSpinner();
        leftPanel.add(ViewUtils.labeled("Effective Send Date", pickerEffectiveSendDate));

        pickerEffectiveReceptionDate = ViewUtils.createDateSpinner();
        leftPanel.add(ViewUtils.labeled("Effective Reception Date", pickerEffectiveReceptionDate)); // FIX bug

        spnPaymentDelay = ViewUtils.createNumberSpinner(0, -1, 3650, 1);
        leftPanel.add(ViewUtils.labeled("Payment delay", spnPaymentDelay));

        checkIsChecked = new JCheckBox("Document is checked");
        leftPanel.add(checkIsChecked);
    }

    private void buildRightPanel() throws DataValidationException {
        rightPanel = ViewUtils.createColumnPanel();

        comboWorkflowStatus = new JComboBox<>(controller.getAllWorkflowStatus());
        comboWorkflowStatus.setEditable(true);
        rightPanel.add(ViewUtils.labeled("Workflow Status", comboWorkflowStatus));

        rbBuy = new JRadioButton("Buy");
        rbSell = new JRadioButton("Sell");
        rbInternal = new JRadioButton("Internal");

        workflowGroup = new ButtonGroup();
        workflowGroup.add(rbBuy);
        workflowGroup.add(rbSell);
        workflowGroup.add(rbInternal);

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
        radioPanel.add(rbBuy);
        radioPanel.add(rbSell);
        radioPanel.add(rbInternal);

        rightPanel.add(ViewUtils.labeled("WorkflowType", radioPanel));

        comboClientSupplier = new JComboBox<>(controller.getAllClientNames());
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

        if (currentDocument != null) {
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

        if (comboDocumentType.getSelectedItem() == null || comboDocumentType.getSelectedItem().toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Document type required");
            return;
        }

        if (comboWorkflowStatus.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Workflow status required");
            return;
        }

        if (comboClientSupplier.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Client/Supplier required");
            return;
        }

        if (!rbBuy.isSelected() && !rbSell.isSelected() && !rbInternal.isSelected()) {
            JOptionPane.showMessageDialog(this, "Workflow type (Buy/Sell/Internal) required");
            return;
        }

        String commentaryText = commentary.getText();

        LocalDate plannedSend = ViewUtils.getDate(pickerPlannedSendDate);
        LocalDate plannedReception = ViewUtils.getDate(pickerPlannedReceptionDate);
        LocalDate effectiveSend = ViewUtils.getDate(pickerEffectiveSendDate);
        LocalDate effectiveReception = ViewUtils.getDate(pickerEffectiveReceptionDate);

        int paymentDelay = (int) spnPaymentDelay.getValue();

        String workflowStatus = comboWorkflowStatus.getSelectedItem().toString().trim();
        String documentType = comboDocumentType.getSelectedItem().toString().trim();
        String clientSupplier = comboClientSupplier.getSelectedItem().toString().trim();

        boolean isBuy = rbBuy.isSelected();
        boolean isSell = rbSell.isSelected();
        boolean isInternal = rbInternal.isSelected();

        int streetNumber = (int) spnStreetNumber.getValue();
        int postalCode = (int) spnPostalCode.getValue();

        String street = txtStreet.getText();
        String city = txtCity.getText();
        String country = txtCountry.getText();

        boolean isChecked = checkIsChecked.isSelected();

        try {
            if (currentDocument == null) {
                controller.createDocument(
                        documentType, commentaryText,
                        plannedSend, plannedReception,
                        effectiveSend, effectiveReception,
                        paymentDelay,
                        workflowStatus,
                        isBuy, isSell, isInternal,
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
                        isBuy, isSell, isInternal,
                        clientSupplier,
                        streetNumber, postalCode,
                        street, city, country,
                        isChecked
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
        commentary.setText("");

        Date now = new Date();

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

        commentary.setText(doc.getComment());

        if (doc.getPlannedSendDate() != null)
            pickerPlannedSendDate.setValue(ViewUtils.toDate(doc.getPlannedSendDate()));

        if (doc.getPlannedDateOfReceipt() != null)
            pickerPlannedReceptionDate.setValue(ViewUtils.toDate(doc.getPlannedDateOfReceipt()));

        if (doc.getActualSendDate() != null)
            pickerEffectiveSendDate.setValue(ViewUtils.toDate(doc.getActualSendDate()));

        if (doc.getActualDateOfReceipt() != null)
            pickerEffectiveReceptionDate.setValue(ViewUtils.toDate(doc.getActualDateOfReceipt()));

        spnPaymentDelay.setValue(doc.getPaymentDelay());

        comboWorkflowStatus.setSelectedItem(doc.getWorkflow().getStatus());

        rbBuy.setSelected(doc.getWorkflow().getWorkflowType().getIsBuy());
        rbSell.setSelected(doc.getWorkflow().getWorkflowType().getIsSell());
        rbInternal.setSelected(doc.getWorkflow().getWorkflowType().getIsInternal());
        comboClientSupplier.setSelectedItem(doc.getClientSupplier());

        spnStreetNumber.setValue(doc.getAddress().getStreetNumber());
        spnPostalCode.setValue(doc.getAddress().getLocality().getPostalCode());

        txtStreet.setText(doc.getAddress().getStreetName());
        txtCity.setText(doc.getAddress().getLocality().getName());
        //txtCountry.setText(doc.getAddress().getLocality().getCountry();

        checkIsChecked.setSelected(doc.getIsChecked());
    }

    /**
     * This method opens a modal to create a new client/supplier.
     * If a new client or supplier is successfully created, it is added to the
     * ClientSuplier Comboboxlist.
     * @see ClientSupplierForm
     */
    public void onNewClientcliked(){
        ClientSupplier newClient = openDialog();

        if(newClient != null){
            allClients.add(newClient);
            comboClientSupplier.addItem(newClient.getName());
            comboClientSupplier.setSelectedItem(newClient.getName());
        } else {
            System.out.println("pas ok");
        }
    }

    /**
     * Opens a modal dialog for creating or selecting a client/supplier.
     * @return The new created {@code ClientSupplier} from the dialog,
     *         or {@code null} if the dialog is closed before saving.
     */
    public ClientSupplier openDialog(){
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