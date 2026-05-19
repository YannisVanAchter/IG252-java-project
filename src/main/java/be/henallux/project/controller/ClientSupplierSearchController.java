package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Locality;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller fictif pour simuler la recherche de clients/fournisseurs.
 * TODO: remplacer par une vraie requête SQL avec JOIN sur 4 tables
 *       (ClientSupplier, FidelityCard, Address, Locality)
 */
public class ClientSupplierSearchController {

    private List<ClientSupplier> clients = new ArrayList<>();

    public ClientSupplierSearchController() throws DataValidationException {

        clients.add(new ClientSupplier(1, "Dupont", "Jean", "jean.dupont@email.com", "32470000001",
                new Address("Avenue Louise", 10, new Locality("Ixelles", 1050)), true, false, true,
                "BE0123456789", LocalDate.of(2020, 1, 15)));

        clients.add(new ClientSupplier(2, "Martin", "Sophie", "sophie.martin@email.com", "32470000002",
                new Address("Rue de la Loi", 42, new Locality("Bruxelles", 1000)), true, false, false,
                "BE0987654321", LocalDate.of(2021, 3, 22)));

        clients.add(new ClientSupplier(3, "Nguyen", "Linh", "linh.nguyen@email.com", "32470000003",
                new Address("Boulevard Anspach", 5, new Locality("Bruxelles", 1000)), false, true, false,
                "BE1122334455", LocalDate.of(2019, 7, 10)));

        clients.add(new ClientSupplier(4, "Dubois", "Marc", "marc.dubois@email.com", "32470000004",
                new Address("Rue Neuve", 88, new Locality("Schaerbeek", 1030)), true, true, false,
                "BE6677889900", LocalDate.of(2022, 5, 5)));

        clients.add(new ClientSupplier(5, "Smith", "Anna", "anna.smith@email.com", "32470000005",
                new Address("Avenue Fonsny", 20, new Locality("Saint-Gilles", 1060)), true, false, true,
                "BE5566778899", LocalDate.of(2018, 11, 30)));
    }

    /**
     * Recherche fictive — simule un SELECT avec JOIN sur 4 tables + WHERE en SQL.
     * Les critères null ou vides sont ignorés (= pas de filtre sur ce champ).
     *
     * @param name          filtre sur le nom (partial, insensible à la casse)
     * @param email         filtre sur l'email (partial, insensible à la casse)
     * @param fidelityCardNumber filtre sur la carte de fidélité (digits)
     * @return liste des ClientSupplier correspondant aux critères
     */
    public List<ClientSupplier> search(String name, String email, String fidelityCardNumber) {
        List<ClientSupplier> results = new ArrayList<>();

        for (ClientSupplier cs : clients) {
            boolean match = true;

            if (name != null && !cs.getName().toLowerCase().contains(name.toLowerCase())) {
                match = false;
            }
            if (email != null && !cs.getEmail().toLowerCase().contains(email.toLowerCase())) {
                match = false;
            }
            // TODO: remplacer par fc.cardNumber quand FidelityCard sera implémenté
            // if (fidelityCardNumber != null && !cs.getFidelityCard().getCardNumber().contains(fidelityCardNumber)){
            //     match = false;
            //}

            if (match) results.add(cs);
        }

        return results;
    }}