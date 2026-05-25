package main.java.be.henallux.project.view;

import main.java.be.henallux.project.controller.DocumentController;
import main.java.be.henallux.project.model.NotificationItem;
import main.java.be.henallux.project.model.Document;
import main.java.be.henallux.project.model.DocumentType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;


/**
 * This view allows users to manage and search through a list of documents.
 * <p>This class extends {@link JPanel} to provide a table view and search functionality for
 * filtering document data based on various parameters such as ID, Type, date.
 * <p>The table is populated via a custom model, {@link DocumentTableModel}, and provides
 * ease of navigation with interactive search fields and filter checkboxes.
 * Clicking on a row opens a detailed Form Document view through the main application window.
 *
 * @see MainWindow#openDocumentForm(Document)
 */
public class DocumentTable extends JPanel {

    private final MainWindow mainWindow;
    private final DocumentController controller;
    private DocumentTableModel model;
    private final ArrayList<Document> documents;
    private ArrayList<Document> displayDocuments;

    private JTextField idDocument;
    private JComboBox<ComboBoxItem<DocumentType>> comboTypeDocumentFilter;
    private JSpinner startCreationDate;
    private JSpinner endCreationDate;
    private JCheckBox useStartDate;
    private JCheckBox useEndDate;

    private JTable table;

