package view;

import javax.swing.*;

public class MenuWindow extends JMenuBar {
    private JMenu appMenu, searchMenu, receipeMenu;
    private JMenuItem closeItem, helpItem, clientItem, suplierItem, documentItem, recipeItem;

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
        searchMenu.add(clientItem);
        suplierItem = new JMenuItem("Supplier");
        searchMenu.add(suplierItem);
        documentItem = new JMenuItem("Document");
        documentItem.addActionListener(e -> {
            window.setPage("DOCUMENT");
        });
        searchMenu.add(documentItem);
        recipeItem = new JMenuItem("Recipe");
        searchMenu.add(recipeItem);


        receipeMenu = new JMenu("Receipe");
        receipeMenu.setMnemonic('R');

        add(appMenu);
        add(searchMenu);
        add(receipeMenu);
    }

}
