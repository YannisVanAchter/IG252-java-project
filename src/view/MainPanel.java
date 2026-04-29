package view;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    public MainPanel() {

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Le Grand Bazard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        add(title, BorderLayout.CENTER);
    }
}