package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.ClientSupplier;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Table model used to display a list of {@link ClientSupplier} instances
 * in a {@link ClientSearchTable} through a {@link javax.swing.JTable}.
 * <p>It provides an action column ("See") used by the UI
 * to trigger navigation to a detailed client view.
 * <p>The model is read-only: all modifications must be done by replacing
 * the underlying data via {@link #setClients(List)}.</p>
 *
 * @see ClientSearchTable
 * @see ClientSupplier
 */
public class ClientSearchTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "Name & First Name", "Email", "Phone", "Client since",
            "Points", "Card",
            "City", "Postal code",
            ""
    };

    public static final int TBL_BTN_SEE = 8;

    private List<ClientSupplier> clients;

    /**
     * Creates a table model used to display client search results in a {@link javax.swing.JTable}.
     * <p>This model stores the list of {@link ClientSupplier} displayed inside the {@link ClientSearchTable}.
     * <p>The table content is entirely driven by the provided list and is intended
     * to be refreshed through {@link #setClients(List)} after each search operation.
     *
     * @param clients the initial list of {@link ClientSupplier} displayed in the table
     * @see #setClients(List)
     * @see ClientSearchTable
     */
    public ClientSearchTableModel(List<ClientSupplier> clients) {
        this.clients = clients;
    }

    /**
     * Replaces the currently displayed client list.
     * <p>After updating the data source, the {@link javax.swing.JTable} is refresh by {@link AbstractTableModel#fireTableDataChanged}.
     * <p>This method is typically called after a new search performed by {@link ClientSearchTable#onSearchClick()}.
     *
     * @param clients the new list of {@link ClientSupplier} to display
     * @see ClientSearchTable#onSearchClick()
     */
    public void setClients(List<ClientSupplier> clients) {
        this.clients = clients;
        fireTableDataChanged();
    }

    /** {@inheritDoc} */
    @Override public int getRowCount()    { return clients.size(); }
    /** {@inheritDoc} */
    @Override public int getColumnCount() { return COLUMNS.length; }
    /** {@inheritDoc} */
    @Override public String getColumnName(int col) { return COLUMNS[col]; }

    /** {@inheritDoc} */
    /**
     * Returns the value displayed for a specific table cell.
     * <p>The returned value depends on the requested column and formatted information from the corresponding {@link ClientSupplier}.
     * <ul><li>Identity and contact information</li>
     *     <li>Client registration date</li>
     *     <li>Fidelity card information</li>
     *     <li>Address and locality information</li>
     *     <li>Action label for the detail view button</li></ul>
     * <p>Missing values ({@code null}) are replaced with {@code "-"}.
     *
     * @param row the row index corresponding to the displayed {@link ClientSupplier}
     * @param col the column index identifying the requested field
     * @return the value displayed inside the requested table cell
     * @see ClientSupplier
     * @see ViewUtils#safeText(String) 
     */
    @Override
    public Object getValueAt(int row, int col) {
        ClientSupplier cs = clients.get(row);
        return switch (col) {
            case 0  -> ViewUtils.safeText(cs.getName()) + " " + ViewUtils.safeText(cs.getFirstname());
            case 1  -> ViewUtils.safeText(cs.getEmail());
            case 2  -> ViewUtils.safeText(cs.getPhoneNumber());
            case 3  -> ViewUtils.formatDate(cs.getBecameClientDate());
            case 4  -> cs.getFidelityCard() != null ? cs.getFidelityCard().getTotalPoint() : "-";
            case 5  -> cs.getFidelityCard() != null ? cs.getFidelityCard().getId() : "-";
            case 6  -> cs.getAddress() != null ? cs.getAddress().getLocality().getCity() : "-";
            case 7  -> cs.getAddress() != null ? cs.getAddress().getLocality().getPostalCode() : "-";
            case 8  -> "See";
            default -> null;
        };
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCellEditable(int row, int col) { return false; }
}