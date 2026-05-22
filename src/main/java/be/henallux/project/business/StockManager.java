package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.*;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class StockManager {
    public void addStockLocation(Location Location) throws DataBaseException {
        StockData data = new StockData();
        data.addStockLocation(Location);
    }

    public void addToStocks(int productID, int quantity, Location storeLocation) throws DataBaseException {
        StockData data = new StockData();
        data.addToStocks(productID, quantity, storeLocation);
    }

    public void subtractFromStock(int productID, int quantity, Location storeLocation) throws DataBaseException {
        StockData data = new StockData();
        data.subtractFromStock(productID, quantity, storeLocation);
    }

    public void deleteStockLocation(Location location) throws DataBaseException {
        StockData data = new StockData();
        data.deleteStockLocation(location);
    }

    public List<Product> getAllShortSuppliedProduct() throws DataBaseException {
        StockData data = new StockData();
        return data.getAllShortSuppliedProduct();
    }
}
