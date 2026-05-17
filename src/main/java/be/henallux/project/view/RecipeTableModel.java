package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Recipe;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Table model used to display a list of {@link Recipe} instances
 * in a {@link javax.swing.JTable}.
 * <p>This model provides read-only access to recipe data and defines the structure of the table,
 * including name, document information, date, and an action column ("See Recipe").
 * <p>The model is updated by replacing the underlying dataset via {@link #setRecipes(List)}.</p>
 *
 * @see Recipe
 */
public class RecipeTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "Name", "Document ID", "Date", "Recipe", ""
    };

    private List<Recipe> recipes;

    public RecipeTableModel(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    /**
     * Replaces the current recipe list and refreshes the table view.
     * @param recipes the new list of recipes
     */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        fireTableDataChanged();
    }

    public Recipe getRecipeAt(int row) {
        return recipes.get(row);
    }

    /** {@inheritDoc} */
    @Override
    public int getRowCount() {
        return recipes != null ? recipes.size() : 0;
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

    //TODO: ajouter le doc
    /** {@inheritDoc} */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Recipe r = recipes.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> r.getName();
//            case 1 -> r.getDocument().getID();
//            case 2 -> r.getDocument().getCreationDate();
//            case 3 -> r.getProduct();
            case 4 -> "See Recipe";
            default -> null;
        };
    }
}