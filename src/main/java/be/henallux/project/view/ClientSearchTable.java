package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.ClientSupplierSearchController;
import main.java.be.henallux.project.model.ClientSupplier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A Swing panel that displays and manages a searchable table of clients.
 * <p>This view allows users to filter clients by name, email, and fidelity card number
 * and displays the results in a table format.
 * <p>Users can click on a row action button to open a detailed client view
 * in the main application window.
 *
 * @see MainWindow#openClientView(ClientSupplier)
 */
public class ClientSearchTable extends JPanel {
    private static final int TBL_BTN_SEE = ClientSearchTableModel.TBL_BTN_SEE;

    private final MainWindow mainWindow;
    private final ClientSupplierSearchController controller;
    private ClientSearchTableModel model;

    private final List<ClientSupplier> displayClients;

    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtFidelityCard;

    private JTable table;

    /**
     * Creates the client search table view.
     * <p>This constructor builds all visual sections, including:
     * <ul><li>The header section</li>
     *     <li>The search filter form</li>
     *     <li>The client result table</li></ul>
     * <p>The initial table content is populated with an unfiltered search.
     *
     * @param mainWindow the parent {@link MainWindow} used to open detailed client views
     * @see ClientSupplierSearchController#search(String, String, String)
     */
    public ClientSearchTable(MainWindow mainWindow) {
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
     *
     * @return a {@link JPanel} representing the header
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
     *
     * @return a {@code JPanel} containing search filters
     * @see #onSearchClick()
     */
    private JPanel buildSearchPanel() {
        txtName = new JTextField(10);
        ViewUtils.setCursor(txtName);
        JPanel namePanel = new JPanel(new BorderLayout(0, 4));
        namePanel.add(new JLabel("Name"), BorderLayout.NORTH);
        namePanel.add(txtName, BorderLayout.CENTER);

        txtEmail = new JTextField(10);
        ViewUtils.setCursor(txtEmail);
        JPanel emailPanel = new JPanel(new BorderLayout(0, 4));
        emailPanel.add(new JLabel("Email"), BorderLayout.NORTH);
        emailPanel.add(txtEmail, BorderLayout.CENTER);

        txtFidelityCard = new JTextField(10);
        ViewUtils.digitsOnly(txtFidelityCard);
        ViewUtils.setCursor(txtFidelityCard);
        JPanel fidelityPanel = new JPanel(new BorderLayout(0, 4));
        fidelityPanel.add(new JLabel("Fidelity card number"), BorderLayout.NORTH);
        fidelityPanel.add(txtFidelityCard, BorderLayout.CENTER);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> onSearchClick());
        ViewUtils.setCursor(btnSearch);

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
     * <p>The table uses {@link ClientSearchTableModel} as its data model and configures custom interactions for the action column.
     * <p>The action column is rendered using {@link ButtonRenderer} and allows the user to open the selected client view.
     *
     * @return a {@code JScrollPane} containing the JTable
     * @see ClientSearchView
     * @see ButtonRenderer
     * @see ClientSearchTableModel
     * @see #onSeeClick()
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
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0 && col == ClientSearchTableModel.TBL_BTN_SEE) {
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    table.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        table.getColumnModel().getColumn(TBL_BTN_SEE).setCellRenderer(new ButtonRenderer());
        ViewUtils.resizeColumnWidth(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 250));
        return scroll;
    }

    /**
     * Handles the search action triggered by the user.
     * <p>Empty input fields are converted to {@code null} before being sent to {@link ClientSupplierSearchController}
     * to disable the corresponding filter criterion.
     * <p>After the search is completed, the table model is updated with the retrieved results.
     *
     * @see ClientSupplierSearchController#search(String, String, String)
     * @see ClientSearchTableModel#setClients(List)
     *
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
     * If no row is selected, this method does nothing.
     * <p>The selected {@link ClientSupplier} is opened through {@link MainWindow#openClientView(ClientSupplier)}.
     *
     * @see MainWindow#openClientView(ClientSupplier)
     * @see ClientSearchView
     */
    public void onSeeClick() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        mainWindow.openClientView(displayClients.get(selectedRow));
    }
}