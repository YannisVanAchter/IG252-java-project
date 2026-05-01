package model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class ClientSupplierTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "", "Name", "First Name", "Email", "TVA", "Type", "Loyality", "Edit", "Delete"
    };

    private ArrayList<ClientSupplier> clientSuppliers;

    public ClientSupplierTableModel(ArrayList<ClientSupplier> clientSuppliers) {
        this.clientSuppliers = clientSuppliers;
    }

    public void setClientSuppliers(ArrayList<ClientSupplier> clientSuppliers) {
        this.clientSuppliers = clientSuppliers;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return clientSuppliers.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ClientSupplier cs = clientSuppliers.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> "";
            case 1 -> cs.getName();
            case 2 -> cs.getFirstname();
            case 3 -> cs.getEmail();
            case 4 -> cs.getVATNumber();
            case 5 -> cs.getType();
            case 6 -> "100" ;       //cs.getLoyalty(); //TODO add loyality
            case 7 -> "Edit";
            case 8 -> "Delete";
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
