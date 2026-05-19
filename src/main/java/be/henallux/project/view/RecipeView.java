package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.Recipe;
import main.java.be.henallux.project.model.RecipeComposition;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A Swing-based view that displays detailed information about a single {@link Recipe}.
 * <p>This view is dynamically rebuilt every time a recipe is loaded using {@link #loadRecipe(Recipe)}.
 * <p>It organizes recipe data into logical sections:
 * general information, composition, and preparation steps.
 * <p>The view also provides a footer with navigation controls to return to the previous screen
 * using {@link MainWindow#goBack()}.
 *
 * @see Recipe
 * @see RecipeSearchTable
 * @see MainWindow
 */
public class RecipeView extends JPanel {

    private static final String LABEL_NO_DATA = "N/A";

    private final MainWindow mainWindow;
    private Recipe recipe;
    private JTable table;


    public RecipeView(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    /**
     * Loads a recipe into the view and rebuilds the entire UI.
     * <p>This method is triggered when a recipe is selected in the JTable of {@link RecipeSearchTable}.
     * <p>If the recipe is {@code null}, the user is notified and the view automatically navigates back
     * using {@link MainWindow#goBack()}.
     * <p>The main content is generated in {@link #buildContent()}.
     *
     * @param recipe the recipe to display
     */
    public void loadRecipe(Recipe recipe) {
        this.recipe = recipe;

        if (this.recipe == null) {
            JOptionPane.showMessageDialog(this, "Please select a recipe.");
            mainWindow.goBack();
            return;
        }

        removeAll();
        JScrollPane pane = new JScrollPane(buildContent());
        pane.setBorder(BorderFactory.createEmptyBorder());
        add(pane, BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private JPanel buildContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(0, 0, 8, 0));
        panel.add(buildTitle());
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildRecipeInfo());
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildCompositionTable());
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildPreparationStep());
        return panel;
    }

    private JPanel buildTitle() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(recipe.getName());
        label.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(label);
        return panel;
    }

    private JPanel buildRecipeInfo() {
        JPanel card = createCard("Recipe Information");
        card.add(labelValue("Recipe label", recipe.getName()));
        card.add(labelValue("ID", String.valueOf(recipe.getId())));
        card.add(labelValue("Final product", recipe.getFinalProduct().getName()));
        return card;
    }

    /**
     * Builds a table displaying the composition of the recipe, including ingredients and quantities.
     * <p>Each row represents a {@link RecipeComposition} linking a product to its required quantity.
     * <p>The table supports row selection and allows navigation to the corresponding {@link ProductView}.
     *
     * @return a {@code JPanel} containing the composition {@code JTable}
     */
    private JPanel buildCompositionTable() {
        JPanel card = createCard("Composition");

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Ingrédient", "Quantité"}, 0
        );

        for (RecipeComposition compo : recipe.getComposition()) {
            model.addRow(new Object[]{compo.getProduct().getName(), compo.getQuantity()});
        }

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (table.getSelectedRow() != -1) {
                    onRowClick();
                }
            }
        });

        int rowCount = Math.max(3, model.getRowCount());
        int tableHeight = Math.min(rowCount * table.getRowHeight() + table.getTableHeader().getPreferredSize().height + 4, 200);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, tableHeight));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, tableHeight));

        card.add(scrollPane);
        return card;
    }

    private JPanel buildPreparationStep() {
        JPanel card = createCard("Preparation step");

        JTextArea textArea = new JTextArea(recipe.getInstruction());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        card.add(new JScrollPane(textArea));
        return card;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBack = new JButton("Back");
        ViewUtils.setCursor(btnBack);
        btnBack.addActionListener(e -> mainWindow.goBack());
        panel.add(btnBack);
        return panel;
    }

    /**
     * Creates a bordered card container with a title.
     *
     * @param title the title of the section
     * @return a styled panel
     */
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createTitledBorder(title));
        return card;
    }

    /**
     * Creates a key-value label row.
     *
     * @param label the field name
     * @param value the field value
     * @return a horizontal panel displaying the label and value
     */
    private JPanel labelValue(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label + " : "));
        panel.add(new JLabel(value));
        return panel;
    }

    /**
     * Handles a table row click event on the recipe composition table.
     * <p>Retrieves the selected {@link Product} from the selected {@link RecipeComposition}
     * and opens its detailed view using {@link MainWindow#openProductView(Product)}.
     * <p>If no valid selection is made, the method safely exits without action.
     */
    private void onRowClick() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        if (recipe == null || recipe.getComposition() == null) return;
        if (selectedRow >= recipe.getComposition().size()) return;

        RecipeComposition composition = recipe.getComposition().get(selectedRow);
        Product product = composition.getProduct();

        if (product != null) {
            mainWindow.openProductView(product);
        }
    }
}