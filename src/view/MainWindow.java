package view;

import javax.swing.*;

public class MainWindow extends JFrame {
    private JPanel currentPanel;

    public MainWindow() {
        super("Magasin du Grand Bazard");

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setJMenuBar(new MenuWindow());
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        add(container);

        setVisible(true);
    }

    public void setPage(JPanel panel) {
        getContentPane().removeAll();
        currentPanel = panel;
        add(currentPanel);
        revalidate();
        repaint();
    }
}
