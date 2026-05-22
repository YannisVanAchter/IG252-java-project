package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Table model used to display a list of {@link Product} in the receipt in a JTable.
 * <p>Provides column definitions and maps document attributes to table cells,
 * including action columns for editing and deleting.
 * <p>The model is read-only and must be refreshed using
 * {@link #setProducts(LinkedHashMap)} when the data changes.
 * @see RecipeSearchView
 */
public class ReceiptTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "Name", "Quantity", "Total"
    };

    private LinkedHashMap<Product, Integer> products;
    private List<Product> productList;

    public ReceiptTableModel(LinkedHashMap<Product, Integer> products) {
        setProducts(products);
    }

    /**
     * Replaces the current receipt dataset and rebuilds the internal row index.
     * <p>The product order is preserved using the insertion order of the provided {@link LinkedHashMap}.
     * <p>This method triggers a full table refresh via {@link AbstractTableModel#fireTableDataChanged()}.
     *
     * @param products a map associating each {@link Product} with its ordered quantity
     */
    public void setProducts(LinkedHashMap<Product, Integer> products) {
        this.products = products;
        this.productList = new ArrayList<>(products.keySet());
        fireTableDataChanged();
    }

    public Product getProductAt(int row) {
        return productList.get(row);
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
     * Returns the value displayed in a specific cell of the receipt table.
     * <p>Column mapping:
     * <ul><li>product name</li>
     *     <li>quantity ordered</li>
     *     <li>total price (quantity × unit price)</li></ul>
     *
     * @param rowIndex the row index of the product
     * @param columnIndex the column index to evaluate
     * @return the value displayed in the table cell
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product p = productList.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> ViewUtils.safeText(p.getName());
            case 1 -> products.get(p);
            case 2 -> String.format("%.2f €", p.getPrice() * products.get(p));
            default -> null;
        };
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}