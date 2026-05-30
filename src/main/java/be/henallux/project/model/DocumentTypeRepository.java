package main.java.be.henallux.project.model;

import java.util.List;
import java.util.Optional;

public class DocumentTypeRepository {

    public static volatile DocumentTypeRepository INSTANCE;
    private final List<DocumentType> TYPES;

    public DocumentTypeRepository() {
        TYPES = List.of(
                new DocumentType(1, "Purchase Order")
        );
    }

    public static DocumentTypeRepository getInstance() {
        if (INSTANCE == null) {
            setInstance(new DocumentTypeRepository());
        }
        return INSTANCE;
    }

    private static void setInstance(DocumentTypeRepository documentTypeRepository) {
        INSTANCE = documentTypeRepository;
    }


    // Research by type name
    public Optional<DocumentType> findByName(String name) {
        return TYPES.stream()
                .filter(t -> t.getName().equals(name))
                .findFirst();
    }

    // All documentTypes
    public List<DocumentType> getDocumentTypes() {
        return TYPES;
    }
}
