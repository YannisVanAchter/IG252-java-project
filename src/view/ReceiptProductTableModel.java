package view;

import model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model used to display a list of {@link Product} for the receipt in a JTable.
 * Provides column definitions and maps document attributes to table cells,
 * including action columns for editing and deleting.
 * The model is read-only and must be refreshed using
 * {@link #setProducts(List)} when the data changes.
 * @see RecipeView
 */
public class ReceiptProductTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "Name", "Price", "Action"
    };

    private List<Product> products;

    public ReceiptProductTableModel(ArrayList<Product> products) {
        this.products = products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        fireTableDataChanged();
    }

    public Product getProductAt(int row) {
        return products.get(row);
    }

    /** {@inheritDoc} */
    @Override
    public int getRowCount() {
        return products != null ? products.size() : 0;
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
        Product p = products.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> p.getName();
            case 1 -> p.getPrice();
            case 2 -> "Add";
            default -> null;
        };
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