    /**
     * Initializes the document table view and loads all documents from the controller.
     * <p>This view is typically instantiated from {@link MainWindow} and integrated into the
     * main application layout. It builds the search panel and the table panel, and initializes
     * the internal document lists used for filtering and display.
     *
     * @param mainWindow the main application window associated with this view.
     */
    public DocumentTable(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = new DocumentController();

        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        ArrayList<Document> loaded = new ArrayList<>();
        try {
            loaded = controller.getAllDocuments();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        documents = loaded;

        displayDocuments = new ArrayList<>(documents);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(buildHeader(), BorderLayout.NORTH);
        top.add(buildSearchPanel(), BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    /**
     * Builds the header panel of the view.
     * <p>Displays the title of the document management section and provides a visual
     * separation from the search and table components.
     *
     * @return a {@link JPanel} containing the header
     */
    private JPanel buildHeader() {
        JLabel title = new JLabel("Document Search");
        title.setFont(new Font("Inter", Font.BOLD, 20));

        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.add(title, BorderLayout.NORTH);
        return header;
    }

    /**
     * Builds the search panel containing input fields and options
     * to define filters and actions for searching or creating client and supplier records.
     * <p>Each item is warp in a JPanel and placed with {@code BorderLayout}
     *
     * @return a {@link JPanel} containing the search panel layout.
     */
    private JPanel buildSearchPanel() {
        idDocument = new JTextField(10);
        ViewUtils.setCursor(idDocument);
        ViewUtils.digitsOnly(idDocument);
        ViewUtils.addFilterListener(idDocument, this::onFilterClick);
        JPanel idFields = new JPanel(new BorderLayout(0, 4));
        idFields.add(new JLabel("Document ID"), BorderLayout.NORTH);
        idFields.add(idDocument, BorderLayout.CENTER);

        comboTypeDocumentFilter = new JComboBox<>();
        ViewUtils.setCursor(comboTypeDocumentFilter);
        try {
            setDocumentTypes(controller.getAllDocumentTypes());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        ViewUtils.addFilterListener(comboTypeDocumentFilter, this::onFilterClick);
        JPanel typeFields = new JPanel(new BorderLayout(0, 4));
        typeFields.add(new JLabel("Document type"), BorderLayout.NORTH);
        typeFields.add(comboTypeDocumentFilter, BorderLayout.CENTER);

        JButton btnCreate = new JButton("Create");
        ViewUtils.setCursor(btnCreate);
        btnCreate.addActionListener(e -> onCreateClick());

        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.add(ViewUtils.makeRow(idFields));
        leftColumn.add(Box.createVerticalStrut(6));
        leftColumn.add(ViewUtils.makeRow(typeFields));
        leftColumn.add(Box.createVerticalStrut(8));
        leftColumn.add(ViewUtils.makeRow(btnCreate));

        useStartDate = new JCheckBox("Start date");
        ViewUtils.setCursor(useStartDate);
        startCreationDate = ViewUtils.createDateSpinner();
        startCreationDate.setEnabled(false);
        startCreationDate.addChangeListener(e -> onFilterClick());
        useStartDate.addActionListener(e -> {
            startCreationDate.setEnabled(useStartDate.isSelected());
            onFilterClick();
        });
        JPanel startDateRow = new JPanel(new BorderLayout(6, 0));
        startDateRow.add(useStartDate, BorderLayout.WEST);
        startDateRow.add(startCreationDate, BorderLayout.CENTER);

        useEndDate = new JCheckBox("End date");
        ViewUtils.setCursor(useEndDate);
        endCreationDate = ViewUtils.createDateSpinner();
        endCreationDate.setEnabled(false);
        endCreationDate.addChangeListener(e -> onFilterClick());
        useEndDate.addActionListener(e -> {
            endCreationDate.setEnabled(useEndDate.isSelected());
            onFilterClick();
        });
        JPanel endDateRow = new JPanel(new BorderLayout(6, 0));
        endDateRow.add(useEndDate, BorderLayout.WEST);
        endDateRow.add(endCreationDate, BorderLayout.CENTER);

        JPanel rightColumn = ViewUtils.createColumnPanel();
        rightColumn.setBorder(BorderFactory.createTitledBorder("Date filter"));
        rightColumn.add(ViewUtils.makeRow(startDateRow));
        rightColumn.add(Box.createVerticalStrut(6));
        rightColumn.add(ViewUtils.makeRow(endDateRow));

        JPanel fieldsRow = new JPanel(new BorderLayout(12, 0));
        fieldsRow.add(leftColumn, BorderLayout.CENTER);
        fieldsRow.add(rightColumn, BorderLayout.EAST);

        JPanel fieldsColumn = new JPanel();
        fieldsColumn.setLayout(new BoxLayout(fieldsColumn, BoxLayout.Y_AXIS));
        fieldsColumn.setBorder(BorderFactory.createTitledBorder("Filters"));
        fieldsColumn.add(ViewUtils.makeRow(fieldsRow));

        return fieldsColumn;
    }

    /**
     * Builds the panel containing the document table.
     * <p>The table uses {@link DocumentTableModel} as its data model and allows single row selection only.
     * <p>A mouse listener is added to detect clicks on specific columns:
     * <ul><li>Column 5: triggers the delete action via {@code onDeleteClick()}.</li>
     *  <li>Other Column: triggers the update action via {@code onUpdateClick()}.</li></ul>
     *
     * @return a {@link JScrollPane} containing the configured table
     */
    private JScrollPane buildTablePanel() {
        model = new DocumentTableModel(displayDocuments);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = table.convertColumnIndexToModel(
                        table.columnAtPoint(e.getPoint()));
                if (col == DocumentTableModel.TBL_BTN_DEL) onDeleteClick();
                if (col == DocumentTableModel.TBL_BTN_UPDATE) onUpdateClick();
            }
        });
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());

                if (col == DocumentTableModel.TBL_BTN_UPDATE || col == DocumentTableModel.TBL_BTN_DEL) {
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    table.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        table.getColumnModel().getColumn(DocumentTableModel.TBL_BTN_DEL).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(DocumentTableModel.TBL_BTN_UPDATE).setCellRenderer(new ButtonRenderer());

        ViewUtils.resizeColumnWidth(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 250));
        return scroll;
    }

