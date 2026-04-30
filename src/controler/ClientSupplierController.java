package controler;

import exception.DataValidationException;
import model.Address;
import model.ClientSupplier;
import model.Location;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class ClientSupplierController {
    public ArrayList<ClientSupplier> getAllClientSupplier() throws DataValidationException {
        ArrayList<ClientSupplier> clients = new ArrayList<>();

        clients.add(new ClientSupplier(
                1,
                "Dupont",
                "Jean",
                "jean.dupont@email.com",
                "+32470000001",
                new Address("Avenue Louise 10", 1050, new Location("Ixelles", 6000)),
                true,
                false,
                true,
                "BE0123456789",
                new Date()
        ));

        clients.add(new ClientSupplier(
                2,
                "Martin",
                "Sophie",
                "sophie.martin@email.com",
                "+32470000002",
                new Address("Avenue Louise 10", 1050, new Location("Ixelles", 6000)),
                true,
                false,
                false,
                "BE0987654321",
                new Date()
        ));

        clients.add(new ClientSupplier(
                3,
                "Nguyen",
                "Linh",
                "linh.nguyen@email.com",
                "+32470000003",
                new Address("Avenue Louise 10", 1050, new Location("Ixelles", 6000)),
                true,
                false,
                true,
                "BE1122334455",
                new Date()
        ));

        clients.add(new ClientSupplier(
                4,
                "Dubois",
                "Marc",
                "marc.dubois@email.com",
                "+32470000004",
                new Address("Avenue Louise 10", 1050, new Location("Ixelles", 6000)),
                true,
                false,
                false,
                "BE6677889900",
                new Date()
        ));

        clients.add(new ClientSupplier(
                5,
                "Smith",
                "Anna",
                "anna.smith@email.com",
                "+32470000005",
                new Address("Avenue Louise 10", 1050, new Location("Ixelles", 6000)),
                true,
                false,
                true,
                "BE5566778899",
                new Date()
        ));

        return clients;
    }

    public void deleteClientSupplier(ClientSupplier csToDelete) {
    }

    public void createClientSupplier(String name, String firstName, String mail, String vatNumber, LocalDate becomeClient, String loyaltyCardId, int loyaltyPoints, boolean isClient, boolean isSupplier, boolean isMember, int streetNumber, int postalCode, String street, String city, String country) {
    }

    public void updateClientSupplier(int id, String name, String firstName, String mail, String vatNumber, LocalDate becomeClient, String loyaltyCardId, int loyaltyPoints, boolean isClient, boolean isSupplier, boolean isMember, int streetNumber, int postalCode, String street, String city, String country) {

    }
}
