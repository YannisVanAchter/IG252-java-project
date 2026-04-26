import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javax.swing.JOptionPane;

public class Main {

    // TODO: Remove Swing from file, it was there for tests purposes while contenerization. 
    /**
     * Main function which launch the program. 
     * 
     * TODO: clear the method. 
     * 
     * @effect create a thread for stocks management. 
     */
    public static void main(String[] args) {
        // Test interface
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Magasin du Grand Bazard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        // Test input and interaction
        JOptionPane.showConfirmDialog(null, "Test input");
    }
}