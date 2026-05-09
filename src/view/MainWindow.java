package view;

import exception.*;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.Stack;

/**
 * This class acts as the central frame of the application.
 * <p>
 * It uses a {@link CardLayout} to manage the different screens (views),
 * and allowing simple navigation between different panels such as MAIN, DOCUMENT, and CLIENT.
 * <p>
 * It also contains the {@link JMenuBar} to display navigation buttons between views.
 */
public class MainWindow extends JFrame {
    private Stack<String> history = new Stack<>();
    private String currentPage;

    private DocumentForm documentForm;
    private ClientSupplierForm clientSupplierForm;
    private ProductView productView;
    private RecipeView recipeView;


    private CardLayout cardLayout;
    private JPanel container;

    public MainWindow() throws DataValidationException {
        super("Magasin du Grand Bazard");

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setJMenuBar(new MenuWindow(this));
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        addPage(new MainPanel(), "MAIN");

        addPage(new DocumentTable(this), "DOCUMENT");
        documentForm = new DocumentForm(this);
        addPage(documentForm, "DOCUMENT_FORM");

        addPage(new ClientSupplierTable(this), "CLIENT_SUPPLIER");
        clientSupplierForm = new ClientSupplierForm(this);
        addPage(clientSupplierForm, "CLIENT_SUPPLIER_FORM");

        addPage(new ProductSearchTable(this), "PRODUCT");
        productView = new ProductView(this);
        addPage(productView, "PRODUCT_VIEW");

        addPage(new RecipeSearchTable(this), "RECIPE");
        recipeView = new RecipeView(this);
        addPage(recipeView, "RECIPE_VIEW");

        add(container);
        setVisible(true);
    }

    /**
     * Adds a new page (panel) to the application.
     *
     * @param panel the panel representing a screen
     * @param name  unique identifier used to switch to this page
     */
    public void addPage(JPanel panel, String name) {
        container.add(panel, name);
    }

    /**
     * Switches the currently displayed page.
     *
     * @param name unique identifier of the page to display define in
     * @see #addPage(JPanel, String)
     */
    public void setPage(String name) {
        if (currentPage != null) {
            history.push(currentPage);
        }
        currentPage = name;
        cardLayout.show(container, name);
    }

    public void goBack() {
        if (!history.isEmpty()) {
            String previous = history.pop();
            currentPage = previous;
            cardLayout.show(container, previous);
        }
    }

    public void openDocumentForm(Document doc) {
        documentForm.loadDocument(doc);
        setPage("DOCUMENT_FORM");
    }

    public void openClientSupplierForm(ClientSupplier cs) {
        clientSupplierForm.loadClientSupplier(cs);
        setPage("CLIENT_SUPPLIER_FORM");
    }

    public void openProductView(Product product) {
        productView.loadProduct(product);
        setPage("PRODUCT_VIEW");
    }

    public void openRecipeView(Recipe recipe) {
        recipeView.loadRecipe(recipe);
        setPage("RECIPE_VIEW");
    }
}
