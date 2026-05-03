package view;

import controller.*;

import java.awt.*;
import java.time.*;
import java.util.Date;
import javax.swing.*;

import model.*;
// Todo : uncomment loyalty & Country in loadDocument when available

/**
 * ClientSupplierForm represents the form for creating and modifying a Client or Supplier.
 * <p>
 * This form allows the user to enter all required information related to a Client/Supplier
 * (name, fistname, type, address, etc.).
 * <p>
 * This view can be open with {@link CardLayout} in JPanel via {@link ClientSupplierTable}
 * This view can be open with {@link JDialog} in modal via {@link DocumentForm}
 * <p>
 * The form communicates with {@link ClientSupplierController} to perform creation and update operations.
 */
public class ClientSupplierForm extends JPanel {
    private MainWindow mainWindow;
    private Boolean isOpenInModal;
    private ClientSupplierController controller;
    private ClientSupplier currentClientSupplier;

    private JPanel appPanel, panelContent, leftPanel, rightPanel;

    private JTextField txtName;
    private JTextField txtFirstName;
    private JTextField txtMail;
    private JTextField txtPhoneNumber;
    private JTextField txtVATNumber;

    private JSpinner becameClientDate;

    private JTextField txtIdLoyaltyCard;
    private JSpinner spnLoyaltyPoint;

    private JCheckBox chkIsClient;
    private JCheckBox chkIsSupplier;
    private JCheckBox chkIsMember;

    private JSpinner spnStreetNumber;
    private JSpinner spnPostalCode;

    private JTextField txtStreet;
    private JTextField txtCity;
    private JTextField txtCountry;

    private JButton btnSave;
    private JButton btnClear;
    private JButton btnNewClient;

