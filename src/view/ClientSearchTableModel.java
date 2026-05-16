package view;

import model.ClientSupplier;
import model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

//TODO: remplacer name + firstName par label
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
            "Name", "First Name", "Email", "Phone", "Client since",
            "Points", "Card",
            "City", "Postal code",
            ""
    };

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
            case 0  -> cs.getName();
            case 1  -> cs.getFirstname();
            case 2  -> cs.getEmail();
            case 3  -> cs.getPhoneNumber();
            case 4  -> cs.getBecameClientDate();
            case 5  -> "100"; // FidelityCard — TODO: cs.getFidelityCard().getTotalPoint()
            case 6  -> "123456789"; // TODO: cs.getFidelityCard()
            case 7  -> cs.getAddress().getLocality().getName();
            case 8  -> cs.getAddress().getLocality().getPostalCode();
            case 9  -> "See";
            default -> null;
        };
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCellEditable(int row, int col) { return false; }
}