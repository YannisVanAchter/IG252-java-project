package main.java.be.henallux.project.business;

import main.java.be.henallux.project.business.exception.BusinessException;
import main.java.be.henallux.project.data.ProductData;
import main.java.be.henallux.project.data.exception.DataBaseException;
import main.java.be.henallux.project.model.*;
import java.util.List;

public class ProductManager {

    private final ProductData productData;

    public ProductManager(ProductData productData) {
        this.productData = productData;
    }

    public List<Product> getAllProducts() throws BusinessException {
        try {
            return productData.getAllProducts();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des produits.", e);
        }
    }

    public Product getProduct(int productID) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("L'identifiant produit est invalide.");
        }
        try {
            Product product = productData.getProduct(productID);
            // Règle métier
            if (product == null) {
                throw new BusinessException("Le produit n'existe pas.");
            }
            return product;
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération du produit.", e);
        }
    }

    public List<Category> getAllProductCategory() throws BusinessException {
        try {
            return productData.getAllProductCategory();
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la récupération des catégories.", e);
        }
    }

    public void createProduct(Product product) throws BusinessException {
        // Validation
        if (product == null) {
            throw new BusinessException("Le produit ne peut pas être nul.");
        }
        if (product.getName() == null || product.getName().isBlank()) {
            throw new BusinessException("Le nom du produit est obligatoire.");
        }
        if (product.getPrice() < 0) {
            throw new BusinessException("Le prix du produit ne peut pas être négatif.");
        }
        if (product.getVAT() < 0 || product.getVAT() > 100) {
            throw new BusinessException("La TVA doit être comprise entre 0 et 100.");
        }
        try {
            productData.createProduct(product);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la création du produit.", e);
        }
    }

    public void changeProductPrice(int productID, double price) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("L'identifiant produit est invalide.");
        }
        if (price < 0) {
            throw new BusinessException("Le prix ne peut pas être négatif.");
        }
        try {
            productData.changeProductPrice(productID, price);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement de prix.", e);
        }
    }

    public void changeProductVAT(int productID, double VAT) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("L'identifiant produit est invalide.");
        }
        // Règle métier
        if (VAT < 0 || VAT > 100) {
            throw new BusinessException("La TVA doit être comprise entre 0 et 100.");
        }
        try {
            productData.changeProductVAT(productID, VAT);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement de TVA.", e);
        }
    }

    public void changeFidelityPoint(int productID, int points) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("L'identifiant produit est invalide.");
        }
        if (points < 0) {
            throw new BusinessException("Les points de fidélité ne peuvent pas être négatifs.");
        }
        try {
            productData.changeFidelityPoint(productID, points);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement des points de fidélité.", e);
        }
    }

    public void changeMinimalQuantity(int productID, int minimalQuantity) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("L'identifiant produit est invalide.");
        }
        if (minimalQuantity < 0) {
            throw new BusinessException("La quantité minimale ne peut pas être négative.");
        }
        try {
            productData.changeMinimalQuantity(productID, minimalQuantity);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors du changement de la quantité minimale.", e);
        }
    }

    public void deleteProduct(int productID) throws BusinessException {
        // Validation
        if (productID <= 0) {
            throw new BusinessException("L'identifiant produit est invalide.");
        }
        try {
            // Règle métier — vérifier que le produit existe avant de le supprimer
            if (productData.getProduct(productID) == null) {
                throw new BusinessException("Le produit n'existe pas.");
            }
            productData.deleteProduct(productID);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression du produit.", e);
        }
    }

    public void createProductCategory(String name) throws BusinessException {
        // Validation
        if (name == null || name.isBlank()) {
            throw new BusinessException("Le nom de la catégorie est obligatoire.");
        }
        try {
            productData.createProductCategory(name);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la création de la catégorie.", e);
        }
    }

    public void addDiscount(Discount discount) throws BusinessException {
        // Validation
        if (discount == null) {
            throw new BusinessException("La remise ne peut pas être nulle.");
        }
        // Règle métier
        if (discount.getPercentage() <= 0 || discount.getPercentage() > 100) {
            throw new BusinessException("Le pourcentage de remise doit être compris entre 1 et 100.");
        }
        if (discount.getStartDate().isAfter(discount.getEndDate())) {
            throw new BusinessException("La date de début doit être antérieure à la date de fin.");
        }
        try {
            productData.addDiscount(discount);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de l'ajout de la remise.", e);
        }
    }

    public void deleteDiscount(int discountID) throws BusinessException {
        // Validation
        if (discountID <= 0) {
            throw new BusinessException("L'identifiant de la remise est invalide.");
        }
        try {
            productData.deleteDiscount(discountID);
        } catch (DataBaseException e) {
            throw new BusinessException("Erreur lors de la suppression de la remise.", e);
        }
    }
}