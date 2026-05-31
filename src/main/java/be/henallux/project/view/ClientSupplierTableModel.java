package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.ClientSupplierController;
import main.java.be.henallux.project.model.ClientSupplier;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Table model used to display a list of {@link ClientSupplier} in a JTable.
 * <p>Provides column definitions and maps each client/supplier attribute
 * to its corresponding table cell, including action columns (Edit/Delete).
 * <p>The model is read-only and must be refreshed using
 * {@link #setClientSuppliers(ArrayList)} when data changes.
 *
 * @see ClientSupplier
 * @see ClientSupplierTable
 * @see ClientSupplierController
 */
public class ClientSupplierTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "id", "Name & First Name", "Email", "TVA", "Type", "Loyalty", "", ""
    };

    public static final int TBL_BTN_DEL = 7;
    public static final int TBL_BTN_UPDATE = 6;

    private ArrayList<ClientSupplier> clientSuppliers;

    public ClientSupplierTableModel(ArrayList<ClientSupplier> clientSuppliers) {
        this.clientSuppliers = clientSuppliers;
    }

    /**
     * Load the ClientSupplier list in the table and allow refresh with new data.
     * <p>It is typically called after a modification of the dataset such as:
     * <ul><li>loading data from {@link ClientSupplierController}</li>
     *    <li>applying filters in {@link ClientSupplierTable}</li>
     *    <li>deleting or updating an entry</li></ul>
     *
     * @param clientSuppliers to display
     * @see AbstractTableModel#fireTableDataChanged();
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

    /**
     * Returns the value displayed in a specific table cell.
     * <p>The returned value depends on the requested column and formatted information from the corresponding {@link ClientSupplier}.
     *
     * <p>Columns include:
     * <ul><li>Identifier and identity information</li>
     *     <li>Contact information (name, email)</li>
     *     <li>Legal and business information (VAT number, type)</li>
     *     <li>Loyalty information when available</li>
     *     <li>Action columns for edit and delete operations</li></ul>
     *
     * <p>Missing or undefined values are replaced with placeholder values such as {@code "-"}.
     *
     * @param rowIndex the row index corresponding to a {@link ClientSupplier}
     * @param columnIndex the column index defining which attribute to display
     * @return the value displayed in the specified cell, or {@code null} if the index is invalid
     * @see ClientSupplier
     * @see ViewUtils#safeText(String)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ClientSupplier cs = clientSuppliers.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> cs.getId();
            case 1 -> ViewUtils.safeText(cs.getName()) + " " + ViewUtils.safeText(cs.getFirstname());
            case 2 -> ViewUtils.safeText(cs.getEmail());
            case 3 -> ViewUtils.safeText(cs.getVATNumber());
            case 4 -> ViewUtils.safeText(cs.getType());
            case 5 -> cs.getFidelityCard() != null ? cs.getFidelityCard().getTotalPoint() : "-";
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
