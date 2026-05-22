public class StockManager {

    private final StockData stockData;

    public StockManager(StockData stockData) {
        this.stockData = stockData;
    }

    public void addStockLocation(Location location) throws BusinessException {
        if (location == null) {
            throw new BusinessException("L'emplacement ne peut pas être nul.");
        }
        if (location.getName() == null || location.getName().isBlank()) {
            throw new BusinessException("Le nom de l'emplacement ne peut pas être vide.");
        }
        try {
            stockData.addStockLocation(location);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout de l'emplacement.", e);
        }
    }

    public void addToStocks(int productID, int quantity, Location storeLocation) throws BusinessException {
        if (productID <= 0) {
            throw new BusinessException("L'identifiant du produit est invalide.");
        }
        if (quantity <= 0) {
            throw new BusinessException("La quantité doit être positive.");
        }
        if (storeLocation == null) {
            throw new BusinessException("L'emplacement de stockage est invalide.");
        }
        try {
            stockData.addToStocks(productID, quantity, storeLocation);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout au stock.", e);
        }
    }

    public void subtractFromStock(int productID, int quantity, Location storeLocation) throws BusinessException {
        if (productID <= 0) {
            throw new BusinessException("L'identifiant du produit est invalide.");
        }
        if (quantity <= 0) {
            throw new BusinessException("La quantité doit être positive.");
        }
        if (storeLocation == null) {
            throw new BusinessException("L'emplacement de stockage est invalide.");
        }
        try {
            // Règle métier — vérifier AVANT de soustraire
            int currentStock = stockData.getStockLevel(productID, storeLocation);
            if (quantity > currentStock) {
                throw new BusinessException("Stock insuffisant pour effectuer cette opération.");
            }
            stockData.subtractFromStock(productID, quantity, storeLocation);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la soustraction du stock.", e);
        }
    }

    public void deleteStockLocation(Location location) throws BusinessException {
        if (location == null) {
            throw new BusinessException("L'emplacement ne peut pas être nul.");
        }
        try {
            // Règle métier — vérifier que l'emplacement est vide avant suppression
            if (stockData.hasProducts(location)) {
                throw new BusinessException("Impossible de supprimer un emplacement contenant des produits.");
            }
            stockData.deleteStockLocation(location);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression de l'emplacement.", e);
        }
    }

    public List<Product> getAllShortSuppliedProduct() throws BusinessException {
        try {
            return stockData.getAllShortSuppliedProduct();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des produits en rupture.", e);
        }
    }
}