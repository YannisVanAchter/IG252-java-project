package be.henallux.project.controller;

import be.henallux.project.business.StockManager;
import be.henallux.project.model.NotificationItem;
import be.henallux.project.model.Product;
import be.henallux.project.model.ClientSupplier;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

/**
 * Controls the stock alert background agent.
 * <p>Follows a producer-consumer pattern: the background thread (producer) writes
 * to the shared resource {@code data}, while the Swing EDT (consumer) reads it
 * via {@link #getSharedResource()}. Access is synchronized on {@code data} and
 * {@code orderedData} to prevent concurrent modification.</p>
 */
public class StockManagementController extends Thread {

    /**
     * Shared common zone containing low-stock products and their associated supplier.
     */
    private final List<Map.Entry<Product, ClientSupplier>> data = new ArrayList<>();

    /**
     * Shared common zone tracking products already ordered and their ordered quantity.
     * <p>Used to avoid duplicate stock alert notifications when an order is already in progress.</p>
     */
    private final Map<Product, Integer> orderedData = new HashMap<>();

    private Runnable onDataUpdated;

    private Thread thread;
    private volatile boolean running = false;

    private final StockManager stockManager;
    private final NotificationController notificationController;

    private static final long TIME = 30000;

    public StockManagementController(NotificationController notificationController) {
        this.stockManager = new StockManager();
        this.notificationController = notificationController;
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

    public void askStockCheckUp(){
        askStockCheckUp(false);
    }
    /**
     * Fetches all low-stock products and their suppliers, then updates the shared resource.
     * <p>Acts as the <b>producer</b>: writes to the shared zone inside {@code synchronized(data)},
     * then calls {@code notifyAll()} to wake up consumers waiting on {@code data}.</p>
     * <p>For each low-stock product, the quantity already ordered (from {@code orderedData}) is
     * subtracted from the missing quantity. Only products that are still missing stock after
     * accounting for pending orders are included in the alert.</p>
     * <p>A notification is pushed only if the alert list has changed since the last check.</p>
     */
    public void askStockCheckUp(boolean silent) {
        try {
            Map<ClientSupplier, List<Product>> lowStockMap = stockManager.getAllShortSuppliedProduct();

            List<Map.Entry<Product, ClientSupplier>> newData = new ArrayList<>();
            for (Map.Entry<ClientSupplier, List<Product>> entry : lowStockMap.entrySet()) {
                ClientSupplier supplier = entry.getKey();
                for (Product product : entry.getValue()) {
                    synchronized (orderedData) {
                        int alreadyOrdered = orderedData.getOrDefault(product, 0);
                        int stillMissing = product.getMinStockQuantity() - product.getTotalQuantity() - alreadyOrdered;
                        if (stillMissing > 0) {
                            newData.add(new SimpleEntry<>(product, supplier));
                        }
                    }
                }
            }

            synchronized (data) {
                boolean hasChanged = !newData.equals(data);
                data.clear();
                data.addAll(newData);
                data.notifyAll();

                if (hasChanged) {
                    if (!newData.isEmpty() && !silent) {
                        notificationController.push(
                                new NotificationItem("Stock alert", "Low stock detected", NotificationItem.Type.WARNING)
                        );
                    }
                    if (onDataUpdated != null) {
                        SwingUtilities.invokeLater(onDataUpdated);
                    }
                }
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

    /**
     * Marks a product as ordered with the given quantity.
     * <p>If the product was already marked as ordered, the quantity is added to the existing value.
     * <p>This prevents duplicate stock alert notifications for products whose order is in progress.
     *
     * @param product  the {@link Product} that has been ordered
     * @param quantity the quantity ordered
     * @see #askStockCheckUp()
     */
    public void markAsOrdered(Product product, int quantity) {
        synchronized (orderedData) {
            if (orderedData.containsKey(product)) {
                orderedData.put(product, orderedData.get(product) + quantity);
            } else {
                orderedData.put(product, quantity);
            }
        }
    }

    /**
     * Marks an ordered product as receive.
     *
     * @param product  the {@link Product} that has been received
     * @param quantity the quantity received
     * @see #markAsOrdered(Product, int)
     * @see #askStockCheckUp()
     */
    public void markAsReceived(Product product, int quantity) {
        synchronized (orderedData) {
            int current = orderedData.getOrDefault(product, 0);
            int remaining = current - quantity;
            if (remaining <= 0) {
                orderedData.remove(product);
            } else {
                orderedData.put(product, remaining);
            }
        }
    }

    public void setOnDataUpdated(Runnable callback) {
        this.onDataUpdated = callback;
    }
}