package main.java.be.henallux.project.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
     *
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
     * Creates a horizontal row grouping two labeled components with fixed spacing between them.
     * Useful for aligning form fields side by side
     *
     * @param leftLabelled  the left component
     * @param rightLabelled the right component
     * @return a JPanel containing both components arranged horizontally
     */
    public static JPanel horizontalRowGroup(Component leftLabelled, Component rightLabelled) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        row.add(leftLabelled);
        row.add(Box.createHorizontalStrut(10));
        row.add(rightLabelled);

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
     * @param label   the text to display in the JLabel
     * @param spinner the JSpinner to be displayed and toggled by the JCheckBox
     * @param chk     the JCheckBox used to enable or disable the JSpinner
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
     * Spinner align on Left
     *
     * @param value initial value
     * @param min   minimum value
     * @param max   maximum value
     * @param step  increment step
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

    public static String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Returns {@code value} when non-null and non-blank, otherwise {@code fallback}.
     * Use this for every String field fetched from the database before displaying it.
     *<pre>{@code
     * label.setText(ViewUtils.safeText(client.getEmail(), "-"));
     * }</pre>
     *
     * @param value    the raw String from the model (can be null)
     * @param fallback the fallback shown when value is absent
     * @return a non-null, display-safe String
     */
    public static String safeText(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value : fallback;
    }

    /**
     * Returns {@code value} when non-null and non-blank, otherwise using {@code "-"} as fallbac.
     * Using for {@link #safeText(String, String)}.
     *
     * @param value the raw String from the model (can be null)
     * @return a non-null, display-safe String
     */
    public static String safeText(String value) {
        return safeText(value, "-");
    }

    /**
     * Attaches a DocumentListener to a JTextField that triggers onFilter on every change.
     *
     * @param textField the text field to listen to
     * @param onFilter  action to run on change
     * @return the same JTextField
     */
    public static JTextField addFilterListener(JTextField textField, Runnable onFilter) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                onFilter.run();
            }

            public void removeUpdate(DocumentEvent e) {
                onFilter.run();
            }

            public void changedUpdate(DocumentEvent e) {
                onFilter.run();
            }
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

    /**
     * Sets an appropriate mouse cursor for the given Swing component based on its type.
     * <p>This method applies a context-aware cursor to improve user experience:
     * <ul><li>Text input components (e.g., {@link JTextField}, {@link JSpinner}) receive a text cursor.</li>
     *     <li>Interactive components (e.g., {@link JButton}, {@link JCheckBox}, {@link JRadioButton}) receive a hand cursor.</li>
     *     <li>All other components receive the default cursor.</li></ul>
     *
     * @param component the Swing component to which the cursor will be applied
     */
    public static void setCursor(JComponent component) {
        if (component instanceof JTextField || component instanceof JSpinner) {
            component.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else if (component instanceof JButton || component instanceof JCheckBox || component instanceof JRadioButton) {
            component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            component.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Attaches a real-time autocomplete filter to an editable {@link JComboBox}.
     * <p>Filters the combobox items as the user types (case-insensitive). The displayed
     * text is automatically capitalized (first letter uppercase only).
     * <ul><li><b>TYPING:</b> filters the list and opens/closes the popup accordingly.</li>
     *   <li><b>ENTER:</b> confirms the best match and restores the full list.</li>
     *   <li><b>ESCAPE:</b> resets the list, clears the selection and the editor.</li></ul>
     *
     * @param <T>      the type wrapped inside each {@link ComboBoxItem}
     * @param comboBox the target editable combo box
     * @param allItems the full reference list, never modified
     */
    public static <T> void setupAutoComplete(JComboBox<ComboBoxItem<T>> comboBox, ArrayList<ComboBoxItem<T>> allItems) {
        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN ||
                        key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT ||
                        key == KeyEvent.VK_SHIFT || key == KeyEvent.VK_CONTROL) {
                    return;
                }

                if (key == KeyEvent.VK_ENTER) {
                    String currentText = editor.getText().trim();
                    ComboBoxItem<T> match = null;
                    for (int i = 0; i < comboBox.getItemCount(); i++) {
                        ComboBoxItem<T> item = comboBox.getItemAt(i);
                        if (item.toString().equalsIgnoreCase(currentText) && match == null) match = item;
                    }

                    if (match == null && comboBox.getItemCount() > 0) {
                        ComboBoxItem<T> first = comboBox.getItemAt(0);
                        if (first.toString().toLowerCase().contains(currentText.toLowerCase())) match = first;
                    }

                    comboBox.removeAllItems();
                    for (ComboBoxItem<T> item : allItems) {
                        comboBox.addItem(item);
                    }

                    if (match != null) {
                        comboBox.setSelectedItem(match);
                        editor.setText(match.toString());
                    } else {
                        comboBox.setSelectedIndex(-1);
                        editor.setText(currentText);
                    }
                    comboBox.hidePopup();
                    return;
                }

                if (key == KeyEvent.VK_ESCAPE) {
                    comboBox.removeAllItems();
                    for (ComboBoxItem<T> item : allItems) {
                        comboBox.addItem(item);
                    }
                    comboBox.setSelectedIndex(-1);
                    editor.setText("");
                    comboBox.hidePopup();
                    return;
                }

                String texte = editor.getText();
                String texteLower = texte.toLowerCase();

                comboBox.removeAllItems();
                if (texte.isEmpty()) {
                    for (ComboBoxItem<T> item : allItems) {
                        comboBox.addItem(item);
                    }
                } else {
                    for (ComboBoxItem<T> item : allItems) {
                        if (item.toString().toLowerCase().contains(texteLower)) comboBox.addItem(item);
                    }
                }

                if (!texte.isEmpty()) {
                    String texteCapitalize = texte.substring(0, 1).toUpperCase() + texte.substring(1).toLowerCase();
                    int caretPos = editor.getCaretPosition();
                    editor.setText(texteCapitalize);
                    try {
                        editor.setCaretPosition(Math.min(caretPos, texteCapitalize.length()));
                    } catch (IllegalArgumentException ex) {
                        editor.setCaretPosition(texteCapitalize.length());
                    }
                }
                editor.setText(texte);

                if (comboBox.getItemCount() > 0) comboBox.showPopup();
                else comboBox.hidePopup();
            }
        });
    }
}