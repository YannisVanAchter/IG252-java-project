package main.java.be.henallux.project.business;

import main.java.be.henallux.project.data.ClientSupplierDA;
import main.java.be.henallux.project.data.DocumentDA;
import main.java.be.henallux.project.data.DocumentTypeDA;
import main.java.be.henallux.project.data.FidelityCardDA;
import main.java.be.henallux.project.data.ProductDA;
import main.java.be.henallux.project.data.QuantityProductDA;
import main.java.be.henallux.project.data.StatusDA;
import main.java.be.henallux.project.data.WorkFlowDA;
import main.java.be.henallux.project.data.WorkFlowTypeDA;
import main.java.be.henallux.project.data.DetailDA;
import main.java.be.henallux.project.data.exception.DataBaseException;

import main.java.be.henallux.project.business.exception.BusinessException;

import main.java.be.henallux.project.model.*;
import main.java.be.henallux.project.model.exception.DataValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientSupplierManager {

    private final ClientSupplierDA clientSupplierDA;
    private final FidelityCardDA fidelityCardDA;
    private final ProductDA productDA;
    private final QuantityProductDA quantityProductDA;
    private final DocumentDA documentDA;
    private final DocumentTypeDA documentTypeDA;
    private final WorkFlowDA workFlowDA;
    private final WorkFlowTypeDA workFlowTypeDA;
    private final StatusDA statusDA;
    private final DetailDA detailDA;

    public ClientSupplierManager() {
        this.clientSupplierDA = ClientSupplierDA.getInstance();
        this.fidelityCardDA = FidelityCardDA.getInstance();
        this.productDA = ProductDA.getInstance();
        this.quantityProductDA = QuantityProductDA.getInstance();
        this.documentDA = DocumentDA.getInstance();
        this.documentTypeDA = DocumentTypeDA.getInstance();
        this.workFlowDA = WorkFlowDA.getInstance();
        this.workFlowTypeDA = WorkFlowTypeDA.getInstance();
        this.statusDA = StatusDA.getInstance();
        this.detailDA = DetailDA.getInstance();
    }

//===================================
//              READ
//===================================

    public List<ClientSupplier> getAllClientSuppliers() throws BusinessException, DataValidationException {
        try {
            return clientSupplierDA.getAll();
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the client suppliers.", e);
        }
    }

    public List<ClientSupplier> getAllSuppliers() throws BusinessException, DataValidationException {
        try {
            return clientSupplierDA.getSuppliers();
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the suppliers.", e);
        }
    }

    public List<ClientSupplier> getAllClients() throws BusinessException, DataValidationException {
        try {
            return clientSupplierDA.getClients();
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the clients.", e);
        }
    }

    public ClientSupplier getClientByCardID(int id) throws BusinessException, DataValidationException {
        if (id <= 0) {
            throw new BusinessException("The card ID is invalid.");
        }
        try {
            return clientSupplierDA.getById(id);
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the client by card ID.", e);
        }
    }

    public ClientSupplier getClientSupplier(int id) throws BusinessException, DataValidationException {
        if (id <= 0) {
            throw new BusinessException("The ID is invalid.");
        }
        try {
            return clientSupplierDA.getById(id);
        } catch (DataBaseException e) {
            throw new BusinessException("Error retrieving the client/supplier.", e);
        }
    }

    public ClientSupplier getUs() throws BusinessException, DataValidationException {
        List<ClientSupplier> all = getAllClientSuppliers();
        return all.stream()
                .filter(ClientSupplier::getIsUs)
                .findFirst()
                .orElseThrow(() -> new BusinessException("No 'us' client supplier found."));
    }

//===================================
//              CREATE
//===================================

    public void createClientSupplier(ClientSupplier clientSupplier) throws BusinessException, DataValidationException {
        if (clientSupplier == null) {
            throw new BusinessException("The client supplier cannot be null.");
        }
        if (clientSupplier.getName() == null || clientSupplier.getName().isBlank()) {
            throw new BusinessException("The client supplier name is required.");
        }
        if (clientSupplier.getIsUs() && (clientSupplier.getIsSupplier() || clientSupplier.getIsClient())) {
            throw new BusinessException("The client supplier cannot be Us and (supplier or client).");
        }
        try {
            clientSupplierDA.insert(clientSupplier);
        } catch (DataBaseException e) {
            throw new BusinessException("Error creating the client supplier.", e);
        }
    }

    public void createFidelityCard(FidelityCard fidelityCard) throws BusinessException, DataValidationException {
        if (fidelityCard == null) {
            throw new BusinessException("Error: fidelity card cannot be null.");
        }
        try {
            List<FidelityCard> fidelityCardList = fidelityCardDA.getAll();
            boolean cardFound = fidelityCardList.stream()
                    .anyMatch(fc -> fc.getClient() == fidelityCard.getClient());
            if (cardFound) {
                throw new BusinessException("Error: This client already has a loyalty card.");
            }
            fidelityCardDA.insert(fidelityCard);
        } catch (DataBaseException e) {
            throw new BusinessException("Error creating loyalty card.", e);
        }
    }

    public void addCheckout(HashMap<Product, Integer> products) throws BusinessException, DataValidationException {
        addCheckout(products, -1, null, false);
    }

    public void addCheckout(HashMap<Product, Integer> products, int clientId) throws BusinessException, DataValidationException {
        addCheckout(products, clientId, null, false);
    }

    public void addCheckout(HashMap<Product, Integer> products, int clientId, FidelityCard fidelityCard) throws BusinessException, DataValidationException {
        addCheckout(products, clientId, fidelityCard, false);
    }

    public void addCheckout(HashMap<Product, Integer> products, int clientId, FidelityCard fidelityCard, boolean useFidelityPoints) throws BusinessException, DataValidationException {

        if (products == null || products.isEmpty()) {
            throw new BusinessException("Error: The product map cannot be empty.");
        }

        if (clientId > 0) {
            getClientSupplier(clientId);
        }

        if (fidelityCard != null) {
            if (clientId <= 0) {
                throw new BusinessException("Error: A fidelity card requires a valid client ID.");
            }
            if (!validateFidelityCardOwnership(clientId, fidelityCard.getId())) {
                throw new BusinessException("Error: This loyalty card does not belong to the specified client.");
            }
        }

        try {
            int totalPointsEarned = 0;

            for (HashMap.Entry<Product, Integer> entry : products.entrySet()) {
                Product product = entry.getKey();
                Integer quantityBought = entry.getValue();
                if (quantityBought == null || quantityBought <= 0) {
                    throw new BusinessException("Error: Invalid quantity for product: " + product.getName());
                }

                List<QuantityProduct> stocks = new ArrayList<>();

                for (QuantityProduct qp : quantityProductDA.getAll()) {
                    if (qp.getProduct().getId() == product.getId()
                            && qp.getLocationProduct().getIsStock()) {
                        stocks.add(qp);
                    }
                }

                if (stocks.isEmpty()) {
                    throw new BusinessException("Error: No stock entry found for product: " + product.getName());
                }

                QuantityProduct stock = stocks.getFirst();
                int newQuantity = stock.getQuantity() - quantityBought;
                if (newQuantity < 0) {
                    throw new BusinessException("Error: Insufficient stock for product: " + product.getName());
                }
                quantityProductDA.update(stock, newQuantity);

                if (fidelityCard != null) {
                    totalPointsEarned += product.getFidelityPoint() * quantityBought;
                }
            }

            if (fidelityCard != null) {
                int currentPoints = fidelityCard.getTotalPoint();
                int pointsToDeduct = useFidelityPoints ? currentPoints : 0;
                int newTotal = currentPoints - pointsToDeduct + totalPointsEarned;
                fidelityCardDA.updateTotalPoint(fidelityCard, newTotal);
            }

        } catch (DataBaseException e) {
            throw new BusinessException("Error recording checkout.", e);
        }
    }

//===================================
//              UPDATE
//===================================

    public void updateClientSupplier(ClientSupplier oldModel, ClientSupplier newModel) throws BusinessException, DataValidationException {
        if (newModel == null) {
            throw new BusinessException("The new client supplier cannot be null.");
        }
        if (newModel.getName() == null || newModel.getName().isBlank()) {
            throw new BusinessException("The client supplier name is required.");
        }
        if (newModel.getEmail() != null && !newModel.getEmail().trim().isEmpty()
                && !newModel.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BusinessException("The email is not in a valid format.");
        }
        if (newModel.getPhoneNumber() != null && !newModel.getPhoneNumber().isBlank()
                && newModel.getPhoneNumber().length() != 11) {
            throw new BusinessException("The phone number must be 11 digits.");
        }
        if (newModel.getIsUs() && (newModel.getIsSupplier() || newModel.getIsClient())) {
            throw new BusinessException("The client supplier cannot be Us and (supplier or client).");
        }
        try {
            clientSupplierDA.update(oldModel, newModel);
        } catch (DataBaseException e) {
            throw new BusinessException("Error updating the client supplier.", e);
        }
    }

//===================================
//              DELETE
//===================================

    public boolean deleteClientSupplier(ClientSupplier clientSupplier) throws BusinessException, DataValidationException {
        if (clientSupplier == null) {
            throw new BusinessException("The client supplier cannot be null.");
        }
        try {
            clientSupplierDA.delete(clientSupplier);
            return true;
        } catch (DataBaseException e) {
            throw new BusinessException("Error deleting the client supplier.", e);
        }
    }

    public void deleteClientAccount(int clientId) throws BusinessException, DataValidationException {
        if (clientId <= 0) {
            throw new BusinessException("Error: The client ID is invalid.");
        }
        try {
            ClientSupplier clientSupplier = clientSupplierDA.getById(clientId);
            clientSupplierDA.delete(clientSupplier);
        } catch (DataBaseException e) {
            throw new BusinessException("Error: Failed to delete client account.", e);
        }
    }

    public void deleteClientAccount(int clientId, int cardId) throws BusinessException, DataValidationException {
        if (cardId <= 0) {
            throw new BusinessException("Error: The card ID is invalid.");
        }
        if (!validateFidelityCardOwnership(clientId, cardId)) {
            throw new BusinessException("Error: This loyalty card does not belong to the specified client.");
        }
        deleteClientAccount(clientId);
    }

//===================================
//              OTHERS
//===================================

    public boolean validateFidelityCardOwnership(int clientId, int cardId) throws BusinessException, DataValidationException {
        if (clientId <= 0 || cardId <= 0) {
            throw new BusinessException("Error: The provided IDs are invalid.");
        }
        try {
            ClientSupplier clientSupplier = clientSupplierDA.getById(clientId);
            if (clientSupplier == null) {
                return false;
            }
            return clientSupplier.getFidelityCard().getId() == cardId;
        } catch (DataBaseException e) {
            throw new BusinessException("Error validating loyalty card ownership.", e);
        }
    }

    public void placeSupplierOrder(int supplierId, HashMap<Product, Integer> products) throws BusinessException, DataValidationException {
        if (supplierId <= 0) {
            throw new BusinessException("Error: The supplier ID is invalid.");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessException("Error: The product list cannot be empty.");
        }

        try {
            ClientSupplier supplier = clientSupplierDA.getById(supplierId);
            if (supplier == null || !supplier.getIsSupplier()) {
                throw new BusinessException("Error: Supplier not found.");
            }

            ClientSupplier us = getUs();

            WorkFlowType buyType = workFlowTypeDA.getByName("Buy");
            if (buyType == null) {
                throw new BusinessException("Error: WorkFlowType 'Buy' not found.");
            }

            Status pending = statusDA.getByName("Pending");
            if (pending == null) {
                throw new BusinessException("Error: Status 'Pending' not found.");
            }

            WorkFlow workflow = new WorkFlow(pending, buyType, us, supplier);
            workFlowDA.insert(workflow);

            DocumentType purchaseOrderType = documentTypeDA.getAll().stream()
                    .filter(dt -> "Purchase Order".equals(dt.getName()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("Error: DocumentType 'Purchase Order' not found."));

            Document purchaseOrder = new Document(
                    0,
                    LocalDate.now(),
                    purchaseOrderType,
                    null,
                    false,
                    LocalDate.now(), null,
                    LocalDate.now(), null,
                    30,
                    workflow,
                    supplier.getAddress(),
                    "Purchase order for " + products.values().stream().mapToInt(Integer::intValue).sum()
                            + " item(s) from " + supplier.getName(),
                    null
            );
            documentDA.insert(purchaseOrder);

            for (HashMap.Entry<Product, Integer> entry : products.entrySet()) {
                Product product = entry.getKey();
                Integer quantityOrdered = entry.getValue();

                if (quantityOrdered == null || quantityOrdered <= 0) {
                    throw new BusinessException("Error: Invalid quantity for product: " + product.getName());
                }

                Detail detail = new Detail(
                        0,
                        product.getPrice(),
                        product.getVat(),
                        0,
                        quantityOrdered,
                        purchaseOrder,
                        product,
                        null
                );
                detailDA.insert(detail);
                purchaseOrder.addDetail(detail);

                List<QuantityProduct> stocks = quantityProductDA.getAll().stream()
                        .filter(qp -> qp.getProduct().getId() == product.getId()
                                && qp.getLocationProduct().getIsStock())
                        .toList();

                if (stocks.isEmpty()) {
                    throw new BusinessException("Error: No stock entry found for product: " + product.getName());
                }

                quantityProductDA.update(stocks.getFirst(), stocks.getFirst().getQuantity() + quantityOrdered);
            }

            Status delivered = statusDA.getByName("Delivered");
            if (delivered == null) {
                throw new BusinessException("Error: Status 'Delivered' not found.");
            }
            workFlowDA.updateFieldStatus(workflow, delivered);

        } catch (DataBaseException e) {
            throw new BusinessException("Error: Failed to place order.", e);
        }
    }}