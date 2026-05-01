package view;

import controler.*;
import exception.*;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.Date;

/**
 * ClientSupplierForm represents the form for creating and modifying a Client or Supplier.
 * <p>
 * This form allows the user to enter all required information related to a Client/Supplier
 * (name, fistname, type, address, etc.).
 * <p>
 * The form communicates with {@link ClientSupplierController} to perform creation and update operations.
 * The table is created with the model {@link ClientSupplierTableModel} to set collumns and row
 */
public class ClientSupplierForm extends JPanel {
    private MainWindow mainWindow;
    private ClientSupplierController controller;
    private ClientSupplier currentClientSupplier;

    private JPanel appPanel, panelContent, leftPanel, rightPanel;

    private JTextField txtName;
    private JTextField txtFirstName;
    private JTextField txtMail;
    private JTextField txtVATNumber;

    private JSpinner becameClientDate;

    private JTextField txtIdLoyalityCard;
    private JSpinner spnLoyalityPoint;

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

    public ClientSupplierForm(MainWindow mainWindow) throws DataValidationException {
        this.mainWindow = mainWindow;
        this.controller = new ClientSupplierController();

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Client Details");
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

        txtName = new JTextField(10);
        leftPanel.add(labeled("Name", txtName));

        txtMail = new JTextField(10);
        leftPanel.add(labeled("Mail", txtMail));

        becameClientDate = createDateSpinner();
        leftPanel.add(labeled("Become client date", becameClientDate));

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
        leftPanel.add(labeled("Street", txtStreet));
        txtCity = new JTextField(10);
        leftPanel.add(labeled("City", txtCity));
        txtCountry = new JTextField(10);
        leftPanel.add(labeled("Country", txtCountry));
    }

    private void buildRightPanel() {
        rightPanel = createColumnPanel();

        txtFirstName = new JTextField(10);
        rightPanel.add(labeled("FirstName", txtFirstName));

        txtVATNumber = new JTextField(10);
        rightPanel.add(labeled("VAT Number", txtVATNumber));

        txtIdLoyalityCard = new JTextField(10);
        rightPanel.add(labeled("Loyality cart ID", txtIdLoyalityCard));

        spnLoyalityPoint = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
        spnLoyalityPoint.setEditor(new JSpinner.NumberEditor(spnLoyalityPoint, "#"));        rightPanel.add(labeled("Loyality Point", spnLoyalityPoint));

        spnStreetNumber = new JSpinner(new SpinnerNumberModel(1, 0, 99999, 1));
        spnStreetNumber.setEditor(new JSpinner.NumberEditor(spnStreetNumber, "#"));
        rightPanel.add(labeled("Street number", spnStreetNumber));
        spnPostalCode = new JSpinner(new SpinnerNumberModel(1000, 0, 99999, 1));
        spnPostalCode.setEditor(new JSpinner.NumberEditor(spnPostalCode, "#"));
        rightPanel.add(labeled("Postal Code", spnPostalCode));


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
        String vatNumber = txtVATNumber.getText().trim();

        LocalDate becameClient = getDate(becameClientDate);

        String loyaltyCardId = txtIdLoyalityCard.getText().trim();
        int loyaltyPoints = (int) spnLoyalityPoint.getValue();

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
                controller.createClientSupplier(
                        name, firstName, mail, vatNumber,
                        becameClient,
                        loyaltyCardId, loyaltyPoints,
                        isClient, isSupplier, isMember,
                        streetNumber, postalCode,
                        street, city, country
                );
            } else {
                controller.updateClientSupplier(
                        currentClientSupplier.getId(),
                        name, firstName, mail, vatNumber,
                        becameClient,
                        loyaltyCardId, loyaltyPoints,
                        isClient, isSupplier, isMember,
                        streetNumber, postalCode,
                        street, city, country
                );
            }

            JOptionPane.showMessageDialog(this, "Client/Supplier saved!");
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

        txtName.setText("");
        txtFirstName.setText("");
        txtMail.setText("");
        txtVATNumber.setText("");

        becameClientDate.setValue(new Date());

        txtIdLoyalityCard.setText("");
        spnLoyalityPoint.setValue(0);

        chkIsClient.setSelected(false);
        chkIsSupplier.setSelected(false);
        chkIsMember.setSelected(false);

        spnStreetNumber.setValue(0);
        spnPostalCode.setValue(1000);

        txtStreet.setText("");
        txtCity.setText("");
        txtCountry.setText("");

        currentClientSupplier = null;
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
        //comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));
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
     * Loads data from a Client/Supplier into the form.
     *
     * @param cs document to display
     * If cs is null:
     * - the form is in create mode
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
            becameClientDate.setValue(cs.getBecameClientDate());
        }

        //txtIdLoyalityCard.setText(cs.getLoyaltyCard.getId());
        //spnLoyalityPoint.setValue(cs.getLoyaltyCard.getPoints());

        chkIsClient.setSelected(cs.getIsClient());
        chkIsSupplier.setSelected(cs.getIsSupplier());
        chkIsMember.setSelected(cs.getIsUs());

        spnStreetNumber.setValue(cs.getAddress().getStreetNumber());
        spnPostalCode.setValue(cs.getAddress().getLocation().getPostalCode());

        txtStreet.setText(cs.getAddress().getStreetName());
        txtCity.setText(cs.getAddress().getLocation().getName());
        //txtCountry.setText(cs.getAddress().getLocation().getCountry);

        btnSave.setText("Edit");
    }
}