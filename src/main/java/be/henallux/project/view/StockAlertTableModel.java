package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model used to display products requiring restocking.
 * <p>Each row represents a product and contains:
 * <ul><li>a selection checkbox</li>
 * <li>the product name</li>
 * <li>the current stock level</li>
 * <li>the minimum stock threshold</li>
 * <li>the suggested quantity to order</li>
 * <li>the stock status</li></ul>
 * <p>This model extends {@link AbstractTableModel} to provide data to a Swing table.
 * @see javax.swing.JTable
 * @see Product
 */
public class StockAlertTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "", "Name", "Current stock level", "Minimum threshold", "Quantity to order", "Status"
    };

    private List<Product> products;
    private List<Boolean> selected;

    /**
     * Creates a new stock alert table model.
     * <p>All products are selected by default.
     * @param products the products displayed in the table
     */
    public StockAlertTableModel(List<Product> products) {
        setProducts(products);
    }

    /**
     * Replaces the current product list and refreshes the table view.
     * <p>All products are automatically selected.
     * @param products the new list of products
     */
    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        this.selected = new ArrayList<>();
        for (int i = 0; i < this.products.size(); i++) selected.add(true);
        fireTableDataChanged();
    }

    /**
     * Returns the product located at the specified row.
     * @param row the row index
     * @return the product at the given row
     */
    public Product getProductAt(int row) {
        return products.get(row);
    }

    /**
     * Toggles all checkboxes: if all are checked, unchecks all; otherwise checks all.
     */
    public void toggleAll() {
        boolean allChecked = selected.stream().allMatch(b -> b);
        for (int i = 0; i < selected.size(); i++) {
            selected.set(i, !allChecked);
        }
        fireTableDataChanged();
    }

    /**
     * Returns the list of products whose checkbox is checked.
     * @return the selected products
     */
    public ArrayList<Product> getSelectedProducts() {
        ArrayList<Product> list = new ArrayList<>();
        for (int i = 0; i < products.size(); i++)
            if (selected.get(i)) {
                list.add(products.get(i));
            }
        return list;
    }

    /**
     * Indicates whether the product at the specified row is critical.
     * <p>A product is considered critical when its stock ratio is below {@code 50%}.
     * @param row the row index
     * @return {@code true} if the product is critical, otherwise {@code false}
     * @see #getStatus(Product)
     */
    public boolean isCritical(int row) {
        return "Critical".equals(getStatus(products.get(row)));
    }

    /**
     * Indicates whether the product at the specified row has a low stock level.
     * @param row the row index
     * @return {@code true} if the product status is low
     * @see #getStatus(Product)
     */
    public boolean isLow(int row) {
        return "Low".equals(getStatus(products.get(row)));
    }

    /**
     * Computes the stock status of a product.
     * <p> Status values:
     * <ul><li>{@code "Critical"} if stock is below 50% of the minimum threshold</li>
     * <li>{@code "Low"} otherwise</li></ul>
     * @param product the product to evaluate
     * @return the product status
     */
    private String getStatus(Product product) {
        if (product.getMinStock() == 0) return "Low";
        double ratio = (double) product.getQuantity().getNbProduct() / product.getMinStock();
        if (ratio < 0.5) return "Critical";
        return "Low";
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
            case 0 -> selected.get(rowIndex);
            case 1 -> p.getName();
            case 2 -> p.getQuantity().getNbProduct();
            case 3 -> p.getMinStock();
            case 4 -> Math.max(0, p.getMinStock() - p.getQuantity().getNbProduct());
            case 5 -> getStatus(p);
            default -> null;
        };
    }


    /**
     * Updates the value of a table cell.
     * <p>Only the selection column is editable.
     * @param aValue the new value
     * @param rowIndex the row index
     * @param columnIndex the column index
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            selected.set(rowIndex, (Boolean) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    /**
     * Returns the Java class associated with a column.
     * <p>Column {@code 0} uses {@link Boolean} for checkboxes.
     * @param columnIndex the column index
     * @return the column class
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean.class;
        }
        return Object.class;
    }


    /**
     * Indicates whether a cell can be edited.
     * <p>Only the checkbox column is editable.
     * @param rowIndex the row index
     * @param columnIndex the column index
     * @return {@code true} if the cell is editable
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }
}