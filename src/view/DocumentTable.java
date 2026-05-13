package view;

import controller.DocumentController;
import exception.DataValidationException;
import model.ClientSupplier;
import model.Document;
import model.DocumentType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;


/**
 * This view allows users to manage and search through a list of documents.
 * This class extends {@link JPanel} to provide a table view and search functionality for
 * filtering document data based on various parameters such as ID, Type, date.
 *
 * The table is populated via a custom model, {@link DocumentTableModel}, and provides
 * ease of navigation with interactive search fields and filter checkboxes.
 *
 * Clicking on a row opens a detailed Form Document view through the main application window.
 *  @see MainWindow#openDocumentForm(Document)
 */
public class DocumentTable extends JPanel {
    private MainWindow mainWindow;
    private DocumentController controller;
    private DocumentTableModel model;
    private ArrayList<Document> documents;
    private ArrayList<Document> displayDocuments;

    private JPanel searchPanel, tablePanel;
    private JTextField idDocument;
    private JComboBox<ComboBoxItem<DocumentType>> comboTypeDocumentFilter;
    private JSpinner startCreationDate;
    private JSpinner endCreationDate;
    private JCheckBox useStartDate;
    private JCheckBox useEndDate;

    private JTable table;

    public DocumentTable(MainWindow mainWindow) throws DataValidationException {
        this.mainWindow = mainWindow;
        this.controller = new DocumentController();

        setLayout(new BorderLayout(10, 16));
        setBorder(new EmptyBorder(16, 16, 16, 16));

        documents = controller.getAllDocuments();
        displayDocuments = new ArrayList<>(documents);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.add(buildHeader(), BorderLayout.NORTH);
        top.add(buildSearchPanel(), BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }
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
     * Each item is warp in a JPanel and placed with {@code BorderLayout}
     *
     * @return a {@link JPanel} containing the search panel layout.
     */
    private JPanel buildSearchPanel() throws DataValidationException {
        searchPanel = new JPanel(new BorderLayout());
        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        idDocument = new JTextField(10);
        idDocument = ViewUtils.digitsOnly(idDocument);
        idDocument = ViewUtils.addFilterListener(idDocument, this::onFilterClick);
        JPanel idFields = new JPanel(new BorderLayout(0, 4));
        idFields.add(new JLabel("Document ID"), BorderLayout.NORTH);
        idFields.add(idDocument, BorderLayout.CENTER);

        comboTypeDocumentFilter = new JComboBox<>();
        setDocumentTypes(controller.getAllDocumentType());
        comboTypeDocumentFilter = ViewUtils.addFilterListener(comboTypeDocumentFilter, this::onFilterClick);
        JPanel typeFields = new JPanel(new BorderLayout(0, 4));
        typeFields.add(new JLabel("Document type"), BorderLayout.NORTH);
        typeFields.add(comboTypeDocumentFilter, BorderLayout.CENTER);

        JButton btnCreate = new JButton("Create");
        btnCreate.addActionListener(e -> onCreateClick());

        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.add(ViewUtils.makeRow(idFields));
        leftColumn.add(Box.createVerticalStrut(6));
        leftColumn.add(ViewUtils.makeRow(typeFields));
        leftColumn.add(Box.createVerticalStrut(8));
        leftColumn.add(ViewUtils.makeRow(btnCreate));

        useStartDate = new JCheckBox("Start date");
        startCreationDate = ViewUtils.createDateSpinner();
        startCreationDate.setEnabled(false);
        startCreationDate.addChangeListener(e -> onFilterClick());
        useStartDate.addActionListener(e -> {
            startCreationDate.setEnabled(useStartDate.isSelected());
            onFilterClick();
        });
        JPanel startDateRow = new JPanel(new BorderLayout(6, 0));
        startDateRow.add(useStartDate,      BorderLayout.WEST);
        startDateRow.add(startCreationDate, BorderLayout.CENTER);

        useEndDate = new JCheckBox("End date");
        endCreationDate = ViewUtils.createDateSpinner();
        endCreationDate.setEnabled(false);
        endCreationDate.addChangeListener(e -> onFilterClick());
        useEndDate.addActionListener(e -> {
            endCreationDate.setEnabled(useEndDate.isSelected());
            onFilterClick();
        });
        JPanel endDateRow = new JPanel(new BorderLayout(6, 0));
        endDateRow.add(useEndDate,      BorderLayout.WEST);
        endDateRow.add(endCreationDate, BorderLayout.CENTER);

        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBorder(BorderFactory.createTitledBorder("Date filter"));
        rightColumn.add(ViewUtils.makeRow(startDateRow));
        rightColumn.add(Box.createVerticalStrut(6));
        rightColumn.add(ViewUtils.makeRow(endDateRow));

        JPanel fieldsRow = new JPanel(new BorderLayout(12, 0));
        fieldsRow.add(leftColumn,  BorderLayout.CENTER);
        fieldsRow.add(rightColumn, BorderLayout.EAST);

        JPanel fieldsColumn = new JPanel();
        fieldsColumn.setLayout(new BoxLayout(fieldsColumn, BoxLayout.Y_AXIS));
        fieldsColumn.setBorder(BorderFactory.createTitledBorder("Filters"));
        fieldsColumn.add(ViewUtils.makeRow(fieldsRow));

        return fieldsColumn;
    }

    /**
     * Builds the panel containing the document table.
     * The table uses {@link DocumentTableModel} as its data model and allows
     * single row selection only.
     * A mouse listener is added to detect clicks on specific columns:
     * <ul><li>Column 5: triggers the delete action via {@code onDeleteClick()}.</li>
     *  <li>Other Column: triggers the update action via {@code onUpdateClick()}.</li></ul>
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
                if (col == 5) onDeleteClick();
                if (col != -1) onUpdateClick();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 250));
        return scroll;
    }

    /**
     * Applies filters to the document list and refreshes live table.
     * Filtering is performed on:
     * <ul><li>Document ID (partial match)</li>
     *   <li>Document type</li>
     *   <li>Creation date (start and end range)</li></ul>
     * If no filters are selected, all documents are displayed.
     * The filtered results are stored in {@code displayDocuments}
     * and the table model is refreshed using
     * {@link ClientSupplierTableModel#setClientSuppliers(java.util.ArrayList)}.
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

    public void onCreateClick(){
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
    public void onUpdateClick(){
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a document to modify.");
            return;
        }

        Document doc = displayDocuments.get(selectedRow);

        mainWindow.openDocumentForm(doc);
    }

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

            controller.deleteDocument(docToDelete);

            documents.remove(docToDelete);
            displayDocuments.remove(selectedRow);
            model.setDocuments(displayDocuments);
        }
    }

    /**
     * Updates the document type filter ComboBox with the available document types.
     * An "All" option is added first to allow unfiltered display.
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