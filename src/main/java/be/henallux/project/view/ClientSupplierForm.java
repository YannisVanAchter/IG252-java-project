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

    /**
     * Constructs a new instance of the ClientSupplierForm.
     * @param mainWindow the main application window associated with this form.
     * @param isOpenInModal a flag indicating if the form is open in a modal window.
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
     * @param mainWindow the main application window associated with this form.
     */
    public ClientSupplierForm(MainWindow mainWindow) {
        this(mainWindow, false);
    }

    /**
     * Builds the header panel containing the title and the back button.
     * If the view is opened in a modal window, clicking the back button closes
     * the modal. Otherwise, it navigates back to the previous view using
     * {@link MainWindow#goBack()}.
     * @return the header {@code JPanel}
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
     * The panel is organized using a {@code GridLayout} with two columns and a horizontal gap of 20 pixels.
     * Using a {@code JScrollPane} for automatic adaptation to window resizing
     * @return a {@code JScrollPanel} containing all elements.
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
     * This panel contains the identity and contact information fields:
     * name, first name, email, phone number, VAT number, client since date, contact types.
     * Required fields are marked by a {@code *} using {@link ViewUtils#labeledRequired(String, JComponent)}.
     * Numeric JTexfild contains numeric-only constraints such on keyboard input. {@link ViewUtils#digitsOnly(JTextField)}
     *
     * @return the left form panel containing identity-related fields
     */
    private JPanel buildLeftPanel() {
        leftPanel = ViewUtils.createColumnPanel();
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
     * This panel contains two grouped sections:
     * <ul>
     *   <li>Address information: street, street number, postal code, city, and country.</li>
     *   <li>Loyalty information: loyalty card identifier and loyalty points.</li>
     * </ul>
     * Some fields include predefined values, such as the non-editable country field
     * or restrictions such as numeric spinners for address and loyalty data.
     *
     * @return the right form panel containing address and loyalty information
     */
    private JPanel buildRightPanel() {
        rightPanel = ViewUtils.createColumnPanel();
        JPanel addressPanel = ViewUtils.createColumnPanel();
        addressPanel.setBorder(BorderFactory.createTitledBorder("Address"));

        txtStreet = new JTextField(10);
        txtStreet.setToolTipText("Ex: Avenue Louise");
        ViewUtils.setCursor(txtStreet);
        addressPanel.add(ViewUtils.labeledRequired("Street", txtStreet));

        spnStreetNumber = ViewUtils.createNumberSpinner(1, 1, 10000, 1);
        ViewUtils.setCursor(spnStreetNumber);
        addressPanel.add(ViewUtils.labeled("Street Number", spnStreetNumber));

        spnPostalCode = ViewUtils.createNumberSpinner(1000, 1, 99999, 1);
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

    private JPanel buildButtonPanel() {
        btnSave = new JButton("Save");
        btnSave.addActionListener(e -> saveEditForm());
        ViewUtils.setCursor(btnSave);

        btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearForm());
        ViewUtils.setCursor(btnClear);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.add(btnClear);
        panel.add(btnSave);
        return panel;
    }

    /**
     * Validates the form fields before submission.
     * This method checks that all required fields are filled and follow the rules imposed to correctly fill out the database.
     * If a validation rule fails, a warning dialog is displayed and the method immediately returns {@code false}.
     * @return {@code true} if all validation rules pass;
     *         {@code false} otherwise
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
     * Validates the form data and saves the client/supplier information.
     * The methode ask to {@link #validateForm()} to control the user input.
     * If the form is valid, this method collects all user inputs and sends them
     * to the controller to create a new client/supplier or update the existing one, depending on {@code currentClientSupplier} is {@code null}.
     * If the view is opened in a modal window, the modal is closed; otherwise,
     * the application navigates back to the previous view using
     * {@link MainWindow#goBack()}.
     * If an unexpected error occurs during the save operation, an error dialog is displayed containing the exception message.
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
        spnPostalCode.setValue(1000);

        txtStreet.setText("");
        txtCity.setText("");
        txtCountry.setText("Belgique");

        currentClientSupplier = null;
    }

    /**
     * Loads data from a Client/Supplier into the form.
     * If {@code cs} is {@code null}, the form is cleared and switched to creation mode.
     * Otherwise, all available data from the given {@link ClientSupplier} is displayed in the form
     * and the save button label is updated to indicate edit mode.
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

        txtIdLoyaltyCard.setText(String.valueOf(cs.getFidelityCard().getId()));
        spnLoyaltyPoint.setValue(cs.getFidelityCard().getTotalPoint());

        chkIsClient.setSelected(cs.getIsClient());
        chkIsSupplier.setSelected(cs.getIsSupplier());
        chkIsMember.setSelected(cs.getIsUs());

        spnStreetNumber.setValue(cs.getAddress().getStreetNumber());
        spnPostalCode.setValue(cs.getAddress().getLocality().getPostalCode());

        Address address = cs.getAddress();
        txtStreet.setText(address != null ? address.getStreetName() : "");
        txtCity.setText(address != null && address.getLocality() != null ? cs.getAddress().getLocality().getCity() : "");
        txtCountry.setText("Belgium");

        btnSave.setText("Edit");
    }

    /**
     * Returns the currently loaded client/supplier.
     * This method is used by {@link DocumentForm#openDialog()} to recover the new client/supplier created in the form.
     * @return the currently loaded {@link ClientSupplier},
     *         or {@code null} if no client/supplier is loaded
     */
    public ClientSupplier getCurrentClientSupplier() {
        System.out.println("getCurrentClientSupplier: " + currentClientSupplier);
        return currentClientSupplier;
    }
}