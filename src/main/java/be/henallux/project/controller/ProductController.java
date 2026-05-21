package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Controller fictif utilisé uniquement pour simuler les vues de l'application.
 * <p>
 * TODO: clean & implement class.
 */
public class ProductController {

    private ArrayList<Product> products = new ArrayList<>();

    public ArrayList<Product> getAllProduct() {

        if (!products.isEmpty()) {
            return products;
        }

        try {
            ProductCategory fruit    = new ProductCategory(1, "fruit");
            ProductCategory boisson  = new ProductCategory(3, "Boisson");

            // --- Pomme ---
            Product pomme = new Product(
                    1, "Pomme",
                    new BigDecimal("1.20"), new BigDecimal("6"),
                    10, true, 20, fruit, null, null
            );
            pomme.setLocation(new ArrayList<>(List.of(
                    new QuantityProduct(
                            new LocationProduct("A", "1", false, false),
                            pomme,
                            2
                    ),
                    new QuantityProduct(
                            new LocationProduct("A", "2", false, false),
                            pomme,
                            2
                    )
            )));
            products.add(pomme);

            // --- Banane ---
            Product banane = new Product(
                    2, "Banane",
                    new BigDecimal("2.00"), new BigDecimal("6"),
                    8, true, 20, fruit, null, null
            );
            banane.setLocation(new ArrayList<>(List.of(new QuantityProduct(
                    new LocationProduct("A", "2", false, false), banane, 120
            ))));
            products.add(banane);

            // --- Chocolat ---
            Product chocolat = new Product(
                    3, "Chocolat",
                    new BigDecimal("5.00"), new BigDecimal("21"),
                    25, true, 10, fruit, null, null
            );
            chocolat.setLocation(new ArrayList<>(List.of(new QuantityProduct(
                    new LocationProduct("B", "1", false, false), chocolat, 60
            ))));
            chocolat.addDiscount(new Discount(
                    2,
                    new BigDecimal("20"),
                    LocalDate.of(2026, 4, 1),
                    LocalDate.of(2026, 5, 31),
                    "-20%",
                    chocolat
            ));
            chocolat.addDiscount(new Discount(
                    4,
                    new BigDecimal("50"),
                    LocalDate.of(2026, 2, 1),
                    LocalDate.of(2026, 3, 31),
                    "-50%",
                    chocolat
            ));
            products.add(chocolat);

            // --- Lait ---
            Product lait = new Product(
                    4, "Lait",
                    new BigDecimal("1.10"), new BigDecimal("6"),
                    5, true, 15, boisson, null, null
            );
            lait.setLocation(new ArrayList<>(List.of(new QuantityProduct(
                    new LocationProduct("C", "1", true, true), lait, 80
            ))));
            products.add(lait);

            // --- Café ---
            Product cafe = new Product(
                    5, "Café",
                    new BigDecimal("3.00"), new BigDecimal("21"),
                    15, true, 10, boisson, null, null
            );
            cafe.setLocation(new ArrayList<>(List.of(new QuantityProduct(
                    new LocationProduct("C", "2", false, false), cafe, 40
            ))));
            cafe.addDiscount(new Discount(
                    3,
                    new BigDecimal("10"),
                    LocalDate.of(2026, 4, 10),
                    LocalDate.of(2026, 5, 20),
                    "-10%",
                    cafe
            ));
            products.add(cafe);
        } catch (DataValidationException e) {
            throw new RuntimeException(e);
        }

        return products;
    }

    public String[] getAllCategory() {
        if (products.isEmpty()) {
            getAllProduct();
        }

        return Stream.concat(
                Stream.of("All"),
                products.stream()
                        .filter(product -> product.getCategory() != null)
                        .map(product -> product.getCategory().getName())
                        .distinct()
        ).toArray(String[]::new);
    }
}