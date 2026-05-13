package view;

import controller.RecipeController;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * This view provides a recipe search interface.
 * This view allows users to:
 * - Search recipes by name
 * - Filter recipes by multiple ingredients
 * - Display results in a selectable table
 * <p>
 * It interacts with {@link RecipeController} to load all recipes
 * and with {@link MainWindow} to open a detailed recipe view when a row is selected.
 */
public class RecipeSearchTable extends JPanel {
    private static final int TBL_BTN_SEE = 4;

    private MainWindow mainWindow;
    private RecipeController controller;
    private RecipeTableModel model;
    private ArrayList<Recipe> recipes;
    private ArrayList<Recipe> displayRecipes;
    private ArrayList<JTextField> searchIngredients;

    private JTextField txtRecipeName;
    private JPanel ingredientRowsPanel;
    private JScrollPane ingredientScroll;
    private JTable table;

    public RecipeSearchTable(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = new RecipeController();
        this.recipes = controller.getAllRecipe();
        this.displayRecipes = new ArrayList<>(recipes);
        this.searchIngredients = new ArrayList<>();

        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel top = new JPanel(new BorderLayout());
        top.add(buildHeader(), BorderLayout.NORTH);
        top.add(buildSearchPanel(), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    /**
     * Constructs and returns a JPanel that represents the header section
     * Use a {@code FlowLayout} to align item on a line.
     *
     * @return a JPanel with a title.
     */
    private JPanel buildHeader() {
        JLabel title = new JLabel("Recipe Search");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.add(title);
        return header;
    }

    /**
     * Constructs and returns a JPanel used for searching by name and ingredients.
     * The panel is divided into two sections:
     * - The left section allows the user to enter a recipe name
     * - The right section provides functionality to add and manage ingredient filters with a scrollable view.
     * Each item is encapsulated in a {@code JPanel} and placed using {@code BorderLayout}
     * The whole is placed in a {@code GridLayout}.
     *
     * @return a JPanel containing the search interface.
     */
    private JPanel buildSearchPanel() {

        txtRecipeName = new JTextField();
        txtRecipeName = ViewUtils.addFilterListener(txtRecipeName, this::onFilterClick);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> onFilterClick());

        JPanel nameFields = new JPanel(new BorderLayout(0, 4));
        nameFields.add(new JLabel(""), BorderLayout.NORTH);
        nameFields.add(txtRecipeName, BorderLayout.CENTER);
        nameFields.setPreferredSize(new Dimension(300, 80));

        JPanel leftColumn = new JPanel(new BorderLayout(0, 8));
        leftColumn.setBorder(BorderFactory.createTitledBorder("Recipe Name"));
        leftColumn.add(nameFields, BorderLayout.NORTH);
        leftColumn.add(btnSearch, BorderLayout.SOUTH);

        JButton btnAddIngredient = new JButton("+ Add ingredient");
        btnAddIngredient.addActionListener(e -> addIngredientRow());

        ingredientRowsPanel = new JPanel();
        ingredientRowsPanel.setLayout(new BoxLayout(ingredientRowsPanel, BoxLayout.Y_AXIS));

        ingredientScroll = new JScrollPane(
                ingredientRowsPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        ingredientScroll.setBorder(BorderFactory.createEmptyBorder());
        ingredientScroll.setPreferredSize(new Dimension(300, 70));

        JPanel rightColumn = new JPanel(new BorderLayout(0, 6));
        rightColumn.setBorder(BorderFactory.createTitledBorder("Ingredients"));
        rightColumn.add(btnAddIngredient, BorderLayout.NORTH);
        rightColumn.add(ingredientScroll, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        searchPanel.add(leftColumn);
        searchPanel.add(rightColumn);

        addIngredientRow();

        return searchPanel;
    }

    /**
     * Constructs and returns a JScrollPane containing a table displaying recipe data.
     * The table is built using the {@link RecipeTableModel} to represent the list of recipes
     * and is made scrollable by embedding it in a JScrollPane.
     * The table supports row selection and triggers the {@code onRowClick()}
     * when a row is selected
     *
     * @return a JScrollPane containing a JTable.
     */
    private JScrollPane buildTablePanel() {
        model = new RecipeTableModel(displayRecipes);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (table.getSelectedRow() == TBL_BTN_SEE) {
                    onRowClick();
                }
            }
        });

        table.getColumnModel().getColumn(TBL_BTN_SEE).setCellRenderer(new ButtonRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 200));
        return scroll;
    }

    /**
     * Adds one ingredient search row to {@code ingredientRowsPanel}.
     * Each row contains a text field and a "<html>&times;</html>" button to delete it.
     * Each addition, the structure is deleted, recalculated, and redrawn.
     * After each addition, the scroll automatically moves to the bottom
     * to ensure the newly added ingredient is visible.
     */
    private void addIngredientRow() {
        JTextField field = new JTextField();
        //field = ViewUtils.addFilterListener(field, this::onFilterClick);
        JButton btnRemove = new JButton("<html>&times;</html>");

        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBorder(new EmptyBorder(2, 4, 2, 4));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        row.add(field, BorderLayout.CENTER);
        row.add(btnRemove, BorderLayout.EAST);

        btnRemove.addActionListener(e -> {
            ingredientRowsPanel.remove(row);
            searchIngredients.remove(field);
            ingredientRowsPanel.revalidate();
            ingredientRowsPanel.repaint();
            onFilterClick();
        });

        searchIngredients.add(field);
        ingredientRowsPanel.add(row);
        ingredientRowsPanel.revalidate();
        ingredientRowsPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = ingredientScroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    /**
     * Filters the list of recipes based on the current search inputs.
     * The filter is applied on:
     * - Recipe name (case-insensitive contains match)
     * - Ingredient fields (each non-empty field must match at least one ingredient in the recipe)
     * <p>
     * The matching items are added on the {@code displayRecipes ArrayList}
     * After filtering, the table model is updated with the new list.
     */
    private void onFilterClick() {
        String nameFilter = txtRecipeName.getText().trim().toLowerCase();
        displayRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            boolean match = true;

            if (!nameFilter.isEmpty()
                    && !recipe.getName().toLowerCase().contains(nameFilter)) {
                match = false;
            }

            for (JTextField field : searchIngredients) {
                String ingredient = field.getText().trim().toLowerCase();
                if (!ingredient.isEmpty()) {
                    boolean found = false;
                    for (RecipeComposition composition : recipe.getCompositions()) {
                        if (composition.getProduct().getName().toLowerCase().contains(ingredient)) {
                            found = true;
                        }
                    }
                    if (!found) {
                        match = false;
                    }
                }
            }

            if (match) displayRecipes.add(recipe);
        }

        model.setRecipes(displayRecipes);
    }

    /**
     * Handles a table row click event.
     * Keep the selected recipe and ask the main window to load the recipe and open its detailed view.
     *
     * @see MainWindow#openRecipeView(Recipe)
     */
    private void onRowClick() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        Recipe recipe = displayRecipes.get(selectedRow);
        mainWindow.openRecipeView(displayRecipes.get(selectedRow));
    }
}