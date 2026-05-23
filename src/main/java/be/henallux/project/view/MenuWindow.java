package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.NotificationController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Main application menu bar.
 * Main application menu bar.
 * <p>This class builds the {@link JMenuBar} of the application and organizes navigation
 * across the different functional areas: application control, management, search,
 * business operations, and help.
 * <p>Each menu item is linked to a view switch handled by {@link MainWindow#setPage(String)},
 * allowing centralized navigation through the {@link CardLayout} system.
 * <p>Keyboard accelerators are defined using {@link Toolkit#getMenuShortcutKeyMaskEx()}
 * to ensure compatibility across Windows, Linux, and macOS.
 * <p>The menu bar also integrates a notification access component via {@link NotifBellButton},
 * allowing the user to access system notifications from any screen.
 *
 * @see MainWindow
 * @see NotifBellButton
 */
public class MenuWindow extends JMenuBar {
    private JMenu fileMenu, managementMenu, searchMenu, businessMenu, helpMenu;
    private JMenuItem homeItem, closeItem, helpItem,
            clientSupItem, documentItem,
            clientItem, productItem, recipeItem,
            receiptItem, stockItem;
    private NotifBellButton btnBell;

    public MenuWindow(MainWindow window) {
        int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        // Application
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        homeItem = new JMenuItem("Home");
        homeItem.setMnemonic(KeyEvent.VK_H);
        homeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, shortcut));
        homeItem.addActionListener(e -> window.setPage("MAIN"));
        fileMenu.add(homeItem);

        fileMenu.addSeparator();

        closeItem = new JMenuItem("Quit");
        closeItem.setMnemonic(KeyEvent.VK_Q);
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcut));
        closeItem.addActionListener(e -> System.exit(0));
        fileMenu.add(closeItem);

        // Management
        managementMenu = new JMenu("Management");
        managementMenu.setMnemonic(KeyEvent.VK_M);

        clientSupItem = new JMenuItem("Client & Supplier");
        clientSupItem.setMnemonic(KeyEvent.VK_C);
        clientSupItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, shortcut));
        clientSupItem.addActionListener(e -> window.setPage("CLIENT_SUPPLIER"));
        managementMenu.add(clientSupItem);

        documentItem = new JMenuItem("Document");
        documentItem.setMnemonic(KeyEvent.VK_D);
        documentItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, shortcut));
        documentItem.addActionListener(e -> window.setPage("DOCUMENT"));
        managementMenu.add(documentItem);

        // Search
        searchMenu = new JMenu("Search");
        searchMenu.setMnemonic(KeyEvent.VK_S);

        clientItem = new JMenuItem("Client");
        clientItem.setMnemonic(KeyEvent.VK_L);
        clientItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, shortcut));
        clientItem.addActionListener(e -> window.setPage("CLIENT"));
        searchMenu.add(clientItem);

        productItem = new JMenuItem("Product");
        productItem.setMnemonic(KeyEvent.VK_P);
        productItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, shortcut));
        productItem.addActionListener(e -> window.setPage("PRODUCT"));
        searchMenu.add(productItem);

        recipeItem = new JMenuItem("Recipe");
        recipeItem.setMnemonic(KeyEvent.VK_R);
        recipeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, shortcut));
        recipeItem.addActionListener(e -> window.setPage("RECIPE"));
        searchMenu.add(recipeItem);

        // Business
        businessMenu = new JMenu("Business tasks");
        businessMenu.setMnemonic(KeyEvent.VK_B);

        receiptItem = new JMenuItem("Sale & receipt");
        receiptItem.setMnemonic(KeyEvent.VK_T);
        receiptItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, shortcut));
        receiptItem.addActionListener(e -> window.setPage("RECEIPT"));
        businessMenu.add(receiptItem);

        stockItem = new JMenuItem("Restock alert");
        stockItem.setMnemonic(KeyEvent.VK_K);
        stockItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, shortcut));
        stockItem.addActionListener(e -> window.setPage("STOCK"));
        businessMenu.add(stockItem);

        //Help
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_E);

        helpItem = new JMenuItem("Help");
        helpItem.setMnemonic(KeyEvent.VK_F1);
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpItem.addActionListener(e -> {
            HelpPanel helpPanel = new HelpPanel();
            JDialog dialog = new JDialog(window, "Help", false);
            dialog.setSize(600, 500);
            dialog.add(helpPanel);
            dialog.setVisible(true);
            SwingUtilities.invokeLater(() -> helpPanel.scrollToTop());
        });
        helpMenu.add(helpItem);

        btnBell = new NotifBellButton(window.getNotificationController());

        add(fileMenu);
        add(managementMenu);
        add(searchMenu);
        add(businessMenu);
        add(Box.createHorizontalGlue());
        add(helpMenu);
        add(btnBell);
    }
}