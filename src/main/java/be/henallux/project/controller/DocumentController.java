package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.exception.DataValidationException;
import main.java.be.henallux.project.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller fictif utilisé uniquement pour simuler les vues de l'application.
 * <p>
 * TODO: clean & implement class.
 */
public class DocumentController {

    private final ArrayList<Document> documents = new ArrayList<>();

    /**
     * Retourne la liste des documents disponibles.
     *
     * @return une liste de documents au format ArrayList
     * @throws DataValidationException a supprimé, je pense. (j'en avais besoin ici, car création d'objet)
     */
    public ArrayList<Document> getAllDocuments() throws DataValidationException {
        documents.add(new Document(
                1,
                LocalDate.of(2020, 12, 2),
                new DocumentType("Invoice"),
                true,
                LocalDate.of(2020, 12, 2),
                LocalDate.of(2020, 12, 2),
                LocalDate.of(2020, 12, 2),
                LocalDate.of(2020, 12, 2),
                20,
                new WorkFlow(
                        1,
                        new Status("Status"),
                        new WorkFlowType("Internal", false, false, true)
                ),
                new ClientSupplier(
                        1,
                        "clem",
                        "cloum",
                        "azer@gmail.com",
                        "12345678",
                        new Address(
                                "Café route",
                                12,
                                "Namur",
                                5000
                        ),
                        true,
                        false,
                        false,
                        "",
                        LocalDate.now()
                ),
                new Address(
                        "Café route",
                        12,
                        "Namur",
                        5000
                ),
                "AUTO"
        ));
        documents.add(new Document(
                2,
                LocalDate.of(2024, 3, 15),
                new DocumentType("Quote"),
                false,
                LocalDate.of(2024, 3, 15),
                LocalDate.of(2024, 3, 20),
                LocalDate.of(2024, 3, 16),
                LocalDate.of(2024, 3, 21),
                30,
                new WorkFlow(
                        2,
                        new Status("Pending"),
                        new WorkFlowType("Buy", false, false, true)
                ),
                new ClientSupplier(
                        2,
                        "Martin",
                        "Sophie",
                        "sophie.martin@email.com",
                        "32470000002",
                        new Address(
                                "Avenue Louise",
                                10,
                                "Bruxelles",
                                1050
                        ),
                        true,
                        false,
                        false,
                        "BE0987654321",
                        LocalDate.now()
                ),
                new Address(
                        "Avenue Louise",
                        10,
                        "Bruxelles",
                        1050
                ),
                "AUTO"
        ));

        return new ArrayList<>(documents);
    }

    /**
     * Supprime un document.
     *
     * @param doc le document à supprimer
     * @return l'état de la suppression
     */
    public boolean deleteDocument(Document doc) {
        return true;
    }

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
     * Crée un nouveau document.
     * La méthode prend tous les arguments en charge
     *
     * @return le nouvel objet
     * @throws DataValidationException en cas de problème de validation
     */
    public boolean createDocument(
            DocumentType documentType,
            String commentaryText,
            LocalDate plannedSend,
            LocalDate plannedReception,
            LocalDate effectiveSend,
            LocalDate effectiveReception,
            int paymentDelay,
            Status workflowStatus,
            boolean isBuy,
            boolean isSell,
            boolean isInternal,
            ClientSupplier clientSupplier,
            int streetNumber,
            int postalCode,
            String street,
            String city,
            String country,
            boolean isChecked
    ) throws DataValidationException {
        System.out.println("Type: " + documentType);
        System.out.println("Commentaire: " + commentaryText);
        System.out.println("Client: " + clientSupplier);
        System.out.println("Adresse: " + streetNumber + " " + street + ", " + postalCode + " " + city + ", " + country);
        System.out.println("Flags: buy=" + isBuy + ", sell=" + isSell + ", internal=" + isInternal);
        return true;
    }

    /**
     * Update un document.
     * La méthode prend tous les arguments en charge
     *
     * @param documentId fais référence ou doc a modifé.
     * @return l'état de l'update
     * @throws DataValidationException en cas de problème de validation
     */
    public boolean updateDocument(
            int documentId,
            DocumentType documentType,
            String commentaryText,
            LocalDate plannedSend,
            LocalDate plannedReception,
            LocalDate effectiveSend,
            LocalDate effectiveReception,
            int paymentDelay,
            Status workflowStatus,
            boolean isBuy,
            boolean isSell,
            boolean isInternal,
            ClientSupplier clientSupplier,
            int streetNumber,
            int postalCode,
            String street,
            String city,
            String country,
            boolean isChecked
    ) throws DataValidationException {
        System.out.println("ID: " + documentId);
        System.out.println("Type: " + documentType);
        System.out.println("Commentaire: " + commentaryText);
        System.out.println("Client: " + clientSupplier);
        System.out.println("Adresse: " + streetNumber + " " + street + ", " + postalCode + " " + city + ", " + country);
        System.out.println("Flags: buy=" + isBuy + ", sell=" + isSell + ", internal=" + isInternal);
        return true;
    }

    /**
     * Retourne la liste des types de documents disponibles.
     * <p>
     * Ici les types sont extraits des documents fictifs existants.
     * Un élément "All" est ajouté en premier pour les filtres.
     * Utilisé pour Combobox
     *
     * @return tableau de noms de types de documents
     * @throws DataValidationException à supprimer (présent à cause de la creation dans getAllDocuments())
     */
    public ArrayList<DocumentType> getAllDocumentType() throws DataValidationException {
        return getAllDocuments().stream()
                .map(Document::getDocumentType)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retourne les statuts des workflows associés aux documents.
     *
     * @return tableau des statuts de workflow
     * @throws DataValidationException à supprimer (présent à cause de la creation dans getAllDocuments())
     */
    public ArrayList<Status> getAllWorkflowStatus() throws DataValidationException {
        return getAllDocuments().stream()
                .map(doc -> doc.getWorkflow().getStatus())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

}