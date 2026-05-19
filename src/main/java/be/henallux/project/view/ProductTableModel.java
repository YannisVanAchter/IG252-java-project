package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Discount;
import main.java.be.henallux.project.model.Product;

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
public class ProductTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "Name", "Category", "Fidelity pts", "Price", "VAT", "Stock",
            "Promo", "Discount", "Promo start",
            ""
    };

    private List<Product> products;

    public ProductTableModel(List<Product> products) {
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

    public Object getValueAt(int row, int col) {
        Product p = products.get(row);
        Discount promo = p.getCurrentDiscount();

        return switch (col) {
            case 0 -> p.getName();
            case 1 -> p.getCategory() != null ? p.getCategory().getName() : "N/A";
            case 2 -> p.getFidelityPoint();
            case 3 -> p.getPrice();
            case 4 -> p.getVat();
            case 5 -> p.getTotalQuantity();
            case 6 -> p.getIsDiscounted();
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

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 6) {
            return Boolean.class;
        }
        return Object.class;
    }
}