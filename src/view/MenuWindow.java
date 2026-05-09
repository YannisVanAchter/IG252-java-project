package view;

import javax.swing.*;

public class MenuWindow extends JMenuBar {
    private JMenu appMenu, searchMenu, receipeMenu;
    private JMenuItem closeItem, helpItem, clientItem, suplierItem, documentItem, productItem, recipeItem;

    public MenuWindow(MainWindow window){
        appMenu = new JMenu("Application");
        appMenu.setMnemonic('A');

        helpItem = new JMenuItem("Help");
        appMenu.add(helpItem);
        closeItem = new JMenuItem("Close");
        closeItem.setMnemonic('Q');
        closeItem.addActionListener(e -> System.exit(0));
        appMenu.add(closeItem);

        searchMenu = new JMenu("Search");
        searchMenu.setMnemonic('S');
        clientItem = new JMenuItem("Client");
        clientItem.addActionListener(e -> {
            window.setPage("CLIENT_SUPPLIER");
        });
        searchMenu.add(clientItem);
        suplierItem = new JMenuItem("Supplier");
        searchMenu.add(suplierItem);
        documentItem = new JMenuItem("Document");
        documentItem.addActionListener(e -> {
            window.setPage("DOCUMENT");
        });
        searchMenu.add(documentItem);
        productItem = new JMenuItem("Product");
        productItem.addActionListener(e -> {
            window.setPage("PRODUCT");
        });
        searchMenu.add(productItem);

        recipeItem = new JMenuItem("Recipe");
        recipeItem.addActionListener(e -> {
            window.setPage("RECIPE");
        });
        searchMenu.add(recipeItem);


        receipeMenu = new JMenu("Receipe");
        receipeMenu.setMnemonic('R');

        add(appMenu);
        add(searchMenu);
        add(receipeMenu);
    }

}
