package main.java.be.henallux.project.controller;

import main.java.be.henallux.project.model.exception.DataValidationException;
import main.java.be.henallux.project.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    public ArrayList<Document> getAllDocuments() {
        try {
            documents.clear();

            ClientSupplier usCompany = new ClientSupplier(
                    999,
                    "TerraClic",
                    "Store",
                    "contact@terraclic.be",
                    "3200000000",
                    new Address(
                            999,
                            "Rue Centrale",
                            1,
                            "Namur",
                            5000
                    ),
                    true,
                    true,
                    true,
                    "BE0000000001",
                    LocalDate.now(),
                    null
            );

            documents.add(new Document(
                    1,
                    LocalDate.of(2020, 12, 2),
                    new DocumentType(1,"Test"),
                    null,
                    true,
                    LocalDate.of(2020, 12, 2),
                    LocalDate.of(2020, 12, 2),
                    LocalDate.of(2020, 12, 2),
                    LocalDate.of(2020, 12, 2),
                    20,
                    new WorkFlow(
                            1,
                            new Status("Status"),
                            new WorkFlowType(1,"Internal", false, false, true),
                            usCompany
                    ),
                    new Address(
                            1,
                            "Café route",
                            12,
                            "Namur",
                            5000
                    ),
                    null,
                    null
            ));

            ClientSupplier martinSophie = new ClientSupplier(
                    2,
                    "Martin",
                    "Sophie",
                    "sophie.martin@email.com",
                    "32470000002",
                    new Address(
                            2,
                            "Avenue Louise",
                            10,
                            "Bruxelles",
                            1050
                    ),
                    true,
                    false,
                    false,
                    "BE0987654321",
                    LocalDate.now(),
                    null
            );

            documents.add(new Document(
                    2,
                    LocalDate.of(2024, 3, 15),
                    new DocumentType(2, "Quote"),
                    null,
                    false,
                    LocalDate.of(2024, 3, 15),
                    LocalDate.of(2024, 3, 20),
                    LocalDate.of(2024, 3, 16),
                    LocalDate.of(2024, 3, 21),
                    30,
                    new WorkFlow(
                            2,
                            new Status("Pending"),
                            new WorkFlowType(2,"Buy", false, false, false),
                            usCompany,
                            martinSophie
                    ),
                    new Address(
                            2,
                            "Avenue Louise",
                            10,
                            "Bruxelles",
                            1050
                    ),
                    null,
                    null
            ));

            return new ArrayList<>(documents);
        } catch (DataValidationException e) {
            return new ArrayList<>();
        }
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
    public ArrayList<ClientSupplier> getAllClientSupplier() {
        try {
            ArrayList<ClientSupplier> clients = new ArrayList<>();

            clients.add(new ClientSupplier(
                    1,
                    "Dupont",
                    "Jean",
                    "jean.dupont@email.com",
                    "32470000001",
                    new Address(1, "Avenue Louise", 10, new Locality("Ixelles", 1050)),
                    true,
                    false,
                    true,
                    "BE0123456789",
                    LocalDate.now(),
                    null
            ));

            clients.add(new ClientSupplier(
                    2,
                    "Martin",
                    "Sophie",
                    "sophie.martin@email.com",
                    "32470000002",
                    new Address(2, "Avenue Louise", 10, new Locality("Ixelles", 1050)),
                    true,
                    false,
                    false,
                    "BE0987654321",
                    LocalDate.now(),
                    null
            ));

            clients.add(new ClientSupplier(
                    3,
                    "Nguyen",
                    "Linh",
                    "linh.nguyen@email.com",
                    "32470000003",
                    new Address(3, "Avenue Louise", 10, new Locality("Ixelles", 1050)),
                    true,
                    false,
                    true,
                    "BE1122334455",
                    LocalDate.now(),
                    null
            ));

            clients.add(new ClientSupplier(
                    4,
                    "Dubois",
                    "Marc",
                    "marc.dubois@email.com",
                    "32470000004",
                    new Address(4, "Avenue Louise", 10, new Locality("Ixelles", 1050)),
                    true,
                    false,
                    false,
                    "BE6677889900",
                    LocalDate.now(),
                    null
            ));

            clients.add(new ClientSupplier(
                    5,
                    "Smith",
                    "Anna",
                    "anna.smith@email.com",
                    "32470000005",
                    new Address(5, "Avenue Louise", 10, new Locality("Ixelles", 1050)),
                    true,
                    false,
                    true,
                    "BE5566778899",
                    LocalDate.now(),
                    null
            ));

            return clients;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Crée un nouveau document.
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
     *
     * @param documentId fais référence au doc à modifier.
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
     *
     * @return liste de types de documents
     * @throws DataValidationException à supprimer
     */
    public ArrayList<DocumentType> getAllDocumentType() {
        try {
            return getAllDocuments().stream()
                    .map(Document::getDocumentType)
                    .distinct()
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    /**
     * Retourne les statuts des workflows associés aux documents.
     *
     * @return liste des statuts de workflow
     * @throws DataValidationException à supprimer
     */
    public ArrayList<Status> getAllWorkflowStatus() {

        return getAllDocuments().stream()
                .map(doc -> doc.getWorkflow().getStatus())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Status createStatus(String value) throws DataValidationException {
        return new Status(value);
    }

    public DocumentType createDocumentType(String value) throws DataValidationException {
        return new DocumentType(1,value);
    }
}