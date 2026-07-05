package be.henallux.project.view;

import be.henallux.project.controller.*;

import java.awt.*;
import java.time.*;
import java.util.Date;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import be.henallux.project.model.*;

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
    private final ClientSupplierController clientSupplierControllerController;
    private final AddressController addressController;
    private ClientSupplier currentClientSupplier;

    private JTextField txtName;
    private JTextField txtFirstName;
    private JTextField txtMail;
    private JTextField txtPhoneNumber;
    private JTextField txtVATNumber;

    private JSpinner becameClientDate;

    private JPanel loyaltyPanel;
    private JCheckBox chkCreateFidelityCard;

    private JCheckBox chkIsClient;
    private JCheckBox chkIsSupplier;
    private JCheckBox chkIsMember;

    private JSpinner spnStreetNumber;
    private JSpinner spnPostalCode;

    private JTextField txtStreet;
    private JTextField txtCity;
    private JTextField txtCountry;

    private JButton btnSave, btnCancelClear;

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
        this.clientSupplierControllerController = new ClientSupplierController();
        this.addressController = new AddressController();

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
     *     <li>navigates back using {@link MainWindow#goBack()} otherwise</li></ul>
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
        txtVATNumber.setToolTipText("BE + 10 digits. \nSelect supplier to add a TVA number.");
        txtVATNumber.setText("BE");
        ViewUtils.setCursor(txtVATNumber);
        toggleEditable(txtVATNumber, false);
        leftContent.add(ViewUtils.labeledRequired("VAT number", txtVATNumber));

        becameClientDate = ViewUtils.createDateSpinner();
        leftContent.add(ViewUtils.labeled("Become client date", becameClientDate));

        chkIsClient = new JCheckBox("Client");
        chkIsSupplier = new JCheckBox("Supplier");
        chkIsMember = new JCheckBox("Staff member");
        chkIsSupplier.addActionListener(e -> { buildLoyaltyPanel(); updateVATField(); });
        chkIsClient.addActionListener(e -> { buildLoyaltyPanel(); updateVATField(); });
        chkIsMember.addActionListener(e -> { buildLoyaltyPanel(); updateVATField(); });

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
        toggleEditable(txtCountry, false);
        addressPanel.add(ViewUtils.labeled("Country", txtCountry));

        addressPanel.add(Box.createVerticalStrut(10));
        addressPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, addressPanel.getPreferredSize().height));

        loyaltyPanel = ViewUtils.createColumnPanel();
        buildLoyaltyPanel();

        rightPanel.add(addressPanel);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(loyaltyPanel);
        return rightPanel;
    }


    /**
     * Affichage des info la carte de fidélité.
     * Si on est en create le client n'existe pas. La carte est créé apres l'enregistrement.
     * Si le client existe, mais n'a pas de carte, il peut en générer une
     * Si le client existe et sa carte est disponible affichage des infos.
     *
     * @see ClientSupplierController#createFidelityCard(FidelityCard)
     */
    private void buildLoyaltyPanel(FidelityCard newCard) {
        loyaltyPanel.removeAll();
        loyaltyPanel.setBorder(BorderFactory.createTitledBorder("Loyalty"));

        boolean isSupplierOnly = chkIsSupplier.isSelected() && !chkIsClient.isSelected() && !chkIsMember.isSelected();
        if (isSupplierOnly) {
            loyaltyPanel.setVisible(false);
            loyaltyPanel.revalidate();
            loyaltyPanel.repaint();
            return;
        }

        loyaltyPanel.setVisible(true);
        FidelityCard card = newCard;

        if (card == null && currentClientSupplier != null) {
            card = currentClientSupplier.getFidelityCard();
        }

        if (currentClientSupplier == null && card == null) {
            JLabel info = new JLabel("A fidelity card will be created after saving.");
            info.setForeground(Color.GRAY);
            loyaltyPanel.add(info);
            chkCreateFidelityCard = new JCheckBox("Create a new fidelity card ?");
            ViewUtils.setCursor(chkCreateFidelityCard);
            chkCreateFidelityCard.setSelected(true);
            loyaltyPanel.add(chkCreateFidelityCard);

        } else if (card == null) {
            JButton btnCreateCard = new JButton("Create fidelity card");
            ViewUtils.setCursor(btnCreateCard);
            btnCreateCard.addActionListener(e -> onCreateFidelityCard());
            loyaltyPanel.add(btnCreateCard);

        } else {
            loyaltyPanel.add(ViewUtils.labeled("Card ID", new JLabel(String.valueOf(card.getId()))));
            loyaltyPanel.add(ViewUtils.labeled("Points", new JLabel(String.valueOf(card.getTotalPoint()))));
        }

        loyaltyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, loyaltyPanel.getPreferredSize().height));
        loyaltyPanel.revalidate();
        loyaltyPanel.repaint();
    }

    private void buildLoyaltyPanel() {
        buildLoyaltyPanel(null);
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

        btnCancelClear = new JButton("Clear");
        btnCancelClear.addActionListener(e -> {
            if (currentClientSupplier == null) {
                clearForm();
            } else {
                loadClientSupplier(currentClientSupplier);
            }
        });
        ViewUtils.setCursor(btnCancelClear);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.add(btnCancelClear);
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
        if (txtPhoneNumber.getText().trim().length() < 10) {
            JOptionPane.showMessageDialog(this, "Phone number must contain at least 10 digits.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (chkIsSupplier.isSelected() && txtVATNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "VAT number is required for suppliers.", "Validation", JOptionPane.WARNING_MESSAGE);
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
     *
     * @see #validateForm()
     * @see ClientSupplierController#createClientSupplier(ClientSupplier)
     */
    private void saveEditForm() {
        if (!validateForm()) return;

        String name = txtName.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String mail = txtMail.getText().trim();
        String phoneNumber = txtPhoneNumber.getText().trim();
        String vatNumber = txtVATNumber.getText().trim();

        LocalDate becameClient = ViewUtils.getDate(becameClientDate);

        boolean isClient = chkIsClient.isSelected();
        boolean isSupplier = chkIsSupplier.isSelected();
        boolean isMember = chkIsMember.isSelected();

        int streetNumber = (int) spnStreetNumber.getValue();
        int postalCode = (int) spnPostalCode.getValue();

        String street = txtStreet.getText().trim();
        String city = txtCity.getText().trim();

        try {
            if (currentClientSupplier == null) {
                Locality locality = new Locality(city, postalCode);
                Address newAddress = new Address(street, streetNumber, city, postalCode);
                addressController.createAddress(newAddress, locality);

                ClientSupplier newItem = new ClientSupplier(name, firstName, mail, phoneNumber, newAddress, isClient, isSupplier, isMember, vatNumber, becameClient, null);
                clientSupplierControllerController.createClientSupplier(newItem);
                currentClientSupplier = newItem;

                if (isClient && chkCreateFidelityCard != null && chkCreateFidelityCard.isSelected()) {
                    onCreateFidelityCard();
                }
            } else {
                Locality locality = new Locality(city, postalCode);
                Address newAddress = new Address(street, streetNumber, city, postalCode);
                addressController.createAddress(newAddress, locality);

                ClientSupplier updatedCs = new ClientSupplier(
                        name, firstName, mail, phoneNumber,
                        newAddress, isClient, isSupplier, isMember,
                        vatNumber, becameClient,
                        currentClientSupplier.getFidelityCard()
                );
                clientSupplierControllerController.updateClientSupplier(currentClientSupplier, updatedCs);
            }

            JOptionPane.showMessageDialog(this, "Client/Supplier saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            if (isOpenInModal != null && isOpenInModal) {
                SwingUtilities.getWindowAncestor(this).dispose();
            } else {
                mainWindow.goBack();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resets all input fields in the form to their default values.
     *
     * @see #loadClientSupplier(ClientSupplier)
     */
    public void clearForm() {

        txtName.setText("");
        toggleEditable(txtName, true);
        txtFirstName.setText("");
        toggleEditable(txtFirstName, true);
        txtMail.setText("");
        txtPhoneNumber.setText("");
        txtVATNumber.setText("");

        becameClientDate.setValue(new Date());
        toggleEditable(becameClientDate, true);

        currentClientSupplier = null;
        buildLoyaltyPanel();

        chkIsClient.setSelected(false);
        chkIsSupplier.setSelected(false);
        chkIsMember.setSelected(false);
        toggleEditable(chkIsClient, true);
        toggleEditable(chkIsSupplier, true);
        toggleEditable(chkIsMember, true);
        updateVATField();


        spnStreetNumber.setValue(0);
        spnPostalCode.setValue(0);

        txtStreet.setText("");
        txtCity.setText("");
        txtCountry.setText("Belgium");
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
        toggleEditable(txtName, false);
        txtFirstName.setText(ViewUtils.safeText(cs.getFirstname(), ""));
        toggleEditable(txtFirstName, false);
        txtPhoneNumber.setText(ViewUtils.safeText(cs.getPhoneNumber(), ""));
        txtMail.setText(ViewUtils.safeText(cs.getEmail(), ""));
        txtVATNumber.setText(ViewUtils.safeText(cs.getVATNumber(), ""));

        if (cs.getBecameClientDate() != null) {
            becameClientDate.setValue(ViewUtils.toDate(cs.getBecameClientDate()));
        }
        toggleEditable(becameClientDate, false);

        buildLoyaltyPanel();

        chkIsClient.setSelected(cs.getIsClient());
        chkIsSupplier.setSelected(cs.getIsSupplier());
        chkIsMember.setSelected(cs.getIsUs());
        toggleEditable(chkIsClient, false);
        toggleEditable(chkIsSupplier, false);
        toggleEditable(chkIsMember, false);
        updateVATField();

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

        btnSave.setText("Save");
        btnCancelClear.setText("Clear");
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

    private void onCreateFidelityCard() {
        try {
            FidelityCard newCard = new FidelityCard(currentClientSupplier);
            clientSupplierControllerController.createFidelityCard(newCard);
            currentClientSupplier.setFidelityCard(newCard);
            buildLoyaltyPanel(newCard);
            JOptionPane.showMessageDialog(this, "Fidelity card created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Active ou désactive le champ VAT number en fonction du type sélectionné.
     * Le VAT number est uniquement requis pour les Suppliers.
     * Si Supplier est décoché, le champ est vidé et désactivé.
     */
    private void updateVATField() {
        boolean isSupplier = chkIsSupplier.isSelected();
        toggleEditable(txtVATNumber, isSupplier);
        if (!isSupplier) {
            txtVATNumber.setText("");
        }
    }

    /**
     * Toggles the editable state of a {@link JTextField}.
     *
     * @param txtField   the text field to update
     * @param isEditable {@code true} to make the field editable; {@code false} to disable it
     */
    private void toggleEditable(JTextField txtField, boolean isEditable) {
        txtField.setEditable(isEditable);
        txtField.setBackground(isEditable ? UIManager.getColor("TextField.background") : Color.LIGHT_GRAY);
        txtField.setForeground(isEditable ? UIManager.getColor("TextField.foreground") : Color.DARK_GRAY);
        txtField.setFocusable(isEditable);
    }

    /**
     * Toggles the editable state of a {@link JCheckBox}.
     *
     * @param checkBox   the checkbox to update
     * @param isEditable {@code true} to enable the checkbox; {@code false} to disable it
     */
    private void toggleEditable(JCheckBox checkBox, boolean isEditable) {
        checkBox.setEnabled(isEditable);
        checkBox.setFocusable(false);
    }

    /**
     * Toggles the editable state of a {@link JSpinner}.
     *
     * @param spinner    the spinner to update
     * @param isEditable {@code true} to enable the spinner; {@code false} to disable it
     */
    private void toggleEditable(JSpinner spinner, boolean isEditable) {
        spinner.setEnabled(isEditable);
        spinner.setBackground(isEditable ? UIManager.getColor("TextField.background") : Color.LIGHT_GRAY);
        spinner.setForeground(isEditable ? UIManager.getColor("TextField.foreground") : Color.DARK_GRAY);
        spinner.setFocusable(false);
    }
}