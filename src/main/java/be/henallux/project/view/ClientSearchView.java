package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Locality;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ClientSearchView displays detailed information about a single Client.
 * This view is dynamically rebuilt every time a client is loaded using {@link #loadClient(ClientSupplier)}.
 * It organizes data into logical sections matching the search output columns:
 * ClientSupplier, FidelityCard, Address, Locality.
 */
public class ClientSearchView extends JPanel {

    private static final String LABEL_NO_DATA = "N/A";
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 20);

    private final MainWindow mainWindow;
    private ClientSupplier client;

    public ClientSearchView(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    /**
     * Loads a client into the view and rebuilds the UI.
     * If the client is null, the user is notified and the view navigates back.
     *
     * @param client the ClientSupplier to display
     */
    public void loadClient(ClientSupplier client) {
        this.client = client;

        if (this.client == null) {
            JOptionPane.showMessageDialog(this, "Please select a client.");
            mainWindow.goBack();
            return;
        }

        removeAll();
        JScrollPane pane = new JScrollPane(buildContent());
        pane.setBorder(BorderFactory.createEmptyBorder());
        add(pane, BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

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

    private JPanel buildTitle() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(ViewUtils.safeText(client.getLabel(), LABEL_NO_DATA));
        label.setFont(FONT_TITLE);
        panel.add(label);
        return panel;
    }

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

    private JPanel buildFidelityInfo() {
        JPanel card = createCard("Fidelity Card");

        if (client == null || !client.getFidelityCard().getIsValid()){
            card.add(labelValue("Fidelity card", LABEL_NO_DATA));
            return card;
        }
        card.add(labelValue("Card Number", String.valueOf(client.getFidelityCard().getId())));
        card.add(labelValue("Total points", String.valueOf(client.getFidelityCard().getTotalPoint())));
        return card;
    }

    private JPanel buildAddressInfo() {
        JPanel card = createCard("Address");

        Address address = client.getAddress();
        Locality locality = address != null ? address.getLocality() : null;

        card.add(labelValue("Street", address != null ? address.getStreetName() : "N/A"));
        card.add(labelValue("Street nb", address != null ? String.valueOf(address.getStreetNumber()) : "N/A"));
        card.add(labelValue("Postal code", locality != null ? String.valueOf(locality.getPostalCode()) : "N/A"));
        card.add(labelValue("City", locality != null ? locality.getCity() : "N/A"));
        return card;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton edit = new JButton("Edit");
        ViewUtils.setCursor(edit);
        edit.addActionListener(e -> {
            mainWindow.openClientSupplierForm(client);
        });
        JButton back = new JButton("Back");
        ViewUtils.setCursor(back);
        back.addActionListener(e -> mainWindow.goBack());

        panel.add(edit);
        panel.add(back);
        return panel;
    }

    /**
     * Creates a bordered card container with a title.
     *
     * @param title the title of the section
     * @return a styled panel
     */
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createTitledBorder(title));
        return card;
    }

    /**
     * Creates a key-value label row.
     *
     * @param label the field name
     * @param value the field value
     * @return a horizontal panel displaying the label and value
     */
    private JPanel labelValue(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label + " : "));
        panel.add(new JLabel(ViewUtils.safeText(value, LABEL_NO_DATA)));
        return panel;
    }
}