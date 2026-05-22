package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.FidelityCard;
import main.java.be.henallux.project.model.Locality;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ClientSearchView displays detailed information about a {@link ClientSupplier}.
 * This view is dynamically rebuilt every time a client is loaded using {@link #loadClient(ClientSupplier)}.
 * <p>This view is generally opened from {@link ClientSearchTable} through {@link MainWindow#openClientView(ClientSupplier)}
 * after the user selects a client from the search results table.
 * <p>It organizes data into logical sections matching the search output columns:
 * ClientSupplier, FidelityCard, Address, Locality.
 *
 * @see ClientSupplier
 * @see ClientSearchTable
 * @see MainWindow
 */
public class ClientSearchView extends JPanel {

    private static final String LABEL_NO_DATA = "N/A";
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);

    private final MainWindow mainWindow;
    private ClientSupplier client;

    /**
     * Creates the detailed client view panel.
     * <p>The displayed client content remains empty until {@link #loadClient(ClientSupplier)}
     * is called by {@link MainWindow#openClientView(ClientSupplier)}
     *
     * @param mainWindow the parent {@link MainWindow} used for navigation actions
     * @see #loadClient(ClientSupplier)
     */
    public ClientSearchView(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(8, 16, 8, 16));
    }

    /**
     * Builds the complete visual content of the view.
     * <p>This method clears all existing components and recreates the different information sections associated
     * with the currently loaded {@link ClientSupplier}.
     * <p>The rebuilt interface contains:
     * <ul><li>The client title section</li>
     *     <li>The client information card</li>
     *     <li>The fidelity card section</li>
     *     <li>The address section</li>
     *     <li>The footer navigation actions</li></ul>
     * <p>Create a {@link JScrollPane} to allow scrolling when the window is resized.
     *
     * @see #loadClient(ClientSupplier)
     */
    private void build() {
        removeAll();

        JPanel top = new JPanel(new BorderLayout());
        top.add(buildContent(), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(top);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(0, 250));

        add(scrollPane, BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    /**
     * Loads and displays a {@link ClientSupplier} inside the view.
     * <p>If the provided client is {@code null}, a warning dialog is displayed
     * and the navigation returns to the previous screen using {@link MainWindow#goBack()}.
     * <p>When a valid client is provided, the entire interface is rebuilt with {@link #build()}
     *
     * @param client the {@link ClientSupplier} to display
     * @see MainWindow#openClientView(ClientSupplier)
     * @see MainWindow#goBack()
     *
     */
    public void loadClient(ClientSupplier client) {
        this.client = client;

        if (this.client == null) {
            JOptionPane.showMessageDialog(this, "Please select a client.");
            mainWindow.goBack();
            return;
        }

        build();
    }

    /**
     * Builds the main content container of the client view.
     * <p>This container assembles all information sections displayed in the detailed client screen.
     * <ul><li>Client title</li>
     *     <li>Client information</li>
     *     <li>Fidelity card information</li>
     *     <li>Address information</li></ul>
     *
     * @return a {@link JPanel} containing the complete client information layout
     */
    private JPanel buildContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(0, 0, 8, 0));
        panel.add(buildTitle());
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildClientInfo());
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildFidelityInfo());
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildAddressInfo());
        return panel;
    }

    /**
     * Builds the title section displaying the client label.
     *
     * @return a {@link JPanel} containing the client title
     * @see ClientSupplier#getLabel()
     */
    private JPanel buildTitle() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(ViewUtils.safeText(client.getLabel(), LABEL_NO_DATA));
        label.setFont(FONT_TITLE);
        panel.add(label);
        return panel;
    }

    /**
     * Builds the section containing general client information.*
     * <ul><li>Name and first name</li>
     *     <li>Email address</li>
     *     <li>Phone number</li>
     *     <li>Client registration date</li>
     *     <li>Staff member status</li></ul>
     *
     * @return a {@link JPanel} containing the client information section
     */
    private JPanel buildClientInfo() {
        JPanel card = createCard("Client Information");

        card.add(labelValue("Name", client.getName()));
        card.add(labelValue("First name", client.getFirstname()));
        card.add(labelValue("Email", client.getEmail()));
        card.add(labelValue("Phone", client.getPhoneNumber()));
        card.add(labelValue("Client since", client.getBecameClientDate() != null
                ? ViewUtils.formatDate(client.getBecameClientDate())
                : LABEL_NO_DATA));
        card.add(labelValue("Is staff member", client.getIsUs() ? "Yes" : "No"));
        return card;
    }

    /**
     * Builds the section displaying fidelity card information.
     *
     * @return a {@link JPanel} containing the fidelity card information
     */
    private JPanel buildFidelityInfo() {
        JPanel card = createCard("Fidelity Card");

        FidelityCard fidelityCard = client != null ? client.getFidelityCard() : null;
        if (fidelityCard == null || !fidelityCard.getIsValid()) {
            card.add(labelValue("Fidelity card", LABEL_NO_DATA));
            return card;
        }
        card.add(labelValue("Card Number", String.valueOf(fidelityCard.getId())));
        card.add(labelValue("Total points", String.valueOf(fidelityCard.getTotalPoint())));
        return card;
    }
    /**
     * Builds the section displaying address and locality information.
     *
     * @return a {@link JPanel} containing the address information section
     * @see Address
     * @see Locality
     */
    private JPanel buildAddressInfo() {
        JPanel card = createCard("Address");

        Address address = client.getAddress();
        Locality locality = address != null ? address.getLocality() : null;

        card.add(labelValue("Street", address != null ? address.getStreetName() : LABEL_NO_DATA));
        card.add(labelValue("Street nb", address != null ? String.valueOf(address.getStreetNumber()) : LABEL_NO_DATA));
        card.add(labelValue("Postal code", locality != null ? String.valueOf(locality.getPostalCode()) : LABEL_NO_DATA));
        card.add(labelValue("City", locality != null ? locality.getCity() : LABEL_NO_DATA));
        return card;
    }

    /**
     * Builds the footer section containing navigation actions.
     * <p>The footer provides buttons allowing the user to:
     * <ul><li>Open the client edition form</li>
     *     <li>Return to the previous screen</li></ul>
     *
     * @return a {@link JPanel} containing footer action buttons
     * @see MainWindow#openClientSupplierForm(ClientSupplier)
     * @see MainWindow#goBack()
     */
    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton edit = new JButton("Edit");
        ViewUtils.setCursor(edit);
        edit.addActionListener(e -> mainWindow.openClientSupplierForm(client));
        JButton back = new JButton("Back");
        ViewUtils.setCursor(back);
        back.addActionListener(e -> mainWindow.goBack());

        panel.add(edit);
        panel.add(back);
        return panel;
    }

    /**
     * Creates a bordered container used to group related information.
     *
     * @param title the displayed title of the card section
     * @return a {@link JPanel} configured as an information card
     */
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createTitledBorder(title));
        return card;
    }

    /**
     * Creates a horizontal row displaying a label and its associated value.
     * <p>If the provided value is {@code null} or empty, {@link #LABEL_NO_DATA} is displayed instead.
     *
     * @param label the displayed field name
     * @param value the displayed field value
     * @return a {@link JPanel} containing the formatted label/value pair
     * @see ViewUtils#safeText(String, String)
     */
    private JPanel labelValue(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label + " : "));
        panel.add(new JLabel(ViewUtils.safeText(value, LABEL_NO_DATA)));
        return panel;
    }
}