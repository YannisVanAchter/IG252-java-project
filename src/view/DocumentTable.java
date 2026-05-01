package view;

import controller.DocumentController;
import model.ClientSupplier;
import model.Document;
import model.DocumentTableModel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;


/**
 * This view allows search and display documents
 *
 * This class extends {@link JPanel} and contains:
 * A search panel allowing you to filter documents according to several criteria
 * A table displaying the documents and filtered documents
 *
 * Possible actions on the table include:
 * Redirection to editing view
 * Deleting a document
 */
public class DocumentTable extends JPanel {
    private MainWindow mainWindow;
    private DocumentController controller;
    private DocumentTableModel model;
    private ArrayList<Document> documents;
    private ArrayList<Document> displayDocuments;

    private JPanel searchPanel, tablePanel;
    private JTextField idDocument;
    private JComboBox<String> comboTypeDocumentFilter;
    private JSpinner startCreationDate;
    private JSpinner endCreationDate;
    private JCheckBox useStartDate;
    private JCheckBox useEndDate;

    private JTable table;

    public DocumentTable(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.controller = new DocumentController();

        setLayout(new BorderLayout(0, 16));

        documents = controller.getAllDocuments();
        displayDocuments = new ArrayList<>(documents);

        buildSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        buildTablePanel();
        add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * Builds the search panel containing the filter fields and action buttons.
     * Each field is encapsulated in a smaller JPanel for better display management.
     * @see #labeled(String, JComponent)
     */
    private void buildSearchPanel() {
        searchPanel = new JPanel(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        idDocument = new JTextField(10);
        fieldsPanel.add(labeled("Document ID", idDocument));

        comboTypeDocumentFilter = new JComboBox<>(new String[]{
                "All", "Invoice", "Contract", "Purchase order", "Delivery note", "Quote"
        });
        fieldsPanel.add(labeled("Document type", comboTypeDocumentFilter));

        useStartDate = new JCheckBox("Start date");
        startCreationDate = new JSpinner(new SpinnerDateModel());
        startCreationDate.setEditor(new JSpinner.DateEditor(startCreationDate, "dd/MM/yyyy"));
        startCreationDate.setEnabled(false);
        useStartDate.addActionListener(e ->
                startCreationDate.setEnabled(useStartDate.isSelected()));
        fieldsPanel.add(labeled(useStartDate, startCreationDate));

        useEndDate = new JCheckBox("End date");
        endCreationDate = new JSpinner(new SpinnerDateModel());
        endCreationDate.setEditor(new JSpinner.DateEditor(endCreationDate, "dd/MM/yyyy"));
        endCreationDate.setEnabled(false);
        useEndDate.addActionListener(e ->
                endCreationDate.setEnabled(useEndDate.isSelected()));
        fieldsPanel.add(labeled(useEndDate, endCreationDate));

        searchPanel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> onFilterClick());

        JButton btnCreate = new JButton("Create");
        btnCreate.addActionListener(e -> onCreateClick());


        buttonPanel.add(btnSearch);
        buttonPanel.add(btnCreate);

        searchPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Construct the panel containing the document table.
     * The table uses {@link DocumentTableModel} as its data model.
     */
    private void buildTablePanel() {
        tablePanel = new JPanel(new BorderLayout());

        model = new DocumentTableModel(displayDocuments);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 4) {
                    onModifyClick();
                };
                if (col == 5) {
                    onDeleteClick();
                };
            }
        });
    }

    /**
     * Creates a panel containing a label and a component.
     * @param text the label text to display
     * @param comp the associated component
     * @return a JPanel containing the label and the component
     */
    private JPanel labeled(String text, JComponent comp) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(new JLabel(text));
        p.add(Box.createVerticalStrut(4));
        p.add(comp);
        return p;
    }

    private JPanel labeled(JCheckBox checkBox, JComponent comp) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(checkBox);
        p.add(Box.createVerticalStrut(4));
        p.add(comp);
        return p;
    }

    private LocalDate toLocalDate(Date date) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Method called when the search button is clicked.
     * 
     * Apply the user to selected filters on the document list:
     * Filter by ID (partial search)
     * Filter by document type
     * Filter by start date (if enabled)
     * Filter by end date (if enabled)
     *
     * Updates the table model with the filtered documents.
     */
    public void onFilterClick() {
        String idText = idDocument.getText().trim();
        String selectedType = (String) comboTypeDocumentFilter.getSelectedItem();

        LocalDate startDate = useStartDate.isSelected() ? toLocalDate((Date) startCreationDate.getValue()) : null;
        LocalDate endDate = useEndDate.isSelected() ? toLocalDate((Date) endCreationDate.getValue()) : null;

        displayDocuments = new ArrayList<>();

        for (Document doc : documents) {
            boolean match = true;

            if (!idText.isEmpty() && !doc.getId().toLowerCase().contains(idText.toLowerCase())) {
                match = false;
            }

            if (selectedType != null && !selectedType.equals("All")
                    && !doc.getType().equals(selectedType)) {
                match = false;
            }


            if (startDate != null && doc.getCreationDate().isBefore(startDate)) {
                match = false;
            }

            if (endDate != null && doc.getCreationDate().isAfter(endDate)) {
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
     * Called when the user clicks on "Modify".
     *
     * Flow of the selected data:
     * 1. Get the selected row from the table.
     * 2. Retrieve the corresponding Document object from displayDocuments.
     * 3. Send this object to the MainWindow.
     * @see MainWindow#openDocumentForm(model.Document)
     * 4. MainWindow forwards it to DocumentForm.
     * 5. The form loads the data to allow editing.
     *
     * Important:
     * - If no row is selected → show an error message.
     * - If an object is passed → form is in EDIT mode.
     * - If null was passed → form would be in CREATE mode.
     */
    public void onModifyClick(){
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

}