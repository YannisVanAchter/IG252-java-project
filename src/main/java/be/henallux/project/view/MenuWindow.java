package main.java.be.henallux.project.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Main application menu bar.
 * <p>This class builds the {@link JMenuBar} of the application, containing the different sections:
 * Application, Management, Search, and Business.
 * It also defines keyboard shortcuts (accelerators) that are compatible across
 * Windows, Linux, and macOS using {@link Toolkit#getMenuShortcutKeyMaskEx()}.*
 * <p>Each menu item triggers a page change in the main window via {@link MainWindow#setPage(String)}.
 * @see MainWindow
 */
public class MenuWindow extends JMenuBar {
    private JMenu appMenu, managementMenu, searchMenu, businessMenu;
    private JMenuItem homeItem, closeItem, helpItem,
            clientSupItem, documentItem,
            clientItem, productItem, recipeItem,
            receiptItem;

    public MenuWindow(MainWindow window) {
        int shortcut = Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMaskEx();

        // Application
        appMenu = new JMenu("Application");
        appMenu.setMnemonic(KeyEvent.VK_A);

        homeItem = new JMenuItem("Home");
        homeItem.setMnemonic(KeyEvent.VK_A);
        homeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, shortcut));
        homeItem.addActionListener(e ->
                window.setPage("MAIN")
        );
        appMenu.add(homeItem);

        helpItem = new JMenuItem("Help");
        helpItem.setMnemonic(KeyEvent.VK_E);
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        appMenu.add(helpItem);

        closeItem = new JMenuItem("Close");
        closeItem.setMnemonic(KeyEvent.VK_Q);
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcut));
        closeItem.addActionListener(e -> System.exit(0));
        appMenu.add(closeItem);

        // Management
        managementMenu = new JMenu("Management");
        managementMenu.setMnemonic(KeyEvent.VK_M);

        clientSupItem = new JMenuItem("Client & Supplier");
        clientSupItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcut));
        clientSupItem.addActionListener(e ->
                window.setPage("CLIENT_SUPPLIER")
        );
        managementMenu.add(clientSupItem);

        documentItem = new JMenuItem("Document");
        documentItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, shortcut));
        documentItem.addActionListener(e ->
                window.setPage("DOCUMENT")
        );
        managementMenu.add(documentItem);

        // Search
        searchMenu = new JMenu("Search");
        searchMenu.setMnemonic(KeyEvent.VK_S);

        clientItem = new JMenuItem("Client");
        clientItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, shortcut));
        clientItem.addActionListener(e ->
                window.setPage("CLIENT")
        );
        searchMenu.add(clientItem);

        productItem = new JMenuItem("Product");
        productItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, shortcut));
        productItem.addActionListener(e ->
                window.setPage("PRODUCT")
        );
        searchMenu.add(productItem);

        recipeItem = new JMenuItem("Recipe");
        recipeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, shortcut));
        recipeItem.addActionListener(e ->
                window.setPage("RECIPE")
        );
        searchMenu.add(recipeItem);

        // Business
        businessMenu = new JMenu("Business task");
        businessMenu.setMnemonic(KeyEvent.VK_B);

        receiptItem = new JMenuItem("Receipt");
        receiptItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, shortcut));
        receiptItem.addActionListener(e ->
                window.setPage("RECEIPT")
        );
        businessMenu.add(receiptItem);

        add(appMenu);
        add(managementMenu);
        add(searchMenu);
        add(businessMenu);
    }
}