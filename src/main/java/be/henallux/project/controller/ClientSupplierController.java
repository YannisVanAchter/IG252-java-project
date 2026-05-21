package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.model.FidelityCard;
import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.Address;
import main.java.be.henallux.project.model.ClientSupplier;
import main.java.be.henallux.project.model.Locality;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Controller fictif utilisé uniquement pour simuler les vues de l'application.
 *
 * TODO: clean & implement class.
 */
public class ClientSupplierController {

    /**
     * Retourne la liste des clients/fournisseurs disponible dans la bd
     *
     * @return une liste de clients/fournisseurs en ArrayList
     * @throws DataValidationException a supprimé, je pense. (j'en avais besoin ici, car création d'objet)
     */
    public ArrayList<ClientSupplier> getAllClientSupplier() {
        ArrayList<ClientSupplier> clients = new ArrayList<>();

        try {
            // --- Client 1 : Dupont Jean (isClient=true) ---
            ClientSupplier dupont = new ClientSupplier(
                    1, "Dupont", "Jean", "jean.dupont@email.com", "32470000001",
                    new Address(1, "Avenue Louise", 10, new Locality("Ixelles", 1050)),
                    true, false, true, "BE0123456789", LocalDate.of(2020, 1, 15), null
            );
            dupont.setFidelityCard(new FidelityCard(1, 150, true, dupont));
            clients.add(dupont);

            // --- Client 2 : Martin Sophie (isClient=true) ---
            ClientSupplier martin = new ClientSupplier(
                    2, "Martin", "Sophie", "sophie.clem@email.com", "32470000002",
                    new Address(2, "Rue de la Loi", 42, new Locality("Bruxelles", 1000)),
                    true, false, false, "BE0987654321", LocalDate.of(2021, 3, 22), null
            );
            martin.setFidelityCard(new FidelityCard(2, 80, true, martin));
            clients.add(martin);

            // --- Client 3 : Clem Cloum (isClient=false) ---
            ClientSupplier clem = new ClientSupplier(
                    3, "Clem", "cloum", "cloum.clem@email.com", "32123456789",
                    new Address(2, "Rue de ici", 12, new Locality("Namur", 5000)),
                    true, false, false, "BE0987654321", LocalDate.of(2021, 3, 22)
            );
            clients.add(clem);

            // --- Client+Fournisseur 4 : Dubois Marc (isClient=true, isSupplier=true) ---
            ClientSupplier dubois = new ClientSupplier(
                    4, "Dubois", "Marc", "marc.dubois@email.com", "32470000004",
                    new Address(4, "Rue Neuve", 88, new Locality("Schaerbeek", 1030)),
                    true, true, false, "BE6677889900", LocalDate.of(2022, 5, 5), null
            );
            dubois.setFidelityCard(new FidelityCard(4, 320, true, dubois));
            clients.add(dubois);

            // --- Client 5 : Smith Anna (isClient=true) ---
            ClientSupplier smith = new ClientSupplier(
                    5, "Smith", "Anna", "anna.smith@email.com", "32470000005",
                    new Address(5, "Avenue Fonsny", 20, new Locality("Saint-Gilles", 1060)),
                    true, false, true, "BE5566778899", LocalDate.of(2018, 11, 30), null
            );
            smith.setFidelityCard(new FidelityCard(5, 500, false, smith));
            clients.add(smith);

            return clients;
        } catch (DataValidationException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Suppression d'un ClientSupplier spécifique dans la bd.
     * La suppression dans la vue est deja gérée dans la vue
     * @param csToDelete le ClientSupplier objet à supprimer
     */
    public boolean deleteClientSupplier(ClientSupplier csToDelete) {
        return true;
    }

    /**
     * Crée un ClientSupplier.
     * La méthode prend tous les arguments en charge
     *
     * @return le nouvel objet ajouté
     * @throws DataValidationException en cas de problème de validation
     */
    public ClientSupplier createClientSupplier (String name, String firstName, String mail, String phoneNumber, String vatNumber, LocalDate becomeClient, String loyaltyCardId, int loyaltyPoints, boolean isClient, boolean isSupplier, boolean isMember, int streetNumber, int postalCode, String street, String city, String country) throws DataValidationException {
        return null; //new ClientSupplier( 1,  name,  firstName,  mail,  phoneNumber, new Address(1,street, streetNumber, city, postalCode),  isClient,  isSupplier,  isMember,  vatNumber, becomeClient);
    }

    /**
     * Update un ClientSupplier.
     * La méthode prend tous les arguments en charge
     * @param id fais référence au ClientSup à modifier.
     *
     * @return l'état de l'update
     * @throws DataValidationException en cas de problème de validation
     */
    public ClientSupplier updateClientSupplier(int id, String name, String firstName, String mail, String phoneNumber, String vatNumber, LocalDate becomeClient, String loyaltyCardId, int loyaltyPoints, boolean isClient, boolean isSupplier, boolean isMember, int streetNumber, int postalCode, String street, String city, String country) throws DataValidationException {
        return null; //new ClientSupplier( 1,  name,  firstName,  mail,  phoneNumber, new Address(1,street, streetNumber, city, postalCode),  isClient,  isSupplier,  isMember,  vatNumber, becomeClient);
    }
}
