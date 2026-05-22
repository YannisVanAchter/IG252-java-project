package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.*;

import java.awt.*;
import java.time.*;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import main.java.be.henallux.project.model.*;

/**
 * ClientSupplierForm represents the form for creating and modifying a Client or Supplier.
 * <p>This form allows the user to enter all required information related to a Client/Supplier
 * (name, firstname, type, address, etc.).
 * <p>This view can be open with {@link CardLayout} in JPanel via {@link ClientSupplierTable}
 * This view can be open with {@link JDialog} in modal via {@link DocumentForm}
 * <p>The form communicates with {@link ClientSupplierController} to perform creation and update operations.
 *
 * @see ClientSupplierController
 * @see ClientSupplier
 * @see ClientSupplierTable
 * @see DocumentForm
 */
public class ClientSupplierForm extends JPanel {
    private final MainWindow mainWindow;
    private final Boolean isOpenInModal;
    private final ClientSupplierController controller;
    private ClientSupplier currentClientSupplier;

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

    /**
     * Constructs a new instance of the ClientSupplierForm.
     *
     * @param mainWindow    the main application window associated with this form.
     * @param isOpenInModal a flag indicating if the form is open in a modal window.
     * @see MainWindow
     */
    public ClientSupplierForm(MainWindow mainWindow, Boolean isOpenInModal) {
        this.mainWindow = mainWindow;
        this.isOpenInModal = isOpenInModal;
        this.controller = new ClientSupplierController();

        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * The main Constructor for a new instance which is not open in modal.
     *
     * @param mainWindow the main application window associated with this form.
     * @see #ClientSupplierForm(MainWindow, Boolean)
     */
    public ClientSupplierForm(MainWindow mainWindow) {
        this(mainWindow, false);
    }

    /**
     * Builds the header panel containing the title and the back button.
     * Builds the header section of the form containing navigation controls and title.
     * <p>The header provides a back button whose behavior depends on the current
     * display mode:
     * <ul><li>closes the dialog if the form is opened in modal mode</li>
     *     <li>navigates back using {@link MainWindow#goBack()} otherwise</li>/ul>
     *
     * @return a {@link JPanel} representing the form header
     * @see MainWindow#goBack()
     */
    private JPanel buildHeader() {
        JButton btnBack = new JButton("←");
        ViewUtils.setCursor(btnBack);
        btnBack.addActionListener(e -> {
            if (isOpenInModal != null && isOpenInModal) {
                SwingUtilities.getWindowAncestor(this).dispose();
            } else {
                mainWindow.goBack();
            }
        });

        JLabel title = new JLabel("Client Details");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.add(btnBack, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        return header;
    }

    /**
     * Constructs and returns the main form panel containing two subpanels.
     * <p>The panel is organized using a {@link  GridLayout} with two columns.
     * <p>Using a {@link JScrollPane} for automatic adaptation to window resizing
     *
     * @return a {@code JScrollPane} containing all elements.
     */
    private JScrollPane buildFormPanel() {
        JPanel form = new JPanel(new GridLayout(1, 2, 20, 0));
        form.add(buildLeftPanel());
        form.add(buildRightPanel());

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        return scrollPane;
    }


    /**
     * Builds and returns the left section of the form.
     * <p>This panel contains the identity and contact information fields:
     * <ul><li>Name and first name</li>
     *     <li>Email and phone number</li>
     *     <li>VAT number</li>
     *     <li>Client since date</li>
     *     <li>Client/supplier/staff type selection</li></ul>
     * <p>Required fields are marked by a {@code *} using {@link ViewUtils#labeledRequired(String, JComponent)}.
     * <p>Numeric JTexfild contains numeric-only constraints such on keyboard input. {@link ViewUtils#digitsOnly(JTextField)}
     *
     * @return a {@link JPanel} containing identity-related form fields
     */
    private JPanel buildLeftPanel() {
        JPanel leftPanel = ViewUtils.createColumnPanel();
        JPanel leftContent = ViewUtils.createColumnPanel();
        leftContent.setBorder(BorderFactory.createTitledBorder("Identity"));

        txtName = new JTextField(10);
        txtName.setToolTipText("Ex: Dupont");
        ViewUtils.setCursor(txtName);
        leftContent.add(ViewUtils.labeledRequired("Name", txtName));

        txtFirstName = new JTextField(10);
        ViewUtils.setCursor(txtFirstName);
        leftContent.add(ViewUtils.labeledRequired("First name", txtFirstName));

        txtMail = new JTextField(10);
        ViewUtils.setCursor(txtMail);
        txtMail.setToolTipText("Ex: jean.dupont@email.com");
        leftContent.add(ViewUtils.labeledRequired("Mail", txtMail));

        txtPhoneNumber = new JTextField(10);
        txtPhoneNumber.setToolTipText("ex: 0032123456");
        ViewUtils.setCursor(txtPhoneNumber);
        ViewUtils.digitsOnly(txtPhoneNumber);
        leftContent.add(ViewUtils.labeledRequired("Phone number", txtPhoneNumber));

        txtVATNumber = new JTextField(10);
        txtVATNumber.setToolTipText("BE + 10 digits");
        txtVATNumber.setText("BE");
        ViewUtils.setCursor(txtVATNumber);
        leftContent.add(ViewUtils.labeledRequired("VAT number", txtVATNumber));

        becameClientDate = ViewUtils.createDateSpinner();
        leftContent.add(ViewUtils.labeled("Become client date", becameClientDate));

        chkIsClient = new JCheckBox("Client");
        chkIsSupplier = new JCheckBox("Supplier");
        chkIsMember = new JCheckBox("Staff member");

        JPanel typeRow = new JPanel();
        typeRow.setLayout(new BoxLayout(typeRow, BoxLayout.X_AXIS));
        typeRow.setToolTipText("At least one type must be selected");
        typeRow.add(chkIsClient);
        typeRow.add(Box.createHorizontalStrut(10));
        typeRow.add(chkIsSupplier);
        typeRow.add(Box.createHorizontalStrut(10));
        typeRow.add(chkIsMember);

        leftContent.add(ViewUtils.labeled("Type *", typeRow));
        leftContent.setMaximumSize(leftPanel.getPreferredSize());

        leftContent.setMaximumSize(new Dimension(Integer.MAX_VALUE, leftContent.getPreferredSize().height));
        leftPanel.add(leftContent);

        return leftPanel;
    }

    /**
     * Builds and returns the right section of the form.
     * <p>This panel contains two grouped sections:
     * <ul><li>Address information: street, street number, postal code, city, and country.</li>
     *   <li>Loyalty information: loyalty card identifier and loyalty points.</li></ul>
     * <p>Some fields include predefined values, such as the non-editable country field
     * or restrictions such as numeric spinners for address and loyalty data.
     *
     * @return the right form panel containing address and loyalty information
     * @see ViewUtils#labeledRequired(String, JComponent)
     */
    private JPanel buildRightPanel() {
        JPanel rightPanel = ViewUtils.createColumnPanel();
        JPanel addressPanel = ViewUtils.createColumnPanel();
        addressPanel.setBorder(BorderFactory.createTitledBorder("Address"));

        txtStreet = new JTextField(10);
        txtStreet.setToolTipText("Ex: Avenue Louise");
        ViewUtils.setCursor(txtStreet);
        addressPanel.add(ViewUtils.labeledRequired("Street", txtStreet));

        spnStreetNumber = ViewUtils.createNumberSpinner(0, 0, 10000, 1);
        ViewUtils.setCursor(spnStreetNumber);
        addressPanel.add(ViewUtils.labeled("Street Number", spnStreetNumber));

        spnPostalCode = ViewUtils.createNumberSpinner(0, 0, 99999, 1);
        ViewUtils.setCursor(spnPostalCode);
        addressPanel.add(ViewUtils.labeled("Postal Code", spnPostalCode));

        txtCity = new JTextField(10);
        txtCity.setToolTipText("Ex: Bruxelles");
        ViewUtils.setCursor(txtCity);
        addressPanel.add(ViewUtils.labeledRequired("City", txtCity));

        txtCountry = new JTextField("Belgium");
        txtCountry.setEditable(false);
        txtCountry.setFocusable(false);
        txtCountry.setBackground(Color.LIGHT_GRAY);
        txtCountry.setForeground(Color.DARK_GRAY);
        addressPanel.add(ViewUtils.labeled("Country", txtCountry));

        addressPanel.add(Box.createVerticalStrut(10));
        addressPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, addressPanel.getPreferredSize().height));

        JPanel loyaltyPanel = ViewUtils.createColumnPanel();
        loyaltyPanel.setBorder(BorderFactory.createTitledBorder("Loyalty"));
        txtIdLoyaltyCard = new JTextField(10);
        ViewUtils.setCursor(txtIdLoyaltyCard);
        loyaltyPanel.add(ViewUtils.labeled("Loyalty card ID", txtIdLoyaltyCard));

        spnLoyaltyPoint = ViewUtils.createNumberSpinner(0, 0, 9999, 1000);
        ViewUtils.setCursor(spnLoyaltyPoint);
        loyaltyPanel.add(ViewUtils.labeled("Loyalty points", spnLoyaltyPoint));
        loyaltyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, loyaltyPanel.getPreferredSize().height));

        rightPanel.add(addressPanel);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(loyaltyPanel);
        return rightPanel;
    }

    /**
     * Builds the action panel containing form validation controls.
     * <p>This panel provides actions allowing the user to:
     * <ul><li>save or update the current {@link ClientSupplier}</li>
     *     <li>clear all input fields</li></ul>
     *
     * @return a {@link JPanel} containing form action buttons
     */
    private JPanel buildButtonPanel() {
        btnSave = new JButton("Save");
        btnSave.addActionListener(e -> saveEditForm());
        ViewUtils.setCursor(btnSave);

        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearForm());
        ViewUtils.setCursor(btnClear);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.add(btnClear);
        panel.add(btnSave);
        return panel;
    }

    /**
     * Validates the form fields before submission.
     * <p>This method checks that all required fields are filled and follow the rules imposed to correctly fill out the database.
     * <p>If a validation rule fails, a warning dialog is displayed and the method immediately returns {@code false}.
     *
     * @return {@code true} if all validation rules pass; {@code false} otherwise
     */
    private Boolean validateForm() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtFirstName.getText().trim().isEmpty()) {
            if (chkIsMember.isSelected() || chkIsClient.isSelected()) {
                JOptionPane.showMessageDialog(this, "First name is required. \n Not required for supplier.", "Validation", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        if (txtMail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mail is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtPhoneNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtVATNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "VAT number is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!chkIsClient.isSelected() && !chkIsSupplier.isSelected() && !chkIsMember.isSelected()) {
            JOptionPane.showMessageDialog(this, "At least one type must be selected (Client, Supplier or Staff).", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtStreet.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Street is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtCity.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "City is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Validates the form data and saves the {@link ClientSupplier} information.
     * <p>The methode ask to {@link #validateForm()} to control the user input.
     * <p>If the form is valid, this method collects all user inputs and sends them
     * to the controller to create a new client/supplier or update the existing one, depending on {@code currentClientSupplier} is {@code null}.
     * <p>If the view is opened in a modal window, the modal is closed; otherwise, the application navigates back to the previous view using
     * {@link MainWindow#goBack()}.
     * If an unexpected error occurs during the save operation, an error dialog is displayed containing the exception message.
     * @see #validateForm()
     * @see ClientSupplierController#createClientSupplier(String, String, String, String, String, LocalDate, String, int, boolean, boolean, boolean, int, int, String, String, String) (...)
     * @see ClientSupplierController#updateClientSupplier(int, String, String, String, String, String, LocalDate, String, int, boolean, boolean, boolean, int, int, String, String, String) (...)
     */
    private void saveEditForm() {
        if (!validateForm()) return;

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

            if (currentClientSupplier == null) {
                JOptionPane.showMessageDialog(this, "Unable to save client/supplier.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this, "Client/Supplier saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            if (isOpenInModal != null && isOpenInModal) {
                SwingUtilities.getWindowAncestor(this).dispose();
            } else {
                mainWindow.goBack();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resets all input fields in the form to their default values.
     * @see #loadClientSupplier(ClientSupplier)
     */
    public void clearForm() {

        txtName.setText("");
        txtFirstName.setText("");
        txtMail.setText("");
        txtPhoneNumber.setText("");
        txtVATNumber.setText("BE");

        becameClientDate.setValue(new Date());

        txtIdLoyaltyCard.setText("");
        spnLoyaltyPoint.setValue(0);

        chkIsClient.setSelected(false);
        chkIsSupplier.setSelected(false);
        chkIsMember.setSelected(false);

        spnStreetNumber.setValue(0);
        spnPostalCode.setValue(0);

        txtStreet.setText("");
        txtCity.setText("");
        txtCountry.setText("Belgium");

        currentClientSupplier = null;
    }

    /**
     * Loads data from a Client/Supplier into the form.
     * <p>If {@code cs} is {@code null}, the form is cleared and switched to creation mode.
     * Otherwise, all available data from the given {@link ClientSupplier} is displayed in the form
     * and the save button label is updated to indicate edit mode.
     *
     * @param cs the client/supplier to load into the form;
     *           {@code null} to initialize the form in creation mode
     */
    public void loadClientSupplier(ClientSupplier cs) {

        if (cs == null) {
            currentClientSupplier = null;
            clearForm();
            return;
        }

        currentClientSupplier = cs;

        txtName.setText(ViewUtils.safeText(cs.getName(), ""));
        txtFirstName.setText(ViewUtils.safeText(cs.getFirstname(), ""));
        txtMail.setText(ViewUtils.safeText(cs.getEmail(), ""));
        txtVATNumber.setText(ViewUtils.safeText(cs.getVATNumber(), "BE"));

        if (cs.getBecameClientDate() != null) {
            becameClientDate.setValue(ViewUtils.toDate(cs.getBecameClientDate()));
        }

        FidelityCard fidelityCard = cs.getFidelityCard();

        if (fidelityCard != null && fidelityCard.getIsValid()) {
            txtIdLoyaltyCard.setText(String.valueOf(fidelityCard.getId()));
            spnLoyaltyPoint.setValue(fidelityCard.getTotalPoint());
        } else {
            txtIdLoyaltyCard.setText("");
            spnLoyaltyPoint.setValue(0);
        }

        chkIsClient.setSelected(cs.getIsClient());
        chkIsSupplier.setSelected(cs.getIsSupplier());
        chkIsMember.setSelected(cs.getIsUs());

        Address address = cs.getAddress();
        if (address != null) {
            spnStreetNumber.setValue(address.getStreetNumber());

            if (address.getLocality() != null) {
                spnPostalCode.setValue(address.getLocality().getPostalCode());
                txtCity.setText(address.getLocality().getCity());
            } else {
                spnPostalCode.setValue(0);
                txtCity.setText("");
            }

            txtStreet.setText(address.getStreetName());
        } else {
            spnStreetNumber.setValue(1);
            spnPostalCode.setValue(0);
            txtStreet.setText("");
            txtCity.setText("");
        }

        btnSave.setText("Edit");
    }

    /**
     * Returns the currently loaded client/supplier.
     * <p>This method is used by {@link DocumentForm#openDialog()} to recover the new client/supplier created in the form.
     *
     * @return the currently loaded {@link ClientSupplier},
     * or {@code null} if no client/supplier is loaded
     */
    public ClientSupplier getCurrentClientSupplier() {
        return currentClientSupplier;
    }
}