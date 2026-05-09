package view;

import model.DocumentType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;
import java.time.*;
import java.util.Date;

/**
 * Utility class providing reusable Swing helpers for the view package.
 * Cannot be instantiated.
 */
public class ViewUtils {
    /**
     * Creates and returns a JPanel configured with a vertical BoxLayout.
     * The panel can be used as a container to arrange components in a column layout.
     *
     * @return a JPanel with a vertical BoxLayout
     */
    public static JPanel createColumnPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    /**
     * Creates a JPanel containing a JLabel with the specified text and a JComponent.
     * The components are arranged vertically using a BoxLayout.
     *
     * @param text the text to display in JLabel
     * @param comp the JComponent added below the JLabel
     * @return a structured JPanel containing the JLabel and the JComponent
     */
    public static JPanel labeled(String text, JComponent comp) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(new JLabel(text));
        p.add(Box.createVerticalStrut(4));
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));
        p.add(comp);
        p.add(Box.createVerticalStrut(4));
        return p;
    }

    public static JPanel labeledRequired(String labelText, JComponent comp) {
        return labeled(labelText + "*", comp);
    }

    /**
     * Wraps a component in a JPanel {@code BorderLayout CENTER}
     * so that it stretches horizontally, with a fixed height.
     * @return {@code JPanel}
     */
    public static JPanel makeRow(JComponent component) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBorder(new EmptyBorder(0, 6, 0, 6));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                component.getPreferredSize().height + 4));
        row.add(component, BorderLayout.CENTER);
        return row;
    }

    /**
     * Creates and returns a date spinner component pre-configured to display dates
     * in the "dd/MM/yyyy" format.
     *
     * @return a JSpinner configured for date selection in "dd/MM/yyyy" format
     */
    public static JSpinner createDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        return spinner;
    }

    /**
     * Creates a JPanel containing a JLabel
     * and a right-aligned panel with a JCheckBox and a JSpinner.
     * The JCheckBox toggles the enabled state of the JSpinner.
     *
     * @param label the text to display in the JLabel
     * @param spinner the JSpinner to be displayed and toggled by the JCheckBox
     * @param chk the JCheckBox used to enable or disable the JSpinner
     * @return a structured JPanel containing the JLabel, JCheckBox, and JSpinner
     */
    public static JPanel labeledToggleDate(String label, JSpinner spinner, JCheckBox chk) {
        spinner.setEnabled(false);

        chk.addActionListener(e -> spinner.setEnabled(chk.isSelected()));

        JPanel right = new JPanel(new BorderLayout(8, 0));
        right.add(chk, BorderLayout.WEST);
        right.add(spinner, BorderLayout.CENTER);

        return labeled(label, right);
    }
    /**
     * Creates a JSpinner configured for integer input.
     *  Spinner align on Left
     * @param value initial value
     * @param min minimum value
     * @param max maximum value
     * @param step increment step
     * @return a number JSpinner
     */
    public static JSpinner createNumberSpinner(int value, int min, int max, int step) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "#");
        spinner.setEditor(editor);
        editor.getTextField().setHorizontalAlignment(JTextField.LEFT);
        return spinner;
    }

    /**
     * Extracts a LocalDate from a date JSpinner.
     *
     * @param spinner a JSpinner containing a Date value
     * @return the corresponding LocalDate
     */
    public static LocalDate getDate(JSpinner spinner) {
        return ((Date) spinner.getValue())
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Converts a LocalDate to a Date for use in JSpinners.
     *
     * @param localDate the date to convert
     * @return the corresponding Date
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) return null;

        return Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
    }

    /**
     * Converts a Date to a LocalDate.
     *
     * @param date the date to convert
     * @return the corresponding LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Attaches a DocumentListener to a JTextField that triggers onFilter on every change.
     *
     * @param textField the text field to listen to
     * @param onFilter action to run on change
     * @return the same JTextField
     */
    public static JTextField addFilterListener(JTextField textField, Runnable onFilter) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { onFilter.run(); }
            public void removeUpdate(DocumentEvent e)  { onFilter.run(); }
            public void changedUpdate(DocumentEvent e) { onFilter.run(); }
        });
        return textField;
    }

    public static JCheckBox addFilterListener(JCheckBox checkBox, Runnable onFilter) {
        checkBox.addActionListener(e -> onFilter.run());
        return checkBox;
    }

    public static <T> JComboBox<T> addFilterListener(JComboBox<T> combo, Runnable onFilter) {
        combo.addActionListener(e -> onFilter.run());
        return combo;
    }

    /**
     * Restricts a JTextField to digit input only.
     * Non-digit characters are consumed and ignored.
     *
     * @param textField the text field to restrict
     * @return the same JTextField
     */
    public static JTextField digitsOnly(JTextField textField) {
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (!Character.isDigit(evt.getKeyChar())) evt.consume();
            }
        });
        return textField;
    }

}
