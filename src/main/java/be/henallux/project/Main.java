package main.java.be.henallux.project;

import main.java.be.henallux.project.controller.NotificationController;
import main.java.be.henallux.project.controller.StockManagementController;
import main.java.be.henallux.project.view.MainWindow;

import javax.swing.JOptionPane;

public class Main {

    /**
     * Main function which launch the program. 
     * 
     * TODO: clear the method. 
     * 
     * @effect create a thread for stocks management. 
     */
    public static void main(String[] args) {
        System.out.println("Test is running");
        try {
            NotificationController notification = new NotificationController();
            StockManagementController stockControl = new StockManagementController(notification);
            new MainWindow(notification, stockControl);
            stockControl.startAgent();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while launching the application: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}