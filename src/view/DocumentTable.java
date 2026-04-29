package view;

import javax.swing.*;

public class DocumentTable extends JPanel {

    public DocumentTable(MainWindow window) {

        JButton back = new JButton("Back");

        back.addActionListener(e -> window.setPage("MAIN"));

        add(new JLabel("Félicitations !!"));
        add(back);
    }
}