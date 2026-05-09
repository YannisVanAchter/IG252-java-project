package view;

import model.Product;
import model.Recipe;
import model.RecipeComposition;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//TODO : add document informations
/**
 * ProductView displaying detailed information about a single Recipe.
 *
 * This view is dynamically rebuilt every time a recipe is loaded using {@link #loadRecipe(Recipe)}.
 * It organizes recipe data into logical sections: general information, composition, and preparation step.
 *
 * The view also provides a footer with navigation controls to return to the previous screen.
 */
public class RecipeView extends JPanel {

    private static final String LABEL_NO_DATA = "N/A";

    private MainWindow mainWindow;
    private Recipe recipe;
    private JTable table;


    public RecipeView(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout(0,12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    /**
     * Loads a recipe into the view and rebuilds the UI.
     * The method is call in {@link MainWindow#openProductView(Product)}
     * when cliked on a recipe in Jtable of {@link RecipeSearchTable}
     *
     * If the recipe is null, the user is notified and the view navigates back automatically.
     * The method clears the current UI and reconstructs all components
     * The main contents are created in {@link #buildContent()}.
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
        panel.add(buildProductInfo());
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

    private JPanel buildProductInfo() {
        JPanel card = createCard("Recipe Information");
        card.add(labelValue("Recipe label", recipe.getName()));
        card.add(labelValue("Document", "100"));
        card.add(labelValue("Creation date", "02/11/2010"));
        return card;
    }

    /**
     * Builds a table displaying the composition of a recipe, including ingredients and their quantities.
     * The table supports row selection and triggers to the {@link ProductView}.
     *
     * @return a JPanel containing a JTable.
     */
    private JPanel buildCompositionTable() {
        JPanel card = createCard("Composition");

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Ingrédient", "Quantité"}, 0
        );

        for (RecipeComposition composition : recipe.getCompositions()) {
            model.addRow(new Object[]{
                    composition.getProduct().getName(),
                    composition.getQuantity(),
            });
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

        int rowCount   = Math.max(3, model.getRowCount());
        int tableHeight = Math.min(rowCount * table.getRowHeight() + table.getTableHeader().getPreferredSize().height + 4, 200);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, tableHeight));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, tableHeight));

        card.add(scrollPane);
        return card;
    }


    private JPanel buildPreparationStep() {
        JPanel card = createCard("Preparation step");

        JTextArea textArea = new JTextArea(recipe.getExplanation());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        card.add(new JScrollPane(textArea));
        return card;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("Back");
        back.addActionListener(e -> mainWindow.goBack());
        panel.add(back);
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
     * Handles a table row click event.
     * Keep the selected product and ask the main window to load the Product and open its detailed view.
     * @see MainWindow#openProductView(Product)
     */
    private void onRowClick() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        if (recipe == null || recipe.getCompositions() == null) return;
        if (selectedRow >= recipe.getCompositions().size()) return;

        RecipeComposition composition = recipe.getCompositions().get(selectedRow);
        Product product = composition.getProduct();

        if (product != null) {
            mainWindow.openProductView(product);
        }
    }

}