package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 StockAlertTableModel provides a table model used to display products that require stock attention in a Swing JTable.
 * <p>This model exposes product inventory information such as current stock level, minimum threshold, and computed
 * restocking quantity, along with a selectable checkbox per row to enable bulk actions.
 * <p>It is primarily used in stock management views where users can review and select products to reorder.
 * <p>The model extends {@link AbstractTableModel} to integrate with {@link javax.swing.JTable} and provides
 * dynamic updates through {@code fireTableDataChanged()} when the underlying dataset changes.
 *
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
     *
     * @param products the list of products to display in the table; if {@code null}, an empty list is used
     */
    public StockAlertTableModel(List<Product> products) {
        setProducts(products);
    }

    /**
     * Replaces the current product dataset and resets selection state.
     * <p>Each product is automatically marked as selected after the update.
     * <p>This method triggers a full table refresh via {@link AbstractTableModel#fireTableDataChanged()}.
     *
     * @param products the new list of products to display; if {@code null}, an empty list is used
     */
    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        this.selected = new ArrayList<>();
        for (int i = 0; i < this.products.size(); i++) selected.add(true);
        fireTableDataChanged();
    }

    /**
     * Returns the product located at the specified row.
     *
     * @param row the row index
     * @return the product at the given row
     */
    public Product getProductAt(int row) {
        return products.get(row);
    }

    /**
     * Toggles the selection state of all products in the table.
     * <p>If all products are currently selected, this method unselects them all.
     * Otherwise, it selects all products.
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
     *
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
     *
     * @param row the row index
     * @return {@code true} if the product is critical, otherwise {@code false}
     * @see #getStatus(Product)
     */
    public boolean isCritical(int row) {
        return "Critical".equals(getStatus(products.get(row)));
    }

    /**
     * Indicates whether the product at the specified row has a low stock level.
     *
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
     *
     * @param product the product to evaluate
     * @return the product status
     */
    private String getStatus(Product product) {
        if (product.getMinStockQuantity() == 0) return "Low";
        double ratio = (double) product.getTotalQuantity() / product.getMinStockQuantity();
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


    /**
     * Returns the value to be displayed at a specific cell in the table.
     * <p>Column mapping:
     * <ul><li>selection checkbox</li>
     *   <li>product name</li>
     *   <li>current stock quantity</li>
     *   <li>minimum stock threshold</li>
     *   <li>quantity to reorder</li>
     *   <li>stock status</li></ul>
     *
     * @param rowIndex the row index of the product
     * @param columnIndex the column index of the value
     * @return the value displayed in the specified cell
     * @see ViewUtils#safeText(String, String)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product p = products.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> selected.get(rowIndex);
            case 1 -> ViewUtils.safeText(p.getName());
            case 2 -> p.getTotalQuantity();
            case 3 -> p.getMinStockQuantity();
            case 4 -> Math.max(0, p.getMinStockQuantity() - p.getTotalQuantity());
            case 5 -> getStatus(p);
            default -> null;
        };
    }


    /**
     * Updates the value of a table cell.
     * <p>Only the selection column is editable.
     *
     * @param aValue      the new value
     * @param rowIndex    the row index
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
     * Returns the Java type used to render a given column in the table.
     * <p>This information is used by {@link javax.swing.JTable} to choose the appropriate
     * renderer and editor for each column.
     * <p>In this model, the 0 column is explicitly typed as {@link Boolean} to ensure
     * correct checkbox rendering, while all other columns use the default {@link Object} type.
     *
     * @param columnIndex the index of the column whose type is requested
     * @return the {@link Class} representing the type of data stored in the column
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
     *
     * @param rowIndex    the row index
     * @param columnIndex the column index
     * @return {@code true} if the cell is editable
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }
}