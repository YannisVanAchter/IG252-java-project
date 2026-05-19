package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.ClientSupplier;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Table model used to display a list of {@link ClientSupplier} in a JTable.
 * Provides column definitions and maps each client/supplier attribute
 * to its corresponding table cell, including action columns (Edit/Delete).
 * The model is read-only and must be refreshed using
 * {@link #setClientSuppliers(ArrayList)} when data changes.
 */
public class ClientSupplierTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "id", "Name & First Name", "Email", "TVA", "Type", "Loyality", "", ""
    };

    public static final int TBL_BTN_DEL = 7;
    public static final int TBL_BTN_UPDATE = 6;

    private ArrayList<ClientSupplier> clientSuppliers;

    public ClientSupplierTableModel(ArrayList<ClientSupplier> clientSuppliers) {
        this.clientSuppliers = clientSuppliers;
    }

    /**
     * Load the ClientSupplier list in the table and allow refresh with new data.
     * @see AbstractTableModel#fireTableDataChanged();
     * @param clientSuppliers
     */
    public void setClientSuppliers(ArrayList<ClientSupplier> clientSuppliers) {
        this.clientSuppliers = clientSuppliers;
        fireTableDataChanged();
    }

    /** {@inheritDoc} */
    @Override
    public int getRowCount() {
        return clientSuppliers.size();
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    /** {@inheritDoc} */
    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    /** {@inheritDoc} */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ClientSupplier cs = clientSuppliers.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> cs.getId();
            case 1 -> cs.getLabel();
            case 2 -> cs.getEmail();
            case 3 -> cs.getVATNumber();
            case 4 -> cs.getType();
            case 5 -> cs.getFidelityCard().getTotalPoint();
            case 6 -> "Edit";
            case 7 -> "Delete";
            default -> null;
        };
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
