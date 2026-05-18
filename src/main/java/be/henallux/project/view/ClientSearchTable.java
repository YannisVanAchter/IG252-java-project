package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.ClientSupplierSearchController;
import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A Swing panel that displays and manages a searchable table of clients.
 * <p>This view allows users to filter clients by name, email, and fidelity card number,
 * and displays the results in a table format.</p>
 * <p>Users can click on a row action button to open a detailed client view
 * in the main application window.</p>
 * @see MainWindow#openClientView(ClientSupplier)
 */
public class ClientSearchTable extends JPanel {
    private static final int TBL_BTN_SEE = 9;

    private MainWindow mainWindow;
    private ClientSupplierSearchController controller;  // ← nouveau controller
    private ClientSearchTableModel model;

    private List<ClientSupplier> displayClients;

    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtFidelityCard;

    private JTable table;

    public ClientSearchTable(MainWindow mainWindow) throws DataValidationException {
        this.mainWindow = mainWindow;
        this.controller = new ClientSupplierSearchController();

        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        displayClients = controller.search(null, null, null);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(buildHeader(), BorderLayout.NORTH);
        top.add(buildSearchPanel(), BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    /**
     * Builds the header section containing the title.
     * @return a JPanel representing the header
     */
    private JPanel buildHeader() {
        JLabel title = new JLabel("Client Search");
        title.setFont(new Font("Inter", Font.BOLD, 20));
        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.NORTH);
        return header;
    }

    /**
     * Builds the search/filter panel containing input fields and the search button.
     * Contains: Name, email, card number.
     * @return a {@code JPanel} containing search filters
     */
    private JPanel buildSearchPanel() {
        txtName = new JTextField(10);
        JPanel namePanel = new JPanel(new BorderLayout(0, 4));
        namePanel.add(new JLabel("Name"), BorderLayout.NORTH);
        namePanel.add(txtName, BorderLayout.CENTER);

        txtEmail = new JTextField(10);
        JPanel emailPanel = new JPanel(new BorderLayout(0, 4));
        emailPanel.add(new JLabel("Email"), BorderLayout.NORTH);
        emailPanel.add(txtEmail, BorderLayout.CENTER);

        txtFidelityCard = new JTextField(10);
        txtFidelityCard = ViewUtils.digitsOnly(txtFidelityCard);
        JPanel fidelityPanel = new JPanel(new BorderLayout(0, 4));
        fidelityPanel.add(new JLabel("Fidelity card number"), BorderLayout.NORTH);
        fidelityPanel.add(txtFidelityCard, BorderLayout.CENTER);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> onSearchClick());

        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBorder(BorderFactory.createTitledBorder("Filters"));
        column.add(ViewUtils.makeRow(namePanel));
        column.add(Box.createVerticalStrut(6));
        column.add(ViewUtils.makeRow(emailPanel));
        column.add(Box.createVerticalStrut(6));
        column.add(ViewUtils.makeRow(fidelityPanel));
        column.add(Box.createVerticalStrut(8));
        column.add(ViewUtils.makeRow(btnSearch));

        return column;
    }

    /**
     * Builds the table panel that displays the list of clients.
     * @return a {@code JScrollPane} containing the JTable
     */
    private JScrollPane buildTablePanel() {
        model = new ClientSearchTableModel(new ArrayList<>(displayClients));
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = table.convertColumnIndexToModel(
                        table.columnAtPoint(e.getPoint()));
                if (col == TBL_BTN_SEE) onSeeClick();
            }
        });

        table.getColumnModel().getColumn(TBL_BTN_SEE).setCellRenderer(new ButtonRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 250));
        return scroll;
    }

    /**
     * Handles the search action triggered by the user.
     * <p>Calls the controller with filter values. Empty fields are converted to {@code null}
     * to indicate no filtering for that criterion.</p>
     * @see ClientSearchTableModel#setClients(List) 
     */
    public void onSearchClick() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String fidelityCard = txtFidelityCard.getText().trim();
        List<ClientSupplier> results = controller.search(
                name.isBlank() ? null : name,
                email.isBlank() ? null : email,
                fidelityCard.isBlank() ? null : fidelityCard
        );

        model.setClients(new ArrayList<>(results));
    }

    /**
     * Opens the detailed view for the selected client.
     * <p>If no row is selected, this method does nothing.</p>
     * @see MainWindow#openClientView(ClientSupplier) 
     * @see ClientView
     */
    public void onSeeClick() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        mainWindow.openClientView(displayClients.get(selectedRow));
    }
}