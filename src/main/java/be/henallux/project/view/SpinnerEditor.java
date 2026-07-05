package be.henallux.project.view;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

/**
 * Custom cell editor used to edit numeric values in a JTable using a {@link JSpinner}.
 * <p>This editor is designed for quantity fields (e.g. ordered stock amounts) and restricts input to integer values between 0 and 9999.
 * <p>It is typically used in combination with a table model such as {@link StockOrderTableModel}.
 *
 * @see JTable
 * @see JSpinner
 * @see SpinnerNumberModel
 */
class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {

    private final JSpinner spinner;

    public SpinnerEditor() {
        spinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
    }

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    /**
     * Returns the component used to edit a table cell.
     * <p>The spinner is initialized with the current cell value or {@code 0} if null.
     *
     * @param table      the JTable being edited
     * @param value      the current cell value
     * @param isSelected whether the cell is selected
     * @param row        the row index
     * @param column     the column index
     * @return the spinner component used for editing
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        spinner.setValue(value == null ? 0 : value);
        return spinner;
    }
}