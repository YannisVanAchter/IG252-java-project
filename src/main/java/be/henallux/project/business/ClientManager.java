public class ClientManager extends ClientSupplierManager {

    protected final ClientSupplierData clientSupplierData;
    private final FidelityCardData fidelityCardData;
    private final CheckoutData checkoutData;
    private final DocumentData documentData;

    public ClientManager(ClientSupplierData clientSupplierData, FidelityCardData fidelityCardData, CheckoutData checkoutData, DocumentData documentData) {
        this.fidelityCardData = fidelityCardData;
        this.checkoutData = checkoutData;
        this.documentData = documentData;
    }

    public List<ClientSupplier> getAllClients() throws BusinessException {
        return super.getAllClientSuppliers();
    }

    public void createFidelityCard(int clientId) throws BusinessException {
        // Validation
        if (clientId <= 0) {
            throw new BusinessException("L'identifiant client est invalide.");
        }
        try {
            // Règle métier — un client ne peut avoir qu'une seule carte
            if (fidelityCardData.hasFidelityCard(clientId)) {
                throw new BusinessException("Ce client possède déjà une carte de fidélité.");
            }
            fidelityCardData.createFidelityCard(clientId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la création de la carte de fidélité.", e);
        }
    }

    public boolean validateFidelityCardOwnership(int clientId, int cardId) throws BusinessException {
        // Validation
        if (clientId <= 0 || cardId <= 0) {
            throw new BusinessException("Les identifiants fournis sont invalides.");
        }
        try {
            return fidelityCardData.validateFidelityCardOwnership(clientId, cardId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la validation de la carte de fidélité.", e);
        }
    }

    public void addCheckout(List<Product> products) throws BusinessException {
        // Validation
        if (products == null || products.isEmpty()) {
            throw new BusinessException("La liste de produits ne peut pas être vide.");
        }
        try {
            checkoutData.addCheckout(products);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'enregistrement du checkout.", e);
        }
    }

    public void addCheckout(List<Product> products, int clientId) throws BusinessException {
        // Validation
        if (products == null || products.isEmpty()) {
            throw new BusinessException("La liste de produits ne peut pas être vide.");
        }
        if (clientId <= 0) {
            throw new BusinessException("L'identifiant client est invalide.");
        }
        try {
            // Règle métier — vérifier que le client existe
            getClientSupplier(clientId);
            checkoutData.addCheckout(products, clientId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'enregistrement du checkout.", e);
        }
    }

    public void addCheckout(List<Product> products, int clientId,
                            FidelityCard fidelityCard, boolean useFidelityPoint) throws BusinessException {
        if (products == null || products.isEmpty()) {
            throw new BusinessException("La liste de produits ne peut pas être vide.");
        }
        if (clientId <= 0) {
            throw new BusinessException("L'identifiant client est invalide.");
        }
        if (fidelityCard == null) {
            throw new BusinessException("La carte de fidélité est invalide.");
        }
        try {
            // Règle métier — vérifier que la carte appartient bien au client
            if (!fidelityCardData.validateFidelityCardOwnership(clientId, fidelityCard.getId())) {
                throw new BusinessException("Cette carte de fidélité n'appartient pas à ce client.");
            }
            checkoutData.addCheckout(products, clientId, fidelityCard, useFidelityPoint);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'enregistrement du checkout.", e);
        }
    }

    public void addCheckout(List<Product> products, int clientId,
                            FidelityCard fidelityCard) throws BusinessException {
        // Délègue à la version complète sans utiliser les points
        addCheckout(products, clientId, fidelityCard, false);
    }

    public void deleteClientAccount(int clientId) throws BusinessException {
        if (clientId <= 0) {
            throw new BusinessException("L'identifiant client est invalide.");
        }
        try {
            // Orchestration — supprimer la carte de fidélité avant le compte
            if (fidelityCardData.hasFidelityCard(clientId)) {
                fidelityCardData.deleteFidelityCard(clientId);
            }
            clientSupplierData.deleteClientAccount(clientId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression du compte client.", e);
        }
    }

    public void deleteClientAccount(int clientId, int cardId) throws BusinessException {
        if (clientId <= 0 || cardId <= 0) {
            throw new BusinessException("Les identifiants fournis sont invalides.");
        }
        try {
            // Règle métier — vérifier que la carte appartient bien au client avant suppression
            if (!fidelityCardData.validateFidelityCardOwnership(clientId, cardId)) {
                throw new BusinessException("Cette carte de fidélité n'appartient pas à ce client.");
            }
            fidelityCardData.deleteFidelityCard(clientId);
            clientSupplierData.deleteClientAccount(clientId, cardId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression du compte client.", e);
        }
    }

    @Override
    public void placeOrder(int clientSupplierId, List<Product> products) throws BusinessException {
        if (clientSupplierId <= 0) {
            throw new BusinessException("L'identifiant client est invalide.");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessException("La liste de produits ne peut pas être vide.");
        }
        try {
            // Règle métier — vérifier que le client existe avant de passer commande
            getClientSupplier(clientSupplierId);
            checkoutData.addCheckout(products, clientSupplierId);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la passation de commande.", e);
        }
    }
}