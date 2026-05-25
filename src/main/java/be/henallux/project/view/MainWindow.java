package main.java.be.henallux.project.view;

import main.java.be.henallux.project.model.*;
import main.java.be.henallux.project.controller.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

/**
 * This class acts as the central frame of the application.
 * <p>It provides the main window structure and manages navigation between all application views
 * using a {@link CardLayout}. Each screen (home, tables, forms, detail views) is registered
 * under a unique identifier and can be displayed on demand.
 * <p>The navigation system is complemented by a history stack allowing basic back navigation.
 * <p>This frame also exposes high-level navigation methods used by views and controllers to open
 * specific forms or detail screens such as {@link DocumentForm} or {@link ClientSupplierForm}.
 * <p>It integrates the {@link JMenuBar} implementation via {@link MenuWindow} and acts as the
 * central entry point for user interactions in the Swing application.
 *
 * @see MenuWindow
 */
public class MainWindow extends JFrame {
    private final Stack<String> history = new Stack<>();
    private String currentPage;
    private final StockManagementController stockManagementController;
    private final NotificationController notificationController;
    private final DocumentForm documentForm;
    private final ClientSupplierForm clientSupplierForm;
    private final ClientSearchView clientSearchView;
    private final ProductSearchView productSearchView;
    private final RecipeSearchView recipeSearchView;
    private final StockOrderCreation orderView;

    private CardLayout cardLayout;
    private JPanel container;

    public MainWindow(NotificationController notificationController, StockManagementController stockManagementController) {
        super("Magasin du Grand Bazard");
        this.notificationController = notificationController;
        this.stockManagementController = stockManagementController;

        setSize(820, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        notificationController.addListener(notif -> {
            new ToastWindow(this, notif);
        });

        setJMenuBar(new MenuWindow(this));
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        addPage(new HomePanel(this), "MAIN");

        addPage(new DocumentTable(this), "DOCUMENT");
        documentForm = new DocumentForm(this);
        addPage(documentForm, "DOCUMENT_FORM");

        addPage(new ClientSupplierTable(this), "CLIENT_SUPPLIER");
        clientSupplierForm = new ClientSupplierForm(this);
        addPage(clientSupplierForm, "CLIENT_SUPPLIER_FORM");

        addPage(new ClientSearchTable(this), "CLIENT");
        clientSearchView = new ClientSearchView(this);
        addPage(clientSearchView, "CLIENT_VIEW");

        addPage(new ProductSearchTable(this), "PRODUCT");
        productSearchView = new ProductSearchView(this);
        addPage(productSearchView, "PRODUCT_VIEW");

        addPage(new RecipeSearchTable(this), "RECIPE");
        recipeSearchView = new RecipeSearchView(this);
        addPage(recipeSearchView, "RECIPE_VIEW");

        addPage(new ReceiptCreateView(this), "RECEIPT");
        addPage(new StockAlertView(this, stockManagementController), "STOCK");
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

    /**
     * Navigates back to the previous page in the navigation history if available.
     * <p>If the history is empty, the current page remains unchanged.
     *
     * @see #setPage(String)
     */
    public void goBack() {
        if (!history.isEmpty()) {
            String previous = history.pop();
            currentPage = previous;
            cardLayout.show(container, previous);
        }
    }

    /**
     * Opens the document form in creation or edition mode depending on the provided document.
     * <p>If {@code doc} is {@code null}, the form is initialized in creation mode.
     * Otherwise, the form is populated with the given {@link Document} for editing.
     *
     * @param doc the {@link Document} to edit, or {@code null} to create a new document
     * @see DocumentForm#loadDocument(Document)
     */
    public void openDocumentForm(Document doc) {
        documentForm.loadDocument(doc);
        setPage("DOCUMENT_FORM");
    }

    /**
     * Opens the client/supplier form in creation or edition mode.
     * <p>If the provided {@link ClientSupplier} is {@code null}, the form is reset to creation mode.
     * Otherwise, the form is populated with the selected entity for modification.
     *
     * @param cs the {@link ClientSupplier} to edit, or {@code null} for creation mode
     * @see ClientSupplierForm#loadClientSupplier(ClientSupplier)
     */
    public void openClientSupplierForm(ClientSupplier cs) {
        clientSupplierForm.loadClientSupplier(cs);
        setPage("CLIENT_SUPPLIER_FORM");
    }

    /**
     * Opens the client detail view for the selected client.
     * <p>The view is populated using the provided {@link ClientSupplier}
     *
     * @param client the {@link ClientSupplier} to edit, or {@code null} for creation mode
     * @see ClientSupplierForm#loadClientSupplier(ClientSupplier)
     */
    public void openClientView(ClientSupplier client) {
        clientSearchView.loadClient(client);
        setPage("CLIENT_VIEW");
    }

    /**
     * Opens the product detail view for the selected product.
     * <p>The view is populated using the provided {@link Product}
     *
     * @param product the {@link Product} to display in detail
     * @see ProductSearchView#loadProduct(Product)
     */
    public void openProductView(Product product) {
        productSearchView.loadProduct(product);
        setPage("PRODUCT_VIEW");
    }

    /**
     * Opens the recipe detail view for the selected recipe.
     * <p>The view is populated using the provided {@link Recipe}
     *
     * @param recipe the {@link Recipe} to display
     * @see RecipeSearchView#loadRecipe(Recipe)
     */
    public void openRecipeView(Recipe recipe) {
        recipeSearchView.loadRecipe(recipe);
        setPage("RECIPE_VIEW");
    }

    /**
     * Opens the stock order creation view with a predefined selection.
     * <p>The view is initialized using a list of selected {@link Product} items and an optional
     * supplier {@link ClientSupplier}, allowing the user to prepare a stock order.
     *
     * @param selectedProduct the list of products included in the order
     * @param selectedSupplier the supplier associated with the order
     * @see StockOrderCreation#loadOrder(ArrayList, ClientSupplier)
     */
    public void openOrderView(ArrayList<Product> selectedProduct, ClientSupplier selectedSupplier){
        orderView.loadOrder(selectedProduct, selectedSupplier);
        setPage("ORDER_CREATION");
    }

    /**
     * Returns the notification controller.
     * <p>This controller is shared across all views to display success, error, and informational messages.
     *
     * @return the shared {@link NotificationController} instance
     */
    public NotificationController getNotificationController() {
        return notificationController;
    }
}
