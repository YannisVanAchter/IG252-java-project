package be.henallux.project.view;

import be.henallux.project.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model used to display a list of {@link Product} for the receipt in a JTable.
 * Provides column definitions and maps document attributes to table cells,
 * including action columns for editing and deleting.
 * The model is read-only and must be refreshed using
 * {@link #setProducts(List)} when the data changes.
 * @see ReceiptCreateView
 */
public class ReceiptProductTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "Name", "Price", "Action"
    };

    public static final int TBL_BTN_ADD = 2;

    private List<Product> products;

    public ReceiptProductTableModel(ArrayList<Product> products) {
        this.products = products;
    }

    /**
     * Updates the list of products displayed in the table model.
     * <p>This method replaces the current dataset and triggers a full table refresh via {@link  AbstractTableModel#fireTableDataChanged()}.
     *
     * @param products the new list of {@link Product} to display
     */
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

    /**
     * Returns the value displayed in a specific cell of the table.
     * <p>Column mapping:
     * <ul><li>product name</li>
     *     <li>formatted product price</li>
     *     <li>action label ("Add")</li></ul>
     *
     * @param rowIndex the row index of the product
     * @param columnIndex the column index to evaluate
     * @return the value to display in the table cell
     * 
     * @see ViewUtils#safeText(String)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product p = products.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> ViewUtils.safeText(p.getName(), "Unknown");
            case 1 -> String.format("%.2f €", p.getPrice());
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
