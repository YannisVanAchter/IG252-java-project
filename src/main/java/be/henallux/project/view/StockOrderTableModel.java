package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Table model used to manage products displayed in a stock order table.
 * <p>Each row represents a product with:
 * <ul><li>its name</li>
 * <li>the suggested quantity to order</li>
 * <li>the quantity actually ordered</li></ul>
 * <p>This class extends {@link AbstractTableModel} to provide data to a Swing table component.
 *
 * @see javax.swing.JTable
 * @see Product
 * @see StockOrderCreation
 */
public class StockOrderTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "Name", "Suggested Quantity", "Quantity Ordered"
    };

    private final ArrayList<ProductRow> rows = new ArrayList<>();

    public final static int TBL_SPN_INDEX = 2;

    /**
     * Creates a new table model from a list of products.
     * @param products the products to display
     * @see #setProducts(ArrayList)
     */
    public StockOrderTableModel(ArrayList<Product> products) {
        setProducts(products);
    }


    /**
     * Replaces the current table content with the provided products.
     * <p>The suggested quantity is calculated using:
     * {@code minimumStock - currentStock}
     * @param products the products to load into the table
     */
    public void setProducts(ArrayList<Product> products) {
        rows.clear();
        for (Product p : products) {
            int suggested = Math.max(0, p.getMinStockQuantity() - p.getTotalQuantity());
            rows.add(new ProductRow(ViewUtils.safeText(p.getName(), "Unknown"), suggested, suggested));
        }
        fireTableDataChanged();
    }

    /**{@inheritDoc}*/
    @Override
    public int getRowCount() {
        return rows.size();
    }

    /**{@inheritDoc}*/
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    /**{@inheritDoc}*/
    @Override
    public String getColumnName(int col) {
        return COLUMNS[col];
    }

    /**
     * Returns the value displayed at a specific cell.
     * <p>Column mapping:
     * <ul>
     *   <li>product name</li>
     *   <li>suggested quantity</li>
     *   <li>ordered quantity</li>
     * </ul>
     *
     * @param row the row index
     * @param col the column index
     * @return the value stored in the specified cell
     */    @Override
    public Object getValueAt(int row, int col) {
        ProductRow r = rows.get(row);

        return switch (col) {
            case 0 -> r.name;
            case 1 -> r.suggestedQty;
            case 2 -> r.orderedQty;
            default -> null;
        };
    }

    /**
     * Indicates the cell 2 can be edited.
     * <p>Only the {@code Quantity Ordered} column is editable.
     * @param row the row index
     * @param col the column index
     * @return {@code true} if the cell is editable, otherwise {@code false}
     */    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 2;
    }

    /**
     * Updates the value of a table cell.
     * <p>Only column {@code 2} is handled.
     *
     * @param value the new value
     * @param row the row index
     * @param col the column index
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 2) {
            rows.get(row).orderedQty = (int) value;
            fireTableCellUpdated(row, col);
        }
    }

    /**
     * Computes the total ordered quantity for all rows.
     * @return the total quantity ordered
     */
    public int getTotal() {
        int total = 0;
        for (ProductRow r : rows) {
            total = total + r.orderedQty;
        }
        return total;
    }

    /**
     * Represents a single row in the stock order table.
     * <p>Each row stores:
     * <ul><li>the product name</li>
     * <li>the suggested quantity</li>
     * <li>the ordered quantity</li></ul>
     */
    static class ProductRow {
        String name;
        int suggestedQty;
        int orderedQty;

        public ProductRow(String name, int suggestedQty, int orderedQty) {
            this.name = name;
            this.suggestedQty = suggestedQty;
            this.orderedQty = orderedQty;
        }
    }
}