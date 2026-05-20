package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.*;
import main.java.be.henallux.project.controller.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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
    private final NotificationController notificationController;
    private DocumentForm documentForm;
    private ClientSupplierForm clientSupplierForm;
    private ClientView clientView;
    private ProductView productView;
    private RecipeView recipeView;
    private StockOrderCreation orderView;

    private CardLayout cardLayout;
    private JPanel container;

    public MainWindow(NotificationController notificationController) {
        super("Magasin du Grand Bazard");
        this.notificationController = notificationController;

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setJMenuBar(new MenuWindow(this, notificationController));
        notificationController.setMainWindow(this);
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        addPage(new MainPanel(), "MAIN");

        addPage(new DocumentTable(this), "DOCUMENT");
        documentForm = new DocumentForm(this);
        addPage(documentForm, "DOCUMENT_FORM");

        addPage(new ClientSupplierTable(this), "CLIENT_SUPPLIER");
        clientSupplierForm = new ClientSupplierForm(this);
        addPage(clientSupplierForm, "CLIENT_SUPPLIER_FORM");

        addPage(new ClientSearchTable(this), "CLIENT");
        clientView = new ClientView(this);
        addPage(clientView, "CLIENT_VIEW");

        addPage(new ProductSearchTable(this), "PRODUCT");
        productView = new ProductView(this);
        addPage(productView, "PRODUCT_VIEW");

        addPage(new RecipeSearchTable(this), "RECIPE");
        recipeView = new RecipeView(this);
        addPage(recipeView, "RECIPE_VIEW");

        addPage(new ReceiptCreateView(this), "RECEIPT");
        addPage(new StockAlertView(this), "STOCK");
        orderView = new StockOrderCreation(this);
        addPage(orderView, "ORDER_CREATION");

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

    public void openClientView(ClientSupplier client) {
        clientView.loadClient(client);
        setPage("CLIENT_VIEW");
    }

    public void openProductView(Product product) {
        productView.loadProduct(product);
        setPage("PRODUCT_VIEW");
    }

    public void openRecipeView(Recipe recipe) {
        recipeView.loadRecipe(recipe);
        setPage("RECIPE_VIEW");
    }

    public void openOrderView(ArrayList seletedProduct, ClientSupplier selectedSupplier){
        orderView.loadOrder(seletedProduct, selectedSupplier);
        setPage("ORDER_CREATION");
    }

    public NotificationController getNotificationController() {
        return notificationController;
    }
}