    /**
     * Constructs a new instance of the ClientSupplierForm.
     *
     * @param mainWindow    the main application window associated with this form.
     * @param isOpenInModal a flag indicating if the form is open in a modal window.
     */
    public ClientSupplierForm(MainWindow mainWindow, Boolean isOpenInModal) {
        this.mainWindow = mainWindow;
        this.isOpenInModal = isOpenInModal;
        this.controller = new ClientSupplierController();

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Client Details");
        title.setFont(new Font("Inter", Font.BOLD, 20));

        JPanel topBar = new JPanel(new BorderLayout());

        JButton btnBack = new JButton("←");
        btnBack.addActionListener(e -> {
            if (isOpenInModal != null && isOpenInModal) {
                SwingUtilities.getWindowAncestor(this).dispose();
            } else {
                mainWindow.goBack();
            }
        });
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

    /**
     * The main Constructor for a new instance of the ClientSupplierForm.
     *
     * @param mainWindow the main application window associated with this form.
     */
    public ClientSupplierForm(MainWindow mainWindow) {
        this(mainWindow, false);
    }

    private void buildLeftPanel() {
        leftPanel = ViewUtils.createColumnPanel();

        txtName = new JTextField(10);
        leftPanel.add(ViewUtils.labeled("Name", txtName));

        txtMail = new JTextField(10);
        leftPanel.add(ViewUtils.labeled("Mail", txtMail));

        becameClientDate = ViewUtils.createDateSpinner();
        leftPanel.add(ViewUtils.labeled("Become client date", becameClientDate));

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        chkIsClient = new JCheckBox("Client");
        chkIsSupplier = new JCheckBox("Supplier");
        chkIsMember = new JCheckBox("Staff member");

        statusPanel.add(chkIsClient);
        statusPanel.add(Box.createHorizontalStrut(10));
        statusPanel.add(chkIsSupplier);
        statusPanel.add(Box.createHorizontalStrut(10));
        statusPanel.add(chkIsMember);
        leftPanel.add(statusPanel);

        txtStreet = new JTextField(10);
        leftPanel.add(ViewUtils.labeled("Street", txtStreet));
        txtCity = new JTextField(10);
        leftPanel.add(ViewUtils.labeled("City", txtCity));
        txtCountry = new JTextField(10);
        leftPanel.add(ViewUtils.labeled("Country", txtCountry));
    }

    private void buildRightPanel() {
        rightPanel = ViewUtils.createColumnPanel();

        txtFirstName = new JTextField(10);
        rightPanel.add(ViewUtils.labeled("FirstName", txtFirstName));

        txtPhoneNumber = new JTextField(10);
        rightPanel.add(ViewUtils.labeled("Phone Number", txtPhoneNumber));

        txtVATNumber = new JTextField(10);
        rightPanel.add(ViewUtils.labeled("VAT Number", txtVATNumber));

        txtIdLoyaltyCard = new JTextField(10);
        rightPanel.add(ViewUtils.labeled("Loyality cart ID", txtIdLoyaltyCard));

        spnLoyaltyPoint = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
        spnLoyaltyPoint.setEditor(new JSpinner.NumberEditor(spnLoyaltyPoint, "#"));
        rightPanel.add(ViewUtils.labeled("Loyality Point", spnLoyaltyPoint));

        spnStreetNumber = new JSpinner(new SpinnerNumberModel(1, 0, 99999, 1));
        spnStreetNumber.setEditor(new JSpinner.NumberEditor(spnStreetNumber, "#"));
        rightPanel.add(ViewUtils.labeled("Street number", spnStreetNumber));
        spnPostalCode = new JSpinner(new SpinnerNumberModel(1000, 0, 99999, 1));
        spnPostalCode.setEditor(new JSpinner.NumberEditor(spnPostalCode, "#"));
        rightPanel.add(ViewUtils.labeled("Postal Code", spnPostalCode));


        btnSave = new JButton("Save");
        if (currentClientSupplier == null) {
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
     * Validates user input and sends the document data to {@link ClientSupplierController}
     * for creation.
     * <p>
     * If required fields are missing, a dialog is displayed and the process
     * is stopped.
     * <p>
     * If {@code openingInModal} is true. The modal closes and DocumentForm
     * can get the new ClientSupplier.
     */
    private void saveEditForm() {

        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name required");
            return;
        }

        if (txtMail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mail required");
            return;
        }

        if (!chkIsClient.isSelected() && !chkIsSupplier.isSelected() && !chkIsMember.isSelected()) {
            JOptionPane.showMessageDialog(this, "Status required");
            return;
        }
        String name = txtName.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String mail = txtMail.getText().trim();
        String phoneNumber = txtPhoneNumber.getText().trim();
        String vatNumber = txtVATNumber.getText().trim();

        LocalDate becameClient = ViewUtils.getDate(becameClientDate);

        String loyaltyCardId = txtIdLoyaltyCard.getText().trim();
        int loyaltyPoints = (int) spnLoyaltyPoint.getValue();

        boolean isClient = chkIsClient.isSelected();
        boolean isSupplier = chkIsSupplier.isSelected();
        boolean isMember = chkIsMember.isSelected();

        int streetNumber = (int) spnStreetNumber.getValue();
        int postalCode = (int) spnPostalCode.getValue();

        String street = txtStreet.getText().trim();
        String city = txtCity.getText().trim();
        String country = txtCountry.getText().trim();

        try {
            if (currentClientSupplier == null) {
                currentClientSupplier = controller.createClientSupplier(
                        name, firstName, mail, phoneNumber, vatNumber,
                        becameClient,
                        loyaltyCardId, loyaltyPoints,
                        isClient, isSupplier, isMember,
                        streetNumber, postalCode,
                        street, city, country
                );
            } else {
                currentClientSupplier = controller.updateClientSupplier(
                        currentClientSupplier.getId(),
                        name, firstName, mail, phoneNumber, vatNumber,
                        becameClient,
                        loyaltyCardId, loyaltyPoints,
                        isClient, isSupplier, isMember,
                        streetNumber, postalCode,
                        street, city, country
                );
            }

            JOptionPane.showMessageDialog(this, "Client/Supplier saved!");

            if (isOpenInModal != null && isOpenInModal) {
                SwingUtilities.getWindowAncestor(this).dispose();
            } else {
                mainWindow.goBack();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    /**
     * Resets all input fields in the form to their default values.
     */
    public void clearForm() {

        txtName.setText("");
        txtFirstName.setText("");
        txtMail.setText("");
        txtPhoneNumber.setText("");
        txtVATNumber.setText("");

        becameClientDate.setValue(new Date());

        txtIdLoyaltyCard.setText("");
        spnLoyaltyPoint.setValue(0);

        chkIsClient.setSelected(false);
        chkIsSupplier.setSelected(false);
        chkIsMember.setSelected(false);

        spnStreetNumber.setValue(0);
        spnPostalCode.setValue(1000);

        txtStreet.setText("");
        txtCity.setText("");
        txtCountry.setText("");

        currentClientSupplier = null;
        isOpenInModal = false;
    }

    /**
     * Loads data from a Client/Supplier into the form.
     *
     * @param cs document to display
     * If cs is null the form is in create mode
     */
    public void loadClientSupplier(ClientSupplier cs) {

        if (cs == null) {
            currentClientSupplier = null;
            clearForm();
            return;
        }

        currentClientSupplier = cs;

        txtName.setText(cs.getName());
        txtFirstName.setText(cs.getFirstname());
        txtMail.setText(cs.getEmail());
        txtVATNumber.setText(cs.getVATNumber());

        if (cs.getBecameClientDate() != null) {
            becameClientDate.setValue(ViewUtils.toDate(cs.getBecameClientDate()));
        }

        //txtIdLoyaltyCard.setText(cs.getLoyaltyCard.getId());
        //spnLoyaltyPoint.setValue(cs.getLoyaltyCard.getPoints());

        chkIsClient.setSelected(cs.getIsClient());
        chkIsSupplier.setSelected(cs.getIsSupplier());
        chkIsMember.setSelected(cs.getIsUs());

        spnStreetNumber.setValue(cs.getAddress().getStreetNumber());
        spnPostalCode.setValue(cs.getAddress().getLocality().getPostalCode());

        txtStreet.setText(cs.getAddress().getStreetName());
        txtCity.setText(cs.getAddress().getLocality().getName());
        //txtCountry.setText(cs.getAddress().getLocation().getCountry);

        btnSave.setText("Edit");
    }

    public ClientSupplier getCurrentClientSupplier() {
        return currentClientSupplier;
    }
}