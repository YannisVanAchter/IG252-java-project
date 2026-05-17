package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Table model used to display a list of {@link Product} in the receipt in a JTable.
 * Provides column definitions and maps document attributes to table cells,
 * including action columns for editing and deleting.
 * The model is read-only and must be refreshed using
 * {@link #setProducts(LinkedHashMap)} when the data changes.
 * @see RecipeView
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

    /** {@inheritDoc} */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product p = productList.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> p.getName();
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