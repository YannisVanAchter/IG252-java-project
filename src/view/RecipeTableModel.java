package view;

import model.Recipe;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class RecipeTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {
            "Name", "Document ID", "Date", "Recipe", ""
    };

    private List<Recipe> recipes;

    public RecipeTableModel(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        fireTableDataChanged();
    }

    public Recipe getRecipeAt(int row) {
        return recipes.get(row);
    }

    @Override
    public int getRowCount() {
        return recipes != null ? recipes.size() : 0;
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

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}