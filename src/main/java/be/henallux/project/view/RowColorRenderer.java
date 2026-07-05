package be.henallux.project.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom table cell renderer used to color stock alert rows.
 * <p>Row colors depend on the stock status returned by
 * {@link StockAlertTableModel#isCritical(int)} and
 * {@link StockAlertTableModel#isLow(int)}.
 * <p>Supported visual states:
 * <ul><li>critical stock</li><li>low stock</li><li>normal selection highlight</li></ul>
 * <p>This renderer extends {@link DefaultTableCellRenderer} and is applied tothe stock alert product table.
 *
 * @see JTable
 * @see StockAlertTableModel
 */
public class RowColorRenderer extends DefaultTableCellRenderer {
    private final StockAlertTableModel model;

    private static final Color BG_CRITICAL = new Color(255, 230, 230);
    private static final Color BG_LOW = new Color(255, 245, 220);
    private static final Color BG_NORMAL = Color.WHITE;
    private static final Color FG_CRITICAL = new Color(183, 28, 28);
    private static final Color FG_LOW = Color.BLACK;    //new Color(243, 180, 52);
    private static final Color FG_NORMAL = Color.BLACK;

    public RowColorRenderer(StockAlertTableModel model) {
        this.model = model;
    }

    /**
     * Returns the component used to render a table cell.
     * <p>Row colors are applied according to the product stock status:
     * <ul><li>critical rows use a red background</li><li>low stock rows use an orange background</li><li>normal rows use a white background</li></ul>
     * <p>Selected rows override the default status colors with a blue selection highlight.
     *
     * @param table      the parent table
     * @param value      the cell value
     * @param isSelected indicates whether the row is selected
     * @param hasFocus   indicates whether the cell has focus
     * @param row        the row index
     * @param column     the column index
     * @return the rendered cell component
     */
    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column
    ) {

        Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column
        );

        if (model.isCritical(row)) {
            c.setBackground(BG_CRITICAL);
            c.setForeground(FG_CRITICAL);

        } else if (model.isLow(row)) {
            c.setBackground(BG_LOW);
            c.setForeground(FG_LOW);

        } else {
            c.setBackground(BG_NORMAL);
            c.setForeground(FG_NORMAL);
        }

        if (isSelected) {
            c.setBackground(new Color(220, 235, 252));
            c.setForeground(Color.BLACK);
        }

        return c;
    }
}