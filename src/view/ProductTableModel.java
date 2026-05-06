package view;

import model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ProductTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {
            "Name", "Price", "VAT", "Points", "Promotion", ""
    };

    private List<Product> products;

    public ProductTableModel(List<Product> products) {
        this.products = products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        fireTableDataChanged();
    }

    public Product getProductAt(int row) {
        return products.get(row);
    }

    @Override
    public int getRowCount() {
        return products != null ? products.size() : 0;
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
        Product p = products.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> p.getName();
            case 1 -> p.getPrice();
            case 2 -> p.getVat();
            case 3 -> p.getPoints();
            case 4 -> p.isInPromotion() ? "Yes" : "No";
            case 5 -> "See Product";
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}