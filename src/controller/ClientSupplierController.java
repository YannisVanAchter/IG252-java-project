package controller;

import exception.DataValidationException;
import model.Address;
import model.ClientSupplier;
import model.Locality;

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
    public ArrayList<ClientSupplier> getAllClientSupplier() throws DataValidationException {
        ArrayList<ClientSupplier> clients = new ArrayList<>();

        clients.add(new ClientSupplier(
                1,
                "Dupont",
                "Jean",
                "jean.dupont@email.com",
                "32470000001",
                new Address("Avenue Louise 10", 1050, new Locality("Ixelles", 6000)),
                true,
                false,
                true,
                "BE0123456789",
                LocalDate.now()
        ));

        clients.add(new ClientSupplier(
                2,
                "Martin",
                "Sophie",
                "sophie.martin@email.com",
                "32470000002",
                new Address("Avenue Louise 10", 1050, new Locality("Ixelles", 6000)),
                true,
                false,
                false,
                "BE0987654321",
                LocalDate.now()
        ));

        clients.add(new ClientSupplier(
                3,
                "Nguyen",
                "Linh",
                "linh.nguyen@email.com",
                "32470000003",
                new Address("Avenue Louise 10", 1050, new Locality("Ixelles", 6000)),
                true,
                false,
                true,
                "BE1122334455",
                LocalDate.now()
        ));

        clients.add(new ClientSupplier(
                4,
                "Dubois",
                "Marc",
                "marc.dubois@email.com",
                "32470000004",
                new Address("Avenue Louise 10", 1050, new Locality("Ixelles", 6000)),
                true,
                false,
                false,
                "BE6677889900",
                LocalDate.now()
        ));

        clients.add(new ClientSupplier(
                5,
                "Smith",
                "Anna",
                "anna.smith@email.com",
                "32470000005",
                new Address("Avenue Louise 10", 1050, new Locality("Ixelles", 6000)),
                true,
                false,
                true,
                "BE5566778899",
                LocalDate.now()
        ));

        return clients;
    }

    /**
     * Suppression d'un ClientSupplier spécifique dans la bd.
     * La suppression dans la vue est deja gérée dans la vue
     * @param csToDelete le ClientSupplier objet à supprimer
     */
    public void deleteClientSupplier(ClientSupplier csToDelete) {
    }

    /**
     * Crée un ClientSupplier.
     * La méthode prend tous les arguments en charge
     *
     * @return le nouvel objet ajouté
     * @throws DataValidationException en cas de problème de validation
     */
    public ClientSupplier createClientSupplier (String name, String firstName, String mail, String phoneNumber, String vatNumber, LocalDate becomeClient, String loyaltyCardId, int loyaltyPoints, boolean isClient, boolean isSupplier, boolean isMember, int streetNumber, int postalCode, String street, String city, String country) throws DataValidationException {
        return new ClientSupplier( 1,  name,  firstName,  mail,  phoneNumber, new Address(street, streetNumber, city, postalCode),  isClient,  isSupplier,  isMember,  vatNumber, becomeClient);
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
        return new ClientSupplier( 1,  name,  firstName,  mail,  phoneNumber, new Address(street, streetNumber, city, postalCode),  isClient,  isSupplier,  isMember,  vatNumber, becomeClient);

    }
}