    /**
     * Applies filters to the document list and refreshes the table content.
     * <p>Filtering is performed on:
     * <ul><li>Document ID (exact numeric match if provided)</li>
     *   <li>Document type</li>
     *   <li>Creation date range (start and/or end date)</li></ul>
     * <p>If no filter is active, all documents are displayed.
     * <p>The resulting list is stored in {@code displayDocuments} and used to refresh the {@link DocumentTableModel}.
     */
    public void onFilterClick() {

        Integer idValue = null;

        String idText = idDocument.getText().trim();

        if (!idText.isEmpty()) {
            try {
                idValue = Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                return;
            }
        }

        ComboBoxItem<DocumentType> selectedItem = (ComboBoxItem<DocumentType>) comboTypeDocumentFilter.getSelectedItem();

        LocalDate startDate = useStartDate.isSelected()
                ? ViewUtils.toLocalDate((Date) startCreationDate.getValue())
                : null;

        LocalDate endDate = useEndDate.isSelected()
                ? ViewUtils.toLocalDate((Date) endCreationDate.getValue())
                : null;

        displayDocuments = new ArrayList<>();

        for (Document doc : documents) {

            boolean match = true;

            if (idValue != null && idValue > 0 && doc.getId() != idValue) {
                match = false;
            }

            if (selectedItem != null && selectedItem.getObject() != null && !doc.getDocumentType().equals(selectedItem.getObject())) {
                match = false;
            }

            if (startDate != null && doc.getDateOfCreation().isBefore(startDate)) {
                match = false;
            }

            if (endDate != null && doc.getDateOfCreation().isAfter(endDate)) {
                match = false;
            }

            if (match) {
                displayDocuments.add(doc);
            }
        }

        model.setDocuments(displayDocuments);
    }

    /**
     * Opens the document creation form.
     * <p>The form is opened in creation mode by passing {@code null} to the main window,
     * which results in an empty document form being displayed.
     *
     * @see MainWindow#openDocumentForm(Document)
     */
    public void onCreateClick() {
        mainWindow.openDocumentForm(null);
    }

    /**
     * Opens the selected document in edit mode.
     * The selected row from the table is converted into a
     * {@link Document} and passed to
     * {@link MainWindow#openDocumentForm(Document)}.
     * Important:
     * - If no row is selected → show an error message.
     * - If an object is passed → form is in EDIT mode.
     * - If null was passed → form would be in CREATE mode.
     */
    public void onUpdateClick() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to modify.");
            return;
        }

        Document doc = displayDocuments.get(selectedRow);

        mainWindow.openDocumentForm(doc);
    }

    /**
     * Handles the delete action triggered from the UI.
     * <p>Retrieves the currently selected row in the table, asks for user confirmation,
     * and delegates the deletion to the overloaded {@link #onDeleteClick(Document)} method.
     * <p>If no row is selected, a warning dialog is shown and the operation is canceled.
     */
    public void onDeleteClick() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this document?",
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            Document docToDelete = displayDocuments.get(selectedRow);
            onDeleteClick(docToDelete);
            controller.deleteDocument(docToDelete);

        }
    }

    /**
     * Deletes the specified Document and updates the UI and model accordingly.
     * <p>If the deletion is successful, the item is removed from both the internal lists
     * and the table model, and a success notification is displayed.
     * <p>If the deletion fails, an error notification is shown with an option to retry the operation.
     *
     * @param docToDelete the Document to delete
     */
    public void onDeleteClick(Document docToDelete) {
        boolean isSuccess = controller.deleteDocument(docToDelete);
        if (isSuccess) {
            documents.remove(docToDelete);
            displayDocuments.remove(docToDelete);
            model.setDocuments(displayDocuments);

            mainWindow.getNotificationController().push(new NotificationItem(
                    "Delete",
                    docToDelete.getLabel() + " has been deleted.",
                    NotificationItem.Type.SUCCESS,
                    null
            ));

        } else {
            mainWindow.getNotificationController().push(new NotificationItem(
                    "Delete",
                    "Failed to delete. Click to retry.",
                    NotificationItem.Type.ERROR,
                    () -> onDeleteClick(docToDelete)
            ));
        }
    }

    /**
     * Updates the document type filter ComboBox with the available document types.
     * An "All" option is added first to allow unfiltered display.
     *
     * @param types list of available document types
     */
    public void setDocumentTypes(ArrayList<DocumentType> types) {
        comboTypeDocumentFilter.removeAllItems();
        comboTypeDocumentFilter.addItem(new ComboBoxItem<>(null, "All"));
        for (DocumentType documentType : types) {
            comboTypeDocumentFilter.addItem(new ComboBoxItem<>(documentType, documentType.getName()));
        }
    }
}