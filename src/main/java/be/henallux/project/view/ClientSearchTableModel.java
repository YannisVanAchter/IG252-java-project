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

    public ClientSearchTableModel(List<ClientSupplier> clients) {
        this.clients = clients;
    }

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