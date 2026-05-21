package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.RecipeSearchController;
import main.java.be.henallux.project.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * A Swing-based view that provides a recipe search interface.
 * <p>This view allows users to search for recipes by name and filter them by multiple ingredients.</p>
 * <p>Results are displayed in a selectable table built using {@link RecipeSearchTableModel}.</p>
 * <p>The view interacts with {@link RecipeSearchController} to retrieve data and with
 * {@link MainWindow} to open a detailed recipe view when a row is selected.</p>
 *
 * @see RecipeSearchController
 * @see RecipeSearchTableModel
 * @see MainWindow
 * @see Recipe
 */
public class RecipeSearchTable extends JPanel {
    private static final int TBL_BTN_SEE = RecipeSearchTableModel.TBL_BTN_SEE;

    private final MainWindow mainWindow;
    private final RecipeSearchController controller;
    private RecipeSearchTableModel model;

    private ArrayList<Recipe> displayRecipes;
    private ArrayList<JTextField> searchIngredients;

    private JTextField txtRecipeName;
    private JPanel ingredientRowsPanel;
    private JScrollPane ingredientScroll;
    private JTable table;

    public RecipeSearchTable(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = new RecipeSearchController();
        this.searchIngredients = new ArrayList<>();
        this.displayRecipes = controller.searchRecipes(null, null);

        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel top = new JPanel(new BorderLayout());
        top.add(buildHeader(), BorderLayout.NORTH);
        top.add(buildSearchPanel(), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    /**
     * Builds the header section of the view.
     * <p>This section contains the title of the recipe search screen.</p>
     * @return a {@code JPanel} containing the header
     */
    private JPanel buildHeader() {
        JLabel title = new JLabel("Recipe Search");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.add(title);
        return header;
    }

    /**
     * Builds the search panel used to filter recipes.
     * <p>The panel is divided into two main sections:</p>
     * <ul><li>Recipe name search field</li>
     *     <li>Dynamic ingredient filter list with add/remove functionality</li></ul>
     * <p>Each ingredient filter is represented by a text field inside a scrollable container.</p>
     * @return a {@code JPanel} containing the full search interface
     */
    private JPanel buildSearchPanel() {

        txtRecipeName = new JTextField();
        ViewUtils.setCursor(txtRecipeName);
        txtRecipeName = ViewUtils.addFilterListener(txtRecipeName, this::onSearchClick);

        JButton btnSearch = new JButton("Search");
        ViewUtils.setCursor(btnSearch);
        btnSearch.addActionListener(e -> onSearchClick());

        JPanel nameFields = new JPanel(new BorderLayout(0, 4));
        nameFields.add(new JLabel(""), BorderLayout.NORTH);
        nameFields.add(txtRecipeName, BorderLayout.CENTER);
        nameFields.setPreferredSize(new Dimension(300, 80));

        JPanel leftColumn = new JPanel(new BorderLayout(0, 8));
        leftColumn.setBorder(BorderFactory.createTitledBorder("Recipe Name"));
        leftColumn.add(nameFields, BorderLayout.NORTH);
        leftColumn.add(btnSearch, BorderLayout.SOUTH);

        JButton btnAddIngredient = new JButton("+ Add ingredient");
        ViewUtils.setCursor(btnAddIngredient);
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
     * Builds a scrollable table displaying the list of recipes.
     * <p>The table is based on {@link RecipeSearchTableModel} and supports single row selection.</p>
     * <p>Clicking on the action column triggers navigation to the detailed view.</p>
     * @return a {@code JScrollPane} containing the {@code JPanel}
     */
    private JScrollPane buildTablePanel() {
        model = new RecipeSearchTableModel(displayRecipes);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (table.columnAtPoint(e.getPoint()) == TBL_BTN_SEE) {
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
     * Adds a new ingredient filter row to the ingredient panel.
     * <p>Each row contains a text field and a remove button allowing dynamic filtering.</p>
     * <p>When a row is removed, the filter is updated automatically.</p>
     */
    private void addIngredientRow() {
        JTextField search = new JTextField();
        ViewUtils.setCursor(search);

        JButton btnRemove = new JButton("<html>&times;</html>");
        ViewUtils.setCursor(btnRemove);

        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBorder(new EmptyBorder(2, 4, 2, 4));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        row.add(search, BorderLayout.CENTER);
        row.add(btnRemove, BorderLayout.EAST);

        JTextField finalSearch = ViewUtils.addFilterListener(search, this::onSearchClick);
        row.remove(search);
        row.add(finalSearch, BorderLayout.CENTER);

        btnRemove.addActionListener(e -> {
            ingredientRowsPanel.remove(row);
            searchIngredients.remove(finalSearch);
            ingredientRowsPanel.revalidate();
            ingredientRowsPanel.repaint();
            onSearchClick();
        });

        searchIngredients.add(finalSearch);
        ingredientRowsPanel.add(row);
        ingredientRowsPanel.revalidate();
        ingredientRowsPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = ingredientScroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }
    /**
     * Filters recipes based on user input.
     * <p>Filtering is applied on:</p>
     * <ul><li>Recipe name (case-insensitive partial match)</li>
     *     <li>Ingredient list (all provided ingredients must match)</li></ul>
     * <p>The resulting list updates {@code displayRecipes} and refreshes the table model.</p>
     */
    public void onSearchClick() {
        String name = txtRecipeName.getText().trim();

        ArrayList<String> ingredients = new ArrayList<>();
        for (JTextField field : searchIngredients) {
            String val = field.getText().trim();
            if (!val.isBlank()) ingredients.add(val);
        }

        ArrayList<Recipe> results = controller.searchRecipes(
                name.isBlank() ? null : name,
                ingredients.isEmpty() ? null : ingredients.get(0)
        );

        for (int i = 1; i < ingredients.size(); i++) {
            String ingredient = ingredients.get(i);
            ArrayList<Recipe> filtered = controller.searchRecipes(null, ingredient);
            results.retainAll(filtered);
        }

        displayRecipes = results;
        model.setRecipes(new ArrayList<>(results));
    }

    /**
     * Handles a table row click event.
     * <p>Retrieves the selected {@link Recipe} from the table model
     * and opens its detailed view using {@link MainWindow#openRecipeView(Recipe)}.</p>
     * <p>If no valid selection is made, the method exits safely.</p>
     */
    private void onRowClick() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        mainWindow.openRecipeView(model.getRecipeAt(selectedRow));
    }
}