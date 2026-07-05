package be.henallux.project.view;

import be.henallux.project.model.Recipe;
import be.henallux.project.model.RecipeComposition;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Table model used to display a list of {@link Recipe} instances
 * in a {@link javax.swing.JTable}.
 * <p>This model provides read-only access to recipe data and defines the structure of the table,
 * including name, document information, and an action column ("See Recipe").
 * <p>The model is updated by replacing the underlying dataset via {@link #setRecipes(List)}.</p>
 *
 * @see Recipe
 */
public class RecipeSearchTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "Name", "Final Product", "Document ID", "Product", ""
    };

    public static final int TBL_BTN_SEE = 4;

    private List<Recipe> recipes;

    public RecipeSearchTableModel(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    /**
     * Replaces the current list of recipes and refreshes the table view.
     * <p>This method triggers a full refresh of the JTable via {@link  AbstractTableModel#fireTableDataChanged()}.
     *
     * @param recipes the new list of {@link Recipe} to display
     */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        fireTableDataChanged();
    }

    public Recipe getRecipeAt(int row) {
        return recipes.get(row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowCount() {
        return recipes != null ? recipes.size() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    /**
     * Returns the value displayed in a specific cell of the recipe table.
     * <p>Column mapping:
     * <ul><li>recipe name</li>
     *     <li>recipe document ID</li>
     *     <li>comma-separated list of product names in the recipe composition</li>
     *     <li>action label ("See Recipe")</li></ul>
     *
     * @param rowIndex the row index of the recipe
     * @param columnIndex the column index to evaluate
     * @return the value displayed in the table cell
     * @see ViewUtils#safeText(String, String)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Recipe r = recipes.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> ViewUtils.safeText(r.getName(), "Unknown");
            case 1 -> ViewUtils.safeText(r.getFinalProduct().getName(), "Unknown");
            case 2 -> r.getId();
            case 3 -> getCompositionLabel(r.getComposition());
            case 4 -> "See Recipe";
            default -> null;
        };
    }

    /**
     * Builds a human-readable label representing the composition of a recipe.
     * <p>The label is created by concatenating all product names contained in the
     * {@link RecipeComposition} list, separated by commas.
     * <p>If no valid product names are available, a fallback "-" is returned.
     *
     * @param compositions the list of recipe composition entries
     * @return a formatted string representing the recipe contents
     * @see StringBuilder
     */
    public String getCompositionLabel(List<RecipeComposition> compositions) {
        StringBuilder out = new StringBuilder();
        for (RecipeComposition compo : compositions) {
            if (!out.isEmpty()) {
                out.append(", ");
            }
            out.append(ViewUtils.safeText(compo.getProduct() != null ? compo.getProduct().getName() : null));
        }
        return !out.isEmpty() ? out.toString() : "-";
    }

}