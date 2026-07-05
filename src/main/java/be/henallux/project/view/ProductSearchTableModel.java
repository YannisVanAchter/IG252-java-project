package be.henallux.project.view;

import be.henallux.project.model.Discount;
import be.henallux.project.model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Table model used to display a list of {@link Product} instances
 * in a {@link javax.swing.JTable} (typically inside a product search view).
 * <p>This model is responsible for providing read-only access to product data,
 * including pricing, stock, category, and promotion information.
 * <p>An additional action column ("See") is included to allow the UI
 * to trigger navigation to a detailed product view.
 * <p>The model is read-only: all modifications must be done by replacing
 * the underlying dataset via {@link #setProducts(List)}.</p>
 *
 * @see ProductSearchTable
 * @see Product
 * @see Discount
 */
public class ProductSearchTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "Name", "Category", "Fidelity pts", "Price", "VAT", "Stock",
            "Promo", "Discount", "Promo start",
            ""
    };

    private List<Product> products;

    public static final int TBL_BTN_SEE = 9;

    public ProductSearchTableModel(List<Product> products) {
        this.products = products;
    }

    /**
     * Replaces the current product list and refreshes the table view.
     * @param products the new list of products
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
     * Returns the value displayed in a specific table cell.
     * <p>The value is computed based on the column index and the corresponding
     * {@link Product} instance located at the given row.
     * <p>Displayed columns include:
     * <ul><li>Product name</li>
     *   <li>Category name (or "-" if not defined)</li>
     *   <li>Fidelity points</li>
     *   <li>Base price</li>
     *   <li>VAT percentage (defaulting to 21% if not specified)</li>
     *   <li>Total stock quantity</li>
     *   <li>Discount status flag</li>
     *   <li>Current discount percentage (if available)</li>
     *   <li>Promotion start date (if available)</li>
     *   <li>Action column to trigger the product detail view</li></ul>
     *
     * <p>Promotion-related values are derived from {@link Product#getCurrentDiscount()}.
     * Missing or undefined values are represented using "-" or default fallbacks.
     *
     * @param row the row index corresponding to a {@link Product}
     * @param col the column index defining which product attribute is displayed
     * @return the value to display in the table cell, or {@code null} if the column index is invalid
     * @see Product
     * @see Discount
     * @see ViewUtils#formatDate(java.time.LocalDate)
     * @see ViewUtils#safeText(String)
     */
    public Object getValueAt(int row, int col) {
        Product product = products.get(row);
        Discount promo = product.getCurrentDiscount();

        return switch (col) {
            case 0 -> ViewUtils.safeText(product.getName());
            case 1 -> product.getCategory() != null ? product.getCategory().getName() : "-";
            case 2 -> product.getFidelityPoint();
            case 3 -> product.getPrice();
            case 4 -> product.getVat() != null ? product.getVat() + "%" : "21%";
            case 5 -> product.getTotalQuantity();
            case 6 -> product.getIsDiscounted();
            case 7 -> promo != null ? promo.getDiscountPercentage() + "%" : "-";
            case 8 -> promo != null ? ViewUtils.formatDate(promo.getStartDate()) : "-";
            case 9 -> "See";
            default -> null;
        };
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Returns the Java type used to render a given column in the table.
     * <p>This information is used by {@link javax.swing.JTable} to choose the appropriate
     * renderer and editor for each column.
     * <p>In this model, the "Promo" column is explicitly typed as {@link Boolean} to ensure
     * correct checkbox rendering, while all other columns use the default {@link Object} type.
     *
     * @param columnIndex the index of the column whose type is requested
     * @return the {@link Class} representing the type of data stored in the column
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 6) {
            return Boolean.class;
        }
        return Object.class;
    }
}