package view;

import javax.swing.*;

public class DocumentTable extends JPanel {

    public DocumentTable(MainWindow window) {

        JButton back = new JButton("Retour");

        back.addActionListener(e -> window.setPage(new JPanel()));

        add(new JLabel("Félicitation !!"));
        add(back);
    }
}
