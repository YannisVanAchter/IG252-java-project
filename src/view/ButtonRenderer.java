package view;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * A custom renderer used to display a {@link JButton} inside a {@link JTable} cell.
 * <p> In Swing, a table normally displays text values inside its cells.
 * This class allows a table cell to visually behave like a button by
 * rendering a {@code JButton} component in the specified column.
 *
 * <p> The renderer is only responsible for the visual representation of the button.
 * It does not handle click actions or events. To make the button interactive,
 * a corresponding editor (such as a custom {@code TableCellEditor}) must also be used.
 *
 * <p>
 * Typical use case:
 * <pre>{@code
 * table.getColumn("Action").setCellRenderer(new ButtonRenderer("Delete"));
 * }</pre>
 */
public class ButtonRenderer extends JButton implements TableCellRenderer {

    /**
     * Creates a button renderer with a default label.
     * <p>The label can later be replaced dynamically depending on the value stored in the table cell.*
     *
     * @param label the default text displayed on the button
     */
    public ButtonRenderer(String label) {
        setOpaque(true);
        setText(label);
    }

    /**
     * Creates a button renderer without an initial label.
     * <p>The displayed text will be determined dynamically from the table cell value.
     */
    public ButtonRenderer() {
        this(null);
    }

    /**
     * This method is automatically called by the {@link JTable}
     * whenever a cell needs to be displayed.
     * The button text is updated according to the cell value.
     *
     * @param table      the JTable that is asking the renderer to draw the cell
     * @param value      the value stored in the cell
     * @param isSelected indicates whether the cell is selected
     * @param hasFocus   indicates whether the cell currently has focus
     * @param row        index of the cell
     * @param column     the column index of the cell
     * @return the component used to render the cell (this button instance)
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}