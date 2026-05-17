package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Controller fictif utilisé uniquement pour simuler les vues de l'application.
 * <p>
 * TODO: clean & implement class.
 */
public class ProductController {

    public ArrayList<Product> getAllProduct() {

        ArrayList<Product> products = new ArrayList<>();

        ProductCategory fruit = new ProductCategory("fruit");
        ProductCategory autre = new ProductCategory("autre");
        ProductCategory boisson = new ProductCategory("Boisson");

        Product pomme = new Product(
                1, "Pomme", 1.20, 0.06, 10,
                null,
                fruit,
                new QuantityProduct(2, 1, 0, new Location(false)),
                20
        );

        Product banane = new Product(
                2, "Banane", 2, 0.06, 8,
                null,
                fruit,
                new QuantityProduct(120, 1, 0, new Location(false)),
                20
        );

        Product chocolat = new Product(
                3, "Chocolat", 5, 0.21, 25,
                null,
                fruit,
                new QuantityProduct(60, 2, 1, new Location(false)),
                10
        );

        Discount chocolatDiscount = new Discount(
                2,
                20,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 5, 31),
                "-20%",
                chocolat
        );

        chocolat.setDiscount(chocolatDiscount);

        Product lait = new Product(
                4, "Lait", 1.10, 0.06, 5,
                null,
                boisson,
                new QuantityProduct(80, 3, 1, new Location(true)),
                15
        );

        Product cafe = new Product(
                5, "Café", 3.00, 0.21, 15,
                null,
                boisson,
                new QuantityProduct(40, 4, 2, new Location(false)),
                10
        );

        Discount cafeDiscount = new Discount(
                3,
                10,
                LocalDate.of(2026, 4, 10),
                LocalDate.of(2026, 5, 20),
                "-10%",
                cafe
        );

        cafe.setDiscount(cafeDiscount);

        products.add(pomme);
        products.add(banane);
        products.add(chocolat);
        products.add(lait);
        products.add(cafe);

        return products;
    }

    public ArrayList<ProductCategory> getAllCategory(){
        ArrayList<ProductCategory> categories = new ArrayList<>();
        categories.add(new ProductCategory("mobilier"));
        categories.add(new ProductCategory("fruit"));
        categories.add(new ProductCategory("legume"));
        categories.add(new ProductCategory("boisson"));
        categories.add(new ProductCategory("autre"));

        return categories;
    }

    public String[] getCategoryNames() {
        return Stream.concat(
                Stream.of("All"),
                getAllCategory().stream().map(ProductCategory::getName)
        ).toArray(String[]::new);
    }
}
