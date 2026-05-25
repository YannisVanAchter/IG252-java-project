package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.business.StockManager;
import main.java.be.henallux.project.model.NotificationItem;
import main.java.be.henallux.project.model.Product;
import main.java.be.henallux.project.model.ClientSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

/**
 * Controls the stock alert background agent.
 * <p>Follows a producer-consumer pattern: the background thread (producer) writes
 * to the shared resource {@code data}, while the Swing EDT (consumer) reads it
 * via {@link #getSharedResource()}. Access is synchronized on {@code data} to
 * prevent concurrent modification.</p>
 */
public class StockManagementController extends Thread {

    /** Shared common zone*/
    private final List<Map.Entry<Product, ClientSupplier>> data = new ArrayList<>();

    private Thread thread;
    private volatile boolean running = false;

    private final StockManager stockManager;
    private final NotificationController notificationController;
    private final SupplierController supplierController;

    private static final long TIME = 60000;

    public StockManagementController(NotificationController notificationController) {
        this.stockManager = new StockManager();
        this.notificationController = notificationController;
        this.supplierController = new SupplierController();
    }

    /**
     * Starts the stock-check background agent.
     * <p>The agent runs every {@value TIME} ms. It acts as the <b>producer</b>:
     * it writes to the shared {@code data} list inside a {@code synchronized} block,
     * then calls {@code notifyAll()} to wake up any waiting consumer threads.</p>
     *
     * @return {@code true} if the agent was started, {@code false} if already running
     */
    public boolean startAgent() {
        if (running) return false;

        running = true;

        thread = new Thread(() -> {
            while (running) {
                try {
                    askStockCheckUp();
                    Thread.sleep(TIME);
                } catch (InterruptedException e) {
                    stopAgent();
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
        return true;
    }

    /**
     * Stops the background agent.
     */
    public void stopAgent() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    /**
     * Fetches all low-stock products and their suppliers, then updates the shared resource.
     * <p>Acts as the <b>producer</b>: writes to the shared zone inside {@code synchronized(data)},
     * then calls {@code notifyAll()} to wake up consumers waiting on {@code data}.</p>
     */
    public void askStockCheckUp() {
        try {
            List<Product> lowStockProducts = stockManager.getAllShortSuppliedProduct();

            List<Map.Entry<Product, ClientSupplier>> newData = new ArrayList<>();
            for (Product p : lowStockProducts) {
                ClientSupplier supplier = supplierController.getSupplierByProduct(p.getId());
                if (supplier != null) {
                    newData.add(new SimpleEntry<>(p, supplier));
                }
            }

            synchronized (data) {
                data.clear();
                data.addAll(newData);
                data.notifyAll();
            }

            if (!newData.isEmpty()) {
                notificationController.push(
                        new NotificationItem("Stock alert", "Low stock detected", NotificationItem.Type.WARNING)
                );
            }
        } catch (Exception e) {
            notificationController.push(
                    new NotificationItem("Stock error", e.getMessage(), NotificationItem.Type.ERROR)
            );
        }
    }
    /**
     * Returns a copy of the current shared stock alert data.
     * <p>Acts as the <b>consumer</b>: reads the shared zone
     * inside {@code synchronized(data)} to prevent concurrent modification.</p>
     *
     * @return a copy of the current alert list (never {@code null})
     */
    public List<Map.Entry<Product, ClientSupplier>> getSharedResource() {
        synchronized (data) {
            return new ArrayList<>(data);
        }
    }
}