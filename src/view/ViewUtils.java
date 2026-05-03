package view;

import model.DocumentType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
        //comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp.getPreferredSize().height));
        p.add(comp);
        return p;
    }

    public static JPanel labeled(JCheckBox checkBox, JComponent comp) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(checkBox);
        p.add(Box.createVerticalStrut(4));
        p.add(comp);
        return p;
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
     * Creates a JSpinner configured for integer input.
     *
     * @param value initial value
     * @param min minimum value
     * @param max maximum value
     * @param step increment step
     * @return a number JSpinner
     */
    public static JSpinner createNumberSpinner(int value, int min, int max, int step) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "#"));
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
