package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.ClientSupplier;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller fictif pour simuler la recherche de clients/fournisseurs.
 * TODO: remplacer par une vraie requête SQL avec JOIN sur 4 tables
 *       (ClientSupplier, FidelityCard, Address, Locality)
 */
public class ClientSupplierSearchController {

    private final ClientSupplierController clientSupplierController;

    public ClientSupplierSearchController() {
        this.clientSupplierController = new ClientSupplierController();
    }

    /**
     * Recherche fictive — simule un SELECT avec JOIN sur 4 tables + WHERE en SQL.
     * Les critères null ou vides sont ignorés (= pas de filtre sur ce champ).
     *
     * @param name               filtre sur le nom (partial, insensible à la casse)
     * @param email              filtre sur l'email (partial, insensible à la casse)
     * @param fidelityCardNumber filtre sur la carte de fidélité (digits)
     * @return liste des ClientSupplier correspondant aux critères
     */
    public List<ClientSupplier> search(String name, String email, String fidelityCardNumber) {

        List<ClientSupplier> clients;
            clients = clientSupplierController.getAllClientSupplier();

        List<ClientSupplier> results = new ArrayList<>();
        for (ClientSupplier cs : clients) {
            boolean match = true;

            if (name != null && !name.isBlank()) {
                if (!cs.getName().toLowerCase().contains(name.toLowerCase())) {
                    match = false;
                }
            }

            if (email != null && !email.isBlank()) {
                if (!cs.getEmail().toLowerCase().contains(email.toLowerCase())) {
                    match = false;
                }
            }

            if (fidelityCardNumber != null && !fidelityCardNumber.isBlank()) {

                if (cs.getFidelityCard() == null) {
                    match = false;

                } else {
                    try {
                        int fidelityId = Integer.parseInt(fidelityCardNumber);

                        if (cs.getFidelityCard().getId() != fidelityId) {
                            match = false;
                        }

                    } catch (NumberFormatException e) {
                        match = false;
                    }
                }
            }
            if (match) {
                results.add(cs);
            }
        }
        return results;
    }
}