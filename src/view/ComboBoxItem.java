package view;

import javax.swing.*;

/**
 * Represents a generic item to be used in JCombobox, where
 * each item has an associated object and a displayable label.
 * This class is reusable for every object
 *
 * @param <T> the type of the object associated with this item
 */
public class ComboBoxItem<T> {
    private T object;
    private String label;

    public ComboBoxItem(T object, String label) {
        this.object = object;
        this.label = label;
    }

    public T getObject() { return object; }

    @Override
    public String toString() { return label; }


    /**
     * Selects an item in the combo box that matches the given object.
     * If the object is found, it becomes the selected item.
     * Otherwise, nothing changes.
     * @param combo the combo box
     * @param object the object to select
     * @param <T> the type of the object
     */
    public static <T> void selectComboItem(JComboBox<ComboBoxItem<T>> combo, T object) {
        int i = 0;
        while (i < combo.getItemCount() && !combo.getItemAt(i).getObject().equals(object)) {
            i++;
        }
        if (i < combo.getItemCount()) {
            combo.setSelectedItem(combo.getItemAt(i));
        }
    }
}