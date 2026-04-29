package view;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    /**
     * This class acts as the central frame of the application.
     * <p>
     * It uses a {@link CardLayout} to manage the different screens (views),
     * and allowing simple navigation between different panels such as MAIN, DOCUMENT, and CLIENT.
     * <p>
     * It also contains the {@link JMenuBar} to display navigation buttons between views.
     */

    private CardLayout cardLayout;
    private JPanel container;

    public MainWindow() {
        super("Magasin du Grand Bazard");

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setJMenuBar(new MenuWindow(this));
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        addPage(new MainPanel(), "MAIN");
        addPage(new DocumentTable(this), "DOCUMENT");
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
        cardLayout.show(container, name);
    }

}
